<template>
  <div class="disk-main-friend-apply-record-main"
       @click="handleLogoClick()">
     <div class="disk-main-friend-apply-record-logo">
       <svg t="1679314527507" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3849" width="20" height="20"><path d="M868.629333 277.111467v-54.485334-0.157866c0.093867-23.466667 14.4512-42.410667 37.917867-42.321067C930.133333 180.241067 938.666667 199.048533 938.666667 222.634667v161.787733c0.0256 18.658133-15.957333 32.6912-34.624 32.6656-15.940267 2.286933-27.895467 0.055467-35.8656-6.698667-10.670933-9.053867-26.683733-30.528-48.029867-64.426666C761.105067 243.9936 638.306133 156.292267 512 156.292267c-188.539733 0-355.554133 165.888-355.554133 354.402133s167.074133 356.8384 355.6096 356.8384c159.069867 0 302.8608-127.726933 340.757333-274.944h0.0384c0-23.5648 17.783467-41.198933 41.352533-41.198933s36.010667 22.3744 36.010667 45.9392c-39.509333 194.752-211.712 341.333333-418.158933 341.333333C276.381867 938.666667 85.333333 747.639467 85.333333 512S276.381867 85.333333 512.055467 85.333333c139.541333 0 278.715733 88.234667 356.573866 191.778134z m25.365334 26.082133l-73.847467 42.760533 72.213333-45.469866c9.570133 15.202133 17.706667 27.264 24.2176 36.002133a148.590933 148.590933 0 0 0 6.839467 8.618667c0.571733 0.6528 0.989867 1.1008 1.216 1.3312a21.4912 21.4912 0 0 0-1.271467-1.143467c-7.808-6.621867-16.494933-10.538667-24.5632-12.049067a33.352533 33.352533 0 0 0-6.1312-0.622933c-0.669867 0-0.9088 0.017067-0.750933 0l6.088533-0.878933 6.148267 0.008533c-26.653867-0.034133-50.7776 22.186667-50.820267 52.514133l14.848-107.153066c15.1296 18.619733 13.930667 5.559467 25.813334 26.082133z" fill="#333333" p-id="3850"></path><path d="M533.333333 490.666667h117.333334a32 32 0 1 1 0 64h-149.333334a32 32 0 0 1-32-32v-149.333334a32 32 0 0 1 64 0V490.666667z" fill="#333333" p-id="3851"></path></svg>
     </div>
    <div class="disk-main-friend-apply-record-content">
      <el-dialog append-to-body :visible.sync="applyRecordDialogVisibility">
        <el-divider>好友申请</el-divider>
        <div style="display: flex">
          <el-button @click="contentChange(0)">我发送的</el-button>
          <el-button @click="contentChange(1)">我收到的</el-button>
        </div>
        <div v-if="fromMe">
          <div class="disk-main-friend-apply-record-content-item-list"
               v-if="sendList.length > 0"
               v-for="item in sendList">
            <div class="disk-main-friend-apply-record-content-item-divider"></div>
            <div class="disk-main-friend-apply-record-content-item">
              <div class="disk-main-friend-apply-record-content-avatar"
                   :title="item.targetUserId">
                <el-avatar :src="getAvatarPath(item.targetUserId)"></el-avatar>
              </div>
              <div class="disk-main-friend-apply-record-content-username"
                   :title="item.targetUsername">
                <span>{{shortenString(item.targetUsername)}}</span>
              </div>
              <div class="disk-main-friend-apply-record-content-applytime">
                <span>{{new Date(item.applyTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}</span>
              </div>
              <div class="disk-main-friend-apply-record-content-applyresult">
                <span>{{resultFormatter(item.applyResult)}}</span>
              </div>
            </div>
          </div>
          <div class="disk-main-friend-apply-record-content-item-divider" v-if="sendList.length > 0"></div>
          <div class="disk-main-friend-apply-record-content-empty" v-if="sendList.length === 0">
            <svg t="1679322267031" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7101" width="140" height="140"><path d="M675.328 117.717333A425.429333 425.429333 0 0 0 512 85.333333C276.352 85.333333 85.333333 276.352 85.333333 512s191.018667 426.666667 426.666667 426.666667 426.666667-191.018667 426.666667-426.666667c0-56.746667-11.093333-112-32.384-163.328a21.333333 21.333333 0 0 0-39.402667 16.341333A382.762667 382.762667 0 0 1 896 512c0 212.074667-171.925333 384-384 384S128 724.074667 128 512 299.925333 128 512 128c51.114667 0 100.8 9.984 146.986667 29.12a21.333333 21.333333 0 0 0 16.341333-39.402667z m48.384 532.928A234.538667 234.538667 0 0 1 520.405333 768a234.538667 234.538667 0 0 1-203.264-117.333333 21.333333 21.333333 0 0 0-36.949333 21.333333 277.184 277.184 0 0 0 240.213333 138.666667c100.16 0 190.997333-53.546667 240.213334-138.666667a21.333333 21.333333 0 0 0-36.906667-21.333333zM341.333333 426.624c0-23.552 18.944-42.624 42.666667-42.624 23.573333 0 42.666667 19.157333 42.666667 42.624v42.752A42.538667 42.538667 0 0 1 384 512c-23.573333 0-42.666667-19.157333-42.666667-42.624v-42.752z m256 0c0-23.552 18.944-42.624 42.666667-42.624 23.573333 0 42.666667 19.157333 42.666667 42.624v42.752A42.538667 42.538667 0 0 1 640 512c-23.573333 0-42.666667-19.157333-42.666667-42.624v-42.752z" fill="#3D3D3D" p-id="7102"></path></svg>
            <p style="font-weight: 700;font-size: 14px;margin-top: 20px">您没有未被处理的好友请求噢</p>
          </div>
        </div>
        <div v-if="!fromMe">
          <div class="disk-main-friend-apply-record-content-item-list"
               v-if="applyList.length > 0"
               v-for="item in applyList">
            <div class="disk-main-friend-apply-record-content-item-divider"></div>
            <div class="disk-main-friend-apply-record-content-item">
              <div class="disk-main-friend-apply-record-content-avatar"
                   :title="item.fromUserId">
                <el-avatar :src="getAvatarPath(item.fromUserId)"></el-avatar>
              </div>
              <div class="disk-main-friend-apply-record-content-username"
                   :title="item.fromUsername">
                <span>{{shortenString(item.fromUsername)}}</span>
              </div>
              <div class="disk-main-friend-apply-record-content-applytime">
                <span>{{new Date(item.applyTime).strftime('%G年%n月%e日%k时%M分%S秒','zh')}}</span>
              </div>
              <div class="disk-main-friend-apply-record-content-applyresult">
                <span>{{resultFormatter(item.applyResult)}}</span>
              </div>
              <div class="disk-main-friend-apply-record-content-operation"
                   v-if="item.applyResult === 0">
                <el-button round size="mini" type="success"
                           @click="handleApply(1,item)">同意</el-button>
                <el-button round size="mini" type="danger"
                           @click="handleApply(2,item)">不同意</el-button>
              </div>
            </div>
          </div>
          <div class="disk-main-friend-apply-record-content-item-divider" v-if="applyList.length > 0"></div>
          <div class="disk-main-friend-apply-record-content-empty" v-if="applyList.length === 0">
            <svg t="1679318507090" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4918" width="140" height="140"><path d="M504.787121 16.587353c-278.279557 0-503.868569 225.589013-503.868569 503.868569 0 278.279557 225.589013 503.868569 503.868569 503.868569s503.868569-225.589013 503.868569-503.868569C1008.65569 242.176366 783.06567 16.587353 504.787121 16.587353zM504.787121 984.015006c-256.016628 0-463.559084-207.542456-463.559084-463.559084 0-256.016628 207.542456-463.559084 463.559084-463.559084s463.559084 207.542456 463.559084 463.559084C968.346205 776.47255 760.803749 984.015006 504.787121 984.015006z" fill="#221714" p-id="4919"></path><path d="M813.328016 306.41457c0-12.974616-10.517753-23.492368-23.492368-23.492368-3.959399 0-7.686011 0.983551-10.959141 2.712828l-0.002015-0.004031-0.159222 0.086665c-0.05341 0.028217-0.105812 0.057441-0.159222 0.086665l-145.717782 79.351238c-0.996652 0.456505-1.954002 0.977505-2.872051 1.563-6.523082 4.174047-10.859375 11.466033-10.859375 19.785911 0 8.18081 4.18614 15.378069 10.528838 19.584364 1.123627 0.746733 2.317795 1.394708 3.567389 1.940902l142.658292 77.602814c3.905989 2.895229 8.740104 4.608382 13.974291 4.608382 12.974616 0 23.492368-10.517753 23.492368-23.492368 0-9.220795-5.313798-17.196027-13.045157-21.041551l0.002015-0.004031L689.611151 386.504479l111.300536-59.371841c0.061472-0.032248 0.122944-0.064495 0.184416-0.097751l0.059456-0.032248-0.001008-0.002015C808.410259 323.003938 813.328016 315.284672 813.328016 306.41457z" p-id="4920"></path><path d="M376.371178 408.029744c1.248586-0.546194 2.442755-1.195176 3.567389-1.940902 6.34169-4.206295 10.528838-11.402546 10.528838-19.584364 0-8.319878-4.337301-15.611864-10.860383-19.785911-0.917041-0.585495-1.874391-1.106495-2.871043-1.563l-145.701659-79.342168c-0.063487-0.034263-0.125967-0.068526-0.189455-0.102789l-0.146122-0.079611-0.002015 0.004031c-3.272122-1.729277-6.999742-2.712828-10.958134-2.712828-12.974616 0-23.492368 10.517753-23.492368 23.492368 0 8.870102 4.917757 16.589369 12.174472 20.587062l-0.001008 0.002015 0.057441 0.03124c0.06248 0.034263 0.125967 0.066511 0.188447 0.099766l111.297513 59.370833-110.672716 59.19851 0.002015 0.004031c-7.730352 3.845525-13.045157 11.820757-13.045157 21.041551 0 12.974616 10.517753 23.492368 23.492368 23.492368 5.235194 0 10.069309-1.714161 13.975299-4.60939L376.371178 408.029744z" p-id="4921"></path><path d="M735.081259 743.901478c-2.663449-124.911034-104.744198-225.355217-230.294137-225.355217-125.898616 0-228.19099 101.003478-230.307238 226.397218-0.030232 0.517977-0.050387 1.036962-0.050387 1.563 0 0.268058 0.012093 0.533093 0.020155 0.800143-0.003023 0.533093-0.020155 1.06417-0.020155 1.598271l0.113874 0c1.212308 13.411974 12.479817 23.921664 26.206204 23.921664 14.536608 0 26.321086-11.78347 26.321086-26.320079 0-0.447435-0.012093-0.892855-0.034263-1.334244 1.987258-96.478738 80.795325-174.08256 177.749715-174.08256 97.035009 0 175.891448 77.731804 177.756769 174.320386-0.001008 0.07558-0.006046 0.151161-0.006046 0.227749 0 0.462551 0.013101 0.922079 0.036279 1.378584 0.002015 0.247903 0.012093 0.493791 0.014108 0.741695 0.018139 0.05341 0.036279 0.104805 0.054418 0.157207 1.155874 13.458329 12.441523 24.023446 26.200158 24.023446 13.419028 0 24.483982-10.05117 26.093338-23.033848l0.209609 0c0-0.728594-0.021162-1.450134-0.027209-2.176712 0.015116-0.361778 0.027209-0.724563 0.027209-1.090372C735.144746 745.053321 735.118545 744.47488 735.081259 743.901478z" p-id="4922"></path><path d="M504.787121 16.460378c-277.982274 0-503.330437 225.348163-503.330437 503.32943 0 277.98429 225.348163 503.330437 503.330437 503.330437s503.330437-225.346148 503.330437-503.330437C1008.117559 241.807534 782.769395 16.460378 504.787121 16.460378zM504.787121 970.571792c-248.960452 0-450.781984-201.820524-450.781984-450.781984 0-248.959444 201.821532-450.781984 450.781984-450.781984 248.96146 0 450.780977 201.82254 450.780977 450.781984C955.568098 768.751268 753.748581 970.571792 504.787121 970.571792z" p-id="4923"></path></svg>
            <p style="font-weight: 700;font-size: 14px;margin-top: 20px">您当前没有好友请求噢</p>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../../../utils/FileRequest";
import {getAvatarPathGlobal} from "../../../../../main";

export default {
  name: "friendApplyRecord",
  data() {
    return {
      fromMe: false,
      applyRecordDialogVisibility: false,
      apply: {
        friendApplyRecordId: '',
        fromUserId: '',
        applyTime: '',
        applyReason: '',
        applyResult: ''
      },
      sendList: [],
      applyList: []
    }
  },
  methods: {
    getSendList() {
      Fileservice({
        url: 'api/apply/friend/my',
        method: 'get',
        params: {
          userId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1) {
          this.sendList = res.data
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getApplyList() {
      Fileservice({
        url: 'api/apply/friend',
        method: 'get',
        params: {
          userId: localStorage.getItem('userId')
        }
      }).then(res=>{
        if (res.code === 1) {
          this.applyList = res.data
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleLogoClick() {
      this.getApplyList()
      this.applyRecordDialogVisibility = !this.applyRecordDialogVisibility
    },
    handleApply(val,apply) {
      Fileservice({
        url: 'api/apply/friend',
        method: 'put',
        params: {
          id : apply.friendApplyRecordId,
          result : val
        }
      }).then(res=>{
        if (res.code === 1) {
          this.getApplyList()
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    shortenString(val) {
      return  val.length > 16 ? val.substring(0,15) + '...' : val
    },
    resultFormatter(val) {
      if (val === 0) {
        return '未处理'
      }
      if (val === 1) {
        return '已同意'
      }
      if (val === 2) {
        return '已拒绝'
      }
    },
    getAvatarPath(val) {
      // console.log('获取头像')
      return getAvatarPathGlobal(val)
    },
    contentChange(val) {
      console.log(val)
      if (val === 0) {
        this.getSendList()
        this.fromMe = true
      }
      if (val === 1) {
        this.fromMe = false
      }
    }
  }
}
</script>

<style scoped>
.disk-main-friend-apply-record-content-item-divider {
  text-align: center;
  display: inline-block;
  width: 100%;
  height: 1px;
  background-color: #f1f2f4;
}
.disk-main-friend-apply-record-content-item {
  margin-top: 10px;
  display: flex;
  align-items: center;
}
.disk-main-friend-apply-record-content-avatar {
  margin-left: 50px;
  margin-right: 20px;
}
.disk-main-friend-apply-record-content-username {
  margin-right: 20px;
}
.disk-main-friend-apply-record-content-applytime {
  margin-right: 20px;
}
.disk-main-friend-apply-record-content-operation {
  position: absolute;
  right: 70px;
}
.disk-main-friend-apply-record-content-empty {
  text-align: center;
}
</style>
