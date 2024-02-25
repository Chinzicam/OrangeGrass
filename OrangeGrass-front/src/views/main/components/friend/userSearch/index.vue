<template>
  <div class="user-search-body">
    <el-select v-model="username" filterable
               :filter-method="dataFilter"
               @visible-change="visibleHideSelectInput"
               placeholder="请选择用户名"
               class="header-item-inner-long">
      <el-option @click.native="hanldeUserClick(item)"
                 v-for="item in usernameList"
                 :key="item.userId"
                 :label="item.username"
                 :value="item.userId">
        <el-popover
          placement="right"
          trigger="hover">
          <div v-html="item.showItem" slot="reference"></div>
          <div class="user-information">
            <div>
              <div class="user-information-avatar">
                <el-avatar :src="getAvatarPath(item.userId)"></el-avatar>
              </div>
              <div class="user-information-username">
                <span>{{item.username}}</span>
              </div>
            </div>
            <div class="user-information-createTime">

            </div>
          </div>
        </el-popover>
      </el-option>
    </el-select>
  </div>
</template>

<script>
import Fileservice from "../../../../../utils/FileRequest";
import {getAvatarPathGlobal} from "../../../../../main";
export default {
  name: "userSearch",
  data() {
    return {
      username: '',
      usernameList: [],
      usernameListFilter: [],
    }
  },
  methods: {
    getAvatarPath(val) {
      return getAvatarPathGlobal(val)
    },
    hanldeUserClick(val) {
      if (val.userId === localStorage.getItem('userId')) {
        this.$message.error('你不能向自己发送好友申请')
      } else {
        this.username = ''
        this.usernameList = new Array()
        this.usernameListFilter = new Array()
        Fileservice({
          url: 'api/apply/friend',
          method: 'post',
          params: {
            fromUserId: localStorage.getItem('userId'),
            targetUserId: val.userId
          }
        }).then(res=>{
          if (res.code === 1) {
            this.$message.success(res.msg)
          } else {
            this.$message.error(res.msg)
          }
        })
      }
    },
    // 自定义筛选方法
    async dataFilter(val) {
      await Fileservice({
        url: 'api/friend/search',
        method: 'get',
        params: {
          username: val
        }
      }).then(res=>{
        if (res.code === 1) {
          this.usernameList = res.data
          this.usernameListFilter = res.data
        }
      })
      if (val) {
        let filterResult = [];
        let originalData = JSON.parse(JSON.stringify(this.usernameListFilter));
        originalData.filter((item) => {
          if (item.username.includes(val) || item.username.toUpperCase().includes(val.toUpperCase())) {
            filterResult.push(item);
          }
        })
        this.setHighlight(filterResult, val) // 匹配文字高亮显示

      } else {
        this.usernameList = this.usernameListFilter;
      }
    },
// 设置文字高亮
    setHighlight(arr, keyword) {
      if (arr && arr.length > 0 && keyword) {
        this.usernameList = [];
        arr.filter((item) => {
          let reg = new RegExp(keyword, 'g');
          let replaceString = `<span style="color:#1292FF;font-weight: bold;">${keyword.trim()}</span>`;
          if (reg.test(item.username)) {
            item.showItem = item.username.replace(reg, replaceString);
            this.usernameList.push(item);
          }
        })
      } else {
        this.usernameList = [];
      }
    },
// 当下拉框出现时触发
    visibleHideSelectInput(val) {
      if(val) {
        this.usernameList = JSON.parse(JSON.stringify(this.usernameListFilter));
      }
    },
  }
}
</script>

<style scoped>

</style>
