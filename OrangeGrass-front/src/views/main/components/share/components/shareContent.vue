<template>
  <div>
    <div class="disk-main-share-empty" v-if="isShareListEmpty">
      <div class="disk-main-empty-content">
        <p class="disk-main-content-icon">
          <svg t="1679043402843" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3833" width="200" height="200"><path d="M614.4 102.4a153.6 153.6 0 1 0 0 307.2 153.6 153.6 0 0 0 0-307.2z m0 68.266667a85.333333 85.333333 0 1 1 0 170.666666 85.333333 85.333333 0 0 1 0-170.666666zM819.2 614.4a153.6 153.6 0 1 0 0 307.2 153.6 153.6 0 0 0 0-307.2z m0 68.266667a85.333333 85.333333 0 1 1 0 170.666666 85.333333 85.333333 0 0 1 0-170.666666z" fill="#444444" p-id="3834"></path><path d="M505.9072 316.245333a34.133333 34.133333 0 0 1 48.384 48.0768l-2.065067 2.0992-221.866666 204.8a34.133333 34.133333 0 0 1-48.384-48.0768l2.065066-2.0992 221.866667-204.8zM325.290667 674.389333a34.133333 34.133333 0 0 1 38.894933-25.361066l2.491733 0.512 341.333334 85.333333a34.133333 34.133333 0 0 1-14.062934 66.7648l-2.491733-0.512-341.333333-85.333333a34.133333 34.133333 0 0 1-24.832-41.403734z" fill="#444444" p-id="3835"></path><path d="M238.933333 494.933333a153.6 153.6 0 1 0 0 307.2 153.6 153.6 0 0 0 0-307.2z m0 68.266667a85.333333 85.333333 0 1 1 0 170.666667 85.333333 85.333333 0 0 1 0-170.666667z" fill="#00B386" p-id="3836"></path></svg>
        </p>
        <p class="disk-main-empty-content-text">
          您没有分享过文件噢～
        </p>
      </div>
    </div>
    <div class="disk-main-share-not-empty" v-if="!isShareListEmpty">
      <div class="disk-main-share-list">
        <el-table v-loading="loading" :data="shareList" max-height="890px" class="myList"
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
          <el-table-column label="分享文件" prop="xname" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column label="分享时间" prop="createTime">
            <template slot-scope="scope">
              {{new Date(scope.row.createTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}
            </template>
          </el-table-column>
          <el-table-column label="状态" prop="status">
            <template slot-scope="scope">
              {{statusFormatter(scope.row.status)}}
            </template>
          </el-table-column>
          <el-table-column label="提取次数" prop="extractCount"></el-table-column>
          <el-table-column>
            <template slot-scope="scope">
              <div style="display: flex" v-if="scope.row.status === 0">
                <div  @click="copyShareUrl(scope.row)"
                      class="disk-main-share-list-operation-buttons"
                      title="复制链接">
                  <svg style="height: 14px;width: 14px" t="1679197992989" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3763" width="200" height="200"><path d="M725.333333 341.333333h128v512H341.333333v-128H213.333333V213.333333h512v128z m0 42.666667v341.333333H384v85.333334h426.666667V384h-85.333334zM256 256v426.666667h426.666667V256H256z" fill="#444444" p-id="3764"></path></svg>
                </div>
                <div  @click="cancelShare(scope.row)"
                      class="disk-main-share-list-operation-buttons"
                      title="取消分享">
                  <svg style="height: 14px;width: 14px" t="1679197701065" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2763" width="200" height="200"><path d="M799.584 687.424l53.728-43.488c131.328-106.368 151.584-299.04 45.248-430.368S599.52 61.952 468.192 168.32l-51.52 41.728L306.72 72.96a32 32 0 0 0-49.28 40.832l640.416 798.4a32 32 0 0 0 49.28-40.832l-147.552-183.936zM518.528 230.496a225.984 225.984 0 1 1 284.448 351.264l-53.44 43.264-118.72-148 84.864-70.688-51.232-61.472-83.68 69.76-114.048-142.176 51.808-41.952zM504.384 823.552a225.984 225.984 0 1 1-284.448-351.264l110.976-89.856-50.336-62.176L169.6 410.112C38.272 516.448 18.016 709.152 124.352 840.48c106.368 131.328 299.04 151.584 430.368 45.248l105.12-85.12-50.336-62.176-105.12 85.12z" p-id="2764"></path><path d="M446.72 526.304l51.2 61.44-125.856 104.864-51.2-61.44z" p-id="2765"></path></svg>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="disk-main-share-detail">
        <div class="disk-main-share-detail-title" style="font-weight: 700;font-size: 14px;padding-right: 150px">
          <p>分享详情</p>
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
            <span>{{limitStringLength(this.detailList[0].xname)}}</span>
          </div>
          <div class="disk-main-share-detail-selected-one-share_time"
               style="text-align: left;margin-top:20px;">
            <span style="margin-left: 20px;color: #878c9b;">分享时间</span>
            <br>
            <span style="margin-left: 20px;font-size: 14px;font-weight: 300">
              {{new Date(this.detailList[0].createTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}</span>
          </div>
        </div>
        <div class="detail-divider" v-if="this.detailList.length === 1"></div>
        <div class="disk-main-share-detail-selected-one-share-extract-time"
             v-if="this.detailList.length === 1"
             style="text-align: left;margin-top:20px;">
          <span style="margin-left: 20px;color: #878c9b;font-size: 13px">保存</span>
          <br>
          <span style="margin-left: 20px;font-size: 14px;font-weight: 300">
            {{this.detailList[0].extractCount+'次'}}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../../../utils/FileRequest";
import {getShareUrlGlobal, getThumbnailPathGlobal} from "../../../../../main";

export default {
  name: "shareContent",
  data() {
    return {
      loading: true,
      isShareListEmpty : false,
      shareList : [],
      detailList:[]

    }
  },
  created() {
    this.getShareList()
  },
  methods: {
    limitStringLength(val) {
      if (val.length > 15) {
        return val.substring(0,14)+'...'+val.substring(val.lastIndexOf('.'))
      } else {
        return val
      }
    },
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
      let url = getShareUrlGlobal(row.id,row.extractCode)
      this.copyValue(url)
    },
    cancelShare(row) {
      Fileservice({
        url : 'api/share/',
        method: 'put',
        params: {
          shareId : row.id,
          targetValue: 1
        }
      }).then(res=>{
        if (res.code === 1) {
          this.getShareList()
          this.$message.success('取消分享成功')
        } else {
          this.$message.error('取消分享失败')
        }
      })
    },
    getShareList() {
      this.loading = true
      Fileservice({
        url: 'api/share/user',
        method: 'get',
        params: {
          userId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1) {
          this.shareList = res.data
          if (this.shareList === null || this.shareList.length === 0) {
            this.isShareListEmpty = true
          }
          this.loading = false
        } else {
          this.loading = false
        }
      })
    },
    handleSelectionChange(val) {
      this.detailList = val
    },
    isImg(val){
      let extension = val.xname.substring(val.xname.lastIndexOf('.')+1,val.xname.length).toLowerCase()
      if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        return true
      } else {
        return false
      }
    },
    //  根据文件扩展名设置文件图片
    setFileImg(row) {
      let extension = row.xname.substring(row.xname.lastIndexOf('.')+1,row.xname.length).toLowerCase().trim()
      if (!this.FILE_IMG_TYPE_LIST.includes(extension)) {
        //  无法识别文件类型的文件
        return this.FILE_IMG_MAP.unknown
      } else if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        // 图片类型，直接显示缩略图
        return this.getThumbnailPath(row.xid)
        // return this.FILE_IMG_MAP[extension]
      } else if (["avi", "mp4", "mpg", "mov", "swf", "flv"].includes(extension)){
        return this.getThumbnailPath(row.xid)
      } else {
        //  可以识别文件类型的文件
        return this.FILE_IMG_MAP[extension]
      }
    },
    getThumbnailPath(id){
      return getThumbnailPathGlobal(id)
    },
    statusFormatter(val) {
      if (val === 0) {
        return '正常'
      }
      if (val === 1) {
        return '用户取消'
      }
    }
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
.disk-main-share-list-operation-buttons {
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
