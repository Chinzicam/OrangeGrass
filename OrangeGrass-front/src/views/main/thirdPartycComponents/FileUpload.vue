<template>
  <div class="main">
    <!-- 文件上传按钮 -->
    <div class="upload-button">
      <el-upload
        action="#"
        :http-request="upload"
        :before-upload="beforeUpload"
        :show-file-list="false"
      >
        <el-button type="primary">选择上传文件</el-button>
      </el-upload>
    </div>

    <el-divider content-position="center">上传列表</el-divider>
    <!-- 正在上传的文件列表 -->
    <div class="upload-list">
      <div class="uploading" v-for="uploadFile in this.uploadFileList">
        <div class="file-info" style="display: flex">
          <span class="fileName" style="display:flex;justify-content: flex-start;align-items: center;font-size: 12px">{{ uploadFile.name }}</span>
          <span class="fileSize" style="font-size: 12px;display: flex;justify-content: flex-end;align-items: center">{{ formatSize(uploadFile.size) }}</span>
        </div>

        <div class="parse">
          <span>解析进度： </span>
          <el-progress
            :text-inside="true"
            :stroke-width="16"
            :percentage="uploadFile.parsePercentage"
          >
          </el-progress>
        </div>
        <div class="progress">
          <span>上传进度：</span>

          <el-progress
            :text-inside="true"
            :stroke-width="16"
            :percentage="uploadFile.uploadPercentage"
          >
          </el-progress>
          <span
            v-if="
            (uploadFile.uploadPercentage > 0) &
            (uploadFile.uploadPercentage < 100)
          "
          >
          <span style="width: 16px;height: 16px" class="uploadSpeed">{{ uploadFile.uploadSpeed }}</span>

          <span  link @click="changeUploadingStop(uploadFile)">
            <img width="16px" height="16px" src="@/assets/images/start.png" v-if="uploadFile.uploadingStop == false">
            <!--            <el-icon size="20" v-if="uploadFile.uploadingStop == false"-->
            <!--            >好啊</el-icon>-->
            <img width="16px" height="16px" src="@/assets/images/pause.png" v-else>
            <!--            <el-icon size="20" v-else>不好</el-icon>-->
          </span>
        </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import emitter from "../../../utils/eventBus";
import SparkMD5 from "spark-md5";
import { ref, reactive, getCurrentInstance, nextTick } from "vue";
import Fileservice from "../../../utils/FileRequest";
import {getUploadChunkSize} from "../../../main";
const request = Fileservice
export default {
  props: {
    currentFolder: Object,
    userId: String,
    uploadFileList: Array
  },
  data () {
    return {
      // uploadFileList: [{
      //   type: String,
      //   name: String,
      //   size: Number,
      //   md5: String,
      //   parsePercentage: 0,
      //   uploadPercentage: 0,
      //   uploadSpeed: "0 M/s",
      //   chunkList: [],
      //   file: {},
      //   uploadingStop: Boolean
      // }]
    }
  },
  created() {
    // this.uploadFileList.pop()
  },
  methods : {
    //换算文件的大小单位
    formatSize(val) {
      if(val === 0) return "0 B"
      let k = 1024;
      let sizes = ['B','KB','MB','GB','PB','TB','EB','ZB','YB'],
        i = Math.floor(Math.log(val) / Math.log(k));
      return (val / Math.pow(k,i)).toPrecision(3) + "" + sizes[i]
    },
    //计算文件的md5值
    computeMd5(file, uploadFile) {
      return new Promise((resolve, reject) => {
        //分片读取并计算md5
        console.log("正在计算md5")
        let chunkTotal = 100; //分片数
        let chunkSize = Math.ceil(file.size / chunkTotal);
        let fileReader = new FileReader();
        let md5 = new SparkMD5.ArrayBuffer();
        let index = 0;
        let loadFile = (uploadFile) => {
          uploadFile.parsePercentage = parseInt((index / file.size) * 100);
          const slice = file.slice(index, index + chunkSize);

          fileReader.readAsArrayBuffer(slice);
        };
        loadFile(uploadFile);
        fileReader.onload = (e) => {
          md5.append(e.target.result);
          if (index < file.size) {
            index += chunkSize;
            loadFile(uploadFile);
          } else {
            // md5.end() 就是文件md5码
            resolve(md5.end());
          }
        };
      });

    },
    //检查文件是否存在
    checkFile(name,md5) {
        return request({
          url: "/api/file/check",
          method: "get",
          params: {
            folderId: this.currentFolder.id,
            fileName: name,
            md5: md5,
            userId: localStorage.getItem('userId')
          },
        });
    },
    //文件上传之前,el-upload自动触发
    async beforeUpload(file) {
      console.log("2.上传文件之前");

      let uploadFile = {};
      uploadFile.name = file.name;
      uploadFile.size = file.size;
      uploadFile.parsePercentage = 0;
      uploadFile.uploadPercentage = 0;
      uploadFile.uploadSpeed = "0 M/s";
      uploadFile.chunkList = new Array();
      uploadFile.file = file;
      uploadFile.uploadingStop = false;

      this.$emit('addFile',uploadFile)
      // this.uploadFileList.push(uploadFile);

      let md5 = await this.computeMd5(file, uploadFile)//async 和 await配可以实现等待异步函数计算完成
      uploadFile.md5 = md5;
      console.log(uploadFile.md5)
      let res = await this.checkFile(uploadFile.name,md5);  //上传服务器检查，以确认是否秒传
      console.log(res)
      let data = res.data;
      if (!data.isUploaded) {
        console.log("未上传")
        uploadFile.chunkList = data.chunkList;
        uploadFile.needUpload = true;
        // this.$emit('addFile',uploadFile)
      } else {
        console.log("已经上传")
        uploadFile.needUpload = false;
        uploadFile.uploadPercentage = 100;
        // this.$emit('addFile',uploadFile)
        console.log("文件已秒传");
        this.$emit('setFile')
        this.$message.success("文件已秒传")
      }


    },
    //点击暂停或开始上传
    changeUploadingStop(uploadFile) {
      uploadFile.uploadingStop = !uploadFile.uploadingStop;
      if (!uploadFile.uploadingStop) {
          this.uploadChunk(uploadFile.file, 1, uploadFile);
      }
    },
    //上传文件,替换el-upload的action
    upload(xhrData) {
      let uploadFile = null;
      for (let i = 0; i < this.uploadFileList.length; i++) {
        if (
          (xhrData.file.name == this.uploadFileList[i].name) &
          (xhrData.file.size == this.uploadFileList[i].size)
        ) {
          this.$emit('updateFile',i,xhrData.file.type)
          // this.uploadFileList[i].type = xhrData.file.type
          console.log(this.uploadFileList[i].type)
          uploadFile = this.uploadFileList[i];

          break;
        }
      }
      if (uploadFile.needUpload) {
        console.log("3.上传文件");

        // 分片上传文件
        // 确定分片的大小
        // this.uploadChunk(xhrData.file, 1, uploadFile);
        if (xhrData.file.size > 1024 * 1024 * 5) {
          this.$message.error("服务器容量有限，请上传5MB以下文件")
        } else {
          this.uploadChunk(xhrData.file, 1, uploadFile);
        }
      }
},
    //上传文件分片
    uploadChunk(file, index, uploadFile) {
      let chunkSize = getUploadChunkSize()
      let chunkTotal = Math.ceil(file.size / chunkSize);
      if (index <= chunkTotal) {
        // 根据是否暂停，确定是否继续上传

        // console.log("4.上传分片");

        let startTime = new Date().valueOf();


        let exit = uploadFile.chunkList.includes(index);
        // console.log("是否存在",exit);


        if (!exit) {
          //    console.log("3.3上传文件",uploadingStop);
          if (!uploadFile.uploadingStop) {
            // 分片上传，同时计算进度条和上传速度
            // 已经上传的不在上传、
            // 上传完成后提示，上传成功
            // console.log("上传分片1",index);
            let form = new FormData();
            let start = (index - 1) * chunkSize;
            let end =
              index * chunkSize >= file.size ? file.size : index * chunkSize;
            let chunk = file.slice(start, end);
            //  downloadBlob(chunk,file)
            //  console.log("chunk",chunk);

            form.append("chunk", chunk);
            form.append("index", index);
            form.append("chunkTotal", chunkTotal);
            form.append("chunkSize", chunkSize);
            form.append("md5", uploadFile.md5);
            form.append("fileSize", file.size);
            form.append("fileName", file.name);

            form.append("fileType", uploadFile.type)
            form.append("folderId", this.currentFolder.id);
            form.append("userId", this.userId);
            // console.log("上传分片", index);

            request({
              url: "/api/file/upload/chunk",
              method: "post",
              data: form,
            }).then((res) => {
              if (res.code === 1) {
                let endTime = new Date().valueOf();
                let timeDif = (endTime - startTime) * 1000 ;
                // console.log("上传文件大小",formatSize(chunkSize));
                // console.log("耗时",timeDif);
                // console.log("then",index);

                // uploadSpeed = (chunkSize/(1024*1024))  / timeDif +" M / s"
                console.log(chunkSize/timeDif)
                uploadFile.uploadSpeed = (chunkSize / timeDif).toFixed(1) + " M/s";
                // console.log(res.data.data);
                //  console.log("f2",uploadFile);
                uploadFile.chunkList.push(index);
                //  console.log("f3",uploadFile);

                uploadFile.uploadPercentage = parseInt(
                  (uploadFile.chunkList.length / chunkTotal) * 100);
                // console.log("上传进度",uploadFile.uploadPercentage);

                if (index == chunkTotal) {
                  this.$emit('setFile')
                  this.$message.success("上传成功")
                }

                this.uploadChunk(file, index + 1, uploadFile);
              } else {
                this.$message.error(res.msg)
              }
            });
          }
        } else {
          uploadFile.uploadPercentage = parseInt(
            (uploadFile.chunkList.length / chunkTotal) * 100
          );

          this.uploadChunk(file, index + 1, uploadFile);
        }
        // }
      }
}
  }
}



</script>

<style  scoped>
.main {
  height: 100%;
  width: 100%;
  margin-top: 40px;
  margin-bottom: 40px;
}
.upload-button {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
}
.uploading {
  padding-top: 27px;
}
.progress {
  /* width: 700px; */
  display: flex;
}
.uploading .parse {
  display: flex;
}
.parse .el-progress {
  /* font-size: 18px; */
  width: 70%;
}
.progress .el-progress {
  /* font-size: 18px; */
  width: 70%;
}
.uploading .fileName {
  font-size: 17px;
  margin-right: 40px;
  margin-left: 80px;

  /* width: 80px; */
}
.uploading .fileSize {
  font-size: 17px;

  /* width: 80px; */
}

.progress .uploadSpeed {
  font-size: 17px;
  margin-left: 5px;
  padding-left: 5px;
  padding-right: 10px;
}

.upload-list {
  height: 300px;
  overflow: scroll;
  overflow-x: hidden;
}

.upload-list::-webkit-scrollbar {
  width: 4px;
}
.upload-list::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: rgba(0,0,0,0.2);
}
.upload-list::-webkit-scrollbar-track {
  border-radius: 0;
  background: rgba(0,0,0,0.1);
}

</style>
