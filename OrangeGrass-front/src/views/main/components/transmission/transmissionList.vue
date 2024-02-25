<template>
  <div class="transmission-list-wrapper">
    <div class="transmission-list-aside">
      <p class="transmission-list-aside-title">传输列表</p>
      <button class="transmission-list-aside-btn" @click="changeList(1)">文件上传</button>
      <button class="transmission-list-aside-btn" @click="changeList(2)">文件下载</button>
    </div>
    <div class="transmission-list-container">
      <div class="transmission-list-body-header">
        <p v-if="showUpload">上传列表</p>
        <p v-if="showDownload">下载列表</p>
        <p @click="closeThis">关闭</p>
      </div>
      <div class="transmission-list-body-body">
        <div class="transmission-list-body-body-upload" v-if="showUpload">
          <upload-list :upload-file-list="uploadFileList"
                       :user-id="userId"
                       :current-folder="currentFolder"
                        @setFile="setFileTrigger"></upload-list>
        </div>
        <div class="transmission-list-body-body-download" v-if="showDownload">
          <download-list :user-id="userId"
                         :current-folder="currentFolder"
                         :is-vip="isVip"
                         :downloading-file-list="downloadFileList"></download-list>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FileUpload from "../../thirdPartycComponents/FileUpload";
import FileDownload from "../../thirdPartycComponents/FileDownload";
import UploadList from "./uploadList";
import DownloadList from "./downloadList";
export default {
  name: "transmissionList",
  components: {DownloadList, UploadList, FileDownload, FileUpload},
  props: {
    downloadFileList: Array,
    uploadFileList: Array,
    currentFolder: Object,
    userId: String,
    isVip: Boolean
  },
  data() {
    return {
      showUpload: true,
      showDownload: false
    }
  },
  methods: {
    setFileTrigger() {
      this.$emit('setFile','ALL')
    },
    closeThis () {
      this.$emit('closeTransmissionList')
    },
    changeList(val) {
      if (val === 1) {
        this.showUpload = true,
        this.showDownload = false
      }
      if (val === 2) {
        this.showUpload = false,
        this.showDownload = true
      }
    }
  }
}
</script>

<style scoped>
.transmission-list-wrapper {
  display: flex;
}
.transmission-list-aside {
  display: inline-block;
  position: absolute;
  line-height: 1;
  width: 144px;
  padding: 12px;
}
.transmission-list-aside-btn {
  border-radius: 10px;
  padding-left: 12px;
  margin-top: 4px;
  display: block;
  width: 120px;
  height: 32px;
  line-height: 32px;
}
.transmission-list-container {
  display: inline-block;
  position: absolute;
  right: 0;
  width: 560px;
  left: 145px;
}
.transmission-list-body-header {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  font-weight: 600;
  border-bottom-width: 1px;
  border-color: #f4f4f4;
  border-bottom-style: solid;
  height: 40px;
  line-height: 24px;
  padding: 8px 16px;
}
.transmission-list-body-body {
  margin: 0;
  -webkit-overflow-scrolling: touch;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}
</style>
