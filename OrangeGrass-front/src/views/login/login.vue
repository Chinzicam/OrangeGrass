<template>
  <div class="box">
    <div class="content">
      <div class="login-wrapper">
        <div class="mask"></div>
        <div class="login-content">
          <h1>
            <span style="color: #FF6666;">橙</span>
            <span style="color: #003399;">草</span>
            <span style="color: #006699;">云</span>
          </h1>
          <div class="title-text">
              您可以通过这个界面登录到系统。
          </div>
          <div class="other-login">
<!--            <img src="./asset/QQ.png" alt="">-->
            <span>欢迎使用本云盘</span>
          </div>
          <div class="login-form">
            <input type="text" class="user" placeholder="账号" v-model="user.username">
            <input type="password" class="password" placeholder="密码" v-model="user.password">
          </div>
          <button class="login-btn" @click="handleLogin()" style="cursor: pointer">登 录</button>
          <div class="tips">
            <span>还没有账号?</span>
            <span @click="toRegister()" style="cursor: pointer">注册</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../utils/FileRequest";
import axios from 'axios'

export default {
  name: "login",
  data() {
    return {
      timer: null,
      user : {
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
      console.log(this.user)
      Fileservice({
        url: 'api/user/login',
        method: 'post',
        data: this.user,
      }).then(res=>{
        console.log(res)
        if (res.code === 1){
          this.$message.success(res.msg)
          localStorage.setItem('userId',res.data.user.userId)
          localStorage.setItem('username',res.data.user.username)
          localStorage.setItem('token',res.data.token)
          if ('Login' === this.$router.currentRoute.name || this.$route.query.redirect === null){
            this.$router.push("/main")
          } else {
            this.$router.push(this.$route.query.redirect)
          }
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    toRegister() {
      this.$router.push('/register')
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
/*  background: url("./asset/bg2.png");*/
/*}*/
</style>
