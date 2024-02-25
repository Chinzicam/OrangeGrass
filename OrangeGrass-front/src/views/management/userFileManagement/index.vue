<template>
  <div>
    <el-table v-loading="loading" :data="userList">
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="createTime" label="注册时间">
          <template slot-scope="scope">
            {{new Date(scope.row.createTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}
          </template>
        </el-table-column>
        <el-table-column prop="userType" label="用户类型">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.userType"
                       :active-value='1'
                       active-text="管理员"
                       :inactive-value='0'
                       inactive-text="普通用户"
                       @change="changeStatus($event, scope.row, scope.$index)" >
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button round type="primary" size="small" @click="handleUpdate(scope.row)">查看用户</el-button>
            <el-button round type="success" size="small" @click="handlePermissionManage(scope.row)">分配权限</el-button>
            <el-button round type="warning" size="small" @click="getFiles(scope.row)">查看文件</el-button>
          </template>
        </el-table-column>
    </el-table>
    <div class="manage-dialog">
      <el-dialog :visible.sync="updateDialogVisibility">
        <el-form :model="updateForm">
          <el-form-item label="用户名：">
            <el-input :readonly="true" v-model="updateForm.username"></el-input>
          </el-form-item>
          <el-form-item label="密码：">
            <el-input v-model="updateForm.password"></el-input>
          </el-form-item>
          <el-form-item label="注册时间：">
            {{new Date(updateForm.createTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}
          </el-form-item>
          <div class="fileList-management-dialog-button">
            <el-button type="primary" round>修改密码</el-button>
            <el-button type="danger" round>删除用户</el-button>
          </div>
        </el-form>
      </el-dialog>
    </div>
    <div class="manage-dialog">
      <el-dialog :visible.sync="FileListDialogVisibility" style="height: 800px">
        <el-table v-loading="fileListLoading" :data="this.fileList">
          <el-table-column label="缩略图">
            <template slot-scope="scope">
              <div v-viewer>
                <img
                  :src="setFileImg(scope.row)"
                  :data-source="getImgPath(scope.row)"
                  style="height: 70px; width: 70px; cursor:pointer">
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="fileName" label="文件名" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="fileSize" label="文件大小">
            <template slot-scope="scope">
              {{getSize(scope.row.fileSize)}}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间">
            <template slot-scope="scope">
              {{new Date(scope.row.createTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}
            </template>
          </el-table-column>
          <el-table-column prop="nsfwScore" label="机器鉴定分数" width="120">
            <template slot-scope="scope">
              <div style="display: flex;justify-content: center;align-items: center">
                <el-progress
                  style="width: 70px"
                  :color="colors"
                  :show-text="false"
                  :percentage="parseInt(scope.row.nsfwScore * 100)"></el-progress>
                <span style="margin-left: 10px">{{parseInt(scope.row.nsfwScore * 100)}}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="isBan" label="是否禁止">
            <template slot-scope="scope">
              {{isBanFormatter(scope.row)}}
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button type="danger" round v-if="scope.row.isBan === 0" @click="setIsBan(scope.row,1)">设置违规</el-button>
              <el-button type="success" round v-if="scope.row.isBan === 1" @click="setIsBan(scope.row,0)">解封</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>
    </div>
    <div class="manage-dialog">
      <el-dialog :visible.sync="permissionManageDialogVisibility">
        <div class="manage-dialog-tree">
          <div class="manage-dialog-total-permission">
            <div class="manage-dialog-total-permission-text">
              <span>全部权限</span>
            </div>
            <el-tree
              :data="permissionTree"
              node-key="id"
              default-expand-all
              show-checkbox
              @check="handleChecked"
              @node-click="handleNodeClick"
              ref="tree"
            ></el-tree>
          </div>
          <div class="manage-dialog-user-permission">
            <div class="manage-dialog-user-permission-text">
              <span>已有权限</span>
            </div>
            <el-tree
              :data="userPermissionTree"
              node-key="id"
              default-expand-all
              @node-click="handleNodeClick"
            ></el-tree>
          </div>
        </div>
        <div class="manage-dialog-btn">
          <el-button round type="warning" @click="handlePermissionUpdate">分配权限</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../utils/FileRequest";
import {getDownloadPathForAdminGlobal, getThumbnailPathForAdminGlobal} from "../../../main";
export default {
  name: "userFileManagement",
  data() {
    return {
      fileListLoading: true,
      loading: true,
      colors: [
        {color: '#13cd66', percentage: 20},
        {color: '#1989fa', percentage: 40},
        {color: '#fdd721', percentage: 60},
        {color: '#fd4949', percentage: 80},
        {color: '#fd4949', percentage: 100}
      ],
      permissionManageDialogVisibility: false,
      updateDialogVisibility: false,
      FileListDialogVisibility: false,
      userList: [],
      fileList: [],
      permissionTree: [],
      allocatePermission: {
        userId: '',
        permissions: [],
      },
      userPermissionTree: [],
      updateForm: {}
    }
  },
  created() {
    this.getFileList()
  },
  methods: {
    changeStatus(e, row, index) {
      //e返回状态，row当前行数据，index下标
      console.log(e, row, index);
      Fileservice({
        url: 'api/user',
        method: 'put',
        data: row
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    setIsBan(row,result) {
      Fileservice({
        url: 'api/nsfw/check',
        method: 'put',
        params: {
          id: row.id,
          result: result
        }
      }).then(res=>{
        if (res.code === 1) {
          row.isBan = result
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getSize(val) {
      if(val === 0) return "0 B"
      var k = 1024;
      var sizes = ['B','KB','MB','GB','PB','TB','EB','ZB','YB'],
        i = Math.floor(Math.log(val) / Math.log(k));
      return (val / Math.pow(k,i)).toPrecision(3) + "" + sizes[i]
    },
    handlePermissionUpdate() {
      console.log(this.allocatePermission)
      Fileservice({
        url: '/api/permission/user',
        method: 'put',
        data: this.allocatePermission
      }).then(res=>{
        if (res.code === 1){
          this.$message.success(res.msg)
          this.getUserPermissionTree(this.allocatePermission.userId)
          this.allocatePermission.permissions = new Array()
        }
      })
    },
    getUserPermissionTree(userId) {
      Fileservice({
        url: '/api/permission/user',
        method: 'get',
        params: {userId : userId}
      }).then(res=>{
        this.userPermissionTree = res.data
      })
    },
    getPermissionTree() {
      Fileservice({
        url: '/api/permission'
      }).then(res=>{
        if (res.code === 1) {
          this.permissionTree = res.data
        }
      })
    },
    handlePermissionManage(val) {
      this.allocatePermission.userId = val.userId
      this.allocatePermission.permissions = new Array()
      this.permissionManageDialogVisibility = true
      this.getPermissionTree()
      this.getUserPermissionTree(val.userId)
    },
    handleUpdate(row) {
      this.updateForm = row
      console.log(this.updateForm)
      this.updateDialogVisibility = true;
    },
    getFiles(row) {
      this.fileList = []
      this.FileListDialogVisibility = true
      this.fileListLoading = true
      console.log(row)
      Fileservice({
        url: '/api/file/' + row.userId,
        method: 'get'
      }).then(res=>{
        if (res.code === 1){
          this.fileList = res.data
          this.fileListLoading = false
        }
      })
    },
    getFileList() {
      this.loading = true
      Fileservice({
        url: '/api/user/admin',
        method: 'get',
      }).then(res=>{
        if (res.code === 1){
          this.userList = res.data
          this.loading = false
        } else {
          this.loading = false
          this.$message.error(res.msg)
        }
      })
    },
    getImgPath(row){
      return getDownloadPathForAdminGlobal(row.id)
    },
    //  根据文件扩展名设置文件图片
    setFileImg(row) {
      let extension = row.fileName.substring(row.fileName.lastIndexOf('.')+1,row.fileName.length).toLowerCase().trim()
      if (!this.FILE_IMG_TYPE_LIST.includes(extension)) {
        //  无法识别文件类型的文件
        return this.FILE_IMG_MAP.unknown
      } else if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        // 图片类型，直接显示缩略图
        return this.getThumbnailPath(row.id)
        // return this.FILE_IMG_MAP[extension]
      } else if (["avi", "mp4", "mpg", "mov", "swf", "flv"].includes(extension)){
        return this.getThumbnailPath(row.id)
      } else {
        //  可以识别文件类型的文件
        return this.FILE_IMG_MAP[extension]
      }
    },
    getThumbnailPath(id){
      return getThumbnailPathForAdminGlobal(id)
    },
    handleNodeClick(val) {
      console.log(val)
    },
    handleChecked(data,checked) {
      this.allocatePermission.permissions = [...this.$refs.tree.getCheckedNodes(),...this.$refs.tree.getHalfCheckedNodes()]
      console.log(this.allocatePermission.permissions)
    },
    isBanFormatter(row) {
      if (row.isBan === 1) {
        return '封禁'
      } else {
        return '未封禁'
      }
    }
  }
}
</script>

<style scoped>
.fileList-management-dialog-button {
  text-align: center;
}
.manage-dialog-tree {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.manage-dialog-btn {
  text-align: center;
}
.manage-dialog-user-permission {
  height: 260px;
  width: 300px;
}
.manage-dialog-total-permission {
  height: 260px;
  width: 300px;
}
</style>
