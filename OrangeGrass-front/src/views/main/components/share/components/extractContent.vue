<template>
  <div>
    <div class="disk-main-share-empty" v-if="isExtractListEmpty">
      <div class="disk-main-empty-content">
        <p class="disk-main-empty-content-icon">
          <svg t="1679043402843" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3833" width="200" height="200"><path d="M614.4 102.4a153.6 153.6 0 1 0 0 307.2 153.6 153.6 0 0 0 0-307.2z m0 68.266667a85.333333 85.333333 0 1 1 0 170.666666 85.333333 85.333333 0 0 1 0-170.666666zM819.2 614.4a153.6 153.6 0 1 0 0 307.2 153.6 153.6 0 0 0 0-307.2z m0 68.266667a85.333333 85.333333 0 1 1 0 170.666666 85.333333 85.333333 0 0 1 0-170.666666z" fill="#444444" p-id="3834"></path><path d="M505.9072 316.245333a34.133333 34.133333 0 0 1 48.384 48.0768l-2.065067 2.0992-221.866666 204.8a34.133333 34.133333 0 0 1-48.384-48.0768l2.065066-2.0992 221.866667-204.8zM325.290667 674.389333a34.133333 34.133333 0 0 1 38.894933-25.361066l2.491733 0.512 341.333334 85.333333a34.133333 34.133333 0 0 1-14.062934 66.7648l-2.491733-0.512-341.333333-85.333333a34.133333 34.133333 0 0 1-24.832-41.403734z" fill="#444444" p-id="3835"></path><path d="M238.933333 494.933333a153.6 153.6 0 1 0 0 307.2 153.6 153.6 0 0 0 0-307.2z m0 68.266667a85.333333 85.333333 0 1 1 0 170.666667 85.333333 85.333333 0 0 1 0-170.666667z" fill="#00B386" p-id="3836"></path></svg>
        </p>
        <p class="disk-main-empty-content-text">
          您没有提取过文件噢～
        </p>
      </div>
    </div>
    <div class="disk-main-share-not-empty" v-if="!isExtractListEmpty">
      <div class="disk-main-share-list">
        <el-table v-loading="loading" :data="extractList" max-height="890px" class="myList"
                  @selection-change="handleSelectionChange">
          <el-table-column type="selection"></el-table-column>
          <el-table-column prop="id" width="60" align="center">
            <template slot-scope="scope">
              <div v-if="!isImg(scope.row)">
                <img :src="setFileImg(scope.row)" style="height: 30px"/>
              </div>
              <div v-if="isImg(scope.row)">
                <img
                  :src="setFileImg(scope.row)"
                  style="height: 30px; width: 30px;">
              </div>
            </template>
          </el-table-column>
          <el-table-column label="文件名" prop="fileName" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column label="分享人" prop="fromUserName"></el-table-column>
          <el-table-column label="提取时间" prop="extractTime">
            <template slot-scope="scope">
              {{new Date(scope.row.extractTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}
            </template>
          </el-table-column>
          <el-table-column>
            <template slot-scope="scope">
              <div style="display: flex">
                <div @click="copyShareUrl(scope.row)"
                     class="disk-main-extract-list-operation-buttons"
                     title="复制链接">
                  <svg style="height: 14px;width: 14px" t="1679197992989" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3763" width="200" height="200"><path d="M725.333333 341.333333h128v512H341.333333v-128H213.333333V213.333333h512v128z m0 42.666667v341.333333H384v85.333334h426.666667V384h-85.333334zM256 256v426.666667h426.666667V256H256z" fill="#444444" p-id="3764"></path></svg>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="disk-main-share-detail">
        <div class="disk-main-share-detail-title" style="font-weight: 700;font-size: 14px;padding-right: 150px">
          <p>提取详情</p>
        </div>
        <div class="disk-main-share-detail-img" v-if="this.detailList.length === 0">
          <img height="120px" width="120px" src="@/assets/images/empty-folder.png">
          <p>选中文件，查看详情</p>
        </div>
        <div class="disk-main-share-multi-select" style="margin-top: 40px" v-if="this.detailList.length > 1">
          <img height="120px" width="120px" src="@/assets/images/empty-folder.png">
          <p>{{'共选中'+this.detailList.length+'个文件'}}</p>
        </div>
        <div class="detail-divider" v-if="this.detailList.length === 1"></div>
        <div class="disk-main-share-detail-selected-one" style="margin-top: 10px"
             v-if="this.detailList.length === 1">
          <div class="disk-main-share-detail-selected-one-info" style="display: flex;font-size: 14px;font-weight: 300">
            <img style="margin-left: 20px;margin-right: 10px" width="20px" height="20px" :src="setFileImg(this.detailList[0])">
            <span>{{limitStringLength(this.detailList[0].fileName)}}</span>
          </div>
          <div class="disk-main-share-detail-selected-one-share_time"
               style="text-align: left;margin-top:20px;">
            <span style="margin-left: 20px;color: #878c9b;">提取时间</span>
            <br>
            <span style="margin-left: 20px;font-size: 14px;font-weight: 300">
              {{new Date(this.detailList[0].extractTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}</span>
          </div>
        </div>
        <div class="detail-divider" v-if="this.detailList.length === 1"></div>

      </div>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../../../utils/FileRequest";
import {getShareUrlGlobal, getThumbnailPathGlobal} from "../../../../../main";

export default {
  name: "extractContent",
  data() {
    return {
      loading: true,
      isExtractListEmpty: false,
      extractList: [],
      detailList:[]
    }
  },
  created() {
    this.getExtractList()
  },
  methods: {
    // 先给要复制的文本或者按钮加上点击事件后，并将要复制的值传过来
    async copyValue(val) {
      if (navigator.clipboard && window.isSecureContext) {
        // navigator clipboard 向剪贴板写文本
        this.$message.success('已将分享链接复制到剪切板')
        return navigator.clipboard.writeText(val)
      } else {
        // 创建text area
        const textArea = document.createElement('textarea')
        textArea.value = val
        // 使text area不在viewport，同时设置不可见
        document.body.appendChild(textArea)
        textArea.focus()
        textArea.select()
        this.$message.success('已将分享链接复制到剪切板')
        return new Promise((res, rej) => {
          // 执行复制命令并移除文本框
          document.execCommand('copy') ? res() : rej()
          textArea.remove()
        })
      }
    },
    copyShareUrl(row) {
      let url = getShareUrlGlobal(row.shareId,row.extractCode)
      this.copyValue(url)
    },
    getExtractList() {
      this.loading = true
      Fileservice({
        url: 'api/extract/user',
        method: 'get',
        params: {
          userId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1) {
          this.extractList = res.data
          console.log(this.extractList)
          if (this.extractList === null || this.extractList.length === 0) {
            this.isExtractListEmpty = true
          }
          this.loading = false
        } else {
          this.loading = false
        }
      })
    },
    handleSelectionChange(val) {
      this.detailList = val
      console.log(this.detailList)
    },
    isImg(val){
      let extension = val.fileName.substring(val.fileName.lastIndexOf('.')+1,val.fileName.length).toLowerCase()
      if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        return true
      } else {
        return false
      }
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
    limitStringLength(val) {
      if (val.length > 15) {
        return val.substring(0,14)+'...'+val.substring(val.lastIndexOf('.'))
      } else {
        return val
      }
    },
    getThumbnailPath(id){
      return getThumbnailPathGlobal(id)
    },
  }
}
</script>

<style scoped>
.disk-main-share-empty {
  position: absolute;
  top: 50%;
  left: 40%;
}
.disk-main-share-not-empty {
  margin-left: 20px;
  height: 100%;
  display: flex;
}
.disk-main-share-list {
  position: relative;
  height: inherit;
  width: calc(100% - 280px);
}
.myList {
  width: 100%;
  height: 100%;
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
.disk-main-share-detail {
  background: #f5f6fa;
  padding-top: 16px;
  border-radius: 8px;
  margin-left: 16px;
  width: 272px;
  height: 890px;
}
.disk-main-share-detail-img {
  position: relative;
  top: 50%;
  left: 50%;
  font-size: 12px;
  font-weight: 400;
  width: 100%;
  text-align: center;
  -webkit-transform: translate(-50%,-50%);
  transform: translate(-50%,-50%);
}
.disk-main-extract-list-operation-buttons {
  cursor: pointer;
  margin: 10px;
}
.detail-divider {
  display: inline-block;
  width: 87%;
  height: 1px;
  background-color: #d4d7de;
}
</style>
