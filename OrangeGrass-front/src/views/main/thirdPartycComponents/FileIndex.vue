<template>
  <div class="index">
    <el-tabs v-model="activeName">
      <el-tab-pane label="上传" name="upload">
        <FileUpload />
      </el-tab-pane>
      <el-tab-pane label="下载" name="download">
        <FileDownload />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import FileUpload from "./FileUpload.vue";
import FileDownload from "./FileDownload.vue";
import { ref } from "vue";
var activeName = ref("upload");
</script>

<style scoped>
.index {
  width: 800px;
  margin: 0 auto;
}
</style>