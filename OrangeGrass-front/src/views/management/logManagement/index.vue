<template>
  <div>
    <el-table :data="logs" v-loading="loading">
      <el-table-column prop="className" label="类名"></el-table-column>
      <el-table-column prop="methodName" label="方法名"></el-table-column>
      <el-table-column prop="costTime" label="接口访问耗时(毫秒)"></el-table-column>
      <el-table-column prop="accessTime" label="接口访问时间">
        <template slot-scope="scope">
          {{new Date(scope.row.accessTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}
        </template>
      </el-table-column>
      <el-table-column prop="fromIp" label="访问IP"></el-table-column>
      <el-table-column prop="location" label="IP属地"></el-table-column>
      <el-table-column prop="uri" label="访问URI"></el-table-column>
    </el-table>
    <div style="text-align: center;margin-top: 20px">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[10, 50, 100, 400]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../utils/FileRequest";

export default {
  name: "logManagement",
  data() {
    return {
      pageNum: 1,
      pageSize: 10,
      loading: true,
      total: 0,
      logs:[]
    }
  },
  created() {
    this.getLogs()
  },
  methods: {
    handleSizeChange(val) {
      this.pageSize = val
      this.getLogs()
    },
    handleCurrentChange(val) {
      console.log(val)
      this.pageNum = val
      this.getLogs()
    },
    getLogs() {
      this.loading = true
      this.logs = []
      Fileservice({
        url: 'api/system/accesslog',
        method: 'get',
        params:{
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res=>{
        if (res.code === 1) {
          console.log(res)
          this.logs = res.data.list
          this.total = res.data.total
          this.loading = false
          this.$message.success(res.msg)
        } else {
          this.loading = false
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
