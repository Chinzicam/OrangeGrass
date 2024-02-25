<template>
  <div class="main">
    <div class="fileList">
      <div class="title">
        文件列表
        <!-- <hr> -->
      </div>

      <el-table :data="fileList" border style="width: 360px">
        <el-table-column prop="name" label="文件名" width="150">
        </el-table-column>

        <el-table-column prop="size" label="文件大小" width="110">
          <template #default="scope">
            {{ formatSize(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column prop="" label="操作" width="100">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="downloadFile(scope.row)"
              >下载</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="downloadList">
      <el-divider content-position="left">下载列表</el-divider>

      <div v-for="file in downloadingFileList">
        <div class="downloading">
          <span class="fileName">{{ file.name }}</span>
          <span class="fileSize">{{ formatSize(file) }}</span>
          <span class="downloadSpeed">{{ file.downloadSpeed }}</span>

          <div class="progress">
            <span>下载进度：</span>

            <el-progress
              :text-inside="true"
              :stroke-width="16"
              :percentage="file.downloadPersentage"
            >
            </el-progress>

            <el-button circle link @click="changeDownloadStop(file)">
              <el-icon size="20" v-if="file.downloadingStop == false"
                ><VideoPause
              /></el-icon>
              <el-icon size="20" v-else><VideoPlay /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { ref, reactive, getCurrentInstance } from "vue";
import emitter from "../../../utils/eventBus";
import Fileservice from "../../../utils/FileRequest";
const { appContext } = getCurrentInstance();
const request = Fileservice
var fileList = reactive([]);
var downloadingFileList = ref([]);
//上传文件之后，重新加载文件列表
emitter.on("reloadFileList", () => {
  load();
});
function load() {
  fileList.length = 0;
  request({
    url: "/fileList",
    method: "get",
  }).then((res) => {
    // console.log("res", res.data.data);
    fileList.push(...res.data.data);
  });
}
load();

//换算文件的大小单位
function formatSize(file) {
  //console.log("size",file.size);
  var size = file.size;
  var unit;
  var units = [" B", " K", " M", " G"];
  var pointLength = 2;
  while ((unit = units.shift()) && size > 1024) {
    size = size / 1024;
  }
  return (
    (unit === "B"
      ? size
      : size.toFixed(pointLength === undefined ? 2 : pointLength)) + unit
  );
}
//点击暂停下载
function changeDownloadStop(file) {
  file.downloadingStop = !file.downloadingStop;
  if (!file.downloadingStop) {

    downloadChunk(1, file);
  }
}
//点击下载文件
function downloadFile(file) {
  // console.log("下载", file);
  file.downloadingStop = false;
  file.downloadSpeed = "0 M/s";
  file.downloadPersentage = 0;
  file.blobList = [];
  file.chunkList = [];
  downloadingFileList.value.push(file);

  downloadChunk(1, file);
}
//点击下载文件分片
function downloadChunk(index, file) {
  var chunkSize = 1024 * 1024 * 5;
  var chunkTotal = Math.ceil(file.size / chunkSize);

  if (index <= chunkTotal) {
    // console.log("下载进度",index);
    var exit = file.chunkList.includes(index);
    console.log("存在", exit);

    if (!exit) {
      if (!file.downloadingStop) {
        var formData = new FormData();
        formData.append("fileName", file.name);
        formData.append("md5", file.md5);
        formData.append("chunkSize", chunkSize);
        formData.append("index", index);
        formData.append("chunkTotal", chunkTotal);
        if (index * chunkSize >= file.size) {
          chunkSize = file.size - (index - 1) * chunkSize;
          formData.set("chunkSize", chunkSize);
        }

        var startTime = new Date().valueOf();

        axios({
          url: "http://localhost:9001/download",
          method: "post",
          data: formData,
          responseType: "blob",
          timeout: 50000,
        }).then((res) => {
          file.chunkList.push(index);
          var endTime = new Date().valueOf();
          var timeDif = (endTime - startTime) / 1000;
          file.downloadSpeed = (5 / timeDif).toFixed(1) + " M/s";
          //todo
          file.downloadPersentage = parseInt((index / chunkTotal) * 100);
          // var chunk = res.data.data.chunk
          // const blob = new Blob([res.data]);
          const blob = res.data;

          file.blobList.push(blob);
          // console.log("res", blobList);
          if (index == chunkTotal) {
            var resBlob = new Blob(file.blobList, {
              type: "application/octet-stream",
            });
            // console.log("resb", resBlob);

            let url = window.URL.createObjectURL(resBlob); // 将获取的文件转化为blob格式
            let a = document.createElement("a"); // 此处向下是打开一个储存位置
            a.style.display = "none";
            a.href = url;
            // 下面两行是自己项目需要的处理，总之就是得到下载的文件名（加后缀）即可

            var fileName = file.name;

            a.setAttribute("download", fileName);
            document.body.appendChild(a);
            a.click(); //点击下载
            document.body.removeChild(a); // 下载完成移除元素
            window.URL.revokeObjectURL(url); // 释放掉blob对象
          }

          downloadChunk(index + 1, file);
        });
      }
    } else {
      file.downloadPersentage = parseInt((index / chunkTotal) * 100);
      downloadChunk(index + 1, file);
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
  width: 450px;
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
