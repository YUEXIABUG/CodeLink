<script setup>
import {computed, onMounted, reactive, ref} from "vue";
import store from "../../../store/store.js";
import {useRouter} from "vue-router";
import myAxios from "../../../plugins/myAxios.js";
import {ElMessage} from "element-plus";
import {Search} from "@element-plus/icons-vue";

const loginUser = computed(() => store.state.currentUser);

const router = useRouter();
let projectList = ref([]);
let needTags = ref([]);
let dialogFormVisible1 = ref(false)
let dialogFormVisible2 = ref(false)
let dialogFormVisible3 = ref(false)
let formLabelWidth = '140px'
let searchContent = ref('')
let searchWidth = ref('300px')
const form = reactive({
  projectName: '',
  pid: 0,
  tid: 0,
  delivery: false,
  type: [],
  resource: '',
  remark: '',
  password: ''
})

// 页面初始化
onMounted(async () => {
  await getAllProjects()
})

// 获取所有项目
async function getAllProjects () {
  myAxios.post('/project/getAllProjects', {}).then(function (response) {
    projectList.value = response.data.data

    // 将需求标签存入needTags
    for (let i = 0; i < projectList.value.length; i++) {
      projectList.value[i].project.needTags = projectList.value[i].project.needTags.split(',')
      // 查看每个标签，如果有'['或者']'就删除
      for (let j = 0; j < projectList.value[i].project.needTags.length; j++) {
        if (projectList.value[i].project.needTags[j].indexOf('[') !== -1 || projectList.value[i].project.needTags[j].indexOf(']') !== -1) {
          projectList.value[i].project.needTags[j] = projectList.value[i].project.needTags[j].replace('[', '').replace(']', '')
        }
      }
      needTags.value.push(projectList.value[i].project.needTags)
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

// 显示对话框
const showDialog = (projectName, pid, isPersonal, tid) => {
  if (isPersonal === 0) {
    dialogFormVisible1.value = true
  } else if (isPersonal === 1){
    dialogFormVisible2.value = true
  }

  form.projectName = projectName
  form.pid = pid
  form.tid = tid
}

const toProjectPage = (pid, tid) => {
  router.push({
    path: '/projects/projectDetail',
    query: {
      pid: pid,
      tid: tid
    }
  })
}

const checkTeamPassword = (tid, password) => {
  myAxios.post('/team/checkTeamPassword', {
    tid: tid,
    teamPassword: password
  }).then(function (response) {
    console.log(response.data)
    if (response.data.code === 200) {
      ElMessage({
        showClose: true,
        message: '密码正确！',
        type: 'success',
        center: true,
        style: "width: 300px",
      })
      dialogFormVisible2.value = false
      dialogFormVisible3.value = true
    } else if(response.data.code === 401) {
      ElMessage({
        showClose: true,
        message: '密码错误！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
      form.password = ''
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
const sendJoinAply = (pid, remark) => {
  myAxios.post('/teamMessages/applyJoinTeam', {
    uid: loginUser.value.uid,
    pid: pid,
    remark: remark
  }).then(function (response) {
    console.log(response.data)
    if (response.data.code === 200) {
      ElMessage({
        showClose: true,
        message: '发送成功！',
        type: 'success',
        center: true,
        style: "width: 300px",
      })
      dialogFormVisible1.value = false
      dialogFormVisible3.value = false
    } else if(response.data.code === 402) {
      ElMessage({
        showClose: true,
        message: '您已加入项目！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    } else if(response.data.code === 403) {
      ElMessage({
        showClose: true,
        message: '用户未登录！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    } else if(response.data.code === 404) {
      ElMessage({
        showClose: true,
        message: '您已经申请过该项目！',
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

// 搜索项目
const searchProject = () => {
  myAxios.post('/project/searchProject', {
    searchContent: searchContent.value
  }).then(function (response) {
    projectList.value = response.data.data

    // 将需求标签存入needTags
    for (let i = 0; i < projectList.value.length; i++) {
      projectList.value[i].project.needTags = projectList.value[i].project.needTags.split(',')
      // 查看每个标签，如果有'['或者']'就删除
      for (let j = 0; j < projectList.value[i].project.needTags.length; j++) {
        if (projectList.value[i].project.needTags[j].indexOf('[') !== -1 || projectList.value[i].project.needTags[j].indexOf(']') !== -1) {
          projectList.value[i].project.needTags[j] = projectList.value[i].project.needTags[j].replace('[', '').replace(']', '')
        }
      }
      needTags.value.push(projectList.value[i].project.needTags)
    }

    console.log(projectList.value)
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

</script>

<template>
  <div>
    <el-input
        class="search"
        v-model="searchContent"
        placeholder="请输入项目名称"
    >
      <template #append>
        <el-button :icon="Search" @click="searchProject"/>
      </template>
    </el-input>
    <el-main style="position: absolute; left: 230px; top: 160px; width: 82%; height: 81vh; background-color: #f0f2f5">
      <el-empty v-if="projectList.length === 0" description="找不到符合要求的项目噢"/>
      <el-row v-else :gutter="20">
        <el-col
            v-for="(project, index) in projectList"
            :key="project.pid"
            :span="4.5"
        >
          <el-card class="card" shadow="hover">
            <div  class="demo-image__preview">
              <el-image
                  :src="project.team.teamAvatar"
                  fit="contain"
                  :initial-index="4"
                  style="height: 140px; width: 140px; margin: 0 auto; display: block;"
              />
            </div>
            <div style="position: relative; padding: 1px; width: 100%; height: 200px;">
              <!--显示projectName-->
              <br/><span style="text-align: center; display: block">{{ project.project.projectName }}</span>
              <div style="position: absolute; top: 20%">
                <br/><span style="font-size: 14px; color: gray">需求标签</span>
                <el-tag v-if="!project.project.needTags || (project.project.needTags?.length === 1 && project.project.needTags[0] === '')" class="ml-2" size="small" type="info">
                  暂无标签
                </el-tag>
                <el-tag v-else v-for="(tag, index) in project.project.needTags" class="ml-2" size="small">
                  {{ tag }}
                </el-tag>
              </div>
              <div style="position: absolute; top: 60%; width: 100%;">
                <el-button type="primary" plain style="position: relative; float: left; left: 3%" @click="toProjectPage(project.project.pid, project.team.tid)">查看项目</el-button>
                <el-button type="primary" plain style="position: relative; float: left; left: 8%" @click="showDialog(project.project.projectName, project.project.pid, project.team.isPersonal, project.team.tid)">加入项目</el-button>
                <el-dialog v-model="dialogFormVisible1" :title='"确认向"+form.projectName+"发送加入请求"' width="500">
                  <el-form :model="form">
                    <el-form-item label="备注信息" :label-width="formLabelWidth" style="position: relative; left: -30px">
                      <el-input v-model="form.remark" style="width: 300px" :rows="2" type="textarea" placeholder="请输入备注信息"/>
                    </el-form-item>
                  </el-form>
                  <template #footer>
                    <div class="dialog-footer">
                      <el-button @click="dialogFormVisible1 = false">取消</el-button>
                      <el-button type="primary" @click="sendJoinAply(form.pid, form.remark)">确认</el-button>
                    </div>
                  </template>
                </el-dialog>
                <el-dialog v-model="dialogFormVisible2" :title="验证密码" width="500">
                  <el-form :model="form">
                    <el-form-item label="队伍密码" :label-width="formLabelWidth" style="position: relative; left: -30px">
                      <el-input v-model="form.password" style="width: 300px" :rows="2" type="password" placeholder="请输入密码"/>
                    </el-form-item>
                  </el-form>
                  <template #footer>
                    <div class="dialog-footer">
                      <el-button @click="dialogFormVisible2 = false">取消</el-button>
                      <el-button type="primary" @click="checkTeamPassword(form.tid, form.password)">验证密码</el-button>
                    </div>
                  </template>
                </el-dialog>
                <el-dialog v-model="dialogFormVisible3" :title='"确认向"+form.projectName+"发送加入请求"' width="500">
                  <el-form :model="form">
                    <el-form-item label="备注信息" :label-width="formLabelWidth" style="position: relative; left: -30px">
                      <el-input v-model="form.remark" style="width: 300px" :rows="2" type="textarea" placeholder="请输入备注信息"/>
                    </el-form-item>
                  </el-form>
                  <template #footer>
                    <div class="dialog-footer">
                      <el-button @click="dialogFormVisible3 = false">取消</el-button>
                      <el-button type="primary" @click="sendJoinAply(form.pid, form.remark)">确认</el-button>
                    </div>
                  </template>
                </el-dialog>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<style scoped>
.card {
  width: 250px;
  height: 350px;
  margin-bottom: 20px;
}

.search {
  position: absolute;
  left: 230px;
  top: 100px;
  width: v-bind("searchWidth");
  height: 40px;
  border: 0.1px solid #ccc;
  border-radius: 5px;
  margin-bottom: 20px;
  transition-duration: 0.3s;
}

.searchButton {

}

</style>