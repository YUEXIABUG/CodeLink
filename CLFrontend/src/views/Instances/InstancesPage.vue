<script lang="js" setup>
  import InstancesHeader from "./components/InstancesHeader.vue";
  import myAxios from "../../plugins/myAxios.js";
  import {ElMessage} from "element-plus";
  import {onMounted, ref} from "vue";
  import {useRouter} from "vue-router";

  const router = useRouter()
  let webInstances = ref([])

  onMounted(async () => {
    await getWebInstances()
  })

  async function getWebInstances () {
    myAxios.post('/webExample/getWebExamples', {})
      .then(function (response) {
        if (response.data.code === 200) {
          webInstances.value = response.data.data
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

  const toInstanceDetail = (instance) => {
    router.push({
      path: '/instanceDetail',
      query: {
        wid: instance.wid
      }
    })
  }
</script>

<template>
  <InstancesHeader/>
  <div style="position: relative; top: 61px; height: 93.85vh;">
    <h1 style="text-align: center;">网站样例  免费下载</h1>

    <div style="position:absolute; top: 130px; width: 99%; height: 80%;">
      <el-row :gutter="10">
        <el-col
            v-for="instance in webInstances"
            :key="instance.wid"
            :lg="5" :md="9" :sm="11" :xl="12" :xs="100"
        >
          <el-card
              class="card"
              shadow="hover"
              @click="toInstanceDetail(instance)"
          >
            <template #header>{{instance.webname}}</template>
            <img :src="instance.webPic" style="width: 100%; height: 230px">
          </el-card>
        </el-col>
      </el-row>

    </div>
  </div>

  <div>

  </div>

</template>

<style scoped>
  .card {
    width: 350px;
    height: 340px;
    margin: 20px;
  }
  .card:hover {
    cursor: pointer;
  }
</style>