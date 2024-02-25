<template>
  <div class="from-user-card-main">
    <div class="from-user-card-main-header">
      <div class="from-user-card-main-header-avatar">

      </div>
      <div class="from-user-card-main-header-username">
        分享人：<span>{{this.fromUser.username}}</span>
      </div>
      <div class="from-user-card-main-header-text"></div>
    </div>
    <div class="from-user-card-main-body">
      <div class="from-user-card-main-body-add-friend">
        <el-button type="primary" round>加为好友</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../../utils/FileRequest";

export default {
  name: "fromUserCard",
  data(){
    return {
      fromUser:{
        username:'',
        userId:''
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    init(){
      Fileservice({
        url: '/api/share/',
        method: 'get',
        params:{shareId:this.$route.query.shareId}
      }).then(res=>{
          if (res.code === 1){
            this.fromUser.userId = res.data.userId
            this.fromUser.username = res.data.username
          }
      })
    }
  }
}
</script>

<style scoped>
.from-user-card-main {
  height: inherit;
}
.from-user-card-main-header {
  margin-top: 200px;
  height: 20%;
}
.from-user-card-main-body {
  height: 40%;
}
</style>
