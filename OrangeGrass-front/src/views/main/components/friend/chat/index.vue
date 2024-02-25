<template>
  <div class="disk-chat">
    <div class="disk-chat-list">
      <div class="disk-chat-list-header">
        <p>和{{friendName}}的会话</p>
      </div>
      <div style="background-color: #f5f6fa;">
        <div class="disk-chat-list-body" ref="bodyScroll" v-loading="loading">
          <div class="disk-chat-empty" v-if="chatContent.length === 0">
            您还没有和对方发过消息噢
          </div>
          <div class="disk-chat-list-content" v-for="item in this.chatContent">
            <div class="disk-chat-list-content-from-me"
                 v-if="item.fromUserId === userId">
              <div class="disk-chat-list-content-from-me-content" v-if="item.contentType === 2">
                <img width="70px" height="70px" :src="item.content">
              </div>
              <div class="disk-chat-list-content-from-me-content" v-if="item.contentType === 0">
                <p>{{item.content}}</p>
              </div>
              <div class="disk-chat-list-content-from-me-content" v-if="item.contentType === 1">
                <div class="preview">
                  <div v-if="!isImg(JSON.parse(item.content))&&!isVideo(JSON.parse(item.content))">
                    <img :src="setFileImg(JSON.parse(item.content))" />
                  </div>
                  <div v-viewer v-if="isImg(JSON.parse(item.content))">
                    <img
                      :src="setFileImg(JSON.parse(item.content))"
                      :data-source="getImgPath(JSON.parse(item.content))"
                      style="cursor:pointer">
                  </div>
                  <div @mouseenter="coverHover(item)"
                       @mouseout="coverLeave(item)"
                       v-if="(!item.status)&&isVideo(JSON.parse(item.content))">
                    <img width="300px" height="200px" :src="setFileImg(JSON.parse(item.content))" />
                  </div>
                  <div
                    @mouseout="coverLeave(item)"
                    v-if="item.status&&isVideo(JSON.parse(item.content))">
                    <j-video-cover
                      class="video"
                      step-nums="15"
                      :show-duration="false"
                      :videoUrl="getVideoDownloadPath(JSON.parse(item.content))"
                    ></j-video-cover>
                  </div>
                </div>
                <a :href="JSON.parse(item.content).shareUrl">
                  <p>查看分享</p>
                </a>
              </div>
              <div class="disk-chat-list-content-from-me-avatar">
                <el-avatar :src="getAvatarPath(item.fromUserId)"></el-avatar>
              </div>
            </div>
            <div class="disk-chat-list-content-to-me" v-else>
              <div class="disk-chat-list-content-to-me-avatar">
                <el-avatar :src="getAvatarPath(item.fromUserId)"></el-avatar>
              </div>
              <div class="disk-chat-list-content-to-me-content" v-if="item.contentType === 2">
                <img width="70px" height="70px" :src="item.content">
              </div>
              <div class="disk-chat-list-content-to-me-content" v-if="item.contentType === 0">
                <p>{{item.content}}</p>
              </div>
              <div class="disk-chat-list-content-to-me-content" v-if="item.contentType === 1">
                <div class="preview">
                  <div v-if="!isImg(JSON.parse(item.content))&&!isVideo(JSON.parse(item.content))">
                    <img :src="setFileImg(JSON.parse(item.content))" />
                  </div>
                  <div v-viewer v-if="isImg(JSON.parse(item.content))">
                    <img
                      :src="setFileImg(JSON.parse(item.content))"
                      :data-source="getImgPath(JSON.parse(item.content))"
                      style="cursor:pointer">
                  </div>
                  <div @mouseenter="coverHover(item)"
                       @mouseout="coverLeave(item)"
                       v-if="(!item.status)&&isVideo(JSON.parse(item.content))">
                    <img width="300px" height="200px" :src="setFileImg(JSON.parse(item.content))" />
                  </div>
                  <div
                    @mouseout="coverLeave(item)"
                    v-if="item.status&&isVideo(JSON.parse(item.content))">
                    <j-video-cover
                      class="video"
                      step-nums="15"
                      :show-duration="false"
                      :videoUrl="getVideoDownloadPath(JSON.parse(item.content))"
                    ></j-video-cover>
                  </div>
                </div>
                <a :href="JSON.parse(item.content).shareUrl">
                  <p >查看分享</p>
                </a>
              </div>
            </div>
          </div>
        </div>
        <div class="disk-chat-list-content-operation">
          <div class="disk-chat-list-content-operation-input">
            <input class="inputs" @keyup.enter="handleSend()" v-model="inputString">
            <img class="emoji" title="emoji～" @click="clickEmoji"  src="../../../../../assets/images/笑脸.png">
            <img @click="handleShareButtonClick" title="分享文件" class="share" src="../../../../../assets/images/share.png">
          </div>
          <div class="emoji-content">
            <Emoji
              v-show="showEmoji"
              @sendEmoji="sendEmoji"
              @closeEmoji="clickEmoji"
            ></Emoji>
          </div>
          <!--        <div class="disk-chat-list-content-operation-send">-->
          <!--          <el-button type="success" round size="small" @click="handleSend()">发送</el-button>-->
          <!--        </div>-->
        </div>
        <span style="position: relative;top: -30px;left:230px;font-size: 14px;font-weight: 300">按enter键发送哦～</span>
      </div>
    </div>
    <div>
      <el-dialog :title="'分享文件'" :visible.sync="shareDialogVisibility">
        <div style="display: flex">
          <el-tree style="width: 30%"
                   :data="this.pathTree"
                   :expand-on-click-node="false"
                   :props="defaultProps"
                   indent="10"
                   default-expand-all
                   @node-click="handleNodeClickNew"
                   ref="newPathTree"
                   class="myTree">

          </el-tree>
          <el-table style="width: 70%" :data="this.fileList">
            <el-table-column width="100px">
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
            <el-table-column width="320px" prop="name">
              <template slot-scope="scope">
                <div style="cursor:pointer;" v-if="scope.row.type === 'FOLDER'" @click="clickFolder(scope.row)">
                  {{scope.row.name.length > 20 ? scope.row.name.substring(0,19)+'...' : scope.row.name}}
                </div>
                <div style="cursor:pointer;" v-if="scope.row.type === 'FILE'">
                  {{scope.row.name.length > 20 ? scope.row.name.substring(0,19)+'...' : scope.row.name}}
                </div>
              </template>
            </el-table-column>
            <el-table-column>
              <template slot-scope="scope">
                <img @click="handleShare(scope.row)" style="cursor:pointer;" title="分享文件" width="20px" height="20px" src="../../../../../assets/images/share.png">
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../../../utils/FileRequest";
import JVideoCover from "../../../thirdPartycComponents/JVideoCover/src/JVideoCover";
import {ref} from "vue";
import Emoji from '../../../../../components/Emoji/index'
import {
  getAvatarPathGlobal,
  getChunkDownloadPath,
  getDownloadPathGlobal,
  getShareUrlGlobal,
  getThumbnailPathGlobal
} from "../../../../../main";

export default {
  name: "chat",
  props: {
    friendId: String
  },
  components: {
    JVideoCover,
    Emoji
  },
  data() {
    return {
      showEmoji: false,
      defaultProps: {
        children: 'subs',
        label: 'name'
      },
      pathTree:[],
      fileList:[],
      shareDialogVisibility: false,
      loading: true,
      friendName: '',
      userId: localStorage.getItem('userId'),
      inputString: '',
      chatContent: [
        // {
        //   chatId:0,
        //   content:'',
        //   contentType:'',
        //   fromUserId:'',
        //   isBan:'',
        //   sentTime:'',
        //   targetUserId:'',
        //   unread:'',
        //   status: ref(false)
        // }
      ]
    }
  },
  created() {
    this.getMessages()
    this.friendIdToName()
  },
  watch: {
    friendId() {
      this.getMessages()
      this.friendIdToName()
    }
  },
  mounted() {
    window.addEventListener('onmessageWS', this.getSocketData)
  },
  beforeDestroy() {
    // 清除定时器、取消订阅等操作
    clearInterval(this.timer);
  },
  destroyed() {
    // 清除缓存、移除事件监听等操作
    window.removeEventListener('onmessageWS', this.getSocketData);
  },
  methods: {
    sendEmoji(msg) {
      let message = {
        fromUserId: this.userId,
        targetUserId: this.friendId,
        status:false,
        contentType: 2,
        content: msg
      }
      Fileservice({
        url: 'api/chat',
        method: 'post',
        params: {
          userId:this.userId,
          friendId:this.friendId,
          contentType:2,
          content:msg
        }
      }).then(res=>{
        if (res.code === 1) {
          this.chatContent.push(message)
          this.handleScrollBottom()
        }
      })
      console.log(message)
    },
    //关闭标签框
    clickEmoji() {
      this.showEmoji = !this.showEmoji;
    },
    coverHover(val) {
      console.log('hover了')
      val.status = true
      console.log(val)
    },
    coverLeave(val) {
      console.log('leave了')
      val.status = false
      console.log(val)
    },
    sendShare(content) {
      let message = {
        fromUserId: this.userId,
        targetUserId: this.friendId,
        status:false,
        contentType: 1,
        content: content
      }
      this.chatContent.push(message)
      Fileservice({
        url: 'api/chat',
        method: 'post',
        params: {
          userId:this.userId,
          friendId:this.friendId,
          contentType:1,
          content:content
        }
      }).then(res=>{
        if (res.code !== 1) {
          this.$message.error(res.msg)
        }
        this.handleScrollBottom()
      })
    },
    handleShare(row) {
      Fileservice({
        url: '/api/share/file',
        method: 'get',
        params: {
          u2fId: row.id,
          extractCodeNeeded: true,
          autoGenerateEnable: true,
          extractCode: '',
          expireTime: ''
        }
      }).then(res=>{
        if (res.code === 1){
          let shareId = res.data.shareId
          let extractCode = res.data.extractCode
          let shareUrl = getShareUrlGlobal(shareId,extractCode)
          let fileName = row.name
          let id = row.id
          // this.sendShare(shareUrl)
          this.sendShare(JSON.stringify({shareUrl:shareUrl,name:fileName,id:id}))
          this.shareDialogVisibilityChange()
        }
      })
    },
    getVideoDownloadPath(val){
      return getChunkDownloadPath(val.id)
    },
    handleNodeClickNew(data) {
      console.log(data.id)
      this.getFiles(data.id)
    },
    getPathTree() {
      Fileservice({
        url: 'api/disk/folder/' + localStorage.getItem('userId'),
        method: 'get'
      }).then(res=>{
        if (res.code === 1) {
          console.log(res.data)
          this.pathTree = new Array(res.data)
        }
      })
    },
    getFiles(folderId) {
      Fileservice({
        url: 'api/disk/'+localStorage.getItem('userId'),
        method: 'get',
        params: {
          folderId:  (folderId === undefined || folderId === null) ? '' : folderId
        }
      }).then(res=>{
        if (res.code === 1) {
          this.fileList = res.data.subs
        }
      })
    },
    handleShareButtonClick() {
      this.shareDialogVisibilityChange()
      this.getFiles()
      this.getPathTree()
    },
    shareDialogVisibilityChange() {
      this.shareDialogVisibility = !this.shareDialogVisibility
    },
    clickFolder(row){
      if(row.type === 'FOLDER') {
        this.fileList = row.subs
        console.log('当前文件夹是' + this.currentFolder.name)
      } else {
        console.log('当前文件是' + row.name)
      }

    },
    friendIdToName() {
      Fileservice({
        url: 'api/user/name',
        method:'get',
        params:{
          id: this.friendId
        }
      }).then(res=>{
        if (res.code === 1) {
          this.friendName = res.data
        }
      })
    },
    getImgPath(val){
      return getDownloadPathGlobal(val.id)
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
    getThumbnailPath(id){
      return getThumbnailPathGlobal(id)
    },
    isVideo(val) {
      let extension = val.name.substring(val.name.lastIndexOf('.')+1,val.name.length).toLowerCase()
      if (["avi", "mp4", "mpg", "mov", "swf", "flv"].includes(extension)) {
        return true
      } else {
        return false
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
    getSocketData (res) {
      let data = JSON.parse(res.detail.data)
      if (data.notifyType === 1) {
        if (data.data.fromUserId === this.friendId){
          this.read()
          let niuniu = {
            chatId:data.data.chatId,
            content:data.data.content,
            contentType:data.data.contentType,
            fromUserId:data.data.fromUserId,
            isBan:data.data.isBan,
            sentTime:data.data.sentTime,
            status: false,
            targetUserId:data.data.targetUserId,
            unread:data.data.unread
          }
          this.chatContent.push(niuniu)
          // this.chatContent.push(data.data)
          this.handleScrollBottom()
        }
      }
    },
    read() {
      console.log('触发read')
      Fileservice({
        url: 'api/chat',
        method: 'get',
        params: {
          userId: this.userId,
          friendId: this.friendId
        }
      }).then(res=>{
        if (res.code === 1) {

        }
      })
    },
    getMessages() {
      this.loading = true
      this.chatContent = []
      Fileservice({
        url: 'api/chat',
        method: 'get',
        params: {
          userId: this.userId,
          friendId: this.friendId
        }
      }).then(res=>{
        if (res.code === 1) {
          for (let i = 0; i < res.data.length ; ++i) {
            let item = res.data[i];
            console.log(item)
            let tmp = {
              chatId:item.chatId,
              content:item.content,
              contentType:item.contentType,
              fromUserId:item.fromUserId,
              isBan:item.isBan,
              sentTime:item.sentTime,
              status: false,
              targetUserId:item.targetUserId,
              unread:item.unread
            }
            this.chatContent.push(tmp)
          }
          // this.chatContent = res.data
          this.handleScrollBottom()
          this.loading = false
        } else {
          this.loading = false
        }
      })
    },
    handleSend() {
      Fileservice({
        url: 'api/chat',
        method: 'post',
        params:{
          userId:this.userId,
          friendId:this.friendId,
          contentType:0,
          content:this.inputString
        }
      }).then(res=>{
        if (res.code === 1) {
          let message = {
            fromUserId: this.userId,
            targetUserId: this.friendId,
            contentType: 0,
            content: this.inputString
          }
          this.chatContent.push(message)
          this.inputString = ''
          this.handleScrollBottom()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getAvatarPath(val) {
      return getAvatarPathGlobal(val)
    },
    // 滚动到底部
    handleScrollBottom() {
      this.$nextTick(() => {
        let scrollElem = this.$refs.bodyScroll;
        scrollElem.scrollTo({ top: scrollElem .scrollHeight, behavior: 'smooth' });

      });
    }
  }
}
</script>

<style scoped>
.disk-chat {
  height: 890px;
}
.disk-chat-list {
  width: 1400px;
}
.disk-chat-list-body {
  padding-top: 10px;
  overflow-y: scroll;
  height: 640px;
  background-color: #f5f6fa;
}
.disk-chat-list-body::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 0.8vh;
}
.disk-chat-list-body::-webkit-scrollbar {
  width: 0.3vw; /* 纵向滚动条 宽度 */
  height: 0.1vh; /* 横向滚动条 高度 */

}
.disk-chat-list-body::-webkit-scrollbar-track{
  background: transparent;
}
.disk-chat-list-header {
  z-index: 2;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 20px;
  background-color: white;
  box-shadow: 0 3px 10px 0 rgb(0 0 0 / 6%);
}
.disk-chat-list-content-operation {
  background-color: #f5f6fa;
  margin-top: 25px;
  z-index: 923;
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.disk-chat-list-content-operation-input {
  width: 700px;
  /*margin-left: 26%;*/
  /*margin-right: 20px;*/
}
.emoji {
  width: 20px;
  height: 20px;
  position: relative;
  z-index:2;
  left: -580px;
  top: -40px;
  cursor: pointer;
}
.share {
  width: 20px;
  height: 20px;
  position: relative;
  z-index:2;
  left: -640px;
  top: -40px;
  cursor: pointer;
}
.inputs {
  width: 90%;
  height: 140px;
  background-color:white;
  /*background-color: rgb(66, 70, 86);*/
  border-radius: 15px;
  border: 2px solid rgb(34, 135, 225);
  padding: 10px;
  box-sizing: border-box;
  transition: 0.2s;
  font-size: 20px;
  color: black;
  font-weight: 100;
  /*margin-top: 5px;*/
  margin-bottom: 10px;
}
.disk-chat-list-content-to-me {
  display: flex;
  margin-left: 10px;
}
.disk-chat-list-content-to-me-content {
  /*height: 70px;*/
  min-height: 70px;
  margin-top: 40px;
  margin-left: 20px;
  font-size: 14px;
  font-weight: 700;
}
.disk-chat-list-content-from-me {
  display: flex;
  justify-content: flex-end;
  text-align: center;
  margin-right: 10px;
}
.disk-chat-list-content-from-me-content {
  /*height: 70px;*/
  min-height: 70px;
  margin-top: 40px;
  margin-right: 20px;
  font-size: 14px;
  font-weight: 700;
}
.emoji-content {
  z-index: 9;
  position: relative;
  bottom: 35px;
  left: -320px;
}
</style>
