<template>
  <div class="disk-main-share">
    <div class="disk-main-share-sidebar">
      <div class="disk-main-share-sidebar-item" style="width: 200px;" tabindex="1" @click="contentChange(0)">
        <p>分享记录</p>
      </div>
      <div class="disk-main-share-sidebar-item" style="width: 200px;" tabindex="1" @click="contentChange(1)">
        <p>收集文件</p>
      </div>
    </div>
    <div class="disk-main-share-contont">
      <share-content v-if="showShareList"></share-content>
      <extract-content v-if="showExtractList"></extract-content>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../../utils/FileRequest";
import ShareContent from "./components/shareContent";
import ExtractContent from "./components/extractContent";

export default {
  name: "diskShare",
  components: {ExtractContent, ShareContent},
  data() {
    return {
      isShareListEmpty : false,
      isExtractListEmpty: false,
      showShareList: true,
      showExtractList: false,
      shareList : [],
      extractList: []
    }
  },
  created() {
    this.getShareList()
  },
  methods: {
    getExtractList() {
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
        }
      })
    },
    getShareList() {
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
        }
      })
    },
    contentChange(val) {
      if (val === 0) {
        this.showShareList = true
        this.showExtractList = false
        this.getShareList()
      } else {
        this.showShareList = false
        this.showExtractList = true
        this.getExtractList()
      }
    }
  }
}
</script>

<style scoped>
.disk-main-share {
  display: flex;
  width: 100%;
}
.disk-main-share-contont {
  width: 88%;
}
.disk-main-share-sidebar {
  margin-top: 20px;
  margin-left: 10px;
  width: 200px;
  height: inherit;
}
.disk-main-share-sidebar-item {
  height: 40px;
  border-radius: 10px;
  font-weight: 400;
  color: #636d7e;
  background-color: #fff;
  cursor: pointer;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  text-align: center;
  align-items: center;
  justify-content: center;
  flex-shrink: 1;
}
.disk-main-share-sidebar-item:focus
{
  color: #06a7ff;
  background-color: #eef9fe;
}
.disk-main-share-sidebar-item:hover
{
  background-color: #fafafc;
}
</style>
