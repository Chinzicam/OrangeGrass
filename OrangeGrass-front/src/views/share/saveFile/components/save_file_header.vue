<template>
  <div class="layoutHeader">
    <div class="module_header">
      <div class="module_header_wrapper">
        {{this.username}}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "save_file_header",
  props: {
    username: String
  }
}
</script>

<style scoped>
.module_header {
  position: relative;
  z-index: 45 !important;
  border-bottom: 1px solid #dee9f5;
}
.module_header_wrapper {
  height: 62px;
  position: relative;
  background: #fff;
  box-shadow: 0 2px 6px 0 rgb(0 0 0 / 5%);
}
</style>
