<template>
  <div>
    <div>
      <el-button @click="handleAddButtonClick">添加广告</el-button>
    </div>
    <el-table :data="advertisements" v-loading="loading">
      <el-table-column label="缩略图">
        <template slot-scope="scope">
          <div @mouseenter="mouseOver(scope.row)"
               @mouseout="mouseOut(scope.row)">
            <img width="275" :height="275/4*3" v-if="!scope.row.isHover" :src="getThumbnailPath(scope.row)">
            <j-video-cover
              v-else
              class="video"
              step-nums="15"
              :width="'275px'"
              :show-duration="false"
              :videoUrl="getVideoPath(scope.row)"
            ></j-video-cover>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="advertisementName" label="广告名"></el-table-column>
      <el-table-column prop="advertisementDescription" label="广告描述"></el-table-column>
    </el-table>
    <div style="text-align: center;margin-top: 20px">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[3, 5, 15, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
    <el-dialog :visible.sync="addDialogVisibility">
      <el-form>
        <el-form-item label="广告名">
          <el-input v-model="addForm.name"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="addForm.description"></el-input>
        </el-form-item>
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="fileChange"
          multiple>
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        </el-upload>
        <el-button @click="handleSubmit">提交</el-button>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import Fileservice from "../../../utils/FileRequest";
import JVideoCover from "../../main/thirdPartycComponents/JVideoCover/src/JVideoCover";
import {getAdvertisementThumbnail, getAdvertisementUrl} from "../../../main";

export default {
  name: "advertisementManagement",
  components: {
    JVideoCover
  },
  data() {
    return {
      pageNum: 1,
      pageSize: 3,
      loading: true,
      total: 0,
      addForm: {
        name:'',
        description:'',
        file:''
      },
      advertisements: [],
      addDialogVisibility : false
    }
  },
  created() {
    this.getAdvertisements()
  },
  methods: {
    mouseOver(val) {
      val.isHover = true
    },
    mouseOut(val) {
      val.isHover = false
    },
    fileChange(val) {
      this.addForm.file = val.raw
    },
    handleSubmit() {
      let fd = new FormData
      fd.append("name",this.addForm.name)
      fd.append("description",this.addForm.description)
      fd.append("file",this.addForm.file)
      console.log(this.addForm)
      Fileservice({
        url: 'api/advertise',
        method: 'post',
        data: fd
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
          this.getAdvertisements()
          this.handleAddButtonClick()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAddButtonClick() {
      this.addDialogVisibility = !this.addDialogVisibility
    },
    getAdvertisements() {
      this.loading = true
      this.advertisements = []
      Fileservice({
        url: 'api/advertise',
        method: 'get',
        params: {
          pageSize: this.pageSize,
          pageNum: this.pageNum
        }
      }).then(res=>{
        if (res.code === 1) {
          console.log('广告返回数据',res.data)
          for (let i = 0 ; i < res.data.list.length ; ++i) {
            let tmp = {
              advertisementDescription: res.data.list[i].advertisementDescription,
              advertisementId: res.data.list[i].advertisementId,
              advertisementLocation: res.data.list[i].advertisementLocation,
              advertisementName: res.data.list[i].advertisementName,
              advertisementSize: res.data.list[i].advertisementSize,
              isHover: false
            }
            this.advertisements.push(tmp)
          }
          this.total = res.data.total
          this.loading = false
          console.log('广告列表',this.advertisements)
        }
      })
    },
    getVideoPath(val) {
      return getAdvertisementUrl(val.advertisementId)
    },
    getThumbnailPath(val) {
      return getAdvertisementThumbnail(val.advertisementId)
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getAdvertisements()
    },
    handleCurrentChange(val) {
      console.log(val)
      this.pageNum = val
      this.getAdvertisements()
    },
  }
}
</script>

<style scoped>

</style>
