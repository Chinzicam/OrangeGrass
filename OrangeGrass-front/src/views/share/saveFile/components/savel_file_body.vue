<template>
  <div class="save-file-body-body">
    <div class="db-main">
      <div class="db-left">
        <div class="share-header">
          <el-button type="primary" round @click="openDialog()">保存到我的网盘</el-button>
        </div>
        <div class="share-list">
          <share-file-viewer></share-file-viewer>
        </div>
      </div>
    </div>
    <div class="db-aside">
      <FromUserCard></FromUserCard>
    </div>
  </div>
</template>

<script>
import ShareFileViewer from "../../components/shareFileViewer";
import FromUserCard from "./fromUserCard";
export default {
  name: "savel_file_body",
  components: {FromUserCard, ShareFileViewer},
  methods: {
    openDialog() {
      this.$emit('dialogTrigger')
    }
  }
}
</script>

<style scoped>
.save-file-body-body {
  height: inherit;
}
.db-main {
  width: 100%;
  height: 100%;
  border-radius: 5px;
}
.db-left {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  background: #fff;
  width: inherit;
  margin: 0 230px 0 0;
  position: relative;
  padding: 0 10px;
  border-top-left-radius: 5px;
  min-width: 400px;
}
.share-header {
  padding-top: 200px;
}
.share-list {
  padding-top: 30px;
}
.db-aside {
  width: 215px;
  margin: 0 8px;
  position: absolute;
  top: 1px;
  right: -1px;
  height: 100%;
  background: #fff;
  border-top-right-radius: 5px;
}
</style>
