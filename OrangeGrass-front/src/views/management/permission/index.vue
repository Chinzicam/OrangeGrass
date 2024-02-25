<template>
  <div>
    <div class="management-permission-header">
      <el-button size="small" round type="primary" @click="handleAddButtonClick()">新增</el-button>
    </div>
    <div>
      <el-dialog :visible.sync="addDialogVisibility">
        <div style="display: flex;justify-content: space-between">
          <div>
            请输入权限名称：<el-input style="width: 225px" v-model="addPermName"></el-input>
          </div>
          <el-button type="success" round size="small" @click="pushItemIntoData()">提交</el-button>
        </div>
      </el-dialog>
    </div>
    <div class="management-permission-main">
      <div class="custom-tree-container">
        <el-tree
          :data="data"
          node-key="id"
          :props="defaultProps"
          default-expand-all
          :expand-on-click-node="false"
          @node-click="handleNodeClick"
          ref="tree"
          style="width: 100%;border-radius: 20px;background-color: #bfcbd9"
        >
     <span class="custom-tree-node" slot-scope="{ node, data }">
       <span>{{ node.label }}</span>
       <span>
         <i @click="() => append(node,data)" class="el-icon-plus"></i><!--增加分组-->
         <!-- 根节点不需要删除和重命名 -->
         <i v-if="data.id !== 0" @click="() => handleNodeDelete(node,data)" class="el-icon-delete"></i><!--删除分组-->
         <i v-if="data.id !== 0" @click="() => rename(node,data)" class="el-icon-edit"></i><!--重命名分组-->
       </span>
     </span>
        </el-tree>
      </div>
      <div class="management-permission-btn">
        <el-button round type="success" size="large" @click="handleUpdate">提交</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../utils/FileRequest";
let id = 10
export default {
  name: "permissionManagement",
  data() {
    return {
      addPermName: '',
      addDialogVisibility: false,
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      data : [
        {
          id: '',
          label: '',
          children: [{
          }]
        }
      ],
    }
  },
  created() {
    this.getPermissionTree()
  },
  methods: {
    pushItemIntoData() {
      this.data.push({
        id:'',
        label: this.addPermName,
        children: []
      })
      Fileservice({
        url: 'api/permission/',
        method: 'post',
        params: {
          name : this.addPermName
        }
      }).then(res=>{
        if (res.code === 1){
          this.$message.success(res.msg)
          this.getPermissionTree()
        } else {
          this.$message.error(res.msg)
        }
      })
      this.handleAddButtonClick()
    },
    handleNodeClick(val) {
      console.log(val)
    },
    handleUpdate() {
      Fileservice({
        url: '/api/permission',
        method: 'post',
        data: this.data
      }).then(res=>{
        if (res.code === 1){
          this.$message.success(res.msg)
        }
      })
    },
    getPermissionTree() {
      Fileservice({
        url: '/api/permission'
      }).then(res=>{
        if (res.code === 1) {
          this.data = res.data
        }
      })
    },
    //点重命名事件
    append(node,data) {
      this.$prompt('节点名字', '增加节点', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        const newChild = {label: value , children: []};
        if (!data.children){
          this.$set(data,'children',[])
        }
        data.children.push(newChild)
        console.log(data)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消修改'
        });
      });
    },
    rename(node,data) {
      this.$prompt('节点名字', '修改节点', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        this.$set(data,'label',value)
        Fileservice({
          url: '/api/permission',
          method: 'put',
          params: {permId: data.id, newName: data.label}
        }).then(res=>{
          if (res.code === 1) {
            this.$message.success(res.msg)
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消修改'
        });
      });
    },
    handleNodeDelete(node,data) {
      Fileservice({
        url: '/api/permission',
        method: 'delete',
        params: {
          permId: data.id
        }
      }).then(res=>{
        if (res.code === 1){
          this.$message.success(res.msg)
          this.getPermissionTree()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAddButtonClick() {
      this.addDialogVisibility = !this.addDialogVisibility
    }
  }
}
</script>

<style scoped>
.management-permission-header {
  margin-bottom: 20px;
}
.management-permission-main {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  height: 100%;
  width: 100%;
}
.custom-tree-container {
  width: 100%;
  display: flex;
  justify-content: center;
}
.management-permission-btn {
  margin-top: 20px;
}
</style>
