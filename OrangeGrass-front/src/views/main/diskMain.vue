<template>
  <div class="main">
    <disk :file-list-loading="fileListLoading"
          :file-list="fileList"
          :path-tree="pathTree"
          :new-path-tree="newPathTree"
          :current-folder="currentFolder"
          :user-id="userId"
          :username="username"
          @setFileListLoading="setFileListLoading"
          @setCurrentFolder="setCurrentFolder"
          @setFileList="setFileList"
          @getMusics="getMusics"
          @getFiles="getFiles"
          @getPics="getPics"
          @getVideos="getVideos"
          @getExe="getExe"
          @getDocuments="getDocuments"
          @handleLogout="handleLogout"
          ref="disk"/>
  </div>
</template>

<script>



import Fileservice from "../../utils/FileRequest";
import disk from "./components/disk";
export default {
  name: 'diskMain',
  components: {disk},
  data() {
    return {
      fileListLoading: true,
      uploadFile:{},
      newPathTree: [],
      currentFolder:{
        id:'',
        name:''
      },
      userId: localStorage.getItem('userId'),
      username: localStorage.getItem('username'),
      // userId: '9d164a8092f949b38daa819813d5db01',
      // userId: 'c8eade1f673041dd86a937e03a0c8350',
      defaultProps: {
        children: 'subs',
        label: 'name'
      },
      //  移动文件模态框数据
      fileList: [{
        id:'',
        name:'',
        size:'',
        type:'',
        subs:[],
        createTime:'',
        updateTime:''
      }],
      pathTree: [{
        id:'',
        name:'',
        size:'',
        type:'',
        subs:[],
        createTime:'',
        updateTime:''
      }],
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
  computed: {},
  created() {
    this.getFiles()
    this.getPathTree()
  },
  methods: {
    setFileListLoading(val) {
      this.fileListLoading = val
    },
    handleLogout() {
      localStorage.clear()
      this.$router.push('/')
    },
    setCurrentFolder(val){
      this.currentFolder = val
    },
    setFileList(val){
      this.fileList = val
    },
    getFiles(folder){
      this.fileListLoading = true
      Fileservice({
        url: "/api/disk/" + this.userId,
        method: 'get',
        params: {
          folderId: folder
        }
      }).then(res=>{
        this.fileList = res.data.subs
        this.pathTree = new Array(res.data)
        this.currentFolder.id = res.data.id
        this.currentFolder.name = res.data.name
        this.$nextTick(() => {
          // this.$refs.tree.filter('FOLDER');
          this.$refs.disk.$refs.filelist.$refs.tree.filter('FOLDER')
        })
        this.fileListLoading = false
      })
    },
    getPics(folder){
      this.fileListLoading = true
      Fileservice({
        url: "/api/disk/image/" + this.userId,
        method: 'get',
        params: {
          folderId : folder
        }
      }).then(res=>{
        this.fileList = res.data.subs
        this.pathTree = new Array(res.data)
        this.currentFolder.id = res.data.id
        this.currentFolder.name = res.data.name
        this.$nextTick(() => {
          // this.$refs.tree.filter('FOLDER');
          this.$refs.disk.$refs.filelist.$refs.tree.filter('FOLDER')
        })
        this.fileListLoading = false
      })
    },
    getVideos(folder){
      this.fileListLoading = true
      Fileservice({
        url : "/api/disk/video/" + this.userId,
        method: "get",
        params: {
          folderId: folder
        }
      }).then(res=>{
        this.fileList = res.data.subs
        this.pathTree = new Array(res.data)
        this.currentFolder.id = res.data.id
        this.currentFolder.name = res.data.name
        this.$nextTick(() => {
          // this.$refs.tree.filter('FOLDER');
          this.$refs.disk.$refs.filelist.$refs.tree.filter('FOLDER')
        })
        this.fileListLoading = false
      })
    },
    getMusics(folder){
      this.fileListLoading = true
      Fileservice({
        url: "/api/disk/music/" + this.userId,
        method: 'get',
        params: {
          folderId : folder
        }
      }).then(res=>{
        this.fileList = res.data.subs
        this.pathTree = new Array(res.data)
        this.currentFolder.id = res.data.id
        this.currentFolder.name = res.data.name
        this.$nextTick(() => {
          // this.$refs.tree.filter('FOLDER');
          this.$refs.disk.$refs.filelist.$refs.tree.filter('FOLDER')
        })
        this.fileListLoading = false
      })
    },
    getDocuments(folder){
      this.fileListLoading = true
      Fileservice({
        url: "/api/disk/document/" + this.userId,
        method: 'get',
        params: {
          folderId: folder
        }
      }).then(res=>{
        this.fileList = res.data.subs
        this.pathTree = new Array(res.data)
        this.currentFolder.id = res.data.id
        this.currentFolder.name = res.data.name
        this.$nextTick(() => {
          // this.$refs.tree.filter('FOLDER');
          this.$refs.disk.$refs.filelist.$refs.tree.filter('FOLDER')
        })
        this.fileListLoading = false
      })
    },
    getExe(folder){
      this.fileListLoading = true
      Fileservice({
        url: "/api/disk/execute/" + this.userId,
        method: 'get',
        params: {
          folderId: folder
        }
      }).then(res=>{
        this.fileList = res.data.subs
        this.pathTree = new Array(res.data)
        this.currentFolder.id = res.data.id
        this.currentFolder.name = res.data.name
        this.$nextTick(() => {
          // this.$refs.tree.filter('FOLDER');
          this.$refs.disk.$refs.filelist.$refs.tree.filter('FOLDER')
        })
        this.fileListLoading = false
      })
    },
    getPathTree() {
      Fileservice({
        url: 'api/disk/folder/' + localStorage.getItem('userId'),
        method: 'get'
      }).then(res=>{
        if (res.code === 1) {
          console.log(res.data)
          this.newPathTree = new Array(res.data)
        }
      })
    }
  }
}
</script>

<style>

</style>
