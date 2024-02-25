<template>
  <div class="recycle-bin-main">
    <div class="recycle-bin-main-empty" v-if="isEmpty">
      <div class="recycle-bin-main-empty-content">
        <p class="recycle-bin-main-empty-content-icon">
          <svg t="1679036882514" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2855" width="200" height="200"><path d="M238.08 846.848c0 54.272 48.64 98.816 108.032 98.816h331.776c59.904 0 108.032-44.032 108.032-98.816l50.688-516.096H186.88l51.2 516.096z m463.36-672.256h-71.68v-70.656c0-13.824-11.264-25.088-25.088-25.088H419.328c-13.824 0-25.088 11.264-25.088 25.088v70.656H133.12v98.816h757.76v-98.816H701.44zM465.92 140.8h92.16v33.28h-92.16V140.8z" p-id="2856"></path></svg>
        </p>
        <p class="recycle-bin-main-empty-content-text">
          您的回收站为空噢～
        </p>
      </div>
    </div>
    <div class="recycle-bin-main-not-empty" v-if="!isEmpty">
      <div class="recycle-bin-main-header">

      </div>
      <div class="recycle-bin-main-content">
        <div class="recycle-bin-main-item" style="cursor:pointer;" v-for="item in fileList">
          <el-popover placement="bottom"
                      trigger="click">
            <div slot="reference" style="text-align: center">
              <div style="display:block;height: 80px">
                <div v-if="!isImg(item)">
                  <img style="max-height: 80px;max-width: 80px" :src="setFileImg(item)"/>
                </div>
                <div v-if="isImg(item)">
                  <img style="max-height: 80px;max-width: 80px" :src="setFileImg(item)">
                </div>
              </div>
              <div style="font-size: 14px;font-weight: 400">
                <p :title="item.fileName">{{limitLength(item.fileName)}}</p>
              </div>
            </div>
            <div
              style="text-align: center;
                  display: flex;
                  flex-direction: column;
                  align-items: center">
              <div v-if="isImg(item)">
                <img style="max-height: 400px;max-width: 400px" :src="downloadImg(item)">
              </div>
              <div class="option-item" @click="handleRecover(item)">
                <span>恢复</span>
              </div>
              <div class="option-item" @click="handleDelete(item)">
                <span>彻底删除</span>
              </div>
            </div>
          </el-popover>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import Fileservice from "../../../utils/FileRequest";
import {getDownloadPathGlobal, getRecycleImg, getThumbnailPathGlobal} from "../../../main";

export default {
  name: "recycleBin",
  data() {
    return {
      isEmpty: false,
      fileList: []
    }
  },
  created() {
    this.getDeletedFiles()
  },
  methods: {
    downloadImg(item) {
      return getRecycleImg(item.id)
    },
    handleDelete(val) {
      Fileservice({
        url:'api/recycle',
        method: 'delete',
        params: {
          fileId: val.id
        }
      }).then(res=>{
        if (res.code === 1) {
          this.getDeletedFiles()
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleRecover(val) {
      Fileservice({
        url:'api/recycle',
        method: 'put',
        params: {
          fileId: val.id
        }
      }).then(res=>{
        if (res.code === 1) {
          this.getDeletedFiles()
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getDeletedFiles() {
      Fileservice({
        url: 'api/recycle',
        method: 'get',
        params: {
           userId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1) {
          this.fileList = res.data
          if (this.fileList === null || this.fileList.length === 0) {
            this.isEmpty = true
          }
        }
      })
    },
    isImg(val){
      let extension = val.fileName.substring(val.fileName.lastIndexOf('.')+1,val.fileName.length).toLowerCase()
      if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        return true
      } else {
        return false
      }
    },
    getImgPath(val){
      return getDownloadPathGlobal(val.fileId)
    },
    //  根据文件扩展名设置文件图片
    setFileImg(row) {
      let extension = row.fileName.substring(row.fileName.lastIndexOf('.')+1,row.fileName.length).toLowerCase().trim()
      if (!this.FILE_IMG_TYPE_LIST.includes(extension)) {
        //  无法识别文件类型的文件
        return this.FILE_IMG_MAP.unknown
      } else if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        // 图片类型，直接显示缩略图
        return this.getThumbnailPath(row.fileId)
        // return this.FILE_IMG_MAP[extension]
      } else if (["avi", "mp4", "mpg", "mov", "swf", "flv"].includes(extension)){
        return this.getThumbnailPath(row.fileId)
      } else {
        //  可以识别文件类型的文件
        return this.FILE_IMG_MAP[extension]
      }
    },
    getThumbnailPath(id){
      return getThumbnailPathGlobal(id)
    },
    limitLength(val) {
      if (val.length >= 7) {
        return val.substring(0,7)+'...'
      } else {
        return val
      }
    }
  }
}
</script>

<style scoped>
.recycle-bin-main {
  width: 100%;
  height: 100%;
  position: relative;
  padding: 0 20px;
  min-width: 750px;
  height: 890px;
  overflow-y: scroll;
}
.recycle-bin-main-empty {
  position: absolute;
  top: 50%;
  left: 50%;
  -webkit-transform: translate(-50%,-50%);
  transform: translate(-50%,-50%);
  text-align: center;
}
.recycle-bin-main-content {
  display: grid;
  grid-template-rows: repeat(10,1fr);
  grid-template-columns: repeat(10,1fr);
  width: 100%;
  height: 100%;
}
.recycle-bin-main-item {
  margin: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.option-item {
  height: 40px;
  border-radius: 10px;
  width: 170px;
  margin: 3px;
  font-weight: 400;
  color: #636d7e;
  background-color: #fff;
  cursor: pointer;
  white-space: nowrap;
  display: flex;
  text-align: center;
  align-items: center;
  justify-content: center;
  flex-shrink: 1;
}
.option-item:focus
{
  color: #06a7ff;
  background-color: #eef9fe;
}
.option-item:hover
{
  background-color: #fafafc;
}
</style>
