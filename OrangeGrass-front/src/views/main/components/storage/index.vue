<template>
  <div class="disk-main-storage">
    <div class="disk-main-storage-progress">
      <el-progress
        :color="colors"
        :show-text="false"
        :percentage="parseInt((userStorage.usedStorage / userStorage.storage * 100)+'')"></el-progress>
    </div>
    <div class="disk-main-storage-footer">
      <div class="disk-main-storage-percentage">
        <span>{{getSize(userStorage.usedStorage)}}/{{getSize(userStorage.storage)}}</span>
      </div>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../../utils/FileRequest";
export default {
  name: "storage",
  data() {
    return {
      colors: [
        {color: '#13cd66', percentage: 20},
        {color: '#1989fa', percentage: 40},
        {color: '#fdd721', percentage: 60},
        {color: '#fd4949', percentage: 80},
        {color: '#fd4949', percentage: 100}
      ],
      userStorage : {
        userId: '',
        storage: 0,
        usedStorage: 0
      }
    }
  },
  created() {
    this.getUserStorage()
  },
  methods: {
    getUserStorage() {
      Fileservice({
        url: 'api/storage',
        method: 'get',
        params: {
          userId: localStorage.getItem('userId')
        }
      }).then(res => {
        if (res.code === 1) {
          this.userStorage = res.data
          console.log(this.userStorage.usedStorage/this.userStorage.storage)
        }
      })
    },
    getSize(val) {
      if(val === 0) return "0 B"
      var k = 1024;
      var sizes = ['B','KB','MB','GB','PB','TB','EB','ZB','YB'],
        i = Math.floor(Math.log(val) / Math.log(k));
      return (val / Math.pow(k,i)).toPrecision(3) + "" + sizes[i]
    },
  }
}
</script>

<style scoped>
.disk-main-storage-progress {
  width: 126px;
  margin-left: 20px;
}
.disk-main-storage-percentage {
  font-weight: 300;
  font-size: 12px;
  padding-right: 63px;
  margin-top: 5px;
}
</style>
