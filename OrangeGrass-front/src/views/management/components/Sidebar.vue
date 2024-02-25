/* eslint-disable vue/no-async-in-computed-properties */
/* eslint-disable vue/no-async-in-computed-properties */
/* eslint-disable vue/no-async-in-computed-properties */
<template>
    <div class="sidebar">
        <el-menu
            class="sidebar-el-menu"
            :default-active="onRoutes"
            :collapse="collapse"
            background-color="#324157"
            text-color="#bfcbd9"
            active-text-color="#20a0ff"
            unique-opened
            router
        >
            <template v-for="item in items">
                <template v-if="item.subs">
                    <el-submenu :index="item.index + ''" :key="item.index">
                        <template slot="title">
                            <i :class="item.icon"></i>
                            <span slot="title">{{ item.title }}</span>
                        </template>
                        <template v-for="subItem in item.subs">
                            <el-submenu
                                v-if="subItem.subs"
                                :index="subItem.index"
                                :key="subItem.index"
                            >
                                <template slot="title">{{ subItem.title }}</template>
                                <el-menu-item
                                    v-for="(threeItem,i) in subItem.subs"
                                    :key="i"
                                    :index="threeItem.index"
                                >{{ threeItem.title }}</el-menu-item>
                            </el-submenu>
                            <el-menu-item
                                v-else
                                :index="subItem.index"
                                :key="subItem.index"
                            >{{ subItem.title }}</el-menu-item>
                        </template>
                    </el-submenu>
                </template>
                <template v-else>
                    <el-menu-item :index="item.index" :key="item.index">
                        <i :class="item.icon"></i>
                        <span slot="title">{{ item.title }}</span>
                    </el-menu-item>
                </template>
            </template>
        </el-menu>
    </div>
</template>

<script>
import bus from './bus'
export default {
  mounted() {
    // this.getItem()
  },
  methods: {
    getItem() {
      this.user.Username = localStorage.getItem('username')
      this.user.Usertype = localStorage.getItem('Usertype')
      const token = localStorage.getItem('token')
      const header = {
        headers: {
          // "Access-Control-Allow-Origin": "*", //解决cors头问题
          // "Access-Control-Allow-Credentials": "true", //解决session问题
          'Content-Type':
            'application/json;charset=UTF-8', // 将表单数据传递转化为form-data类型
          Authorization: token
        },
        withCredentials: true // 此处不使用，采用nginx 时，post接收不了数据
      }
      const url = 'api/v1/menu/' + localStorage.getItem('Usertype')
      this.$axios.get(url, header).then(res => {
        console.log(res)
        this.items = res.data.MyMenu
        const url = 'api/v1/Authorities/' + localStorage.getItem('ms_username')
        this.$axios.get(url, header).then(res => {
          if (res.data.code === 1) {
            console.log(res.data.permissions)
            localStorage.setItem('btnPemission', JSON.stringify(res.data.permissions))
          }
        })
      })
      return 1
    }
  },
  data() {
    return {
      user: { Username: '' },
      collapse: false,
      items: [
        {
          title: '用户管理',
          index: 'userfile',
          icon: 'el-icon-lx-home',
        },
        {
          title: '权限管理',
          index: 'permission',
          icon: 'el-icon-lx-home',
        },
        {
          title: '图片管理',
          index: 'images',
          icon: 'el-icon-lx-home'
        },
        {
          title: '申诉管理',
          index: 'appeal',
          icon: 'el-icon-lx-home',
        },
        {
          title: '举报管理',
          index: 'report',
          icon: 'el-icon-lx-home',
        },
        {
          title: '广告管理',
          index: 'advertise',
          icon: 'el-icon-lx-home',
        },
        {
          title: '日志管理',
          index: 'log',
          icon: 'el-icon-lx-home',
        },
        {
          title: '系统设置',
          index: 'config',
          icon: 'el-icon-lx-home',
        }
      ]
    }
  },
  computed: {
    onRoutes() {
      return this.$route.path.replace('/', '')
    }
  },
  created() {
    // 通过 Event Bus 进行组件间通信，来折叠侧边栏
    bus.$on('collapse', msg => {
      this.collapse = msg
      bus.$emit('collapse-content', msg)
    })
  }
}
</script>

<style scoped>
.sidebar {
    display: block;
    position: absolute;
    left: 0;
    top: 70px;
    bottom: 0;
    /* overflow-y: scroll; */
}
.sidebar::-webkit-scrollbar {
    width: 0;
}
.sidebar-el-menu:not(.el-menu--collapse) {
    width: 250px;
}
.sidebar > ul {
    height: 100%;
}
</style>
