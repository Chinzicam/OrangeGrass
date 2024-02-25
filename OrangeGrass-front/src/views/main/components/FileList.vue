<template>
  <div class="FileList">
    <div class="filelistContainer">
      <div class="filelist-aside-nav">
        <el-tree :data="pathTree"
                 :expand-on-click-node="false"
                 :props="defaultProps"
                 default-expand-all
                 @node-click="handleNodeClick"
                 :filter-node-method="filterNode"
                 ref="tree"
                 class="myTree"
                 style="display: none"
        ></el-tree>

        <el-tree :data="newPathTree"
                 :expand-on-click-node="false"
                 :props="defaultProps"
                 :indent="10"
                 default-expand-all
                 @node-click="handleNodeClickNew"
                 ref="newPathTree"
                 class="myTree"
        ></el-tree>
      </div>
      <div class="filelist-main">
        <el-table
          v-loading="fileListLoading"
          :data="fileList" height="100%" class="myList" @selection-change="handleSelectionChange">
          <el-table-column type="selection"></el-table-column>
          <el-table-column prop="id" width="60" align="center">
            <template slot-scope="scope">
              <div @dblclick="clickFolder(scope.row)" v-if="!isImg(scope.row)">
                <img :src="setFileImg(scope.row)" style="height: 30px"/>
              </div>
              <div v-viewer v-if="isImg(scope.row)">
                <img
                  :src="setFileImg(scope.row)"
                  :data-source="getImgPath(scope.row)"
                  style="height: 30px; width: 30px; cursor:pointer">
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="name" width="270" label="文件名" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <div style="cursor:pointer;" v-if="scope.row.type === 'FOLDER'" @click="clickFolder(scope.row)">
                {{scope.row.name}}
              </div>
              <div style="cursor:pointer;" v-if="scope.row.type === 'FILE'" @click="clickFile(scope.row)">
                {{scope.row.name}}
              </div>
            </template>
          </el-table-column>
          <!--          <el-table-column prop="type" width="80" label="文件类型" >-->
          <!--            <template slot-scope="scope">-->
          <!--              <div  @dblclick="clickFolder(scope.row)">-->
          <!--                {{scope.row.type === 'FOLDER' ? '文件夹' : '文件'}}-->
          <!--              </div>-->
          <!--            </template>-->
          <!--          </el-table-column>-->
          <el-table-column prop="createTime" width="260" label="创建时间" >
            <template slot-scope="scope">
              <div  @dblclick="clickFolder(scope.row)">
                {{new Date(scope.row.createTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="大小">
            <template slot-scope="scope">
              <div  @dblclick="clickFolder(scope.row)" v-if="scope.row.size != 0">
                {{getSize(scope.row.size)}}
              </div>
              <div  @dblclick="clickFolder(scope.row)" v-if="scope.row.size === 0">
                -
              </div>
            </template>
          </el-table-column>
          <!--      <er-table-column prop="fileLocation" label="文件位置"></er-table-column>-->
          <el-table-column>
<!--            <template slot="header">-->
<!--              <span>操作</span>-->
<!--            </template>-->
            <template slot-scope="scope">
              <div class="filelist-main-operation-buttons">
                <div title="下载" class="filelist-main-download-button" @click="handleDownload(scope.row)" v-if="scope.row.type === 'FILE' && scope.row.isBan != 1">
                  <img width="14px" height="14px" src="@/assets/images/download.png">
                </div>
                <div title="分享" class="filelist-main-share-button" @click="handleShare(scope.row)" v-if="scope.row.isBan != 1">
                  <img width="14px" height="14px" src="@/assets/images/share.png">
                </div>
                <div title="重命名" class="filelist-main-rename-button" @click="openRenameDialog(scope.row)">
                  <img width="14px" height="14px" src="@/assets/images/rename.png">
                </div>
                <div title="删除" class="filelist-main-delete-button" @click="handleDelete(scope.row)">
                  <img width="14px" height="14px" src="@/assets/images/delete.png">
                </div>
                <div title="更多" class="filelist-main-more-button" v-if="scope.row.type === 'FILE'">
                  <el-popover
                    placement="bottom"
                    trigger="hover">
                    <img width="14px" height="14px" src="@/assets/images/more.png" slot="reference">
                    <div style="text-align: center;">
                      <span style="cursor:pointer;" @click="handleAppeal(scope.row)" v-if="scope.row.isBan === 1">申诉</span>
                    </div>
                    <div style="text-align: center;">
                      <span style="cursor:pointer;" @click="handleReport(scope.row)" v-if="scope.row.isBan === 0">举报</span>
                    </div>
                  </el-popover>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div class="filelist-main-renameDialog">
          <el-dialog :visible.sync="renameDialogVisibility">
            <span>{{renameForm.oldName}}</span>
            <div class="filelist-main-renameDialog-body">
              <div class="filelist-main-renameDialog-body-text">
                <span>请输入新的名称</span>
              </div>
              <div class="filelist-main-renameDialog-body-input">
                <el-input v-model="renameForm.newName"></el-input>
              </div>
              <div class="filelist-main-renameDialog-body-submit">
                <el-button round type="primary" size="small" @click="handleRename">提交</el-button>
              </div>
            </div>
          </el-dialog>
        </div>
        <div class="filelist-main-shareDialog">
          <el-dialog :visible.sync="shareDialogVisibility">
            <div class="share-dialog-header">
              <span class="share-dialog-header-title">分享文件(夹):{{this.shareForm.name}}</span>
              <img :src="setFileImg(this.shareForm)" style="height: 30px;width: 30px">
            </div>
            <div class="share-dialog-body">
              <div class="share-dialog-body-header">

              </div>
              <div class="share-dialog-body-content-before" v-show="!shareFormSubmit">
                <el-form ref="form" :model="shareForm" label-width="80px">
                  <el-form-item label="有效期：">
                    <el-radio-group v-model="shareForm.expireTime">
                      <el-radio label="1天"></el-radio>
                      <el-radio label="7天"></el-radio>
                      <el-radio label="30天"></el-radio>
                      <el-radio label="永久有效"></el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <div class="share-dialog-body-content-before-extract-code">
                    <el-form-item label="提取码：">
                      <el-checkbox-group v-model="shareForm.extractCodeNeeded">
                        <el-checkbox label="有提取码" name="type"></el-checkbox>
                        <div class="share-dialog-body-extract-code">
                          <el-radio-group v-model="shareForm.autoGenerateEnable">
                            <el-radio :label="true">系统随机生成提取码</el-radio>
                            <el-radio :label="false">
                              <input placeholder="请输入提取码" v-model="shareForm.extractCode">
                            </el-radio>
                          </el-radio-group>
                        </div>
                      </el-checkbox-group>
                    </el-form-item>
                  </div>
                  <el-button type="primary" round @click="createShare()">创建链接</el-button>
                </el-form>
              </div>
              <div class="share-dialog-body-content-after" v-show="shareFormSubmit">
                <div class="share-dialog-body-content-after-status">
                  <span class="share-dialog-body-content-after-status">成功创建分享链接，访问者无需提取码可直接查看分享文件
            </span>
                </div>
                <div class="share-dialog-body-content-after-url-wrapper">
                  <div class="share-dialog-body-content-after-url">
                    <input v-model="shareUrl" readonly="readonly" class="input_inner">
                  </div>
                </div>
                <div class="share-dialog-body-content-after-extract-code">
                  <div class="share-dialog-body-content-after-extract-code-label">
                    提取码
                  </div>
                  <div class="share-dialog-body-content-after-extract-code-val">
                    <input v-model="shareForm.extractCode" readonly="readonly" class="input_inner">
                  </div>
                </div>
                <div class="share-dialog-body-content-after-tip">
                  链接<span class="primary_text">{{shareForm.expireTime}}</span>后失效
                </div>
              </div>
            </div>
          </el-dialog>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import Fileservice from "../../../utils/FileRequest";
import {VIDEO_TYPE_LIST,MUSIC_TYPE_LIST} from "../../../utils/globalConst";
import axios from "axios";
import {
  getChunkDownloadNewPath,
  getChunkDownloadPath,
  getDownloadPathGlobal,
  getShareUrlGlobal,
  getThumbnailPathGlobal
} from "../../../main";

export default {
  name: "FileList",
  props: {
    fileListLoading: Boolean,
    fileList: Array,
    pathTree: Array,
    newPathTree: Array,
    currentFolder: Object,
    downloadFileList: Array,
    userId: String
  },
  data(){
    return {
      limitRate: 1024,
      shareUrl:'',
      shareFormSubmit: false,
      shareForm: {
        id:'',
        name:'',
        type:'',
        expireTime:'',
        extractCodeNeeded:true,
        autoGenerateEnable:true,
        extractCode:'',
        currentUserId:'',
        shareId:''
      },
      shareDialogVisibility: false,
      photoVisible: false,
      bigImgUrls: [],
      renameDialogVisibility:false,
      renameForm:{
        id:'',
        oldName:'',
        newName:'',
        type:'',
      },
      uploadFile:{},
      // currentFolder:{
      //   id:'',
      //   name:''
      // },
      // userId: 'c8eade1f673041dd86a937e03a0c8350',
      defaultProps: {
        children: 'subs',
        label: 'name'
      },
      dialogMoveFile: {
        isBatchMove: false,
        visible: false, //  是否可见
        fileTree: [], //  目录树
        defaultProps: {
          children: 'children',
          label: 'label',
          type: 'type'
        }
      },
      multipleSelection: [],
      viewFilePath: '',
    }
  },
  created() {
    this.getSpeedLimitRate()
  },
  methods:{
    getSpeedLimitRate() {
      Fileservice({
        url: 'api/system/speed',
        method: 'get'
      }).then(res=>{
        if (res.code === 1) {
          this.limitRate = res.data
        }
      })
    },
    handleReport(row) {
      Fileservice({
        url: 'api/report',
        method: 'post',
        params: {
          fileId: row.id,
          userId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAppeal(row) {
      Fileservice({
        url: 'api/appeal',
        method: 'post',
        params: {
          fileId: row.id,
          userId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    createShare(){
      console.log(this.shareForm)
      Fileservice({
        url: '/api/share/file',
        method: 'get',
        params: {
          u2fId: this.shareForm.id,
          extractCodeNeeded: this.shareForm.extractCodeNeeded,
          autoGenerateEnable: this.shareForm.autoGenerateEnable,
          extractCode: this.shareForm.extractCode,
          expireTime: this.shareForm.expireTime
        }
      }).then(res=>{
        if (res.code === 1){
          this.$message.success(res.msg)
          this.shareFormSubmit = true
          this.shareForm.shareId = res.data.shareId
          this.shareUrl = getShareUrlGlobal(this.shareForm.shareId,undefined)
          this.shareForm.extractCode = res.data.extractCode
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleShare(val) {
      console.log(val)
      this.shareUrl = ''
      this.shareForm.extractCode = ''
      this.shareForm.shareId = ''
      this.shareForm.expireTime = ''
      this.shareForm.id = val.id
      this.shareForm.name = val.name
      this.shareForm.type = val.type
      this.shareForm.currentUserId = this.userId
      this.shareFormSubmit = false
      this.shareDialogVisibility = true
    },
    openRenameDialog(val){
      this.renameDialogVisibility = true
      this.renameForm.id = val.id
      this.renameForm.type = val.type
      this.renameForm.oldName = val.name
    },
    handleRename() {
      let val = this.renameForm
      console.log(val)
      if (val.type === 'FILE') {
        let fd = new FormData
        fd.append('userFileId',val.id)
        fd.append('newName',val.newName)
        Fileservice.put('/api/file/rename',fd, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(res=>{
          if (res.code === 1){
            this.getMyFiles()
            this.$message.success(res.msg)
            this.renameDialogVisibility = false
          } else {
            this.$message.error(res.msg)
          }
        })
      }
      if (val.type === 'FOLDER') {
        let fd = new FormData
        fd.append('folderId',val.id)
        fd.append('newName',val.newName)
        Fileservice.put('/api/folder/rename',fd, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(res=>{
          if (res.code === 1){
            this.getMyFiles()
            this.$message.success(res.msg)
            this.renameDialogVisibility = false
          } else {
            this.$message.error(res.msg)
          }
        })
      }
    },
    handleDelete(val) {
      if (val.type === 'FILE'){
        Fileservice({
          url:'/api/file',
          method: 'delete',
          params: {
            'fileId': val.id
          }
        }).then(res=>{
          if (res.code === 1){
            this.getMyFiles()
            this.$message.success(res.msg)
          } else {
            this.$message.error(res.msg)
          }
        })
        console.log(val)
      } else {
        console.log(val)
      }
    },
    getchunkDownloadPath(val){
      return getChunkDownloadPath(val.id)
    },
    getDownloadPath(val){
      return getDownloadPathGlobal(val.id)

    },
    getImgPath(val){
      return getDownloadPathGlobal(val.id)
      let extension = val.name.substring(val.name.lastIndexOf('.')+1,val.name.length).toLowerCase()
      if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        return getDownloadPathGlobal(val.id)
      } else {
        return this.setFileImg(val)
      }
    },
    isImg(val){
      let extension = val.name.substring(val.name.lastIndexOf('.')+1,val.name.length).toLowerCase()
      if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        return true
      } else {
        return false
      }
    },
    //  根据文件扩展名设置文件图片
    setFileImg(row) {
      let extension = row.name.substring(row.name.lastIndexOf('.')+1,row.name.length).toLowerCase().trim()
      if (row.type === 'FOLDER') {
        //  文件夹
        return this.FILE_IMG_MAP.dir
      } else if (!this.FILE_IMG_TYPE_LIST.includes(extension)) {
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
    // 树形节点过滤
    filterNode(value, data, node) {
      return data.type === value
    },
    async handleNodeClickNew(data) {
      console.log(data.id)
      this.$emit('setFiles','ALL',data.id)
    },
    async handleNodeClick(data) {
      console.log(data)
      if (data.type === 'FOLDER') {
        if (data.subs === null){
          this.$emit('fileListChange',null)
          // this.fileList = null;
          let val = {
            id : data.id,
            name : data.name
          }
          this.$emit("folderChange",val)
          console.log('当前文件夹是' + this.currentFolder.name)
        } else {
          this.$emit('fileListChange',data.subs)
          let val = {
            id : data.id,
            name : data.name
          }
          this.$emit("folderChange",val)
          console.log('当前文件夹是' + this.currentFolder.name)
        }
      }
    },
    clickFile(row){
      let filePath = getChunkDownloadPath(row.id)
      let extension = row.name.substring(row.name.lastIndexOf('.')+1,row.name.length).toLowerCase()
      //  若当前点击项是可以使用office在线预览的
      if (['ppt', 'pptx', 'doc', 'docx', 'xls', 'xlsx'].includes(extension)) {
        // window.open(this.getFileOnlineViewPathByOffice(row.id), '_blank')
        window.open(filePath, '_blank')
      }
      //  若当前点击项是pdf
      if (extension === 'pdf') {
        window.open(filePath, '_blank')
      }
      //  若当前点击项是html、js、css、json
      if (['html', 'js', 'css', 'json'].includes(extension)) {
        window.open(filePath, '_blank')
      }
      if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        let data = {
          imgReviewVisible: true,
          imgReviewList: [
            {
              id: row.id,
              fileUrl: this.getImgPath(row),
              downloadLink: this.getImgPath(row),
              filename: row.name,
              extension: extension
            }
          ],
          activeIndex: 0
        }
        this.$store.commit('setImgReviewData', data)
      }
      /**
       * 视频
       */
      if (VIDEO_TYPE_LIST.includes(extension)){
        let data = {
          visible: true,
          url: this.getchunkDownloadPath(row),
          filename: row.name
        }
        this.$store.commit('setVideoReviewData', data)
      }
      /**
       * 音乐
       */
      if (MUSIC_TYPE_LIST.includes(extension)){
        let data = {
          visible: true,
          url: this.getchunkDownloadPath(row),
          filename: row.name
        }
        this.$store.commit('setVideoReviewData', data)
      }
    },
    clickFolder(row){
      if(row.type === 'FOLDER') {
        this.$emit('fileListChange',row.subs)
        let val = {
          id : row.id,
          name : row.name
        }
        this.$emit("folderChange",val)
        console.log('当前文件夹是' + this.currentFolder.name)
      } else {
        console.log('当前文件是' + row.name)
      }

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
          this.$message.success(res.msg)
        }
      })
    },
    modeUpload(item){
      this.uploadFile = item.file
      console.log(this.uploadFile)
    },
    getThumbnailPath(id){
      return getThumbnailPathGlobal(id)
    },

    //first download interface
    async handleDownloadWhileFileBaned(row){
      console.log(row)
      Fileservice({
        url:"/api/file/download/"+row.id,
        method:'get',
        responseType: 'blob'}).then((res) => {
        console.log(res)
        if (!res) {
          this.$message.warning('文件下载失败')
          return
        }
        let blob = new Blob([res], {type: 'application/octet-stream'})
        let downloadElement = document.createElement('a')
        let href = window.URL.createObjectURL(blob) // 创建下载的链接
        downloadElement.href = href
        downloadElement.download = row.name // 下载后文件名
        document.body.appendChild(downloadElement)
        downloadElement.click() // 点击下载
        document.body.removeChild(downloadElement) // 下载完成移除元素
        window.URL.revokeObjectURL(href) // 释放掉blob对象
      })
      console.log(11);
    },

    handleDownload(row) {
      if (row.isBan === 0) {
        this.downloadFile(row)
      } else {
        this.handleDownloadWhileFileBaned(row)
      }
    },
    //点击下载文件
    downloadFile(val) {
      let file = {
        downloadingStop : false,
        downloadSpeed : "0 M/s",
        downloadPersentage : 0,
        blobList : [],
        chunkList : [],
        id : val.id,
        size: val.size,
        name: val.name
      }
      // console.log("下载", file);
      // file.downloadingStop = false;
      // file.downloadSpeed = "0 M/s";
      // file.downloadPersentage = 0;
      // file.blobList = [];
      // file.chunkList = [];
      this.$emit('addDownloadFile',file)
      // this.downloadingFileList.push(file);
      this.downloadChunk(1, file);
    },
    //点击下载文件分片
    downloadChunk(index, file) {
      let chunkSize = this.limitRate * 1024;
      let chunkTotal = Math.ceil(file.size / chunkSize);

      if (index <= chunkTotal) {
        // console.log("下载进度",index);
        let exit = file.chunkList.includes(index);

        if (!exit) {
          if (!file.downloadingStop) {
            let formData = new FormData();
            formData.append("fileName", file.name);
            formData.append("u2fId", file.id);
            formData.append("chunkSize", chunkSize);
            formData.append("index", index);
            formData.append("chunkTotal", chunkTotal);
            formData.append("userId",localStorage.getItem('userId'))
            if (index * chunkSize >= file.size) {
              chunkSize = file.size - (index - 1) * chunkSize;
              formData.set("chunkSize", chunkSize);
            }

            let startTime = new Date().valueOf();

            axios({
              url: getChunkDownloadNewPath(),
              method: "post",
              data: formData,
              responseType: "blob",
              timeout: 50000,
              headers: {
                'Content-Type': 'application/json;charset=UTF-8', // 将表单数据传递转化为form-data类型
                Authorization: localStorage.getItem('token')
              }
            }).then((res) => {
              if (index === 1) {
                this.$message.success("开始下载")
              }
              file.chunkList.push(index);
              let endTime = new Date().valueOf();
              let timeDif = (endTime - startTime) * 1000 ;
              file.downloadSpeed = (chunkSize / timeDif).toFixed(1) + " M/s";
              //todo
              file.downloadPersentage = parseInt((index / chunkTotal) * 100);
              // var chunk = res.data.data.chunk
              // const blob = new Blob([res.data]);
              const blob = res.data;

              file.blobList.push(blob);
              // console.log("res", blobList);
              if (index == chunkTotal) {
                let resBlob = new Blob(file.blobList, {
                  type: "application/octet-stream",
                });
                // console.log("resb", resBlob);

                let url = window.URL.createObjectURL(resBlob); // 将获取的文件转化为blob格式
                let a = document.createElement("a"); // 此处向下是打开一个储存位置
                a.style.display = "none";
                a.href = url;
                // 下面两行是自己项目需要的处理，总之就是得到下载的文件名（加后缀）即可

                let fileName = file.name;

                a.setAttribute("download", fileName);
                document.body.appendChild(a);
                a.click(); //点击下载
                document.body.removeChild(a); // 下载完成移除元素
                window.URL.revokeObjectURL(url); // 释放掉blob对象
              }

              this.downloadChunk(index + 1, file);
            });
          }
        } else {
          file.downloadPersentage = parseInt((index / chunkTotal) * 100);
          this.downloadChunk(index + 1, file);
        }
      }
    },


    handleSelectionChange(val) {
      this.$emit("folderChange",this.currentFolder)
      this.multipleSelection = val;
      console.log(this.multipleSelection)
      this.$parent.setDetail(val)
    },
    getSize(val) {
      if(val === 0) return "0 B"
      var k = 1024;
      var sizes = ['B','KB','MB','GB','PB','TB','EB','ZB','YB'],
        i = Math.floor(Math.log(val) / Math.log(k));
      return (val / Math.pow(k,i)).toPrecision(3) + "" + sizes[i]
    },
    getMyPics(){
      this.$emit('setFiles','PICTURE',this.currentFolder.id)
    },
    getMyDocuments(){
      this.$emit('setFiles','DOCUMENT',this.currentFolder.id)
    },
    getMyVideos(){
      this.$emit('setFiles','VIDEO',this.currentFolder.id)
    },
    getMyFiles(){
      this.$emit('setFiles','ALL',this.currentFolder.id)
    },
    getMyMusics(){
      this.$emit('setFiles','MUSIC',this.currentFolder.id)
    },
    getMyOtherFiles(){
      console.log('other')
    }
  }

}
</script>

<style scoped>
.primary_text {
  color: #06a7ff;
}
.FileList {
  position: relative;
  width: inherit;
  height: 100%;
}
/*.fileUploader {*/
/*  display: flex;*/
/*  height: 10%;*/
/*}*/
.filelistContainer {
  display: flex;
  position: relative;
  height: 100%;
  width: inherit;
}
.filelist-aside-nav {
  position: relative;
  width: 9%;
  /*height: 100%;*/
  background-color: white;
  /*border: 5px solid lightskyblue;*/
  padding: 16px;
  /*box-shadow: 0 0 0 1px lightskyblue;*/
  /*border-right: 1px solid #f1f2f4;*/
  border-right: 1px solid #f1f2f4;
}
.filelist-main {
  position: relative;
  height: inherit;
  width: 91%;
}
.filelist-main-renameDialog-body {
  display: flex;
  text-align: center;
  position: relative;
}
.filelist-main-renameDialog-body-text {
  width: 170px;
}
.filelist-main-renameDialog-body-input {
  width: 250px;
}
.filelist-main-renameDialog-body-submit {
  width: 20%;
}
.myList ::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 0.8vh;
}
.myList ::-webkit-scrollbar {
  width: 0.3vw; /* 纵向滚动条 宽度 */
  height: 0.1vh; /* 横向滚动条 高度 */

}
.myList ::-webkit-scrollbar-track{
  background: transparent;
}

.filelist-main-operation-buttons {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.filelist-main-download-button {
  cursor: pointer;
  margin: 10px;
}
.filelist-main-share-button {
  cursor: pointer;
  margin: 10px;
}
.filelist-main-rename-button {
  cursor: pointer;
  margin: 10px;
}
.filelist-main-delete-button {
  cursor: pointer;
  margin: 10px;
}
.filelist-main-more-button {
  cursor: pointer;
  margin: 10px;
}
.share-dialog-header {
  display: flex;
  text-align: left;
  height: 48px;
  box-sizing: border-box;
  padding: 15px 50px 15px 24px;
  border-bottom: 1px solid #f1f3f8;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.share-dialog-header-title {
  color: #303133;
  font-size: 14px;
  line-height: 18px;
  font-weight: 700;
}
.share-dialog-body-content-after {
  box-sizing: border-box;
  padding-top: 24px;
  width: 452px;
  height: 100%;
  border-right: 1px solid #f0f0f2;
  position: relative;

}
.share-dialog-body-content-after-status {
  color: #06a7ff;
}
.share-dialog-body-content-after-url-wrapper {
  position: relative;
  display: inline-block;
}
.share-dialog-body-content-after-url {
  margin-top: 19px;
  width: 295px;
}
.input_inner {
  border-color: #d4d7de;
  border-radius: 8px;
  padding-left: 8px;
  padding-right: 8px;
  line-height: 12px;
  height: 32px;
  -webkit-appearance: none;
  background-color: #fff;
  background-image: none;
  border: 1px solid #dcdfe6;
  box-sizing: border-box;
  color: #03081a;
  display: inline-block;
  font-size: inherit;
  height: 40px;
  line-height: 40px;
  outline: 0;
  padding: 0 12px;
  transition: border-color .2s cubic-bezier(.645,.045,.355,1);
  width: 100%;
}
.share-dialog-body-content-after-extract-code {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 16px;
}
.share-dialog-body-content-after-extract-code-label {
  display: inline-block;
  font-size: 12px;
}
.share-dialog-body-content-after-extract-code-val {
  margin-left: 8px;
  width: 94px;
}
.share-dialog-body-content-after-tip {
  margin-top: 16px;
  font-size: 12px;
  color: #4f526c;
  line-height: 17px;
}
.share-dialog-body-content-before-extract-code {
  vertical-align: middle;
}
</style>
