<template>
  <div class="file_detail">
    <div class="file_detail_empty" v-if="detail === null">
      <div class="file_detail_header">
        <span>文件内容：</span>
      </div>
      <div class="file_detail_body">
        <div class="file_detail_image">
          <img src="@/assets/images/empty-folder-new.ce4801de.png">
          <span style="display: flex;justify-content: center;align-items: center">选中文件/文件夹，查看详细</span>
        </div>
      </div>
    </div>
    <div class="file_detail_header" v-if="detail != null">
      <div class="file_detail_header_folder" v-if="detail[0].type === 'FOLDER' && detail.length === 1">
        <span>文件夹内容：</span>
      </div>
      <div class="file_detail_header_file" v-if="detail[0].type === 'FILE' && detail.length === 1">
        <span>文件详情：</span>
      </div>
      <div class="file_detail_selected" v-if="detail.length > 1">
        <div class="file_detail_selected_text">
          <span>共选中{{detail.length}}个文件</span>
        </div>
        <div class="file_detail_selected_img">
          <img :src="this.FILE_IMG_MAP.dir"/>
        </div>
      </div>
    </div>
    <div class="file_detail_body" v-if="detail != null && detail.length === 1 && detail[0].type === 'FILE'">
      <div class="file_detail_image" v-if="!isVideo(detail[0])">
        <img :src="setFileImg(detail[0])"/>
      </div>
      <div class="preview" v-else>
        <j-video-cover
          class="video"
          step-nums="15"
          :width="'275px'"
          :show-duration="false"
          :videoUrl="getVideoDownloadPath(detail[0])"
        ></j-video-cover>
      </div>
      <div class="file_detail_name">
      <span v-if="detail[0].type === 'FOLDER'">
      文件夹名：
      </span>
        <span v-if="detail[0].type === 'FILE'">
      </span>
        <span>{{detail[0].name}}</span>
      </div>
      <div class="file_detail_createTime">
        <span>创建时间：</span>
        <span>{{new Date(detail[0].createTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}</span>
      </div>
      <div class="file_detail_updateTime">
        <span>最后修改：</span>
        <span>{{new Date(detail[0].updateTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}</span>
      </div>
      <div class="file_detail_type">
        <span>文件格式：{{getMimetype(detail[0])}}</span>
      </div>
      <div class="file_detail_size">
        <span>文件大小：</span>
        <!--        <span>{{detail[0].size}}</span>-->
        <span>{{getSize(detail[0].size)}}</span>
      </div>
      <div class="file_detail_currentFolder">
        <span>当前文件夹：</span>
        <span>{{currentFolder.name}}</span>
      </div>
    </div>
    <div class="folder_detail_body" v-if="detail != null && detail.length === 1 && detail[0].type === 'FOLDER'">
      <div class="folder_detail_label">
        <div class="folder_detail_image">
          <img style="height: 30px; width: 30px" :src="setFileImg(detail[0])"/>
        </div>
        <div class="folder_detail_name">
        <span  v-if="detail[0].type === 'FOLDER'">
          文件夹名：{{detail[0].name}}
        </span>
        </div>
      </div>
      <div class="folder_detail_contents">
        <div class="folder_detail_content" v-for="index in detail[0].subs">
          <img style="height: 30px; width: 30px" :src="setFileImg(index)"/>
          <span :title="index.name"
                style="height: 30px;display: flex;justify-content: center;align-items: center" >
            {{index.name.length > 20 ? index.name.substring(0,19)+'...' : index.name}}</span>
        </div>
        <div v-if="detail[0].subs === null">
          <div class="file_detail_body">
            <div class="file_detail_image">
              <img src="@/assets/images/empty-folder-new.ce4801de.png">
              <span style="display: flex;justify-content: center;align-items: center">此文件夹为空</span>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import JVideoCover from "../thirdPartycComponents/JVideoCover/src/JVideoCover";
import {getChunkDownloadPath, getThumbnailPathGlobal} from "../../../main";
export default {
  name: "FileDetail",
  props: {
    detail: Array,
    currentFolder : Object
  },
  components: {
    JVideoCover
  },
  methods: {
    getVideoDownloadPath(val){
      return getChunkDownloadPath(val.id)
    },
    isVideo(val) {
      let extension = val.name.substring(val.name.lastIndexOf('.')+1,val.name.length).toLowerCase()
      if (["avi", "mp4", "mpg", "mov", "swf", "flv"].includes(extension)) {
        return true
      } else {
        return false
      }
    },
    getSize(val) {
      if(val === 0) return "0 B"
      var k = 1024;
      var sizes = ['B','KB','MB','GB','PB','TB','EB','ZB','YB'],
        i = Math.floor(Math.log(val) / Math.log(k));
      return (val / Math.pow(k,i)).toPrecision(3) + "" + sizes[i]
    },
    setFileImg(row) {
      let extension = row.name.substring(row.name.lastIndexOf('.')+1,row.name.length).toLowerCase()
      if (row.type === 'FOLDER') {
        //  文件夹
        return this.FILE_IMG_MAP.dir
      } else if (!this.FILE_IMG_TYPE_LIST.includes(extension)) {
        //  无法识别文件类型的文件
        return this.FILE_IMG_MAP.unknown
      } else if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        // 图片类型，直接显示缩略图
        return this.getThumbnailPath(row.id)
        // return this.FILE_IMG_MAP[extension]
      } else if (["avi", "mp4", "mpg", "mov", "swf", "flv"].includes(extension)){
        return this.getThumbnailPath(row.id)
      } else {
        //  可以识别文件类型的文件
        return this.FILE_IMG_MAP[extension]
      }
    },
    getThumbnailPath(id){
      return getThumbnailPathGlobal(id)
    },
    getMimetype(val) {
      if (val.type === 'FOLDER') {
        return '文件夹'
      } else {
        return val.name.substring(val.name.lastIndexOf('.')+1)
      }
    }
  }
}
</script>

<style scoped>
.file_detail {
  padding-left: 10px;
}
.file_detail_header {
  margin-top: 20px;
  text-align: left;
}
.file_detail_body {
  font-size: 5px;
  text-align: left;
  margin-top: 30px;
}
.file_detail_name {
  text-align: left;
  margin-top: 20px;
}
.file_detail_type {
  text-align: left;
  margin-top: 20px;
}
.file_detail_size {
  text-align: left;
  margin-top: 20px;
}
.file_detail_createTime {
  text-align: left;
  margin-top: 20px;
}
.file_detail_updateTime {
  text-align: left;
  margin-top: 20px;
}
.file_detail_image {
  text-align: center;
  margin-bottom: 10px;
}
.file_detail_currentFolder {
  text-align: left;
  margin-top: 20px;
}
.file_detail_selected_img {
  text-align: center;
  margin-top: 30px;
}




.folder_detail_body {
  display: flex;
  flex-flow: column;
  font-size: 5px;
  /*text-align: left;*/
  margin-top: 30px;
}
.folder_detail_label {
  display: flex;
  justify-items: center;
  align-items: center;
  border-bottom-style: solid;
  border-bottom-color: gray;
  border-bottom-width: 1px;
}
.folder_detail_contents {
  padding-top: 20px;
  display: flex;
  flex-flow: column;
  justify-items: center;
}
.folder_detail_content {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 20px;
}
</style>
