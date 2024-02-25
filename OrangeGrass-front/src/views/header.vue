<template>
  <div class="disk-header">
    <div class="disk-header-logo" style="cursor:pointer;" @click="()=>{this.$router.push('/main')}">
      <img height="59px" width="59px" src="@/assets/images/disklogo.png">
    </div>
    <div class="disk-header-content">
      <div class="disk-header-content-item">
        <div class="disk-header-content-main-avatar" style="cursor: pointer" :title="this.username">
          <el-avatar :src="getAvatarPath()"></el-avatar>
          <!--          <img :title="this.userId" height="59px" width="59px" src="@/assets/images/disklogo.png">-->
        </div>
      </div>
      <div class="disk-header-content-item">
        <div class="disk-header-content-btn">
          <div class="disk-header-log-out-btn">
            <el-button round size="small" @click="handleLogout()">登出</el-button>
          </div>
        </div>
      </div>
      <div class="disk-header-content-item">
        <div class="disk-header-to-management-btn" style="font-size: 5px;cursor: pointer">
          <p @click="()=>{this.$router.push('/admin')}">管理页面</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {getAvatarPathGlobal} from "../main";

export default {
  name: "globalHeader",
  data () {
    return {
      username: '',
    }
  },
  created() {
    this.username = localStorage.getItem('username')
  },
  methods: {
    handleLogout() {
      localStorage.clear()
      this.$router.push('/')
    },
    getAvatarPath() {
      console.log('获取头像')
      return getAvatarPathGlobal(localStorage.getItem('userId'))
    },
  }
}
</script>

<style scoped>
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
</style>
