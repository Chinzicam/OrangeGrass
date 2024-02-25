<template>
  <div class="disk-header-content-avatar"
       @click="editAvatarDialogVisibility = !editAvatarDialogVisibility">
    <el-avatar :src="getAvatarPath()"></el-avatar>
    <el-dialog append-to-body
               :visible.sync="editAvatarDialogVisibility">
      <div v-viewer>
        <img style="position: relative;left:36%;cursor:pointer;"
             width="140px" height="140px"
             :data-source="getAvatarPath()"
             :src="getAvatarPath()">
      </div>
      <el-divider>上传头像</el-divider>
      <el-upload
        action="#"
        :http-request="upload"
        :show-file-list="false"
      >
        <el-button type="primary">选择上传文件</el-button>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script>
import Fileservice from "../../../../utils/FileRequest";
import {compressImage} from "../../../../utils/compressImage";
import {getAvatarPathGlobal} from "../../../../main";

export default {
  name: "avatar",
  data() {
    return {
      editAvatarDialogVisibility: false,
    }
  },
  methods: {
    //上传文件,替换el-upload的action
    upload(file) {
      console.log(file)
      let config = {
        width: 100, // 压缩后图片的宽
        height: 100, // 压缩后图片的高
        quality: 1 // 压缩后图片的清晰度，取值0-1，值越小，所绘制出的图像越模糊
      }
      compressImage(file.file,config).then(result=>{
        let f = new File([result], file.file.name, {type: file.file.type, lastModified: Date.now()});
        let formData = new FormData
        formData.append('userId',localStorage.getItem('userId'))
        formData.append('avatar',f)
        Fileservice({
          url: 'api/avatar',
          method: 'post',
          data:formData
        }).then(res=>{
          if (res.code === 1) {
            this.$message.success(res.msg)
          } else {
            this.$message.error(res.msg)
          }
        })
      })
    },
    getAvatarPath() {
      return getAvatarPathGlobal(localStorage.getItem('userId'))
    },
  }
}
</script>

<style scoped>

</style>
