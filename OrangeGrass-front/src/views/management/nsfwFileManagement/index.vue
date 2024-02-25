<template>
  <div>
    <div style="margin-bottom: 20px">
      <el-button @click="currentChange(0)">查看未处理的图片</el-button>
      <el-button @click="currentChange(1)">查看已处理的图片</el-button>
      <el-button style="margin-left: 100px" @click="checkUncheckedImages" v-if="current === 0&&this.images.length > 0">后台鉴黄</el-button>
    </div>
    <el-table v-loading="loading" :data="images">
      <el-table-column width="170px">
        <template slot-scope="scope">
          <div v-viewer>
            <img
              :src="getThumbnailPath(scope.row.id)"
              :data-source="getImgPath(scope.row)"
              style="height: 130px; width: 130px; cursor:pointer">
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="fileName" label="文件名" width="400px" :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column v-if="this.current === 1" prop="nsfwScore" label="鉴黄分" width="180px">
        <template slot-scope="scope">
          <div style="display: flex;justify-content: center;align-items: center">
            <el-progress
              style="width: 70px"
              :color="colors"
              :show-text="false"
              :percentage="parseInt(scope.row.nsfwScore * 100)"></el-progress>
            <span style="margin-left: 10px">{{parseInt(scope.row.nsfwScore * 100) === 0 ? 1 : parseInt(scope.row.nsfwScore * 100)}}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column width="300px"></el-table-column>
      <el-table-column prop="isBan" width="200px" label="状态">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.isBan"
                     :active-value='1'
                     active-text="封禁"
                     :inactive-value='0'
                     inactive-text="正常"
                     @change="changeStatus($event, scope.row, scope.$index)" >
          </el-switch>
        </template>
      </el-table-column>


    </el-table>
    <div style="text-align: center;margin-top: 20px">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[4, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../utils/FileRequest";
import {getDownloadPathForAdminGlobal, getThumbnailPathForAdminGlobal} from "../../../main";

export default {
  name: "nsfwFileManagement",
  data() {
    return {
      colors: [
        {color: '#13cd66', percentage: 20},
        {color: '#1989fa', percentage: 40},
        {color: '#fdd721', percentage: 60},
        {color: '#fd4949', percentage: 80},
        {color: '#fd4949', percentage: 100}
      ],
      pageNum: 1,
      pageSize: 4,
      loading: true,
      images: [],
      current: 0,
      total: 0
    }
  },
  created() {
    this.getUncheckedImages()
  },
  methods: {
    currentChange(val) {
      this.current = val
      this.pageNum = 1
      if (this.current === 1) {
        this.getCheckedImages()
      } else {
        this.getUncheckedImages()
      }
    },
    checkUncheckedImages() {
      this.$message.success("请耐心等待后台处理噢～")
      Fileservice({
        url: 'api/nsfw/images',
        method: 'get'
      }).then(res=>{

      })
    },
    getUncheckedImages() {
      this.images = []
      this.loading = true
      Fileservice({
        url: 'api/file/uncheckedImages',
        method: 'get',
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res => {
        if (res.code === 1) {
          this.$message.success(res.msg)
          this.images = res.data.list
          this.total = res.data.total
          this.loading = false
        } else {
          this.loading = false
          this.$message.error(res.msg)
        }
      })
    },
    getCheckedImages() {
      this.images = []
      this.loading = true
      Fileservice({
        url: 'api/file/checkedImages',
        method: 'get',
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res => {
        if (res.code === 1) {
          this.$message.success(res.msg)
          this.images = res.data.list
          this.total = res.data.total
          this.loading = false
        } else {
          this.loading = false
          this.$message.error(res.msg)
        }
      })
    },
    changeStatus(e, row, index) {
      //e返回状态，row当前行数据，index下标
      console.log(e, row, index);
      Fileservice({
        url: 'api/file',
        method: 'put',
        data: row
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(val) {
      this.pageNum = val
      if (this.current === 1) {
        this.getCheckedImages()
      }
      if (this.current === 0) {
        this.getUncheckedImages()
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      if (this.current === 1) {
        this.getCheckedImages()
      }
      if (this.current === 0) {
        this.getUncheckedImages()
      }
    },
    getImgPath(row){
      return getDownloadPathForAdminGlobal(row.id)
    },
    getThumbnailPath(id){
      return getThumbnailPathForAdminGlobal(id)
    },
  }
}
</script>

<style scoped>

</style>
