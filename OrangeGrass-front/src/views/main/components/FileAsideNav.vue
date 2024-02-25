<template>
  <div class="file_aside_nav">
    <div class="file_aside_nav_myFiles" tabindex="1" @click="getMyFiles()">
      <svg style="height: 14px;width: 14px;padding-right: 20px" t="1674113834486" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2675" width="200" height="200"><path d="M787.2 380.8c-9.6-9.6-22.4-12.8-35.2-12.8l-480 3.2c-12.8 0-25.6 3.2-35.2 12.8-19.2 19.2-19.2 48 0 67.2l240 240c0 0 0 0 0 0 0 0 0 0 0 0 3.2 3.2 9.6 6.4 12.8 9.6 0 0 3.2 3.2 3.2 3.2 16 6.4 38.4 3.2 51.2-9.6l240-243.2C806.4 428.8 803.2 400 787.2 380.8z" p-id="2676"></path></svg>
      <span style="width: 40px;font-size: 14px" >我的文件</span>
      <div style="width: 20px"></div>
    </div>
    <div class="file_aside_nav_myPics" tabindex="1" @click="getMyPics()">
      <svg style="height: 14px;width: 14px" t="1674049227809" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="11449" width="200" height="200"><path d="M128 853.333333h768.064L896 170.666667H127.936L128 853.333333zM127.936 128h768.128C919.594667 128 938.666667 146.986667 938.666667 170.666667v682.666666c0 23.573333-19.029333 42.666667-42.602667 42.666667H127.936A42.56 42.56 0 0 1 85.333333 853.333333V170.666667c0-23.573333 19.029333-42.666667 42.602667-42.666667z m200.128 527.082667c22.890667-19.626667 68.48-36.416 98.794667-36.416h20.949333c40.533333 0 95.914667-20.437333 126.549333-46.698667l52.373334-44.885333c22.890667-19.626667 68.48-36.416 98.794666-36.416H810.666667a21.333333 21.333333 0 0 0 0-42.666667h-85.12c-40.533333 0-95.936 20.437333-126.570667 46.698667l-52.373333 44.885333C523.690667 559.210667 478.165333 576 447.786667 576h-20.949334c-40.490667 0-95.914667 20.437333-126.549333 46.698667L199.445333 709.12a21.333333 21.333333 0 1 0 27.776 32.384l100.842667-86.442667z" fill="#3D3D3D" p-id="11450"></path><path d="M352 373.333333m-53.333333 0a53.333333 53.333333 0 1 0 106.666666 0 53.333333 53.333333 0 1 0-106.666666 0Z" fill="#3D3D3D" p-id="11451"></path></svg>
      <p style="width: 40px;font-size: 14px">图片</p>
    </div>
    <div class="file_aside_nav_myDocs" tabindex="1" @click="getMyDocuments()">
      <svg style="height: 14px;width: 14px" t="1674047850987" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8787" width="200" height="200"><path d="M672 128 640 128 256 128C217.6 128 192 153.6 192 192l0 640c0 38.4 25.6 64 64 64l512 0c38.4 0 64-25.6 64-64L832 320 832 288 672 128zM800 819.2c0 25.6-19.2 51.2-51.2 51.2L268.8 870.4c-25.6 0-51.2-19.2-51.2-51.2L217.6 204.8c0-25.6 19.2-51.2 51.2-51.2L640 153.6 640 256c0 38.4 25.6 64 64 64l96 0L800 819.2zM716.8 288c-25.6 0-51.2-19.2-51.2-51.2L665.6 160l128 128L716.8 288z" p-id="8788"></path><path d="M691.2 416 332.8 416C326.4 416 320 409.6 320 403.2l0 0C320 390.4 326.4 384 332.8 384l352 0C697.6 384 704 390.4 704 403.2l0 0C704 409.6 697.6 416 691.2 416z" p-id="8789"></path><path d="M691.2 563.2 332.8 563.2C326.4 563.2 320 550.4 320 544l0 0c0-6.4 6.4-12.8 12.8-12.8l352 0c6.4 0 12.8 6.4 12.8 12.8l0 0C704 550.4 697.6 563.2 691.2 563.2z" p-id="8790"></path><path d="M499.2 704 332.8 704C326.4 704 320 697.6 320 684.8l0 0c0-6.4 6.4-12.8 12.8-12.8l160 0c6.4 0 12.8 6.4 12.8 12.8l0 0C512 697.6 505.6 704 499.2 704z" p-id="8791"></path></svg>
      <p style="width: 40px;font-size: 14px">文档</p>
    </div>
    <div class="file_aside_nav_myVideos" tabindex="1" @click="getMyVideos()">
      <svg style="height: 14px;width: 14px" t="1674049421601" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="12417" width="200" height="200"><path d="M743.7 552.6 426.6 764c-33.4 22.2-60.8 7.6-60.8-32.6L365.8 292.6c0-40.2 27.4-54.9 60.8-32.5l317.1 211.5C777.1 493.7 777.1 530.3 743.7 552.6L743.7 552.6 743.7 552.6z" fill="#272636" p-id="12418"></path></svg>
      <p style="width: 40px;font-size: 14px">视频</p>
    </div>
    <div class="file_aside_nav_myMusics" tabindex="1" @click="getMyMusics()">
      <svg style="height: 14px;width: 14px" t="1674050105212" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="16088" width="200" height="200"><path d="M330.057143 679.277714c-20.772571 12.013714-35.602286 30.811429-40.832 51.913143-10.587429 41.984 20.736 81.517714 70.345143 88.356572 20.297143 2.797714 38.144 3.2 53.430857 1.28 32.365714-4.114286 58.806857-19.017143 79.780571-45.513143 14.244571-18.285714 21.778286-39.862857 21.833143-61.988572l0.146286-81.225143c-91.922286 13.220571-153.965714 29.202286-184.685714 47.177143z m185.508571-482.596571c0.036571-12.763429 14.811429-18.742857 22.272-8.96l180.443429 217.691428c37.12 41.179429 48.585143 98.322286 27.666286 145.810286-16.347429 35.730286-48.530286 57.344-94.976 64.365714-8.009143 1.225143-15.853714-3.821714-17.517715-11.245714-1.645714-7.424 3.474286-14.427429 11.392-15.634286 37.193143-5.741714 61.549714-22.180571 74.368-49.846857 17.005714-37.814857 8.374857-83.328-21.046857-116.114285l-157.129143-188.233143 4.242286 479.085714c0.237714 27.501714-8.850286 54.290286-26.368 76.982857-25.6 32.493714-59.794286 51.675429-101.595429 56.868572-18.962286 2.377143-40.228571 1.865143-63.817142-1.389715-67.401143-9.289143-109.403429-62.537143-94.500572-119.04 7.332571-28.397714 27.483429-53.705143 55.424-69.906285 36.022857-21.211429 102.582857-38.4 200.393143-52.443429l0.749714-407.990857z" fill="#979797" p-id="16089"></path></svg>
      <p style="width: 40px;font-size: 14px">音频</p>
    </div>
    <div class="file_aside_nav_myOtherFiles" tabindex="1" @click="getMyOtherFiles()">
      <svg style="height: 14px;width: 14px" t="1674050341473" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="17031" width="200" height="200"><path d="M133.3 645.3C59.7 645.3 0 585.6 0 512c0-73.6 59.7-133.3 133.3-133.3 73.6 0 133.3 59.7 133.3 133.3 0 73.6-59.7 133.3-133.3 133.3zM512 645.3c-73.6 0-133.3-59.7-133.3-133.3 0-73.6 59.7-133.3 133.3-133.3 73.6 0 133.3 59.7 133.3 133.3 0 73.6-59.7 133.3-133.3 133.3zM890.7 645.3c-73.6 0-133.3-59.7-133.3-133.3 0-73.6 59.7-133.3 133.3-133.3 73.6 0 133.3 59.7 133.3 133.3 0 73.6-59.7 133.3-133.3 133.3z" p-id="17032"></path></svg>
      <p style="width: 40px;font-size: 14px">其他</p>
    </div>
    <div class="file_aside_nav_divider"></div>
    <div class="file_aside_nav_deleted" tabindex="1" @click="contentChange()">
      <p style="width: 40px;font-size: 14px">回收站</p>
    </div>
    <div style="margin-bottom: 10px" class="file_aside_nav_divider"></div>
    <div class="file_aside_nav_footer">
      <div>
        <storage></storage>
      </div>
    </div>
  </div>
</template>

<script>
import Storage from "./storage";
export default {
  name: "FileAsideNav",
  components: {Storage},
  props: {
    currentFolder: Object,
  },
  data(){
    return {

    }
  },
  methods: {
    contentChange(){
      this.$emit('contentChange',2)
    },
    getMyPics(){
      this.$emit('contentChange',1)
      this.$emit('setFiles','PICTURE',this.currentFolder.id)
    },
    getMyDocuments(){
      this.$emit('contentChange',1)
      this.$emit('setFiles','DOCUMENT',this.currentFolder.id)
    },
    getMyVideos(){
      this.$emit('contentChange',1)
      this.$emit('setFiles','VIDEO',this.currentFolder.id)
    },
    getMyFiles(){
      this.$emit('contentChange',1)
      this.$emit('setFiles','ALL',this.currentFolder.id)
    },
    getMyMusics(){
      this.$emit('contentChange',1)
      this.$emit('setFiles','MUSIC',this.currentFolder.id)
    },
    getMyOtherFiles(){
      console.log('other')
    }
  }
}
</script>

<style scoped>
.file_aside_nav {
  padding-left: 10px;
}
.file_aside_nav_myFiles {
  height: 40px;
  border-radius: 10px;
  font-weight: 700;
  color: #636d7e;
  background-color: #fff;
  cursor: pointer;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 1;
  margin-top: 20px;
}
.file_aside_nav_myFiles:focus
{
  color: #06a7ff;
  background-color: #eef9fe;
}
.file_aside_nav_myFiles:hover
{
  background-color: #fafafc;
}
.file_aside_nav_myDocs {
  height: 40px;
  border-radius: 10px;
  font-weight: 400;
  color: #636d7e;
  background-color: #fff;
  cursor: pointer;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 1;
}
.file_aside_nav_myDocs:focus
{
  color: #06a7ff;
  background-color: #eef9fe;
}
.file_aside_nav_myDocs:hover
{
  background-color: #fafafc;
}
.file_aside_nav_myMusics {
  height: 40px;
  border-radius: 10px;
  font-weight: 400;
  color: #636d7e;
  background-color: #fff;
  cursor: pointer;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 1;
}
.file_aside_nav_myMusics:focus
{
  color: #06a7ff;
  background-color: #eef9fe;
}
.file_aside_nav_myMusics:hover
{
  background-color: #fafafc;
}
.file_aside_nav_myVideos {
  height: 40px;
  border-radius: 10px;
  font-weight: 400;
  color: #636d7e;
  background-color: #fff;
  cursor: pointer;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 1;
}
.file_aside_nav_myVideos:focus
{
  color: #06a7ff;
  background-color: #eef9fe;
}
.file_aside_nav_myVideos:hover
{
  background-color: #fafafc;
}
.file_aside_nav_myOtherFiles {
  height: 40px;
  border-radius: 10px;
  font-weight: 400;
  color: #636d7e;
  background-color: #fff;
  cursor: pointer;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  text-align: center;
  align-items: center;
  justify-content: center;
  flex-shrink: 1;
}
.file_aside_nav_myOtherFiles:focus
{
  color: #06a7ff;
  background-color: #eef9fe;
}
.file_aside_nav_myOtherFiles:hover
{
  background-color: #fafafc;
}
.file_aside_nav_myPics {
  height: 40px;
  border-radius: 10px;
  font-weight: 400;
  color: #636d7e;
  background-color: #fff;
  cursor: pointer;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 1;
}
.file_aside_nav_myPics:focus
{
  color: #06a7ff;
  background-color: #eef9fe;
}
.file_aside_nav_myPics:hover
{
  background-color: #fafafc;
}
.file_aside_nav_deleted {
  height: 40px;
  border-radius: 10px;
  font-weight: 700;
  color: #636d7e;
  background-color: #fff;
  cursor: pointer;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 1;
}
.file_aside_nav_deleted:focus
{
  color: #06a7ff;
  background-color: #eef9fe;
}
.file_aside_nav_deleted:hover
{
  background-color: #fafafc;
}
.file_aside_nav_footer {
  padding-top: 530px;
}
.file_aside_nav_divider {
  display: inline-block;
  width: 70%;
  height: 1px;
  background-color: #f1f2f4;
}
</style>
