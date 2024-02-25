<template>
  <div>
    <div style="margin-bottom: 20px">
      <el-button @click="currentChange(0)">查看未处理的举报</el-button>
      <el-button @click="currentChange(1)">查看已处理的举报</el-button>
    </div>
    <el-table v-loading="loading" :data="reportRecord">
      <el-table-column>
        <template slot-scope="scope">
          <div v-viewer>
            <img
              :src="setFileImg(scope.row)"
              :data-source="getImgPath(scope.row)"
              style="height: 130px; width: 130px; cursor:pointer">
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="fileName" label="文件名"></el-table-column>
      <el-table-column prop="userName" label="举报人"></el-table-column>
      <el-table-column prop="reportTime" label="举报时间">
        <template slot-scope="scope">
          {{new Date(scope.row.reportTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}
        </template>
      </el-table-column>
      <el-table-column prop="result" label="结果">
        <template slot-scope="scope">
          {{resultFormatter(scope.row.result)}}
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button round size="small" type="success" @click="Operate(2,scope.row.reportId)">不违规</el-button>
          <el-button round size="small" type="warning" @click="Operate(1,scope.row.reportId)">违规</el-button>
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
  name: "reportManagement",
  data() {
    return {
      pageNum: 1,
      pageSize: 4,
      loading: true,
      reportRecord: [],
      current: 0,
      total:0
    }
  },
  created() {
    this.getUncheckedReportRecord()
  },
  methods: {
    currentChange(val) {
      this.current = val
      this.pageNum = 1
      if (this.current === 1) {
        this.getCheckedReportRecord()
      } else {
        this.getUncheckedReportRecord()
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      if (this.current === 1) {
        this.getCheckedReportRecord()
      }
      if (this.current === 0) {
        this.getUncheckedReportRecord()
      }
    },
    handleCurrentChange(val) {
      this.pageNum = val
      if (this.current === 1) {
        this.getCheckedReportRecord()
      }
      if (this.current === 0) {
        this.getUncheckedReportRecord()
      }
    },
    Operate(val,reportId) {
      Fileservice({
        url: 'api/report',
        method: 'put',
        params: {
          result: val,
          reportId: reportId,
          operatorId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1){
          this.$message.success(res.msg)
          if (this.current === 1) {
            this.getCheckedReportRecord()
          } else {
            this.getUncheckedReportRecord()
          }
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getCheckedReportRecord() {
      this.reportRecord = []
      this.loading = true
      Fileservice({
        url: 'api/report/checked',
        method: 'get',
        params:{
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
          this.loading = false
          this.reportRecord = res.data.list
          this.total = res.data.total
        } else {
          this.loading = false
          this.$message.error(res.msg)
        }
      })
    },
    getUncheckedReportRecord() {
      this.reportRecord = []
      this.loading = true
      Fileservice({
        url: 'api/report/unchecked',
        method: 'get',
        params:{
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
          this.loading = false
          this.reportRecord = res.data.list
          this.total = res.data.total
        } else {
          this.loading = false
          this.$message.error(res.msg)
        }
      })
    },
    resultFormatter(val) {
      if (val === 1) {
        return '认定违规'
      } else if (val === 2) {
        return '认定不违规'
      } else {
        return '未审核'
      }
    },
    getImgPath(row){
      return getDownloadPathForAdminGlobal(row.fileId)
    },
    //  根据文件扩展名设置文件图片
    setFileImg(row) {
      let extension = row.fileName.substring(row.fileName.lastIndexOf('.')+1,row.fileName.length).toLowerCase().trim()
      if (row.type === 'FOLDER') {
        //  文件夹
        return this.FILE_IMG_MAP.dir
      } else if (!this.FILE_IMG_TYPE_LIST.includes(extension)) {
        //  无法识别文件类型的文件
        return this.FILE_IMG_MAP.unknown
      } else if (['jpg', 'png', 'jpeg', 'gif'].includes(extension)) {
        // 图片类型，直接显示缩略图
        return this.getThumbnailPath(row.fileId)
        // return this.FILE_IMG_MAP[extension]
      } else if (["avi", "mp4", "mpg", "mov", "swf", "flv"].includes(extension)){
        return this.getThumbnailPath(row.fileId)
      } else {
        //  可以识别文件类型的文件
        return this.FILE_IMG_MAP[extension]
      }
    },
    getThumbnailPath(id){
      return getThumbnailPathForAdminGlobal(id)
    },
  }
}
</script>

<style scoped>

</style>
