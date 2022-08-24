package com.mall.search_service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.utils.StringUtils;
import com.mall.common.model.SkuForEsSearchModel;
import com.mall.common.utils.R;
import com.mall.search_service.config.ELSearchConfig;
import com.mall.search_service.constant.EsConstant;
import com.mall.search_service.feign.ProductFeignService;
import com.mall.search_service.service.SearchMallParamService;
import com.mall.search_service.vo.BrandVO;
import com.mall.search_service.vo.SearchMallParam;
import com.mall.search_service.vo.SearchMallResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchMallParamServiceImpl implements SearchMallParamService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    ProductFeignService productFeignService;

    /**
     * 动态构建出查询需要的DSL语句
     */
    @Override
    public SearchMallResult search(SearchMallParam param) {

        SearchMallResult searchMallResult = null;
        //执行检索请求,返回搜索条件匹配的ES索引的结果
        SearchRequest searchRequest = buildSearchRequest(param);
       //发送ES搜索请求
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, ELSearchConfig.COMMON_OPTIONS);
            //响应数据封装成集合
            searchMallResult = buildSearchResult(response,param);
        }catch (IOException e){
            e.printStackTrace();
        }
        return searchMallResult;
    }


//++++++++++++++++++++++++++++++++++++++++
    /**
     * 执行检索请求,返回搜索条件匹配的ES索引的结果
     */
    private SearchRequest buildSearchRequest(SearchMallParam param) {
//创建DSL查询
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//执行查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //对全文搜索的所有条件逐个匹配
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        if (param.getCatalog3Id() != null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        if (param.getBrandId() != null && param.getBrandId().size()>0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        if ( param.getAttrs() != null && param.getAttrs().size()>0){
            //每一个子属性对应一次查询
            for (String attr : param.getAttrs()) {
                //嵌入式查询
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] strs = attr.split("_");   //attrs=机身内存_128GB
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",strs[0]));
                //查询并列的值,如：机身内存=128GB,256GB,512GB
                //之后分隔为单个值,只有首个有值
                String[] attrValues  = strs[1].split(":");
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                //不参与ES匹配评分设置
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }
        if (param.getHasStock() != null){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("hasStock",param.getHasStock()==1));
        }
        if (!StringUtils.isEmpty(param.getSkuPrice())){
            RangeQueryBuilder skuPriceRange = QueryBuilders.rangeQuery("skuPrice");
            String skuPrice = param.getSkuPrice();
            //按照价格搜索的连接符来隔开左右数值
            String[] prices = skuPrice.split("_");
            //判断价格区间输入正确
            //没有输入最高值（长度是1）、没有输入最低值（长度是2）、两个都输入（长度是2）
            int priceLow=0;
            int priceHign = 0;
            try{
                if(prices.length==2){
                    if(skuPrice.startsWith("_")){  //_price
                        priceHign = Math.max(Integer.parseInt(prices[1]), 0);  //最高价格
                        skuPriceRange.gte(priceLow).lte(priceHign);
                    }else {   //price1_price2
                        priceLow= Math.max(Integer.parseInt(prices[0]), 0);  //最低价格
                        priceHign = Math.max(Integer.parseInt(prices[1]), 0);  //最高价格
                        if (priceLow >= priceHign) {
                            int transfer = priceLow;
                            priceLow = priceHign;
                            priceHign = transfer;
                        }
                        skuPriceRange.gte(priceLow);
                        skuPriceRange.lte(priceHign);
                    }
                }else if(prices.length==1){
                    priceLow= Math.max(Integer.parseInt(prices[0]), 0);  //最低价格
                    skuPriceRange.gte(priceLow);
                }
            }catch(NumberFormatException e) {
                log.info("输入异常:"+e.getMessage());
                e.printStackTrace();
            }
            boolQueryBuilder.filter(skuPriceRange);
        }
        //把所有的条件封装
        sourceBuilder.query(boolQueryBuilder);
//执行查询条件的排序、分页、高亮
        //排序
        if (!StringUtils.isEmpty(param.getSort())) {
            String[] sorts = param.getSort().split("_");  //colunm_asc/desc
            SortOrder order = sorts[1].equalsIgnoreCase("asc") ? SortOrder.ASC:SortOrder.DESC;
            sourceBuilder.sort(sorts[0],order);
        }
        //分页
        if (null!=param.getPageNum()) {
            sourceBuilder.from((param.getPageNum()-1)*EsConstant.PAGESIZE);
            sourceBuilder.size(EsConstant.PAGESIZE);
        }
        //高亮
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }
//保存为聚合分析集合
        //品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId"); //显示的数量
        //嵌套聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName"));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg"));
        sourceBuilder.aggregation(brand_agg);
        //商品分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId");
        //嵌套聚合
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName"));
        sourceBuilder.aggregation(catalog_agg);
        //属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attrAggregation = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        //嵌套聚合
        attrAggregation.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName"));
        attrAggregation.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue"));
        attr_agg.subAggregation(attrAggregation);
        sourceBuilder.aggregation(attr_agg);

        System.out.println("构建的dsl语句：" + sourceBuilder.toString());
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    /**
     * ES搜索的响应数据封装成集合
     */
    private SearchMallResult buildSearchResult(SearchResponse response, SearchMallParam param) {
//查询命中的结果
        SearchMallResult searchMallResult =new SearchMallResult();
        SearchHits hits = response.getHits();
        List<SkuForEsSearchModel> skuForEsSearchModels = new ArrayList<>();
        //查询结果的集合
        if(null!=hits.getHits() && hits.getHits().length>0){
            for (SearchHit hit : hits.getHits()) {
                //保证只命中一个结果时
                String sourceAsString = hit. getSourceAsString();
                SkuForEsSearchModel esModel = JSON.parseObject(sourceAsString, SkuForEsSearchModel.class);
                //保证命中多个结果时
//                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//高亮标题处理
                if (!StringUtils.isEmpty(param.getKeyword())){
                    //标题符合搜索条件的数值为高亮
                    HighlightField highlightField = hit.getHighlightFields().get("skuTitle");
                    String skuTitle = highlightField.getFragments()[0].string();
                    esModel.setSkuTitle(skuTitle);
                }
                skuForEsSearchModels.add(esModel);
            }
        };
        searchMallResult.setProducts(skuForEsSearchModels);
//品牌聚合处理
        Aggregations responseAggregations = response.getAggregations();
        ParsedLongTerms brand_agg = responseAggregations.get("brand_agg");
        List<SearchMallResult.BrandVO> BrandVOes=new ArrayList<>();
        //获取品牌信息
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchMallResult.BrandVO brandVO =new SearchMallResult.BrandVO();
            brandVO.setBrandId(bucket.getKeyAsNumber().longValue());
            //获取嵌套聚合
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brand_name = brand_name_agg.getBuckets().get(0).getKeyAsString();  //取决于显示数量的设定值
            brandVO.setBrandName(brand_name);  //保存名
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brand_img = brand_img_agg.getBuckets().get(0).getKeyAsString();  //取决于显示数量的设定值
            brandVO.setBrandImg(brand_img);  //保存图片
            BrandVOes.add(brandVO);
        }
        searchMallResult.setBrands(BrandVOes);
//商品分类聚合处理
        ParsedLongTerms catalog_agg = responseAggregations.get("catalog_agg");
        List<SearchMallResult.CatalogVO> catalogVOes = new ArrayList<>();
        //获取商品分类信息
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            SearchMallResult.CatalogVO catalogVO = new SearchMallResult.CatalogVO();
            catalogVO.setCatalogId((bucket.getKeyAsNumber().longValue())); //保存id
            //获取嵌套聚合
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();  //取决于显示数量的设定值
            catalogVO.setCatalogName(catalog_name);  //保存名
            catalogVOes.add(catalogVO);
        }
        searchMallResult.setCatalogs(catalogVOes);
//属性聚合处理
        //获取嵌入式聚合
        ParsedNested attr_agg = responseAggregations.get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        List<SearchMallResult.AttrVO> AttrVOes = new ArrayList<>();
        //获取属性信息
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchMallResult.AttrVO AttrVO = new SearchMallResult.AttrVO();
            //获取嵌套聚合
            Long attrId = bucket.getKeyAsNumber().longValue();
            Aggregations subAttrAgg = bucket.getAggregations();
            ParsedStringTerms attrNameAgg=subAttrAgg.get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            ParsedStringTerms attrValueAgg = subAttrAgg.get("attr_value_agg");

//            //处理sku的attrValue含有多个属性并列的情况，如: attrValue=[128GB;256GB, 128GB;256GB;512GB, 512GB]
//            List<String> attrValues = new ArrayList<>();
//            for (Terms.Bucket attrValueAggBucket : attrValueAgg.getBuckets()) {
//                String attrValue = attrValueAggBucket.getKeyAsString();
//                if(attrValue.contains(";")){
//                    String[] attrValueSplit = attrValue.split(";");
//                    SearchMallResult.AttrVO AttrVOSplit = new SearchMallResult.AttrVO();
//                    attrValues.addAll(Arrays.asList(attrValueSplit));
//                }else {
//                    attrValues.add(attrValue);
//                }
//            }

            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> {
                return item.getKeyAsString();
            }).collect(Collectors.toList());
            //去除重复的属性值
            attrValues = attrValues.stream().distinct().collect(Collectors.toList());
            //保存id,名,值
            AttrVO.setAttrId(attrId);
            AttrVO.setAttrName(attrName);
            AttrVO.setAttrValue(attrValues);
            AttrVOes.add(AttrVO);
        }
        searchMallResult.setAttrs(AttrVOes);
//计算数据的分页
        searchMallResult.setPageNum(param.getPageNum()); //保存页码
        long total = hits.getTotalHits().value;
        searchMallResult.setTotal(total);  //保存总记录数
        int totalPage = ((int) total + EsConstant.PAGESIZE - 1) / EsConstant.PAGESIZE;
        searchMallResult.setTotalPages(totalPage);  //保存总页码
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            pageNavs.add(i);
        }
        searchMallResult.setPageNavs(pageNavs); //保存可遍历的页码
//保存搜索条件的面包屑导航
        //属性的面包屑处理
        List<String> attrs = param.getAttrs();
        if (attrs != null && attrs.size() > 0) {
            List<SearchMallResult.NavVO> navVOList = attrs.stream().map(attr -> {
                SearchMallResult.NavVO navVO = new SearchMallResult.NavVO();
                String[] split = attr.split("_");
                navVO.setNavValue(split[1]); //保存属性值
                long id = Long.parseLong(split[0]);
                searchMallResult.getAttrIds().add(id); //保存属性Id
                //从ES查询获取属性名
                for (SearchMallResult.AttrVO attrVOe : AttrVOes) {
                    if(attrVOe.getAttrId()==id){
                        navVO.setNavName(attrVOe.getAttrName());   //保存属性名
                    }
                }
                String replace = replaceQueryString(param, attr,"attrs");
                navVO.setLink("http://search.mall.com/list.html?"+replace );  //保存跳转网址
                return navVO;
            }).collect(Collectors.toList());
            searchMallResult.setNavs(navVOList);   //保存ES属性查询
        }
        //品牌的面包屑处理
        if (param.getBrandId() != null && param.getBrandId().size() >0) {
            List<SearchMallResult.NavVO> navs = searchMallResult.getNavs();
            SearchMallResult.NavVO navVo = new SearchMallResult.NavVO();
            navVo.setNavName("品牌");  //保存品牌title
            //远程查询品牌信息
            //TODO 后续更改为专属商品分类的查询
            R brandListByIds = productFeignService.getBrandListByIds(param.getBrandId());
            if (brandListByIds.getCode() == 0) {
                List<BrandVO> brand = brandListByIds.getData("brandList", new TypeReference<List<BrandVO>>() {});
                StringBuilder buffer = new StringBuilder();
                String replace = "";
                //多个品牌有关网址参数的拼接
                for (BrandVO brandVo : brand) {
                    buffer.append(brandVo.getName() + " ");
                    replace = replaceQueryString(param, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.mall.com/list.html?" + replace);
            }
            navs.add(navVo);
            searchMallResult.setNavs(navs);
        }
        return  searchMallResult;
    }

    /**
     * 编码处理品牌的搜索参数
     */
    private String replaceQueryString(SearchMallParam param, String value, String key) {
        //当面包屑所有条件清空时，网址参数同时清空
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+", "%20"); //浏览器会将“+”替换为%20，需校正为浏览器的格式
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//      当面包屑所有条件清空时，网址参数同时清空
        return param.getWebQueryString().replace("&"+key+"=" + encode, "");
    }

}
