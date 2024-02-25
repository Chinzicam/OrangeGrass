<template>
<div>
  <el-container>
    <el-header style="height: 100px">
      <div>
        <el-upload
          action="http://localhost:8989/"
          :file-list="uploadFile"
          :http-request="modeUpload"
        >
          <el-button size="small" type="primary">上传</el-button>
        </el-upload>
        <el-button @click="handleUpload">点击上传文件</el-button>
      </div>
    </el-header>
    <el-container class="MainContainer">
      <el-aside class="aside">
        <el-tree :data="pathTree"
                 :expand-on-click-node="false"
                 :props="defaultProps"
                 default-expand-all
                 @node-click="handleNodeClick"
                 :filter-node-method="filterNode"
                 ref="tree"
                 class="myTree"
                 style="background-color: lightskyblue"
        ></el-tree>
      </el-aside>
      <el-main>
        <el-table style="width: 100%;" :data="fileList.slice((currentPage-1)*PageSize,currentPage*PageSize)" class="myList">
        <el-table-column type="selection"></el-table-column>
        <el-table-column prop="id" width="60" align="center">
          <template slot-scope="scope">
            <div @dblclick="clickFolder(scope.row)">
              <img :src="setFileImg(scope.row)" style="height: 30px"/>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="文件名">
          <template slot-scope="scope">
            <div  @dblclick="clickFolder(scope.row)">
              {{scope.row.name}}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="文件类型">
          <template slot-scope="scope">
            <div  @dblclick="clickFolder(scope.row)">
              {{scope.row.type === 'FOLDER' ? '文件夹' : '文件'}}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间">
          <template slot-scope="scope">
            <div  @dblclick="clickFolder(scope.row)">
              {{new Date(scope.row.createTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}
            </div>
          </template>
        </el-table-column>
        <!--      <er-table-column prop="fileLocation" label="文件位置"></er-table-column>-->
        <el-table-column >
          <template slot="header">
            <span>操作</span>
            <i
              class="el-icon-circle-plus"
              title="展开操作列按钮"
              @click="$store.commit('changeOperaColumnExpand', FOLD_TYPE.UNFOLD)"
            ></i>
            <i class="el-icon-remove" title="收起操作列按钮"
               @click="$store.commit('changeOperaColumnExpand', FOLD_TYPE.FOLD)"></i>
          </template>
          <template slot-scope="scope">
            <div>
              <el-button type="primary" v-if="scope.row.type === 'FILE'">
                <a @click="handleDownload(scope.row)">下载</a>
              </el-button>
              <el-button type="danger">删除</el-button>
              <el-button type="success">其他</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      </el-main>
    </el-container>
    <el-container class="FooterContainer">
      <el-footer>
        <div class="block">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page.sync="currentPage"
            :page-size="PageSize"
            layout="prev, pager, next, jumper"
            :total="fileList.length">
          </el-pagination>
        </div>
      </el-footer>
    </el-container>
  </el-container>
</div>
</template>

<script>
import Fileservice from "@/utils/FileRequest";
import datetime from "@/utils/datetime";

export default {
  name: "FileList",
  props: {
    fileList: Array,
    pathTree: Array
  },
  data(){
    return {
      PageSize:5,
      currentPage:1,
      uploadFile:{},
      currentFolder:{
        id:'',
        name:''
      },
      userId: '9d164a8092f949b38daa819813d5db01',
      // userId: 'c8eade1f673041dd86a937e03a0c8350',
      defaultProps: {
        children: 'subs',
        label: 'name'
      },
      dialogMoveFile: {
        isBatchMove: false,
        visible: false, //  是否可见
        fileTree: [], //  目录树
        defaultProps: {
          children: 'children',
          label: 'label',
          type: 'type'
        }
      },
      viewFilePath: '',
    }
  },
  methods:{
    //  根据文件扩展名设置文件图片
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
      } else {
        //  可以识别文件类型的文件
        return this.FILE_IMG_MAP[extension]
      }
    },
    // 树形节点过滤
    filterNode(value, data, node) {
      return data.type === value
    },
    async handleNodeClick(data) {
      console.log(data)
      if (data.type === 'FOLDER') {
        if (data.subs === null){
          this.fileList = null;
          this.currentFolder.id = data.id
          this.currentFolder.name = data.name
          console.log('当前文件夹是' + this.currentFolder.name)
        } else {
          this.fileList = data.subs
          this.currentFolder.id = data.id
          this.currentFolder.name = data.name
          console.log('当前文件夹是' + this.currentFolder.name)
        }
      }
    },
    clickFolder(row){
      if (row.type == 'FOLDER'){
        this.fileList = row.subs
        this.currentFolder.id = row.id
        this.currentFolder.name = row.name
        console.log('当前文件夹是' + this.currentFolder.name)
      }
    },
    handleUpload(){
      let fd = new FormData()
      fd.append('file',this.uploadFile)
      fd.append('userId',this.userId)
      fd.append('folderId',this.currentFolder.id)
      Fileservice.post('/api/file/upload',fd, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(res=>{
        if (res.code === 1){
          this.$message.success(res.msg)
        }
      })
    },
    modeUpload(item){
      this.uploadFile = item.file
      console.log(this.uploadFile)
    },
    getThumbnailPath(id){
      let a = 'http://127.0.0.1:8989/api/thumbnail/' + id
      return a.toString()
    },
    async handleDownload(row){
      console.log(row)
      Fileservice({
        url:"/api/file/download/"+row.id,
        method:'get',
        responseType: 'blob'}).then((res) => {
        console.log(res)
        if (!res) {
          this.$message.warning('文件下载失败')
          return
        }
        let blob = new Blob([res], {type: 'application/octet-stream'})
        let downloadElement = document.createElement('a')
        let href = window.URL.createObjectURL(blob) // 创建下载的链接
        downloadElement.href = href
        downloadElement.download = row.name // 下载后文件名
        document.body.appendChild(downloadElement)
        downloadElement.click() // 点击下载
        document.body.removeChild(downloadElement) // 下载完成移除元素
        window.URL.revokeObjectURL(href) // 释放掉blob对象
      })
      console.log(11);
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
      this.PageSize = val
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
      this.currentPage = val
    }
  }

}
</script>

<style scoped>
.aside {
  background-color: lightskyblue;
  border-radius: 20px;
}
.MainContainer {
  height: 700px;
}
.block {
  text-align: center;
}
</style>
