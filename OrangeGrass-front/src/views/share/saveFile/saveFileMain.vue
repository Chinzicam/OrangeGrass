<template>
  <div class="save-main">
    <div class="layoutHeader">
      <global-header></global-header>
    </div>
    <div class="layoutBody">
      <savel_file_body
      @dialogTrigger="saveFileDialogTrigger"></savel_file_body>
      <el-dialog :visible.sync="saveFileDialogVisibility">
        选中【<span>{{this.targetFolder.name}}</span>】文件夹
        <el-tree :data="folderTree"
                 :expand-on-click-node="false"
                 :props="defaultProps"
                 default-expand-all
                 @node-click="handleNodeClick"
        ></el-tree>
        <el-button type="primary" round @click="handleSave()">添加到该文件夹</el-button>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../utils/FileRequest";
import Save_file_header from "./components/save_file_header";
import Savel_file_body from "./components/savel_file_body";
import GlobalHeader from "../../header";
export default {
  name: "saveFileMain",
  components: {GlobalHeader, Savel_file_body, Save_file_header},
  data() {
    return {
      defaultProps: {
        children: 'subs',
        label: 'name'
      },
      targetFolder:{},
      folderTree: [],
      username: '',
      saveFileDialogVisibility : false,
    }
  },
  mounted() {
    this.checkLogin()
    this.getFolderTree()
  },
  methods: {
    handleSave() {
      let folderId = this.targetFolder.id
      let userId = localStorage.getItem('userId')
      let shareId = this.$route.query.shareId
      Fileservice({
        url: '/api/share/save',
        method: 'post',
        params: {folderId: folderId,userId: userId,shareId:shareId}
      }).then(res=>{
        if (res.code === 1){
          this.$message.success(res.msg)
          this.$router.push("/main")
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getFolderTree() {
      Fileservice({
        url: '/api/disk/folder/' + localStorage.getItem('userId'),
        method: 'get'
      }).then(res=>{
        if (res.code === 1){
          this.folderTree = new Array(res.data)
          this.targetFolder = res.data
          console.log(this.targetFolder)
        }
      })
    },
    saveFileDialogTrigger() {
      this.saveFileDialogVisibility = !this.saveFileDialogVisibility
    },
    checkLogin() {
      if (localStorage.getItem('userId') === null ||
        localStorage.getItem('username') === null ||
        localStorage.getItem('userId') === ''
      ) {
        this.$router.push({path:'/login',query:{redirect: '/save?shareId=' + this.$route.query.shareId}})
      } else {
        this.username = localStorage.getItem('username')
      }
    },
    async handleNodeClick(data) {
      this.targetFolder = data
      console.log(this.targetFolder)
    },
  }
}
</script>

<style scoped>
.save-main {
  height: 900px;
}
.layoutBody {
  text-align: left;
  top: 73px;
  margin: 0 30px;
  border-bottom: 0;
  position: relative;
  min-width: 1180px;
  border-bottom: 0;
  box-sizing: border-box;
  height: 90%;
  zoom: 1;
}
</style>
