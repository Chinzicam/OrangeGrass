<template>
  <div class="disk-main-friend">
    <div class="disk-main-friend-sidebar" v-loading="loading">
      <div class="disk-main-friend-header">
        <div class="disk-main-friend-header-search">
          <user-search></user-search>
        </div>
      </div>
      <div class="disk-main-friend-list">
        <div class="disk-main-friend-list-item"
             @click="selectFriend(item)"
             :title="item.friendUsername" v-for="item in friendList">
          <el-badge :hidden="item.unreadMessageNumber === 0"
                    :value="item.unreadMessageNumber" :max="99" class="item">
            <div style="display: flex;align-items: center">
              <div class="disk-main-friend-list-item-avatar">
                <el-avatar :src="getAvatarPath(item.friendId)"></el-avatar>
              </div>
              <div class="disk-main-friend-list-item-friendUsername">
                <span>{{item.friendUsername}}</span>
              </div>
            </div>
          </el-badge>
        </div>
      </div>
    </div>
    <div class="disk-main-friend-content">
      <div style="height: 910px"
           v-if="selectedFriendId===null || selectedFriendId===''">
        <p>请选择一位好友</p>
      </div>
      <chat v-if="selectedFriendId!==null && selectedFriendId!=''"
            :friend-id="selectedFriendId"></chat>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../../utils/FileRequest";
import Chat from "./chat";
import UserSearch from "./userSearch";
import {getAvatarPathGlobal} from "../../../../main";

export default {
  name: "friend",
  components: {UserSearch, Chat},
  props: {
    selectedFriendId: String
  },
  data() {
    return {
      loading: true,
      friendList: [],
    }
  },
  beforeDestroy() {
    // 清除定时器、取消订阅等操作
    clearInterval(this.timer);
  },
  destroyed() {
    // 清除缓存、移除事件监听等操作
    window.removeEventListener('onmessageWS', this.getSocketData);
  },
  mounted() {
    window.addEventListener('onmessageWS', this.getSocketData)
  },
  created() {
    this.getFriendList()
  },
  methods: {
    getSocketData (res) {
      let data = JSON.parse(res.detail.data)
      if (data.notifyType === 1) {
        for (let i = 0 ; i < this.friendList.length ; ++i) {
          let a = this.friendList[i]
          if (a.friendId === data.data.fromUserId) {
            if (a.friendId !== this.selectedFriendId) {
              a.unreadMessageNumber ++
            }
          }
        }
      }
    },
    selectFriend(val) {
      val.unreadMessageNumber = 0
      this.$emit('selectedFriendIdChange',val.friendId)
    },
    getFriendList() {
      this.loading = true
      Fileservice({
        url: 'api/friend',
        method: 'get',
        params: {
          userId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1) {
          this.friendList = res.data
          this.loading = false
        } else {
          this.loading = false
          this.$message.error(res.msg)
        }
      })
    },
    getAvatarPath(val) {
      return getAvatarPathGlobal(val)
    },
  }
}
</script>

<style scoped>
.disk-main-friend {
  display: flex;
  width: 100%;
}
.disk-main-friend-content {
  width: 88%;
  margin-left: 20px;
}
.disk-main-friend-sidebar {
  margin-top: 20px;
  margin-left: 10px;
  width: 200px;
  height: inherit;
}
.disk-main-friend-header-search {
  display: flex;
  height: 40px;
  align-items: center;
}
.disk-main-friend-header-search-input {
}
.disk-main-friend-header-search-button {
  margin-left: 20px;
  margin-right: 10px;
}
.disk-main-friend-list-item-friendUsername {
  margin-left: 10px;
}
.disk-main-friend-list-item {
  height: 40px;
  border-radius: 10px;
  width: 170px;
  margin: 20px;
  font-weight: 400;
  color: #636d7e;
  background-color: #fff;
  cursor: pointer;
  white-space: nowrap;
  display: flex;
  text-align: center;
  align-items: center;
  justify-content: flex-start;
  flex-shrink: 1;
}
.disk-main-friend-list-item:focus
{
  color: #06a7ff;
  background-color: #eef9fe;
}
.disk-main-friend-list-item:hover
{
  background-color: #fafafc;
}
</style>
