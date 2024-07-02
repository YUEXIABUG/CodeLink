<script lang="js" setup>
  import InstancesHeader from "./components/InstancesHeader.vue";
  import myAxios from "../../plugins/myAxios.js";
  import {ElMessage} from "element-plus";
  import {onMounted, ref} from "vue";
  import {useRouter} from "vue-router";

  const router = useRouter()
  let webExampleInfo = ref({})

  onMounted(async () => {
    await getWebExampleInfo()
  })

  async function getWebExampleInfo () {
    myAxios.post('/webExample/getWebExampleInfo', {
      wid: router.currentRoute.value.query.wid
    }).then(function (response) {
      if (response.data.code === 200) {
        webExampleInfo.value = response.data.data
        console.log(webExampleInfo.value)
      } else {
        ElMessage({
          showClose: true,
          message: '网络异常，请稍后再试！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
        console.log(response.data)
      }
    }).catch(function (error) {
      console.log(error.message)
      ElMessage({
        showClose: true,
        message: '网络异常，请稍后再试！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    })
  }
</script>

<template>
  <InstancesHeader/>

  <div style="position: relative; top: 61px; height: 93.85vh;">
    <h1 style="text-align: center;">{{webExampleInfo.webname}}</h1>
    <div style="position:absolute; top: 130px; width: 99%; height: 80%;">
      <el-row :gutter="10">
        <el-col :span="12">
          <el-image
              style="width: 100%; height: 100%"
              :src="webExampleInfo.webPic"
              fit="contain"
          ></el-image>
        </el-col>
        <el-col :span="12">
          <el-card>
            <p style="color: black">网站名称：{{webExampleInfo.webname}}</p>
            <p style="color: black">网站描述：{{webExampleInfo.webDesc}}</p>
            <p><a v-bind:href="webExampleInfo.webSite" target="_blank" style="color: black">网站预览地址：{{webExampleInfo.webSite}}</a></p>
            <p><a v-bind:href="webExampleInfo.webDownloadSite" target="_blank" style="color: black">网站下载地址：{{webExampleInfo.webDownloadSite}}</a></p>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>

</style>