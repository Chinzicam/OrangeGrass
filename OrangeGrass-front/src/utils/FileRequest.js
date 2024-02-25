import axios from 'axios'

// 创建axios实例
const Fileservice = axios.create({
  // baseURL: process.env.NODE_ENV === 'production' ? process.env.VUE_APP_BASE_API : '/', // api 的 base_url
  baseURL: "http://127.0.0.1:8989/",
  // timeout: Config.timeout // 请求超时时间
  timeout: 1000000000
})


Fileservice.interceptors.request.use(
  config => {
    if (localStorage.getItem('token')) {
      config.headers['Authorization'] = localStorage.getItem('token') // 让每个请求携带自定义token 请根据实际情况自行修改
    }
    return config
  },
  error => {
    Promise.reject(error)
  }
)

Fileservice.interceptors.response.use(
  response => {
    console.log(response)
    let res = response.data
    if (res.status === 602) {
      this.$message.error(res.msg)
      router.push({ path: '/' })
    }
    if (res.status === 777) {
      this.$message.error(res.msg)
      router.push({ path: '/' })
    }
    return res
  },
  error => {
    let res = error
    return res
  }
)


// Fileservice.interceptors.response.use(
//   response => {
//     return response
//   },
//   error => {
//     // 兼容blob下载出错json提示
//     if (error.response.data instanceof Blob && error.response.data.type.toLowerCase().indexOf('json') !== -1) {
//       const reader = new FileReader()
//       reader.readAsText(error.response.data, 'utf-8')
//       reader.onload = function(e) {
//         const errorMsg = JSON.parse(reader.result).message
//         Notification.error({
//           title: errorMsg,
//           duration: 5000
//         })
//       }
//     } else {
//       let code = 0
//       try {
//         code = error.response.data.status
//       } catch (e) {
//         if (error.toString().indexOf('Error: timeout') !== -1) {
//           Notification.error({
//             title: '网络请求超时',
//             duration: 5000
//           })
//           return Promise.reject(error)
//         }
//       }
//       console.log(code)
//       if (code) {
//         if (code === 401) {
//           store.dispatch('LogOut').then(() => {
//             // 用户登录界面提示
//             Cookies.set('point', 401)
//             location.reload()
//           })
//         } else if (code === 403) {
//           router.push({ path: '/401' })
//         } else {
//           const errorMsg = error.response.data.message
//           if (errorMsg !== undefined) {
//             Notification.error({
//               title: errorMsg,
//               duration: 5000
//             })
//           }
//         }
//       } else {
//         Notification.error({
//           title: '接口请求失败',
//           duration: 5000
//         })
//       }
//     }
//     return Promise.reject(error)
//   }
// )
export default Fileservice
