<template>
  <div class="folderCreator-wrapper">
    <el-button style="color: #f0faff" @click="visibility=true" size="small" round>
      <span style="color: #00a0e9;font-size: 14px">新建文件夹</span>
    </el-button>
    <div class="folderCreator-dialog-wrapper">
      <el-dialog :visible.sync="visibility">
        <div class="folderCreator-dialog-info-wrapper">
          <div class="folderCreator-dialog-info-userid-wrapper">
            <span>当前用户id：【{{userId}}】</span>
          </div>
          <div class="folderCreator-dialog-info-foldername-wrapper">
            <span>当前文件夹名：【{{currentFolder.name}}】</span>
          </div>
        </div>
        <div class="folderCreator-dialog-main-wrapper">
          <div class="folderCreator-dialog-main-foldername-wrapper">
            <span>请输入文件夹名</span>

          </div>
          <div class="folderCreator-dialog-main-foldername-foldername-input-wrapper">
            <el-input v-model="folderEntity.FolderName" width="50px" placeholder="foldername"></el-input>
          </div>
          <div>
            <el-button @click="handleClick"
                       style="margin-left: 20px"
                       size="small"
                       type="primary"
                       round>
              提交</el-button>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../utils/FileRequest";
export default {
  name: "FolderCreator",
  props : {
    currentFolder:Object,
    userId:String
  },
  data(){
    return {
      visibility: false,
      folderEntity: {
        FolderName: ''
      }
    }
  },
  methods: {
    handleClick(){
      console.log(this.folderEntity.FolderName)
      Fileservice.post("api/folder/" + this.userId + '/' + this.currentFolder.id,this.folderEntity).then(res=>{
        console.log(res)
        if (res.code === 1){
          this.$emit('setFiles','ALL')
          this.$message.success(res.msg)
          this.visibility = false
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<style scoped>
.folderCreator-dialog-main-wrapper {
  display: flex;
}
.folderCreator-dialog-main-foldername-wrapper {
  margin-right: 20px;
}
</style>
