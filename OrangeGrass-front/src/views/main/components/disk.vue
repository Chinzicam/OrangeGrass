<template>
  <div class="disk-main">
    <div class="disk-header">
      <div class="disk-header-logo" style="cursor:pointer;" @click="()=>{this.$router.push('/main')}">
        <img height="59px" width="59px" src="@/assets/images/disklogo.png">
      </div>
      <div class="disk-header-content">
        <div class="disk-header-content-item" style="display: flex;justify-content: center;align-items: center">
          <friend-apply-record title="好友申请" style="cursor: pointer;margin-right: 20px"></friend-apply-record>
          <div class="disk-header-content-main-avatar" style="cursor: pointer;margin-right: 20px" :title="this.username">
            <avatar></avatar>
          </div>
          <div class="disk-header-content-main-username" style="font-size: 12px;font-weight: 700;max-width: 140px">
            <span>{{this.username}}</span>
          </div>
        </div>
        <div class="disk-header-content-item">
          <div class="disk-header-content-btn">
            <div class="disk-header-log-out-btn">
              <el-button round size="small" @click="handleLogout()">登出</el-button>
            </div>
          </div>
        </div>
        <div class="disk-header-content-item" v-if="isAdmin">
          <div class="disk-header-to-management-btn" style="font-size: 5px;cursor: pointer">
            <p @click="()=>{this.$router.push('/userfile')}">管理页面</p>
          </div>
        </div>
        <div class="disk-header-content-item">
          <div class="disk-header-transmission-btn">
            <div class="disk-header-transmission-list-title">
              <p @click="showTransmissionList=!showTransmissionList" style="cursor: pointer;font-size: 5px" >传输列表</p>
            </div>
            <div class="disk-main-transmission-list" v-show="showTransmissionList">
              <transmission-list :upload-file-list="uploadFileList"
                                 :download-file-list="downloadingFileList"
                                 :current-folder="currentFolder"
                                 :user-id="userId"
                                 :is-vip="isVip"
                                 @setFile="setFiles"
                                 @closeTransmissionList="closeTransmissionList"></transmission-list>
            </div>
          </div>
        </div>
        <div class="disk-header-content-item" v-if="!isVip">
          <div style="
            cursor: pointer;
            width: 100px;
            padding: 4px;
            border-radius: 26px;
            text-align: center;
            vertical-align: middle;
            background: linear-gradient(95.9deg,#ffeec3 3.14%,#ffe5ba 23.99%,#ffd8aa 43.77%,#ffd4a2 62.06%,#ffc896 79.86%,#fcb4ba 97.91%);
            color: #6b2d00;
            border: 0;"
            @click="activateMembership">
              <span style="font-weight: 700;
                    border-radius: 16px;
                    border-width: 0;
                    color: #64360d;
                    font-size: 12px;">开通会员</span>
          </div>
        </div>
        <div class="disk-header-content-item" v-if="isVip">
          <div style="
            cursor: pointer;
            width: 100px;
            padding: 4px;
            border-radius: 26px;
            text-align: center;
            vertical-align: middle;
            background: linear-gradient(95.9deg,#ffeec3 3.14%,#ffe5ba 23.99%,#ffd8aa 43.77%,#ffd4a2 62.06%,#ffc896 79.86%,#fcb4ba 97.91%);
            color: #6b2d00;
            border: 0;"
            @click="renewalMember">
              <span style="font-weight: 700;
                    border-radius: 16px;
                    border-width: 0;
                    color: #64360d;
                    font-size: 12px;">续费会员</span>
          </div>
        </div>
        <div style="width: 100px"></div>
      </div>
    </div>
    <div class="disk-body">
      <div class="disk-sidebar">
        <disk-sidebar
          @selectedFriendIdChange="selectedFriendIdChange"
          :unread-message-exists="unreadMessageExists"
          @setUnreadMessageExists="setUnreadMessageExists"
        @contentChange="contentChange"></disk-sidebar>
      </div>
      <div class="disk-aside-nav" v-if="showMain&&showDisk">
        <FileAsideNav
          :currentFolder="currentFolder"
          @setFiles="setFiles"
        @contentChange="contentChange"></FileAsideNav>
      </div>
      <div class="disk-filelist" v-if="showMain&&showDisk">
        <div class="disk-filelist-header">
          <div class="disk-filelist-header-fileUploader-text">
            <span style="padding-left: 13px">路径</span>
          </div>
          <div class="disk-filelist-header-fileUploader">
            <FileUploader :userId="userId"
                          :currentFolder="currentFolder"
                          :upload-file-list="uploadFileList"
                          @addUploadFile="addItemForUploadList"
                          @updateUploadFile="updateUploadListType"
                          @setFiles="setFiles"></FileUploader>

          </div>
          <div class="disk-filelist-header-folderCreator">
            <FolderCreator :user-id="userId"
                           :current-folder="currentFolder"
                           @setFiles="setFiles"></FolderCreator>
          </div>
        </div>
        <div class="disk-filelist-body">
          <file-list :file-list-loading="this.fileListLoading"
                     :fileList="this.fileList"
                     :pathTree="this.pathTree"
                     :newPathTree="this.newPathTree"
                     :current-folder="this.currentFolder"
                     :user-id="userId"
                     @fileListChange="fileListChange"
                     @addDownloadFile="addDownloadFile"
                     @folderChange="folderChange"
                     @setFiles="setFiles"
                     ref="filelist"
          ></file-list></div>
      </div>
      <div class="disk-body-detail" v-if="showMain&&showDisk">
        <FileDetail :detail="this.details"
                    :current-folder="currentFolder"></FileDetail>
      </div>
      <div class="disk-body-recycle-bin" v-if="showMain&&showRecycleBin">
        <RecycleBin></RecycleBin>
      </div>
      <div class="disk-body-share-record" v-if="showShare">
        <disk-share></disk-share>
      </div>
      <div class="disk-body-friend" v-if="showFriend">
        <friend ref="friend"
                :selected-friend-id="selectedFriendId"
                @selectedFriendIdChange="selectedFriendIdChange"></friend>
      </div>
    </div>
  </div>
</template>

<script>
import FileList from "./FileList";
import FileUploader from "./FileUploader";
import FolderCreator from "./FolderCreator";
import FileDetail from "./FileDetail";
import FileAsideNav from "./FileAsideNav";
import FileUpload from "../thirdPartycComponents/FileUpload";
import TransmissionList from "./transmission/transmissionList";
import DiskSidebar from "./diskSidebar";
import RecycleBin from "./recycleBin";
import DiskShare from "./share";
import FriendApplyRecord from "./friend/apply/friendApplyRecord";
import Friend from "./friend";
import Avatar from "./avatar";
import Fileservice from "../../../utils/FileRequest";
import {getAvatarPathGlobal} from "../../../main";

export default {
  name: "disk",
  props: {
    fileListLoading: Boolean,
    fileList: Array,
    pathTree: Array,
    newPathTree: Array,
    currentFolder: Object,
    userId: String,
    username: String
  },
  components: {
    Avatar,
    Friend,
    FriendApplyRecord,
    DiskShare,
    RecycleBin,
    DiskSidebar, TransmissionList, FileAsideNav, FileDetail, FolderCreator, FileUploader, FileList},
  data(){
    return {
      isVip: false,
      isAdmin: false,
      selectedFriendId: '',
      unreadMessageExists:false,
      showDisk: true,
      showRecycleBin: false,
      showMain: true,
      showShare: false,
      showFriend: false,
      downloadingFileList: [],
      showTransmissionList: false,
      uploadFileList: [{
        type: String,
        name: String,
        size: Number,
        md5: String,
        parsePercentage: 0,
        uploadPercentage: 0,
        uploadSpeed: "0 M/s",
        chunkList: [],
        file: {},
        uploadingStop: false
      }],
      details:[{}],
    }
  },
  created() {
    // this.details = new Array()
    this.downloadingFileList.pop()
    this.uploadFileList.pop()
    this.details = null;
    this.getUserType()
    this.getUserMembership()
  },
  watch: {
    selectedFriendId(val) {
      console.log('检测到变化',val)
    }
  },
  methods : {
    renewalMember() {
      Fileservice({
        url: 'api/user/membership',
        method: 'put',
        params: {
          userId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
          this.isVip = true
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    activateMembership() {
        Fileservice({
          url: 'api/user/membership',
          method: 'put',
          params: {
            userId: localStorage.getItem('userId')
          }
        }).then(res=>{
          if (res.code === 1) {
            this.$message.success(res.msg)
            this.isVip = true
          } else {
            this.$message.error(res.msg)
          }
        })
    },
    getUserMembership() {
      Fileservice({
        url: 'api/user/membership',
        method: 'get',
        params: {
          userId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1) {
          this.isVip = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getUserType() {
      Fileservice({
        url: 'api/user/isadmin',
        method: 'get'
      }).then(res=>{
        if (res.code === 1) {
          this.isAdmin = res.data
        }
      })
    },
    fileListLoadingChange(val) {
      this.$emit('setFileListLoading',val)
    },
    contentChange(val) {
      //显示文件
      if (val === 1) {
        this.showMain = true
        this.showDisk = true
        this.showRecycleBin = false
        this.showShare = false
        this.showFriend = false
      }
      //显示回收站
      if (val === 2) {
        this.showMain = true
        this.showDisk = false
        this.showRecycleBin = true
        this.showShare = false
        this.showFriend = false
      }
      //显示分享
      if (val === 3) {
        this.showMain = false
        this.showDisk = false
        this.showRecycleBin = false
        this.showShare = true
        this.showFriend = false
      }
      //显示好友
      if (val === 4) {
        this.showMain = false
        this.showDisk = false
        this.showRecycleBin = false
        this.showShare = false
        this.showFriend = true
      }
    },
    getAvatarPath() {
      return getAvatarPathGlobal(localStorage.getItem('userId'))
    },
    handleLogout() {
      this.$emit('handleLogout')
    },
    addDownloadFile (val) {
      this.downloadingFileList.push(val)
    },
    closeTransmissionList () {
      console.log('关闭')
      this.showTransmissionList = false
    },
    updateUploadListType (index,val) {
      this.uploadFileList[index].type = val
    },
    addItemForUploadList (val) {
      console.log("addItemForUploadList")
      console.log(val)
      this.uploadFileList.push(val)
      console.log(this.uploadFileList)
    },
    folderChange(val) {
      this.$emit('setCurrentFolder',val)
      // this.currentFolder = val
    },
    setDetail(val) {
      if (val.length >= 1){
        this.details  = val
      } else {
        this.details = null
      }
    },
    setFiles(type,folder) {
      console.log(type)
      if (type === 'MUSIC') {
        this.$emit('getMusics',folder)
      }
      if (type === 'VIDEO') {
        this.$emit('getVideos',folder)
      }
      if (type === 'DOCUMENT') {
        this.$emit('getDocuments',folder)
      }
      if (type === 'EXE') {
        this.$emit('getExecute',folder)
      }
      if (type === 'ALL') {
        this.$emit('getFiles',folder)
      }
      if (type === 'PICTURE') {
        this.$emit('getPics',folder)
      }
    },
    fileListChange(val) {
      this.$emit('setFileList',val)
    },
    setUnreadMessageExists(val) {
      this.unreadMessageExists = val
    },
    getFriendList() {
      this.$refs.friend.getFriendList()
    },
    selectedFriendIdChange(val) {
      this.selectedFriendId = val
    }
  }
}
</script>

<style>
.disk-main {
  position: relative;
  height : 100%;
  width : 100%;
}
.disk-header {
  display: flex;
  justify-content: space-between;
  z-index: 2;
  position: relative;
  align-items: center;
  padding-left: 24px;
  /*margin-right: 24px;*/
  padding-right: 24px;
  box-shadow: 0 3px 10px 0 rgb(0 0 0 / 6%);
  /*border-radius: 20px;*/
  height: 20%;
  text-align: center;
  /*width: 100%;*/
  /*background-color: #00a0e9;*/
}
.disk-header-logo {
  height: inherit;
  margin-left: 20px;
}
.disk-header-content {
  display: flex;
  justify-content: center;
  align-items: center;
  /*width: 400px;*/
}
.disk-header-content-item {
  margin-left: 20px;
}
.disk-body {
  /*height: 100%;*/
  /*height: 80%;*/
  width: inherit;
  top: 0px;
  padding-top: 73px;
  position: absolute;
  text-align: center;
  background-color: white;
  display: flex;
}
.disk-sidebar {
  width: 5%;
}
.disk-aside-nav {
  border-top-left-radius: 20px;
  border-bottom-left-radius: 20px;
  border-right: 1px solid #f1f2f4;
  /*background-color: #ff4949;*/
  width: 10%;
}
.disk-filelist {
  /*background-color: #13ce66;*/
  position: relative;
  width: 70%;
  border-right: 1px solid #f1f2f4;
}
.disk-body-detail {
  border-top-right-radius: 20px;
  border-bottom-right-radius: 20px;
  /*background-color: #feec9e;*/
  width: 15%;
}
.disk-filelist-header {
  /*background-color: #E65D6E;*/
  display: flex;
  height: 5%;
}
.disk-filelist-header-fileUploader-text {
  display: flex;
  justify-content: flex-start;
  /*padding-left: 20px;*/
  border-bottom-style: solid;
  border-bottom-color: #06a7ff;
  border-bottom-width: 1px;
  align-items: center;
  margin-top: 5px;
  width: 9%;
}
.disk-body-share-record {
  /*background-color: #13ce66;*/
  position: relative;
  width: 100%;
}
.disk-body-recycle-bin {
  position: relative;
  width: 100%;
}
.disk-filelist-header-fileUploader {
  margin: 5px;
}
.disk-filelist-header-folderCreator {
  margin: 5px;
}
.disk-filelist-body {
  background-color: #FFBA00;
  height: 95%;
}
.disk-main-transmission-list {
  position: absolute;
  /*left: 704px;*/
  top: 50px;
  right: 50px;
  width: 704px;
  font-size: 12px;
  background: #fff;
  -webkit-box-shadow: 0 2px 8px 0 rgb(0 0 0 / 16%);
  box-shadow: 0 2px 8px 0 rgb(0 0 0 / 16%);
  border-radius: 8px;
  height: 408px;
  z-index: 2;
}
</style>
