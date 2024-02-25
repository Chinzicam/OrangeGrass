// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui' //element-ui的全部组件
import 'element-ui/lib/theme-chalk/index.css'//element-ui的css

// import './assets/css/main.css'
// import './assets/css/color-dark.css'
// import './assets/css/icon.css'
// import './assets/css/icon/iconfont.css'

import store from "./store";
import globalConst from './utils/globalConst'
import datetime from "./utils/datetime";
import Viewer from 'v-viewer'
import 'viewerjs/dist/viewer.css'

import uploader from 'vue-simple-uploader'
import userFileManagement from "./views/management/userFileManagement";
import permissionManagement from "./views/management/permission";
import appealManagement from "./views/management/appealManagement";
import reportManagement from "./views/management/reportManagement";
import logManagement from "./views/management/logManagement";
import systemConfig from "./views/management/systemConfig";
import Fileservice from "./utils/FileRequest";

//用于网络请求
let targetIp = 'localhost:8989'

//用于生成分享链接
let front = 'localhost:8089'

export function getUploadChunkSize() {
  return 1024 * 256; //256KB
  // return 1024 * 1024 *2; //2MB
}

export function getThumbnailPathGlobal (val) {
  return 'http://' + targetIp + '/api/thumbnail/' + val
}
export function getThumbnailPathForAdminGlobal (val) {
  return 'http://' + targetIp + '/api/thumbnail/admin/' + val
}
export function getRecycleImg (val) {
  return 'http://' + targetIp + '/api/file/download/recycle/' + val
}
export function getAvatarPathGlobal (val) {
  return 'http://' + targetIp + '/api/avatar/' + val
}
export function getChunkDownloadPath (val){
  return 'http://' + targetIp + '/api/file/chunkdownload/' + val
}
export function getChunkDownloadNewPath (){
  return 'http://' + targetIp + '/api/file/chunkdownload/new'
}
export function getDownloadPathGlobal (val){
  return 'http://' + targetIp + '/api/file/download/' + val
}
export function getDownloadPathForAdminGlobal (val){
  return 'http://' + targetIp + '/api/file/download/admin/' + val
}
export function getShareUrlGlobal (id,pwd) {
  return  'http://' + front + '/#/share?id=' + id + (pwd === undefined ? '' : ('&pwd=' + pwd))
}
export function getWebSocketUrlGlobal(val) {
  return 'ws://' + targetIp + '/api/websocket/' + val
}
export function getAdvertisementUrl(val) {
  return 'http://' + targetIp + '/api/advertise/video/' + val
}
export function getAdvertisementThumbnail(val) {
  return 'http://' + targetIp + '/api/advertise/thumbnail/' + val
}


// 使用钩子函数对路由进行权限跳转
router.beforeEach((to, from, next) => {
  let x = ['用户文件管理','权限管理','申诉管理','举报管理','日志管理','系统设置','图片管理']

  if (to.name === 'login' || to.name === 'Login') {
    next()
  } else if (to.meta.title && x.includes(to.meta.title)) {
    Fileservice({
      url: 'api/user/isadmin',
      method: 'get'
    }).then(res=>{
      if (res.code === 1) {
        if (res.data === true) {
          next()
        } else {
          alert("无权限")
        }
      }
    })
  } else {
    next()
  }

})

Vue.use(uploader)
Viewer.setDefaults({
  url: "data-source",// 大图地址参数
  filter(image) {
    // 只有大图的才能放大
    let dataSource = image.getAttribute('data-source')
    return true
  }
});
Vue.use(Viewer)
Vue.use(datetime)
Vue.use(ElementUI) //使用elementUI

Vue.use(globalConst);

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
