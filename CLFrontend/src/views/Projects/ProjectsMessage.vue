<script setup>
  import { onMounted, ref, reactive, computed } from "vue";
  import { useStore } from "vuex";
  import { useRouter } from "vue-router";
  import {ElMessage} from "element-plus";
  import myAxios from "../../plugins/myAxios.js";

  const router = useRouter();
  const store = useStore();

  const loginUser = computed(() => store.state.currentUser);

  let teamMessagesList = ref([])
  let userTags = ref([])
  let dialogVisible1 = ref(false)
  let dialogVisible2 = ref(false)
  const form = reactive({
    username: '',
    projectName: '',
    nid: 0,
    pid: 0,
    tid: 0,
    applyUserId: 0,
    delivery: false,
    type: [],
    resource: '',
    remark: '',
  })

  onMounted(async () => {
    if (!loginUser.value) {
      await router.push('/login');
    }

    await getTeamMessagesList();
  });

  const getTeamMessagesList = async () => {
    myAxios.post('/teamMessages/getTeamMessages', {
      uid: loginUser.value.uid
    }).then(function (response) {
      if (response.data.code === 200) {
        teamMessagesList.value = response.data.data

        // 将用户标签存入userTags中
        for (let i = 0; i < teamMessagesList.value.length; i++) {
          teamMessagesList.value[i].user.userTags = teamMessagesList.value[i].user.userTags.split(',')
          // 查看每个标签，如果有'['或者']'就删除
          for (let j = 0; j < teamMessagesList.value[i].user.userTags.length; j++) {
            if (teamMessagesList.value[i].user.userTags[j].indexOf('[') !== -1 || teamMessagesList.value[i].user.userTags[j].indexOf(']') !== -1) {
              teamMessagesList.value[i].user.userTags[j] = teamMessagesList.value[i].user.userTags[j].replace('[', '').replace(']', '')
            }
          }
          userTags.value.push(teamMessagesList.value[i].user.userTags)
        }
      } else {
        ElMessage({
          showClose: true,
          message: '网络异常，请稍后再试！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
      }
    }).catch(function (error) {
      console.log(error)
      ElMessage({
        showClose: true,
        message: '网络异常，请稍后再试！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    })
  }

  const reject = (applyUserId, nid, remark, tid, pid) => {
    form.applyUserId = applyUserId
    form.nid = nid
    form.remark = remark
    form.tid = tid
    form.pid = pid

    dialogVisible1.value = true
  }
  const agree = (applyUserId, nid, remark, tid, pid) => {
    form.applyUserId = applyUserId
    form.nid = nid
    form.remark = remark
    form.tid = tid
    form.pid = pid

    dialogVisible2.value = true
  }

  const rejectJoinApply = (tid, pid, nid, applyUserId) => {
    console.log(tid, pid, nid, applyUserId)
    myAxios.post('/teamMessages/rejectJoinApply', {
      uid: loginUser.value.uid,
      tid: tid,
      pid: pid,
      nid: nid,
      applyUserId: applyUserId
    }).then(function (response) {
      if (response.data.code === 200) {
        ElMessage({
          showClose: true,
          message: '已拒绝申请！',
          type: 'success',
          center: true,
          style: "width: 300px",
        })
        getTeamMessagesList()
        dialogVisible1.value = false
      } else {
        ElMessage({
          showClose: true,
          message: '网络异常，请稍后再试！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
      }
    }).catch(function (error) {
      console.log(error)
      ElMessage({
        showClose: true,
        message: '网络异常，请稍后再试！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    })
  }

  const agreeJoinApply = (tid, pid, nid, applyUserId) => {
    myAxios.post('/teamMessages/agreeJoinApply', {
      uid: loginUser.value.uid,
      tid: tid,
      pid: pid,
      nid: nid,
      applyUserId: applyUserId
    }).then(function (response) {
      if (response.data.code === 200) {
        ElMessage({
          showClose: true,
          message: '已同意申请！',
          type: 'success',
          center: true,
          style: "width: 300px",
        })
        getTeamMessagesList()
        dialogVisible2.value = false
      } else {
        ElMessage({
          showClose: true,
          message: '网络异常，请稍后再试！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
      }
    }).catch(function (error) {
      console.log(error)
      ElMessage({
        showClose: true,
        message: '网络异常，请稍后再试！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    })
  }

  const handleClose1 = () => {
    dialogVisible1.value = false
  }
  const handleClose2 = () => {
    dialogVisible2.value = false
  }

  const toProjectDetailPage = (pid, tid) => {
    router.push({
      path: '/projects/projectDetail',
      query: {
        pid: pid,
        tid: tid
      }
    })
  }

</script>

<template>
  <el-main style="position: absolute; left: 230px; top: 100px; width: 82%; height: 86vh; background-color: #f0f2f5">
    <el-empty v-if="teamMessagesList.length === 0" description="还没有申请信息~"/>
    <el-row v-else :gutter="20" class="mb-4">
      <el-col
          v-for="(teamMessage, index) in teamMessagesList"
          :key="teamMessage.uid"
          :span="4.5"
      >
        <el-card class="card" shadow="hover" v-if="teamMessage.teamMessage.applyMessage !== 4">
          <div  class="demo-image__preview">
            <el-image
                :src="teamMessage.user.userAvatar"
                fit="contain"
                :initial-index="4"
                style="height: 140px; width: 140px; margin: 0 auto; display: block;"
            />
          </div>
          <div style="position: relative; padding: 1px; width: 100%; height: 200px;">
            <p style="text-align: center; display: block">
              <span style="color: rgba(38,153,225,0.9)">{{teamMessage.user.username}}</span>
              <span>申请加入</span>
            </p>
            <p style="text-align: center; display: block">{{teamMessage.project.projectName}}</p>

            <div style="position: absolute; top: 30%; width: 100%; height: 30%">
              <el-tag v-for="(tag, index) in teamMessage.user.userTags" class="ml-2" size="small">
                {{ tag }}
              </el-tag>
              <el-tag v-if="!teamMessage.user.userTags || teamMessage.user.userTags?.length === 0" class="ml-2" size="small" type="info">
                暂无标签
              </el-tag>
            </div>
            <div style="position: absolute; top: 60%; width: 100%;">
              <el-button type="primary" plain style="position: relative; float: left; left: 11%" @click="agree(teamMessage.user.uid, teamMessage.teamMessage.nid, teamMessage.teamMessage.remark, teamMessage.team.tid, teamMessage.project.pid)" v-if="teamMessage.teamMessage.applyMessage===0">同意</el-button>
              <el-button type="danger" plain style="position: relative; float: left; left: 24%" @click="reject(teamMessage.user.uid, teamMessage.teamMessage.nid, teamMessage.teamMessage.remark, teamMessage.team.tid, teamMessage.project.pid)" v-if="teamMessage.teamMessage.applyMessage===0">拒绝</el-button>
              <p v-if="teamMessage.teamMessage.applyMessage===1" style="color: rgba(30,157,30,0.75); position: absolute; left: 31%">已同意申请</p>
              <p v-if="teamMessage.teamMessage.applyMessage===2" style="color: rgba(238,35,35,0.69); position: absolute; left: 31%">已拒绝申请</p>
              <el-dialog
                  v-model="dialogVisible1"
                  title="拒绝加入申请"
                  width="500"
                  :before-close="handleClose1"
              >
                备注信息：
                <div style="left: -1px; background: rgba(243,239,239,0.42); width: 100%; height: 85%; border-radius: 5px">
                  <p style="padding: 5px">{{ form.remark }}</p>
                </div>
                <template #footer>
                  <div class="dialog-footer">
                    <el-button @click="dialogVisible1 = false">取消</el-button>
                    <el-button type="danger" @click="rejectJoinApply(form.tid, form.pid, form.nid, form.applyUserId)">拒绝</el-button>
                  </div>
                </template>
              </el-dialog>
              <el-dialog
                  v-model="dialogVisible2"
                  title="确定要同意申请吗"
                  width="500"
                  :before-close="handleClose2"
              >
                备注信息：
                <div style="left: -1px; background: rgba(243,239,239,0.42); width: 100%; height: 85%; border-radius: 5px">
                  <p style="padding: 5px">{{ form.remark }}</p>
                </div>
                <template #footer>
                  <div class="dialog-footer">
                    <el-button @click="dialogVisible2 = false">取消</el-button>
                    <el-button type="primary" @click="agreeJoinApply(form.tid, form.pid, form.nid, form.applyUserId)">同意</el-button>
                  </div>
                </template>
              </el-dialog>
            </div>
          </div>
        </el-card>
        <el-card class="card" shadow="hover" v-if="teamMessage.teamMessage.finishedMessage === 1">
          <div  class="demo-image__preview">
            <el-image
                :src="teamMessage.team.teamAvatar"
                fit="contain"
                :initial-index="4"
                style="height: 140px; width: 140px; margin: 0 auto; display: block;"
            />
          </div>
          <br/>
          <div style="position: relative; padding: 1px; width: 100%; height: 200px;">
            <p style="text-align: center; display: block;">{{teamMessage.project.projectName}}</p>
            <p style="text-align: center; display: block; color: rgba(0,187,255,0.8)">已完成</p>
            <div style="position: absolute; top: 55%; width: 100%;">
              <el-button type="primary" plain style="position: relative; float: left; left: 28%" @click="toProjectDetailPage(teamMessage.project.pid, teamMessage.project.tid)">查看项目</el-button>
            </div>
          </div>
        </el-card>
        <el-card class="card" shadow="hover" v-if="teamMessage.teamMessage.quitMessage === 1">
          <div  class="demo-image__preview">
            <el-image
                :src="teamMessage.user.userAvatar"
                fit="contain"
                :initial-index="4"
                style="height: 140px; width: 140px; margin: 0 auto; display: block;"
            />
          </div>
          <br/>
          <div style="position: relative; padding: 1px; width: 100%; height: 200px;">
            <p style="text-align: center; display: block">
              <span style="color: rgba(38,153,225,0.9)">{{teamMessage.user.username}}</span>
              <span>退出</span>
            </p>
            <p style="text-align: center; display: block">{{teamMessage.project.projectName}}</p>

            <div style="position: absolute; top: 30%; width: 100%; height: 30%">
            </div>
          </div>
        </el-card>
        <el-card class="card" shadow="hover" v-if="teamMessage.teamMessage.breakMessage === 1">
          <div  class="demo-image__preview">
            <el-image
                :src="teamMessage.team.teamAvatar"
                fit="contain"
                :initial-index="4"
                style="height: 140px; width: 140px; margin: 0 auto; display: block;"
            />
          </div>
          <br/>
          <div style="position: relative; padding: 1px; width: 100%; height: 200px;">
            <p style="text-align: center; display: block;">{{teamMessage.project.projectName}}</p>
            <p style="text-align: center; display: block; color: rgba(255,0,89,0.85)">已被解散</p>
<!--            <div style="position: absolute; top: 55%; width: 100%;">-->
<!--              <el-button type="primary" plain style="position: relative; float: left; left: 28%" @click="toProjectDetailPage(teamMessage.project.pid, teamMessage.project.tid)">查看项目</el-button>-->
<!--            </div>-->
          </div>
        </el-card>
      </el-col>
    </el-row>
  </el-main>

</template>

<style scoped>
  .card {
    width: 250px;
    height: 350px;
    margin-bottom: 20px;
  }
</style>