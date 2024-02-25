<template>
  <div class="share-extract-card-container">
    <div class="share-extract-card-banner">
    </div>
    <div class="share-extract-card">
      <el-card>
        <div class="share-extract-card-header">
          <div class="share-extract-card-header-user-info">
            <div class="share-extract-card-header-user-info-avatar">
              <el-avatar :src="getAvatarPath()" v-if="this.fromUserId != ''"></el-avatar>
            </div>
            <div class="share-extract-card-header-user-info-box">
              <p style="display: flex;justify-content: center;align-items: center">
                {{this.fromUserName}}
              </p>
            </div>
          </div>
          <div class="share-extract-card-header-button" v-if="showAddFriend">
            <el-button @click="handleAddFriend()">加为好友</el-button>
          </div>
        </div>
        <div class="share-extract-card-body">
          <div class="share-extract-card-body-text">
            <p>请输入提取码：</p>
          </div>
          <div class="share-extract-card-body-extract">
            <el-input style="width: 280px;
                      border: 1px solid #f2f2f2;"
                      v-model="extractCode"></el-input>
            <el-button round type="primary" style="margin-left: 14px" @click="handleExtract">提取文件</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../utils/FileRequest";
import {getAvatarPathGlobal} from "../../../main";
export default {
  name: "extractCard",
  data() {
    return{
      showAddFriend: false,
      shareId: '',
      extractCode: '',
      fromUserName: '',
      fromUserId: ''
    }
  },
  mounted() {
    this.checkLogin()
    this.checkShareStatus()
    this.init()
  },
  methods: {
    checkLogin() {
      if (localStorage.getItem('userId') === null ||
        localStorage.getItem('username') === null ||
        localStorage.getItem('userId') === ''
      ) {
        this.$router.push({path:'/login',query:{redirect: '/save?shareId=' + this.$route.query.id}})
      } else {
        this.username = localStorage.getItem('username')
      }
    },
    handleAddFriend() {
      if (this.fromUserId === localStorage.getItem('userId')) {
        this.$message.error('不能添加自己为好友')
      } else {
        Fileservice({
          url: 'api/apply/friend',
          method: 'post',
          params: {
            fromUserId: localStorage.getItem('userId'),
            targetUserId: this.fromUserId
          }
        }).then(res=>{
          if (res.code === 1) {
            this.$message.success(res.msg)
          } else {
            this.$message.error(res.msg)
          }
        })
      }
    },
    checkShareStatus () {
      Fileservice({
        url : 'api/share/check',
        method: 'get',
        params: {
          shareId: this.$route.query.id
        }
      }).then(res=>{
        if (res.code === 1) {
          let NORMAL = 0
          let CANCELED = 1
          let BANNED = 2
          //正常
          if (res.data === NORMAL) {

          }
          //用户取消
          if (res.data === CANCELED) {
            this.$router.push('/share/canceled')
          }
          //违规
          if (res.data === BANNED) {
            this.$router.push('/share/banned')
          }
        }
      })
    },
    getAvatarPath() {
      return getAvatarPathGlobal(this.fromUserId)
    },
    init() {
      this.shareId = this.$route.query.id
      this.extractCode = this.$route.query.pwd
      Fileservice({
        url : '/api/share/',
        params: {shareId:this.shareId},
        method: 'get'
      }).then(res=>{
        if (res.code === 1){
          this.fromUserName = res.data.username
          this.fromUserId = res.data.userId
          if (this.fromUserId === localStorage.getItem('userId')) {
            this.showAddFriend = false
          } else {
            this.showAddFriend = true
          }
        }
      })
    },
    handleExtract() {
      Fileservice({
          url: '/api/extract',
          method: 'get',
          params: {
            userId: localStorage.getItem('userId'),
            shareId: this.shareId,
            extractCode: this.extractCode
          }
        }).then(res=>{
         if (res.code === 1){
           this.$message.success(res.msg)
           this.$router.push({path:'/save',query:{shareId: this.shareId}})
           console.log(res.data)
         } else {
           this.$message.error(res.msg)
         }
      })
    }
  }
}
</script>

<style scoped>
.share-extract-card-container {
  width: 460px;
}
.share-extract-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-right: 35px;
}
.share-extract-card-body {
  padding: 20px 30px 48px;
}
.share-extract-card-header-user-info {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.share-extract-card-body-extract {
  display: flex;
  align-items: center;
}
</style>
