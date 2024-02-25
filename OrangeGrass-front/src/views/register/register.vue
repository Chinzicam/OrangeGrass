<template>
  <div class="box">
    <div class="content">
      <div class="login-wrapper">
        <div class="mask"></div>
        <div class="login-content">
          <h1>注 册</h1>
          <div class="title-text">
            欢迎来到这个界面，您可以通过这个界面注册账号。
          </div>
          <div class="other-login">
<!--            <img src="./asset/QQ.png" alt="">-->
            <span>欢迎使用本系统</span>
          </div>
          <div class="login-form">
            <input type="text" class="user" placeholder="账号" v-model="user.username">
            <input type="password" class="password" placeholder="密码" v-model="user.password">
          </div>
          <button class="login-btn" style="cursor: pointer" @click="handleRegister()">注 册</button>
          <div class="tips">
            <span>已有账号?</span>
            <span @click="toLogin" style="cursor: pointer">登陆</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../utils/FileRequest";
import axios from "axios";
export default {
  name: "Register",
  data () {
    return {
      timer: null,
      user: {
        username: '',
        password: ''
      }
    }
  },
  mounted() {
    this.init()
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    handleLogin() {
      Fileservice({
        url: 'api/user/login',
        // url: 'http://127.0.0.1:8989/api/user/login',
        method: 'post',
        data: this.user,
      }).then(res=>{
        console.log(res)
        if (res.code === 1){
          localStorage.setItem('userId',res.data.user.userId)
          localStorage.setItem('username',res.data.user.username)
          localStorage.setItem('token',res.data.token)
          this.$router.push("/main")
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleRegister() {
      console.log(this.user)
      Fileservice({
        url: '/api/user/',
        method: 'post',
        data: this.user
      }).then(res=>{
        console.log(res)
        if (res.code === 1){
          this.$message.success(res.msg)
          this.handleLogin()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    toLogin() {
      this.$router.push('/')
    },
    init() {
      let img = require('./asset/bg'+1+'.png')
      document.querySelector('.content').style.background = 'url(' + img + ') no-repeat'
      document.querySelector('.other-login').style.backgroundColor = 'rgb(248, 182, 217)';
      let flag = 1;
      this.timer = setInterval(()=>{
        if (flag % 2 == 0) {
          document.querySelector('.other-login').style.backgroundColor = 'rgb(248, 182, 217)';
        } else {
          document.querySelector('.other-login').style.backgroundColor = 'rgb(182, 211, 248)';
        }
        if (flag == 4) {
          flag = 1;
        } else {
          flag++;
        }
        let imgUrl = require('./asset/bg'+flag+'.png')
        // document.querySelector('.content').style.background = `url("./asset/bg${flag}.png") no-repeat`;
        document.querySelector('.content').style.background = 'url(' + imgUrl + ') no-repeat'
      }, 5000);
    }
  }
}
</script>

<style scoped>
@import './style.css';
/*.content {*/
/*  background: url("./asset/bg1.png");*/
/*}*/
</style>
