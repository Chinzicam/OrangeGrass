<template>
  <div class="share-file-viewer">
    <div class="share-file-viewer-header">

    </div>
    <div class="share-file-viewer-body">
      <img :src="getImgPath(this.fileinfo)">

    </div>
  </div>
</template>

<script>
import Fileservice from "../../../utils/FileRequest";
import {getThumbnailPathGlobal} from "../../../main";
export default {
  name: "shareFileViewer",
  data() {
    return {
      fileinfo: {
        filename:'',
        fileid:''
      }
    }
  },
  created() {
    this.getFileInfo()
  },
  methods: {
    getFileInfo() {
      Fileservice({
        url: '/api/share/fileinfo',
        params: {shareId:this.$route.query.shareId},
        method: 'get'
      }).then(res=>{
        if (res.code === 1){
          this.fileinfo = res.data
          console.log(this.fileinfo)
        }
      })
    },
    getImgPath(val){
      let extension = val.filename.substring(val.filename.lastIndexOf('.')+1,val.filename.length).toLowerCase()
      if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        return this.getThumbnailPath(val.fileid)
      } else {
        return this.setFileImg(val)
      }
    },
    setFileImg(row) {
      let extension = row.filename.substring(row.filename.lastIndexOf('.')+1,row.filename.length).toLowerCase().trim()
      if (row.type === 'FOLDER') {
        //  文件夹
        return this.FILE_IMG_MAP.dir
      } else if (!this.FILE_IMG_TYPE_LIST.includes(extension)) {
        //  无法识别文件类型的文件
        return this.FILE_IMG_MAP.unknown
      } else if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        // 图片类型，直接显示缩略图
        return this.getThumbnailPath(row.fileid)
        // return this.FILE_IMG_MAP[extension]
      } else if (["avi", "mp4", "mpg", "mov", "swf", "flv"].includes(extension)){
        return this.getThumbnailPath(row.fileid)
      } else {
        //  可以识别文件类型的文件
        return this.FILE_IMG_MAP[extension]
      }
    },
    getThumbnailPath(id){
      return getThumbnailPathGlobal(id)
    },
  }
}
</script>

<style scoped>

</style>
