<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @closed="dialogClose" 
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item label="组名" prop="attrGroupName">
        <el-input v-model="dataForm.attrGroupName" placeholder="组名"></el-input>
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input v-model="dataForm.sort" placeholder="排序"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="descript">
        <el-input v-model="dataForm.descript" placeholder="描述"></el-input>
      </el-form-item>
      <el-form-item label="组图标" prop="icon">
        <el-input v-model="dataForm.icon" placeholder="组图标"></el-input>
      </el-form-item>
      <el-form-item label="所属分类" prop="catalogId">
        <!-- <el-input v-model="dataForm.catalogId" placeholder="所属分类id"></el-input> @change="handleChange" -->
        <!-- <el-cascader  filterable  placeholder="试试搜索：手机" v-model="catalogPath" :options="categorys"  :props="props"></el-cascader> -->
        <!-- :catalogPath="catalogPath"自定义绑定的属性，可以给子组件传值 -->
        <category-cascader :catalogPath.sync="catalogPath"></category-cascader>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import CategoryCascader from '../common/common-category-cascader';

export default {
  data() {
    return {
      props:{
        value:"catId",
        label:"name",
        children:"children"
      },
      visible: false,
      categorys: [],
      catalogPath: [],  //接收 增加/修改 所属分类的父与子catId
      dataForm: {
        attrGroupId: 0,
        attrGroupName: "",
        sort: "",
        descript: "",
        icon: "",
        catalogId: 0   //接收 增加/修改 所属分类的catId
      },
      dataRule: {
        attrGroupName: [
          { required: true, message: "组名不能为空", trigger: "blur" }
        ],
        sort: [{ required: true, message: "排序不能为空", trigger: "blur" }],
        descript: [
          { required: true, message: "描述不能为空", trigger: "blur" }
        ],
        icon: [{ required: true, message: "组图标不能为空", trigger: "blur" }],
        catalogId: [
          { required: true, message: "所属分类id不能为空", trigger: "blur" }
        ]
      }
    };
  },
  components:{CategoryCascader},
  
  methods: {
    //清空父与子catId
    dialogClose(){
      this.catalogPath = [];
    },
    //获取所属分类列表
    getCategorys(){
      this.$http({
        url: this.$http.adornUrl("/product/category/list/tree"),
        method: "get"
      }).then(({ data }) => {
        this.categorys = data.categoryEntityList;
      });
    },
    //修改 所属分类
    init(id) { 
      //参数已有分组id,或默认0时(新增 / 修改)
      this.dataForm.attrGroupId = id || 0;  
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
        //已有分组id时，获取对应信息(修改)
        if (this.dataForm.attrGroupId) {
          this.$http({
            url: this.$http.adornUrl(
              `/product/attrgroup/info/${this.dataForm.attrGroupId}`
            ),
            method: "get",
            params: this.$http.adornParams()
          }).then(({ data }) => {          
            if (data && data.code === 0) { //有数据且响应码正常
              this.dataForm.attrGroupName = data.attrGroupEntity.attrGroupName;
              this.dataForm.sort = data.attrGroupEntity.sort;
              this.dataForm.descript = data.attrGroupEntity.descript;
              this.dataForm.icon = data.attrGroupEntity.icon;
              //获取自身catId
              this.dataForm.catalogId = data.attrGroupEntity.catalogId;
              //获取所属分类的父与子catId
              this.catalogPath =  data.attrGroupEntity.catalogPath;
            }
          });
        }
      });
    },
    // 提交/更新 属性分组
    dataFormSubmit() {
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(
              `/product/attrgroup/${
                !this.dataForm.attrGroupId ? "save" : "update"
              }`
            ),
            method: "post",
            data: this.$http.adornData({
              attrGroupId: this.dataForm.attrGroupId || undefined,
              attrGroupName: this.dataForm.attrGroupName,
              sort: this.dataForm.sort,
              descript: this.dataForm.descript,
              icon: this.dataForm.icon,
              catalogId: this.catalogPath[this.catalogPath.length-1]   //剔除路径含有的父catId
            })
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");  //给接收页面发送一个事件，携带上数据
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    }
  },
  created(){
    this.getCategorys();
  }
};
</script>
