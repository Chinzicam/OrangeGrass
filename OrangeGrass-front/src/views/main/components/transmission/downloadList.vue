<template>
  <div class="main">
    <div class="downloadList">

      <div v-for="file in this.downloadingFileList">
        <div class="downloading">
          <span class="fileName">{{ file.name }}</span>
          <span class="fileSize">{{ formatSize(file.size) }}</span>
          <span class="downloadSpeed" v-if="!isVip">{{ file.downloadSpeed }}</span>
          <span class="downloadSpeed"  v-if="isVip">
            <span style="color: #FFBA00;margin-right: 20px">{{'超级会员尊享加速～'}}</span>
            <span v-if="file.downloadPersentage != 100 && !file.downloadingStop" >{{randomSpeed}}</span>
            <span style="color: #FFBA00">{{file.downloadSpeed}}</span>
          </span>

          <div class="progress">
            <span>下载进度：</span>

            <el-progress
              :text-inside="true"
              :stroke-width="16"
              :percentage="file.downloadPersentage"
            >
            </el-progress>

            <span style="padding-left: 140px" v-if="file.downloadPersentage<100"  link @click="changeDownloadStop(file)">
               <img width="16px" height="16px" src="@/assets/images/start.png" v-if="file.downloadingStop == false">
                  <!--            <el-icon size="20" v-if="uploadFile.uploadingStop == false"-->
                  <!--            >好啊</el-icon>-->
               <img width="16px" height="16px" src="@/assets/images/pause.png" v-else>
                  <!--            <el-icon size="20" v-else>不好</el-icon>-->
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Fileservice from "../../../../utils/FileRequest";
import {getChunkDownloadNewPath} from "../../../../main";
const request = Fileservice

export default {
  name: 'downloadList',
  props: {
    currentFolder: Object,
    userId: String,
    downloadingFileList: Array,
    isVip: Boolean
  },
  data() {
    return {
      limitRate: 0,
      randomSpeed: '30KB/S',
      timer: null
    }
  },
  created() {
    this.getSpeedLimitRate()
    this.setRandomSpeed()
  },
  methods:
    {
      setRandomSpeed() {
        let that = this
        this.timer = setInterval(()=>{
          that.randomSpeed = Math.floor(Math.random() * 70) + 'KB/S + '
        },1000)
      },
      getSpeedLimitRate() {
        Fileservice({
          url: 'api/system/speed',
          method: 'get'
        }).then(res=>{
          if (res.code === 1) {
            this.limitRate = res.data
          }
        })
      },
    //换算文件的大小单位
    formatSize(val) {
      if(val === 0) return "0 B"
      let k = 1024;
      let sizes = ['B','KB','MB','GB','PB','TB','EB','ZB','YB'],
        i = Math.floor(Math.log(val) / Math.log(k));
      return (val / Math.pow(k,i)).toPrecision(3) + "" + sizes[i]
    },
    //点击暂停下载
    changeDownloadStop(file) {
      file.downloadingStop = !file.downloadingStop;
      if (!file.downloadingStop) {
        this.downloadChunk(1, file);
      }
    },
    //点击下载文件分片
    async downloadChunk(index, file) {
      await this.getSpeedLimitRate()
      let chunkSize = this.limitRate * 1024;
      let chunkTotal = Math.ceil(file.size / chunkSize);
      console.log(file.size,chunkSize)

      if (index <= chunkTotal) {
        // console.log("下载进度",index);
        let exit = file.chunkList.includes(index);

        if (!exit) {
          if (!file.downloadingStop) {
            let formData = new FormData();
            formData.append("fileName", file.name);
            formData.append("u2fId", file.id);
            formData.append("chunkSize", chunkSize);
            formData.append("index", index);
            formData.append("chunkTotal", chunkTotal);
            formData.append("userId",localStorage.getItem('userId'))
            if (index * chunkSize >= file.size) {
              chunkSize = file.size - (index - 1) * chunkSize;
              formData.set("chunkSize", chunkSize);
            }

            let startTime = new Date().valueOf();

            axios({
              url: getChunkDownloadNewPath(),
              method: "post",
              data: formData,
              responseType: "blob",
              timeout: 50000,
              headers: {
                'Content-Type': 'application/json;charset=UTF-8', // 将表单数据传递转化为form-data类型
                Authorization: localStorage.getItem('token')
              }
            }).then((res) => {
              file.chunkList.push(index);
              let endTime = new Date().valueOf();
              let timeDif = (endTime - startTime) * 1000 ;
              file.downloadSpeed = (chunkSize / timeDif).toFixed(1) + " M/s";
              //todo
              file.downloadPersentage = parseInt((index / chunkTotal) * 100);
              // var chunk = res.data.data.chunk
              // const blob = new Blob([res.data]);
              const blob = res.data;

              file.blobList.push(blob);
              // console.log("res", blobList);
              if (index == chunkTotal) {
                let resBlob = new Blob(file.blobList, {
                  type: "application/octet-stream",
                });
                // console.log("resb", resBlob);

                let url = window.URL.createObjectURL(resBlob); // 将获取的文件转化为blob格式
                let a = document.createElement("a"); // 此处向下是打开一个储存位置
                a.style.display = "none";
                a.href = url;
                // 下面两行是自己项目需要的处理，总之就是得到下载的文件名（加后缀）即可

                let fileName = file.name;

                a.setAttribute("download", fileName);
                document.body.appendChild(a);
                a.click(); //点击下载
                document.body.removeChild(a); // 下载完成移除元素
                window.URL.revokeObjectURL(url); // 释放掉blob对象
              }

              this.downloadChunk(index + 1, file);
            });
          }
        } else {
          file.downloadPersentage = parseInt((index / chunkTotal) * 100);
          this.downloadChunk(index + 1, file);
        }
      }
    }
  }
}

</script>

<style  scoped>
.main {
  display: flex;
}
.fileList {
  width: 400px;
}
.downloadList {
  width: 100%;
  height: 350px;
  overflow-y: scroll;
}
.downloadList::-webkit-scrollbar {
  width: 4px;
}
.downloadList::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: rgba(0,0,0,0.2);
}
.downloadList::-webkit-scrollbar-track {
  border-radius: 0;
  background: rgba(0,0,0,0.1);
}
.title {
  margin-top: 5px;
  margin-bottom: 5px;
}
.downloading {
  margin-top: 10px;
}
.downloading .fileName {
  margin-left: 76px;
  margin-right: 30px;
}
.downloading .fileSize {
  /* margin-left: 70px; */
  margin-right: 30px;
}
.downloading .progress {
  display: flex;
}
.progress .el-progress {
  /* font-size: 18px; */
  width: 310px;
}
</style>
