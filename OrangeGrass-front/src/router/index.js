import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import diskMain from "../views/main/diskMain";
import share from "../views/share/share";
import FileUpload from "../views/main/thirdPartycComponents/FileUpload"
import transmissionList from "../views/main/components/transmission/transmissionList";
import login from "../views/login/login";
import Register from '../views/register/register'
import saveFileMain from "../views/share/saveFile/saveFileMain";
import userFileManagement from "../views/management/userFileManagement/index"
import Home from "../views/management/components/Home";
import permissionManagement from "../views/management/permission/index"
import systemConfig from '../views/management/systemConfig/index'
import appealManagement from '../views/management/appealManagement/index'
import reportManagement from '../views/management/reportManagement/index'
import logManagement from '../views/management/logManagement/index'
import shareBanned from "../views/share/components/shareBanned";
import shareCanceled from "../views/share/components/shareCanceled";
import nsfwFileManagement from "../views/management/nsfwFileManagement/index"
import advertisementManagement from "../views/management/advertisementManagement/index";

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/helloworld',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path: '/main',
      name: "Disk",
      component: diskMain
    },
    {
      path: '/',
      name: 'Login',
      component: login
    },
    {
      path: '/login',
      name: 'login',
      component: login
    },
    {
      path: '/share',
      name: 'Share',
      component: share,
    },
    {
      path: '/share/banned',
      name: 'banned',
      component: shareBanned
    },
    {
      path: '/share/canceled',
      name: 'canceled',
      component: shareCanceled
    },
    {
      path: '/fileupload',
      name: 'test',
      component: FileUpload
    },
    {
      path: '/tran',
      name: 'tran',
      component: transmissionList
    },
    {
      path: '/test',
      name: 'test',
      component: login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/save',
      name: 'save',
      component: saveFileMain
    },
    {
      path: '/admin',
      name: '后台管理',
      hidden: true,
      component: Home,
      children: [
        {
          path: '/userfile',
          component: userFileManagement,
          name: '用户文件管理',
          meta: { title: '用户文件管理'}
        },
        {
          path: '/permission',
          component: permissionManagement,
          name: '权限管理',
          meta: { title: '权限管理'}
        },
        {
          path: '/images',
          name: 'images',
          component: nsfwFileManagement,
          meta: { title: '图片管理'}
        },
        {
          path: '/appeal',
          component: appealManagement,
          name: '申诉管理',
          meta: { title: '申诉管理'}
        },
        {
          path: '/report',
          component: reportManagement,
          name: '举报管理',
          meta: { title: '举报管理'}
        },
        {
          path: '/advertise',
          component: advertisementManagement,
          name: '广告管理',
          meta: { title: '广告管理'}
        },
        {
          path: '/log',
          component: logManagement,
          name: '日志管理',
          meta: { title: '日志管理'}
        },
        {
          path: '/config',
          component: systemConfig,
          name: '系统设置',
          meta: {title: '系统设置'}
        }
      ]
    }
  ]
})
