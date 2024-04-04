<template>
    <div class="header">
        <!-- 折叠按钮 -->
        <div class="collapse-btn" @click="collapseChage">
            <i v-if="!collapse" class="el-icon-s-fold"></i>
            <i v-else class="el-icon-s-unfold"></i>
        </div>
        <div class="logo" style="cursor:pointer;"
             @click="()=>{this.$router.push('/main')}">
          🍊 橙 草 云
        </div>
        <div class="header-right">
            <div class="header-user-con">
                <!--实时时间-->
                <div class="time">
                  <el-button class="time_btn">
                    <span>{{nowDate}}</span>
                  </el-button>
                </div>
                <!-- 全屏显示 -->
                <div class="btn-fullscreen" @click="handleFullScreen()">
                    <el-tooltip effect="dark" :content="fullscreen?`取消全屏`:`全屏`" placement="bottom">
                        <i class="el-icon-rank"></i>
                    </el-tooltip>
                </div>
                <!-- 消息中心 -->
                <div class="btn-bell" v-show="false">
                    <el-tooltip
                        effect="dark"
                        :content="message?`有${message}条未读消息`:`消息中心`"
                        placement="bottom"
                    >
                        <router-link to="/tabs">
                            <i class="el-icon-bell"></i>
                        </router-link>
                    </el-tooltip>
                    <span class="btn-bell-badge" v-if="message"></span>
                </div>
                <!-- 用户头像 -->
                <div class="user-avator">
                    <el-avatar :src="getAvatarPath()" />
                </div>
                <!-- 用户名下拉菜单 -->
                <el-dropdown class="user-name" trigger="click" @command="handleCommand">
                    <span class="el-dropdown-link">
                        {{username}}
                        <i class="el-icon-caret-bottom"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                        <a href="https://github.com/lin-xin/vue-manage-system" target="_blank">
                            <el-dropdown-item>项目仓库</el-dropdown-item>
                        </a>
                        <el-dropdown-item command="loginout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </div>
    </div>
</template>
<script>
import bus from './bus'
import {getAvatarPathGlobal} from "../../../main";
// import WebSocket from '../../utils/WebSocket'
export default {
  data() {
    return {
      collapse: false,
      fullscreen: false,
      name: 'linxin',
      message: 2,
      nowDate: ''
    }
  },
  computed: {
    username() {
      const username = localStorage.getItem('username')
      return username || this.name
    }
  },
  created() {
    // WebSocket.Init(this.username)

  },
  methods: {
    getAvatarPath() {
      return getAvatarPathGlobal(localStorage.getItem('userId'))
    },
    // 实时时间
    // 时间格式化
    currentTime() {
      setInterval(this.formatDate, 500)
    },
    formatDate() {
      const date = new Date()
      const year = date.getFullYear() // 年
      const month = date.getMonth() + 1 // 月
      const day = date.getDate() // 日
      const week = date.getDay() // 星期
      const weekArr = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      let hour = date.getHours() // 时
      hour = hour < 10 ? '0' + hour : hour // 如果只有一位，则前面补零
      let minute = date.getMinutes() // 分
      minute = minute < 10 ? '0' + minute : minute // 如果只有一位，则前面补零
      let second = date.getSeconds() // 秒
      second = second < 10 ? '0' + second : second // 如果只有一位，则前面补零
      this.nowDate = `${year}/${month}/${day} ${hour}:${minute}:${second} ${weekArr[week]}`
    },
    // 用户名下拉菜单选择事件
    handleCommand(command) {
      console.log(command)
      if (command === 'loginout') {
        console.log('loginout')
        const user = {
          Username: localStorage.getItem('ms_username')
        }
        this.$axios.post('api/v1/user/logout', user).then(res => {
          console.log(res)
          if (res.data.code === 1) {
            this.$message.success('登出成功！')
            localStorage.removeItem('ms_username')
            localStorage.clear()
            this.$router.push('/login')
          }
        })
      }
    },
    // 侧边栏折叠
    collapseChage() {
      this.collapse = !this.collapse
      bus.$emit('collapse', this.collapse)
    },
    // 全屏事件
    handleFullScreen() {
      const element = document.documentElement
      if (this.fullscreen) {
        if (document.exitFullscreen) {
          document.exitFullscreen()
        } else if (document.webkitCancelFullScreen) {
          document.webkitCancelFullScreen()
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen()
        } else if (document.msExitFullscreen) {
          document.msExitFullscreen()
        }
      } else {
        if (element.requestFullscreen) {
          element.requestFullscreen()
        } else if (element.webkitRequestFullScreen) {
          element.webkitRequestFullScreen()
        } else if (element.mozRequestFullScreen) {
          element.mozRequestFullScreen()
        } else if (element.msRequestFullscreen) {
          // IE11
          element.msRequestFullscreen()
        }
      }
      this.fullscreen = !this.fullscreen
    }
  },
  mounted() {
    if (document.body.clientWidth < 1500) {
      this.collapseChage()
    };
    this.currentTime()
  },
  beforeDestroy() {
    if (this.formatDate) {
      clearInterval(this.formatDate)
    }
  }
}
</script>
<style scoped>
.time_btn {
  background: rgba(255, 255, 255, 0);
  color: white;
  border: 0;
}
.header {
    position: relative;
    box-sizing: border-box;
    width: 100%;
    height: 70px;
    font-size: 22px;
    color: #fff;
}
.collapse-btn {
    float: left;
    padding: 0 21px;
    cursor: pointer;
    line-height: 70px;
}
.header .logo {
    float: left;
    width: 250px;
    line-height: 70px;
}
.header-right {
    float: right;
    padding-right: 50px;
}
.header-user-con {
    display: flex;
    height: 70px;
    align-items: center;
}
.btn-fullscreen {
    transform: rotate(45deg);
    margin-right: 5px;
    font-size: 24px;
}
.btn-bell,
.btn-fullscreen {
    position: relative;
    width: 30px;
    height: 30px;
    text-align: center;
    border-radius: 15px;
    cursor: pointer;
}
.btn-bell-badge {
    position: absolute;
    right: 0;
    top: -2px;
    width: 8px;
    height: 8px;
    border-radius: 4px;
    background: #f56c6c;
    color: #fff;
}
.btn-bell .el-icon-bell {
    color: #fff;
}
.user-name {
    margin-left: 10px;
}
.user-avator {
    margin-left: 20px;
}
.user-avator img {
    display: block;
    width: 40px;
    height: 40px;
    border-radius: 50%;
}
.el-dropdown-link {
    color: #fff;
    cursor: pointer;
}
.el-dropdown-menu__item {
    text-align: center;
}
</style>
