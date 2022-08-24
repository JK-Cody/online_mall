<template>
<div>
     <!-- 拖拽功能 -->
    <el-switch
      v-model="draggable"
      active-text="开启拖拽"
      inactive-text="关闭拖拽"
    >
    </el-switch>
    
    <el-button @click="batchUpdate" v-if="draggable">批量保存</el-button>

    <el-button type="danger" @click="batchDelete">批量删除</el-button>

<!-- 输出categoryEntityList的树状数据 -->
    <el-tree :data="categoryEntityList" :props="defaultProps" 
     :expand-on-click-node="false"    
     show-checkbox 
     node-key="catId"
     :default-expanded-keys="expandedKey" 
     :draggable="draggable"
     @node-drop="handleDrop"
     :allow-drop="allowDrop"
     ref="menuTree"
    >
    <!-- 数据的分级显示 -->
    <span class="custom-tree-node" slot-scope="{ node, data }">
      <span>{{ node.label }}</span>
      <span>
         <!-- 当为3级商品时，显示添加按钮 -->
       <el-button  
          v-if="node.level <= 2"
          type="text"
          size="mini"
          @click="() => append(data)"
        >Append
        </el-button>
        <!-- 当为1级商品时，显示修改按钮 -->
        <el-button
        type="text"
        size="mini"
        @click="() => edit(data)"
       >Edit
       </el-button>
        <!-- 当为1级商品时，显示删除按钮 -->
        <el-button
          v-if="node.childNodes.length==0"
          type="text"
          size="mini"
          @click="() => remove(node, data)"
        >Delete
        </el-button>
      </span>
    </span>
    </el-tree>

<!-- 商品的查询框 -->
      <!-- <el-input
        placeholder="请输入商品名称"
        v-model="input"
        clearable>
      </el-input>
      <el-button type="primary" @click="getCategory">确定</el-button> --> 

<!-- 添加商品的输入框 -->
    <el-dialog :title="callMethodTitle" :visible.sync="dialogVisible" width="30%" :close-on-click-modal="false">
      <el-form :model="category">
        <el-form-item label="商品名称"> 
          <!-- 只要求输入名称 -->
           <el-input v-model="category.name" autocomplete="off"></el-input>
        </el-form-item> 
        <el-form-item label="图标">
          <el-input v-model="category.icon" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="category.productUnit" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <!-- 输入框的确认 -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="callMethod">确 定</el-button>
      </span>
    </el-dialog>
 </div>

</template> 

<script>
export default {
    name: 'category',
    components: {},
    directives: {},
    data() {
      return {
        callMethodTitle: "",  //输入框的当前方法标识
        callMethodType: "",    //处于添加还是修改的判断 
        category: {name: "", parentCid: 0, catLevel: 0, showStatus: 1, sort: 0, catId: null,icon: "", productUnit: ""}, //新增/修改 商品对象的默认属性
        categoryEntityList: [],   //商品列表
        // categoryInfo: {},   //单个商品
        expandedKey: [],  //用于展开删除后返回的列表关闭状态      
        dialogVisible: false,  //输入框开关
        defaultProps: {
          children: 'children',     //商品分类对象的子项目 (属性children)
          label: 'name',   //商品分类对象的分类名称  (属性name)
        },
        draggable: false, //拖拽开关
        maxLevel: 0,   //拖拽商品的最大层级
        updateNodes: [], //被拖拽的节点的树状数据
        dropParentCid: [],  //用于展开数据
      };
    },
    mounted() {},
    //计算属性 类似于data概念
    computed: {},

    //监控data中的数据变化
    watch: {},

    created(){
      this.getList();
    }, 

    methods: {
 //获取三级分类列表
      getList() {
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

//获取单个商品
        // getCategory(data) {
        //   //发送展示列表请求
        //   this.$http({   // $http是自定义请求工具httpRuquest
        //     url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
        //     method: "get"
        //   }).then(({data}) => { 
        //     //获取后台数据的商品列表
        //     console.log("商品:",data.categoryInfo);
        //     this.categoryInfo =null;
        //     this.categoryInfo = data.categoryInfo;
        //     }) 
        //     .catch(() => {});
        //   },

 //商品拖拽
         allowDrop(draggingNode, dropNode, type) {  //被拖拽节点draggingNode，被换位的节点dropNode
          //  console.log("拖拽节点:", draggingNode, dropNode, type);  
            let level = 0;
            this.countNodeLevel(draggingNode);  //赋值层级给maxLevel,为 0 或 3 
            if(this.maxLevel==0){
                level = 1;
            }else{
                level = Math.abs(this.maxLevel - draggingNode.level) +1;    //1级（3）  2级（2） 3级（1）
            }        
            // 2级目录可以切换平级或上级——3级目录可以切换上级；
                //不能切换到最下级再以下
            if (type =="inner") {   //type的类型有prev、inner、next               
                return ( level + dropNode.level ) <= 3;      
            //上级目录不能降级,不允许拖拽到3级目录   
            } else {
                return (level + dropNode.parent.level) <= 3;
            }         
          }, 
//计算被拖拽节点的最大层级 (适用不规律层级)
           countNodeLevel(node) {
          //如果是最下级则maxLevel为0，其他为最大层级
          if (node.childNodes != null && node.childNodes.length > 0) {
            //遍历每个子节点
            for (let i = 0; i < node.childNodes.length; i++) { 
              if (node.childNodes[i].level > this.maxLevel) {
                this.maxLevel = node.childNodes[i].level;      
              }
              this.countNodeLevel(node.childNodes[i]);  
            }
           }
         },

//对每次拖拽进行节点数据收集
          handleDrop(draggingNode, dropNode, dropType, ev) { //被拖拽节点draggingNode，被换位的节点dropNode
              console.log("handleDrop: ", draggingNode, dropNode, dropType);
              let parentCid=0;
              let siblings = null;  
              let dropCatLevel = 0;
              let draggingNodeLevel = draggingNode.level; //原层级
              
              if(dropType =="inner"){   
                  parentCid=dropNode.data.catId;   //重新获取父id
                  siblings = dropNode.childNodes;   //draggingNode已纳入dropNode
                  dropCatLevel=siblings[0].level;   //重新获取层级
              }else{    
                  parentCid = dropNode.data.parentCid;  //重新获取父id
                  siblings = dropNode.parent.childNodes;  //draggingNode已纳入dropNode
                  dropCatLevel=dropNode.level;      //重新获取层级
              }   
              //用于展开数据
              this.dropParentCid.push(parentCid);
               //重新收集树状数据
              for (let i = 0; i < siblings.length; i++) { 
                  //当遍历到被拖拽节点时
                   if (siblings[i].data.catId == draggingNode.data.catId) {   
                      //父id改变时
                      if(draggingNode.data.parentCid != parentCid){   
                          //先更新拖拽节点的子节点层级
                          this.updateChildNodeLevlel(siblings[i]);
                          //后更更新拖拽节点的数据
                          this.updateNodes.push({   
                              catId: siblings[i].data.catId,  
                              sort: i,  //排序
                              parentCid: parentCid,
                              catLevel: dropCatLevel,                     
                          });
                      }
                  //当遍历到原子节点
                   }else{  
                        this.updateNodes.push({ 
                          catId: siblings[i].data.catId,
                          sort: i,
                      });                    
                   }
              }       
              console.log("updateNodes:", this.updateNodes);
              
          },       
//修改被拖拽节点的子节点层级
        updateChildNodeLevlel(node) {
            for (let i = 0; i < node.childNodes.length; i++) {
                var cNode = node.childNodes[i].data;
                this.updateNodes.push({
                    catId: cNode.catId,
                    catLevel: node.childNodes[i].level,
                    sort: i,
                });
                this.updateChildNodeLevlel(node.childNodes[i]);
            }
        },

// 处于添加还是修改的判断 
          callMethod(){
              if(this.callMethodType == "add"){
                    this.addCategory();
              }
                if(this.callMethodType == "edit"){
                    this.editCategory();
              }
           },

 // 添加方法
        append(data) {
          this.category.parentCid=data.catId;
          this.category.catLevel=data.catLevel*1+1;
          this.callMethodType= "add";
          this.callMethodTitle="添加商品分类";
          //清除原数据
          this.category.catId = null;
          this.category.name=""; 
          this.category.icon="";
          this.category.productUnit="";
          this.category.sort = 0;
          this.category.showStatus = 1;
          //开启输入框
          this.dialogVisible= true;
        },
        //添加一个商品
        addCategory(){
          console.log("提交的分类数据", this.category);
          this.$http({ 
          url: this.$http.adornUrl("/product/category/save"),
          //发送请求携带输入框的数据
          data: this.$http.adornData(this.category, false),     //第一参数为数据集合，第二参数为不开启数据，第三参数默认json传送
          method: "post",   //发出请求
        }).then(({data}) => { 
          this.$message({
            type: "success",
            message: "商品保存成功!",
          });
          this.dialogVisible = false;
          this.getList();
          // 获取上级的商品列表id，用于展开列表
          this.expandedKey = [this.category.parentCid];
          }) 
          .catch(() => {}); 
        },

//修改方法
        edit(data) {
            this.callMethodType="edit";
            this.callMethodTitle="修改商品分类";
            //修改一个商品(自定)
            // this.getCategory(data);
            // this.category.catId=data.catId;
            // this.category.name=data.name;
            // this.category.icon=data.icon;
            // this.category.productUnit=data.productUnit;           
            this.$http({
            url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
            method: "get",
          }).then(({ data }) => {
            // 由於category對象在前端有默認值，需要返回後端原值
            // console.log("要回显得数据:", data);
            this.category.catId = data.categoryInfo.catId;
            this.category.name = data.categoryInfo.name;      
            this.category.icon = data.categoryInfo.icon;
            this.category.productUnit = data.categoryInfo.productUnit;
            this.category.parentCid = data.categoryInfo.parentCid;
            this.dialogVisible = true;
          });
        },
        //修改三级分类商品
        editCategory(){
            // console.log("修改的分类数据", this.category);
            //筛选需要修改的字段
            var { catId, name, icon, productUnit } = this.category;
            this.$http({ 
            url: this.$http.adornUrl("/product/category/update"),
            //发送请求携带输入框的数据
            method: "post",   //发出请求
            data: this.$http.adornData({ catId, name, icon, productUnit }, false)
            }).then(({ data }) => { 
            this.$message({
            type: "success",
            message: "商品修改成功!",
            });
            this.dialogVisible = false;
            this.getList();
            // 获取上级的商品列表id，用于展开列表
            this.expandedKey = [this.category.parentCid];
            })
         }, 

// 批量修改
    batchUpdate() {
      this.$http({
        url: this.$http.adornUrl("/product/category/batchUpdate"),
        method: "post",
        data: this.$http.adornData(this.updateNodes, false),
      })
        .then(({ data }) => {
          this.$message({
            type: "success",
            message: "商品批量修改成功!",
          });
          // 刷新出新的菜单
          this.getList();
          // 设置需要默认展开的菜单
          this.expandedKey = this.dropParentCid;
          this.updateNodes = [];
          this.maxLevel = 0;
          this.dropParentCid = [];
        })
        .catch(() => {});
      },

// 删除方法
        remove(node, data) {
          //获取data的唯一值作集合
          var ids = [data.catId];
          //确认弹窗
          this.$confirm(`是否删除【${data.name}】当前商品?`, "提示",
           {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
           }).then(() => {
            this.$http({
            url: this.$http.adornUrl("/product/category/delete"),    
            //发送请求携带输入框的数据      
            data: this.$http.adornData(ids, false),
            method: "post",
           }).then(({data}) => {
              this.$message({
                type: "success",
                message: "商品删除成功!",
              });
              
              this.getList();
              // 获取上级的商品列表id，用于展开列表
              this.expandedKey = [node.parent.data.catId]
             }).catch(() => {});   
          }).catch(() => {     
              this.$message({
              type: "info",
              message: "已取消删除",
             });
           });
        },

  // 批量删除
          batchDelete() {
                let catIds= [];
                //获取被选中的元素
                let checkedNodes = this.$refs.menuTree.getCheckedNodes();
                console.log("被选中的元素", checkedNodes);
                for (let i = 0; i < checkedNodes.length; i++) {
                  catIds.push(checkedNodes[i].catId);
                }
                //确认弹窗
                this.$confirm(`是否批量删除【${catIds}】当前列表?`, "提示",
                {
                  confirmButtonText: "确定",
                  cancelButtonText: "取消",
                  type: "warning",
                }).then(() => {
                  this.$http({
                  url: this.$http.adornUrl("/product/category/delete"),    
                  //发送请求携带数据      
                  data: this.$http.adornData(catIds, false),
                  method: "post",
                }).then(({data}) => {
                    this.$message({
                      type: "success",
                      message: "商品批量删除成功!",
                    });
                    
                    this.getList();
                  }).catch(() => {});   
                }).catch(() => {     
                    this.$message({
                    type: "info",
                    message: "已取消删除",
                  });
                });
              },

      },     
};
</script>

<style scoped>

</style>