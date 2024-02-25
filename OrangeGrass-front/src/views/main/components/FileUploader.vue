<template>
  <div class="fileuploader-wrapper">
    <div class="fileUploader">
      <el-button round @click="visibility=true" size="small" type="primary">
        <span style="font-size: 14px">上传</span>
      </el-button>
    </div>
    <div class="dialog-wrapper">
      <el-dialog title="传输列表" :visible.sync="visibility">
        <div class="fileUploaderBody-wrapper">
<!--          <el-upload-->
<!--            action="http://localhost:8989/"-->
<!--            :file-list="uploadFile"-->
<!--            :http-request="modeUpload"-->
<!--          >-->
<!--            <el-button size="small" round type="primary">上传</el-button>-->
<!--          </el-upload>-->
<!--          <el-button size="small" round @click="handleUpload">点击上传文件</el-button>-->
          <FileUpload
          :current-folder="currentFolder"
          :user-id="userId"
          :upload-file-list="uploadFileList"
          @addFile="addFile"
          @updateFile="updateFile"
          @setFile="setFileTrigger"
          />
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import FileUpload from "../thirdPartycComponents/FileUpload";
import Fileservice from "../../../utils/FileRequest";

export default {
  name: "FileUploader",
  components: {
    FileUpload
  },
  props: {
    uploadFileList: Array,
    uploadFile: Object,
    userId: String,
    currentFolder: Object
  },
  data(){
    return {
      visibility: false,
    }
  },
  methods : {
    setFileTrigger() {
      this.$emit('setFiles','ALL',this.currentFolder.id)
    },
    addFile (val) {
      this.$emit('addUploadFile',val)
    },
    updateFile (index,val) {
      this.$emit('updateUploadFile',index,val)
    },
    modeUpload(item){
      this.uploadFile = item.file
      console.log(this.uploadFile)
    },
    handleUpload(){
      let fd = new FormData()
      fd.append('file',this.uploadFile)
      fd.append('userId',this.userId)
      fd.append('folderId',this.currentFolder.id)
      Fileservice.post('/api/file/upload',fd, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(res=>{
        if (res.code === 1){
          this.$emit('setFiles','ALL')
          this.$message.success(res.msg)
          this.visibility = false
        } else {
          this.$message.error(res.msg)
        }
      })
      this.visibility = false
    },
  }
}
</script>

<style scoped>
.fileUploaderBody-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
