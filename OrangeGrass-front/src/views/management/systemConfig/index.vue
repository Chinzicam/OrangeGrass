<template>
  <div class="system-config-container">
    <div class="system-config-header" style="margin-bottom: 20px">
      <el-button size="small" round type="success" @click="handleSystemCofigDialogButtonClick()">新增</el-button>
    </div>
    <div class="system-config-body">
      <el-table :data="systemConfigs">
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="value" label="是否启用">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.value"
                       :active-value='1'
                       :inactive-value='0'
                       @change="changeStatus($event, scope.row, scope.$index)" >

            </el-switch>
          </template>
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <div v-if="scope.row.name === '图片上传时鉴黄'" style="display: flex;align-items: center">
              <span>涉黄分数：</span>
              <el-slider
                style="width: 240px"
                :max="100"
                v-model="nsfwLimit" @change="handleNsfwSliderChange"></el-slider>
            </div>
            <div v-if="scope.row.name === '限速'" style="display: flex;align-items: center">
              <span>限速值：</span>
              <el-slider
                style="width: 240px"
                :max="1024*10"
                :format-tooltip="limitRateFormatTooltip"
                v-model="limitRate" @change="handleSpeedLimitSliderChange"></el-slider>
            </div>
            <div v-if="scope.row.name === '广告投放'" style="display: flex;align-items: center">
              <span>广告投放时长：</span>
              <el-slider
                style="width: 240px"
                :max="30"
                :format-tooltip="advertisementLengthFormatTooltip"
                v-model="advertisementLength" @change="handleAdvertisementLengthChange"
                ></el-slider>
            </div>
          </template>
        </el-table-column>
<!--        <el-table-column label="操作">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="danger" round size="small" @click="handleDelete(scope.row)">删除</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </div>
<!--    <div class="system-config-item-add">-->
<!--      <el-dialog :visible="itemAddDialogVisibility"-->
<!--                  @close="handleItemAddClick">-->
<!--        <el-form v-model="itemAddForm">-->
<!--          <div>-->
<!--            设置名称：-->
<!--            <el-select v-model="itemAddForm.id">-->
<!--              <el-option-group>-->
<!--                <el-option v-for="item in this.configItems"-->
<!--                           :label="item.name"-->
<!--                           :value="item.id">-->
<!--                </el-option>-->
<!--              </el-option-group>-->
<!--            </el-select>-->
<!--          </div>-->
<!--          <div style="margin-top: 10px">-->
<!--            设置值-->
<!--            <el-select v-model="itemAddForm.value">-->
<!--              <el-option-group>-->
<!--                <el-option label="启用" value="1"></el-option>-->
<!--                <el-option label="禁用" value="0"></el-option>-->
<!--              </el-option-group>-->
<!--            </el-select>-->
<!--          </div>-->
<!--          <div style="text-align: center;margin-top: 20px">-->
<!--            <el-button type="success" round size="small" @click="handleItemAdd()">-->
<!--              提交-->
<!--            </el-button>-->
<!--          </div>-->
<!--        </el-form>-->
<!--      </el-dialog>-->
<!--    </div>-->
<!--    <div class="system-config-check">-->
<!--      <el-dialog :visible="configCheckDialogVisibility"-->
<!--                 @close="handleSystemConfigCheck">-->
<!--        <div>-->
<!--          <el-button type="primary" round size="small" @click="handleItemAddClick">新增</el-button>-->
<!--        </div>-->
<!--        <el-table :data="systemConfigDetail">-->
<!--          <el-table-column prop="configItemName" label="设置项名称"></el-table-column>-->
<!--          <el-table-column prop="value" label="设置项值">-->
<!--            <template slot-scope="scope">-->
<!--              {{getLabel(scope.row)}}-->
<!--            </template>-->
<!--          </el-table-column>-->
<!--        </el-table>-->
<!--      </el-dialog>-->
<!--    </div>-->
    <div class="system-config-add">
      <el-dialog :visible="this.configAddDialogVisibility"
                 @close="handleSystemCofigDialogButtonClick"
                 style="text-align: center">
        <el-form v-model="systemConfigAddForm" style="text-align: center">
          请输入名称：<el-input v-model="systemConfigAddForm.configName" style="width: 170px">方案名称</el-input>
        </el-form>
        <el-button size="small" round type="primary" @click="submitSystemConfig()" style="margin-top: 20px">提交</el-button>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import Fileservice from "../../../utils/FileRequest";

export default {
  name: "systemConfig",
  data () {
    return {
      advertisementLength:0,
      nsfwLimit:0,
      limitRate:0,
      configItems: [],
      currentConfig: {},
      itemAddForm: {

      },
      itemAddDialogVisibility: false,
      systemConfigAddForm: {
        configName:''
      },
      systemConfigDetail: {

      },
      configAddDialogVisibility: false,
      configCheckDialogVisibility: false,
      systemConfigs: []
    }
  },
  created() {
    this.getSystemConfigs()
    this.getItems()
  },
  methods : {
    handleAdvertisementLengthChange(val) {
      Fileservice({
        url: 'api/system/adlength',
        method: 'put',
        params: {
          advertisementLength: this.advertisementLength
        }
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    advertisementLengthFormatTooltip(val) {
      return val + '秒'
    },
    handleNsfwSliderChange(val) {
      Fileservice({
        url: 'api/system/nsfw',
        method: 'put',
        params: {
          score: this.nsfwLimit
        }
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    limitRateFormatTooltip(val) {
      val = val * 1024
      if(val === 0) return "0 B"
      var k = 1024;
      var sizes = ['B','KB','MB','GB','PB','TB','EB','ZB','YB'],
        i = Math.floor(Math.log(val) / Math.log(k));
      return (val / Math.pow(k,i)).toPrecision(3) + "" + sizes[i] + '/S'
    },
    handleSpeedLimitSliderChange(val) {
      console.log(val)
      Fileservice({
        url: 'api/system/speed',
        method: 'put',
        params: {
          rate: this.limitRate
        }
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleDelete(row) {
      Fileservice({
        url: 'api/system/item',
        method: 'delete',
        params:{
          id:row.id
        }
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
          this.getSystemConfigs()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    changeStatus(e, row, index) {
      //e返回状态，row当前行数据，index下标
      console.log(e, row, index);
      Fileservice({
        url: 'api/system/item',
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
    getItems() {
      Fileservice({
        url: 'api/system/items',
        method: 'get',
      }).then(res=>{
        this.configItems = res.data
      })
    },
    handleSystemConfigCheck(row) {
      this.currentConfig = row
      this.configCheckDialogVisibility = !this.configCheckDialogVisibility
      console.log(row)
      Fileservice({
        url: 'api/system/detail',
        method: 'get',
        params: {id: row.id}
      }).then(res=>{
        if (res.code === 1) {
          this.systemConfigDetail = res.data
        }
      })
    },
    getSystemConfigs(){
      Fileservice({
        url: 'api/system/items',
        method: 'get',
      }).then(res=>{
        this.systemConfigs = res.data
        console.log(this.systemConfigs)
        for(let i = 0 ; i < this.systemConfigs.length ; ++i) {
          if (this.systemConfigs[i].name === '限速') {
            this.limitRate = parseInt(this.systemConfigs[i].extra)
          }
          if (this.systemConfigs[i].name === '图片上传时鉴黄') {
            this.nsfwLimit = parseFloat(this.systemConfigs[i].extra)
          }
          if (this.systemConfigs[i].name === '广告投放') {
            this.advertisementLength = parseInt(this.systemConfigs[i].extra)
          }
        }
      })
    },
    handleSystemCofigDialogButtonClick() {
      this.configAddDialogVisibility = !this.configAddDialogVisibility
    },
    getLabel(row) {
      console.log(row)
      if (row.value === 1+'') {
        return '启用'
      } else {
        return '禁用'
      }
    },
    submitSystemConfig() {
      console.log(this.systemConfigAddForm)
      Fileservice({
        url: 'api/system/item',
        method: 'post',
        params: {name: this.systemConfigAddForm.configName}
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
          this.getSystemConfigs()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleItemAddClick() {
      this.itemAddDialogVisibility = !this.itemAddDialogVisibility
    },
    handleItemAdd() {
      Fileservice({
        url: 'api/system/item',
        method: 'post',
        params: {
          configId: this.currentConfig.id,
          itemId: this.itemAddForm.id,
          itemValue: this.itemAddForm.value
        }
      }).then(res=>{
        if (res.code === 1) {
          this.$message.success(res.msg)
          Fileservice({
            url: 'api/system/detail',
            method: 'get',
            params: {id: this.currentConfig.id}
          }).then(res=>{
            if (res.code === 1) {
              this.systemConfigDetail = res.data
            }
          })
        } else {
          this.$message.error(res.msg)
        }
      })
    },
  }
}
</script>

<style scoped>

</style>
