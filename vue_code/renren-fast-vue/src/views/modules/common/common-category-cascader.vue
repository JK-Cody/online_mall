<template>
  <div>
    <el-cascader
      :key="refresh"
      filterable
      clearable
      placeholder="试试搜索：手机"
      v-model="getPath"
      :options="categorys"
      :props="setting"
    ></el-cascader>
  </div>
</template>

<script>

export default {
  name: 'commom-category-cascader',
  components: {},   
  props: {
    catalogPath: {  //数组保存
      type: Array,  
      default(){
        return [];
      }
    }
  },
  data() {
    return {
      setting: {
        value: "catId",
        label: "name",
        children: "children"
      },
      categorys: [],
      getPath: this.catalogPath,
      refresh:"",  //用于重置key
    };
  },
  watch:{
    catalogPath(v){
      this.getPath = this.catalogPath;
    },
    getPath(v){
      //方式一：给接收页面发送一个事件，携带上数据
      // this.$emit("update:catalogPath",v); 
      //方式二：pubsub-js进行传值
      this.PubSub.publish("catPath",v);
    }
  },
  methods: {
    //显示商品分类列表
    getCategoryList() {
      this.$http({
        url: this.$http.adornUrl("/product/category/list/tree"),
        method: "get"
      }).then(({ data }) => {
        this.categorys = data.categoryEntityList;
      });
    },
    //清空选项
    refreshCascader() {
      this.getPath = "";
      ++this.refresh; //ker改变后，选项将重置
    }
  },
  
  //生命周期 - 创建完成（可以访问当前this实例）
  created() {
    this.getCategoryList();
  },
  //生命周期 - 挂载完成（可以访问DOM元素）
  mounted() {},
  destroyed() {}, //生命周期 - 销毁完成
  activated() {}, //如果页面有keep-alive缓存功能，这个函数会触发
};
</script>
<style scoped>
</style>