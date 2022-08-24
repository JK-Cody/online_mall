<template> <!-- 商品分类的共用获取页面 -->
<div>
  <el-input placeholder="输入关键字进行过滤" v-model="filterText"></el-input>
  <!-- 输出categoryEntityList的树状数据 -->
    <el-tree 
     :data="categoryEntityList" 
     :props="defaultProps" 
     node-key="catId"
     ref="menuTree" 
     @node-click="getCategoryEntityListChildren"   
     :filter-node-method="filterNode"
     :highlight-current = "true"
     >
    </el-tree>
</div>
</template> 

<script>
export default {
    name: 'commom-category',
    components: {},
    props: {},
    data() {
      return {
        filterText: "",
        categoryEntityList: [],   //商品列表
        expandedKey: [],  //用于展开删除后返回的列表关闭状态      
        defaultProps: {
          children: 'children',     //商品分类对象的子项目 (属性children)
          label: 'name',   //商品分类对象的分类名称  (属性name)
        },
      };
    },
    mounted() {}, 
    computed: {},
    //监控data中的数据变化
    watch: {
      filterText(val) {
        this.$refs.menuTree.filter(val);
    }
   },
    methods: {
 //获取各级列表
      getCategoryList() {
        //发送展示列表请求
        this.$http({   // $http是自定义请求工具httpRuquest
          url: this.$http.adornUrl("/product/category/list/tree"),
          method: "get"
        }).then(({data}) => { 
          //获取后台数据的商品列表
          this.categoryEntityList = data.categoryEntityList; 
          }) 
          .catch(() => {});
        },
       //发送树状数据
       getCategoryEntityListChildren(data,node,component){          
            this.$emit("listChildren",data,node,component)     //给接收页面发送一个事件，携带上数据   
       },  
      //树节点过滤
      filterNode(value, data) {
         if (!value) return true;
            return data.name.indexOf(value) !== -1;
       },    
      }, 
        created(){
          this.getCategoryList();
        }      
};
</script>

<style scoped>

</style>