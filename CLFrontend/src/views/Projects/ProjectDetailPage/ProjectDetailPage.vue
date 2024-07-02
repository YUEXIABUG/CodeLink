<script setup>
  import {useRouter} from "vue-router";
  import {ElMessage} from "element-plus";
  import myAxios from "../../../plugins/myAxios.js";
  import {computed, onMounted, reactive, ref} from "vue";
  import store from "../../../store/store.js";
  import ProjectsHeader from "./components/ProjectsHeader.vue";

  const loginUser = computed(() => store.state.currentUser);

  const router = useRouter();
  // 接收传参
  const pid = parseInt(router.currentRoute.value.query.pid)
  const createUserId =  parseInt(router.currentRoute.value.query.createUserId)
  let projectDetail = ref({})
  let teamDetail = ref({})
  let isCreator = ref(false)
  let isMember = ref(false)
  let isFinished = ref(false)
  let projectTags = ref([])
  let needTags = ref([])
  let userList = ref([])
  let createUserInfo = ref({})
  let userInfo = ref([])
  let createUserTags = ref([])
  let userTags = ref([])
  let dialogFormVisible1 = ref(false)
  let dialogFormVisible2 = ref(false)
  let dialogFormVisible3 = ref(false)
  let dialogFormVisible4 = ref(false)

  let formLabelWidth = '140px'
  const form = reactive({
    projectName: '',
    pid: 0,
    tid: 0,
    delivery: false,
    type: [],
    resource: '',
    desc: '',
  })

  onMounted(async () => {
    if (!loginUser.value) {
      await router.push('/login');
    }

    await getProjectDetail()
  })

  async function getProjectDetail() {
    myAxios.post('/project/getProjectDetail', {
      pid: pid,
    }).then(function (response) {
      if (response.data.code === 200) {
        projectDetail.value = response.data.data.project
        if (projectDetail.value.isFinish === 1) {
          isFinished.value = true
        }
        teamDetail.value = response.data.data.team
        if (projectDetail.value.createUserId === loginUser.value.uid) {
          isCreator.value = true
        }

        // 将项目标签存入projectTags
        projectDetail.value.projectTags = projectDetail.value.projectTags.split(',')
        // 查看每个标签，如果有'['或者']'就删除
        for (let j = 0; j < projectDetail.value.projectTags.length; j++) {
          if (projectDetail.value.projectTags[j].indexOf('[') !== -1 || projectDetail.value.projectTags[j].indexOf(']') !== -1) {
            projectDetail.value.projectTags[j] = projectDetail.value.projectTags[j].replace('[', '').replace(']', '')
          }
        }
        projectTags.value = projectDetail.value.projectTags

        // 将所需标签存入needTags
        projectDetail.value.needTags = projectDetail.value.needTags.split(',')
        // 查看每个标签，如果有'['或者']'就删除
        for (let j = 0; j < projectDetail.value.needTags.length; j++) {
          if (projectDetail.value.needTags[j].indexOf('[') !== -1 || projectDetail.value.needTags[j].indexOf(']') !== -1) {
            projectDetail.value.needTags[j] = projectDetail.value.needTags[j].replace('[', '').replace(']', '')
          }
        }
        needTags.value = projectDetail.value.needTags

        // 将createTime转换为年月日
        let date = new Date(projectDetail.value.createTime)
        let year = date.getFullYear()
        let month = date.getMonth() + 1
        let day = date.getDate()
        projectDetail.value.createTime = year + '年' + month + '月' + day + '日'

        userList.value.push(projectDetail.value.createUserId)
        if (teamDetail.value.usersId && teamDetail.value.usersId !== '') {
          // 将usersId转换为数组, usersId是字符串，形如:'[1000001, 1000007]'
          let usersId = teamDetail.value.usersId
          usersId = usersId.replace('[', '').replace(']', '').split(',')
          for (let i = 0; i < usersId.length; i++) {
            userList.value.push(parseInt(usersId[i]))
          }
        }

        // 登录用户是否是项目成员
        for (let i = 0; i < userList.value.length; i++) {
          if (loginUser.value.uid === userList.value[i]) {
            isMember.value = true
          }
        }

        getUsersInfo()
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

  async function getUsersInfo() {
    myAxios.post('/project/getProjectUsers', {
      pid: projectDetail.value.pid,
      tid: teamDetail.value.tid
    }).then(function (response) {
      if (response.data.code === 200) {
        userList.value = response.data.data

        // 将userRole=1的用户信息存入createUserInfo
        for (let i = 0; i < userList.value.length; i++) {
          if (userList.value[i].userRole === 1) {
            createUserInfo.value = userList.value[i]
          }
        }
        // 将userRole=0的用户信息存入userInfo
        for (let i = 0; i < userList.value.length; i++) {
          if (userList.value[i].userRole === 0) {
            userInfo.value.push(userList.value[i])
          }
        }

        // 将createUser的标签存入createUserTags
        createUserTags.value = createUserInfo.value.userTags.split(',')
        // 查看每个标签，如果有'['或者']'就删除
        for (let j = 0; j < createUserTags.value.length; j++) {
          if (createUserTags.value[j].indexOf('[') !== -1 || createUserTags.value[j].indexOf(']') !== -1) {
            createUserTags.value[j] = createUserTags.value[j].replace('[', '').replace(']', '')
          }
        }

        // 将用户标签存入userTags
        for (let i = 0; i < userInfo.value.length; i++) {
          if(!userInfo.value[i].userTags){
            userInfo.value[i].userTags = []
          }
          userInfo.value[i].userTags = userInfo.value[i].userTags.split(',')
          // 查看每个标签，如果有'['或者']'就删除
          for (let j = 0; j < userInfo.value[i].userTags.length; j++) {
            if (userInfo.value[i].userTags[j].indexOf('[') !== -1 || userInfo.value[i].userTags[j].indexOf(']') !== -1) {
              userInfo.value[i].userTags[j] = userInfo.value[i].userTags[j].replace('[', '').replace(']', '')
            }
          }
          userTags.value.push(userInfo.value[i].userTags)
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

  const toUserPage = (uid) => {
    router.push({
      path: '/user',
      query: {
        uid: uid
      }
    })
  }

  const toEditProjectInfo = (pid, tid, uid) => {
    router.push({
      path: '/projects/editProject',
      query: {
        pid: pid,
        tid: tid,
        uid: uid
      }
    })
  }

  // 显示对话框
  const showDialog1 = (projectName, pid) => {
    dialogFormVisible1.value = true
    form.projectName = projectName
    form.pid = pid
  }
  const showDialog2 = (projectName, pid, tid) => {
    dialogFormVisible2.value = true
    form.projectName = projectName
    form.pid = pid
    form.tid = tid
  }
  const showDialog3 = (projectName, pid, tid) => {
    dialogFormVisible3.value = true
    form.projectName = projectName
    form.pid = pid
    form.tid = tid
  }
  const showDialog4 = (projectName, pid) => {
    dialogFormVisible4.value = true
    form.projectName = projectName
    form.pid = pid
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

  const finishProject = (pid) => {
    myAxios.post('/project/finishProject', {
      uid: loginUser.value.uid,
      pid: pid
    }).then(function (response) {
      console.log(response.data)
      if (response.data.code === 200) {
        ElMessage({
          showClose: true,
          message: '项目已完成！',
          type: 'success',
          center: true,
          style: "width: 300px",
        })
        dialogFormVisible4.value = false
        // 刷新当前页面
        router.push({
          path: '/projects/projectDetail',
          query: {
            pid: pid,
            tid: teamDetail.value.tid
          }
        })
      } else if (response.data.code === 401) {
        ElMessage({
          showClose: true,
          message: '用户未登录！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
      } else if (response.data.code === 402) {
        ElMessage({
          showClose: true,
          message: '您不是项目创建者！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
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

  const quitProject = (pid, tid) => {
    myAxios.post("/team/quitTeam", {
      uid: loginUser.value.uid,
      pid: pid,
      tid: tid
    }).then(function (response){
      console.log(response.data)
      if (response.data.code === 200) {
        ElMessage({
          showClose: true,
          message: '退出成功！',
          type: 'success',
          center: true,
          style: "width: 300px",
        })
        dialogFormVisible2.value = false
        // 返回myProjects页面
        router.push('/projects')
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

  const breakProject = (pid, tid) => {
    myAxios.post("/project/breakProject", {
      uid: loginUser.value.uid,
      pid: pid,
      tid: tid
    }).then(function (response){
      console.log(response.data)
      if (response.data.code === 200) {
        ElMessage({
          showClose: true,
          message: '已解散该项目',
          type: 'success',
          center: true,
          style: "width: 300px",
        })
        dialogFormVisible2.value = false
        // 返回myProjects页面
        router.push('/projects')
      } else if (response.data.code === 401) {
        ElMessage({
          showClose: true,
          message: '登录状态异常，请重新登录！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
      } else if (response.data.code === 402) {
        ElMessage({
          showClose: true,
          message: '无权限解散该项目！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
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

</script>

<template>
  <ProjectsHeader/>
  <el-main style="height: 93.85vh">
    <el-card style="position: relative; float: left; width: 27%; height: 400px">
      <el-avatar :size="180" :src="teamDetail.teamAvatar" class="teamAvatar"></el-avatar>
      <p class="projectName">{{projectDetail.projectName}}</p>
    </el-card>
    <el-card style="position: relative; float: left; left: 2.65%;width: 70%; height: 400px">
      <el-form
          label-width="auto"
          style="position: absolute; left: 35%; top: 10%; transform: translate(-50%, 0); width: 60%; height: 80%;">
        <el-form-item label="项目名称:">{{projectDetail.projectName}}</el-form-item>
        <el-form-item label="项目简介:">
          <div style="width: 100%; height: 100px; border: 1px solid rgba(128,128,128,0.47); border-radius: 10px">
            <p style="position: relative; left: 10px; top: 5px; width: 93.4%; height: 90%;">
              {{projectDetail.projectDes}}
            </p>
          </div>
        </el-form-item>
        <el-form-item label="项目标签:">
          <el-tag v-if="!projectTags || (projectTags?.length === 1 && projectTags[0] === '')" class="ml-2" size="small" type="info">
            暂无标签
          </el-tag>
          <el-tag v-else v-for="tag in projectTags" :key="tag">{{tag}}</el-tag>
        </el-form-item>
        <el-form-item label="所需标签:">
          <el-tag v-if="!needTags || (needTags?.length === 1 && needTags[0] === '')" class="ml-2" size="small" type="info">
            暂无标签
          </el-tag>
          <el-tag v-else v-for="tag in needTags" :key="tag">{{tag}}</el-tag>
        </el-form-item>
        <el-form-item label="创建时间:">{{projectDetail.createTime}}</el-form-item>
      </el-form>
      <el-button v-if="isCreator === true && isFinished === false" @click="toEditProjectInfo(projectDetail.pid, teamDetail.tid, loginUser.uid)" class="toEditProjectInfo">编辑信息</el-button>
      <div class="projectIsFinished">
        <span v-if="isFinished === true" style="font-size: 25px; color: rgba(0,166,255,0.84)">项目已完成</span>
      </div>
    </el-card>
    <el-card style="position: relative; float: left; width: 99.8%; height: 400px; top: 30px">
      <div style="position: absolute; width: 400px; height: 360px; border-right: 1px solid rgba(129,129,129,0.27)">
        <p>项目创建者</p>
        <el-avatar :size="150" :src="createUserInfo.userAvatar" class="createUserAvatar"></el-avatar>
        <p style="position: absolute; top: 65%; left: 50%; transform: translate(-50%, -50%);">{{createUserInfo.username}}</p>
        <div style="position: absolute; top: 75%; left: 50%; transform: translate(-50%, -50%);">
          <el-tag v-if="!createUserTags || (createUserTags?.length === 1 && createUserTags[0] === '')"></el-tag>
          <el-tag v-else v-for="tag in createUserTags" :key="tag">{{tag}}</el-tag>
        </div>
        <el-button
            type="primary"
            style="position: absolute; top: 85%; left: 50%; transform: translate(-50%, -50%);"
            v-if="!isCreator"
            @click="toUserPage(createUserInfo.uid)"
        >
          查看主页
        </el-button>
      </div>
      <div style="position: absolute; left: 430px; width: 1200px; height: 360px;">
        <p>项目成员</p>
        <div v-for="(user, index) in userInfo" :key="user.uid" class="users">
          <el-avatar :size="150" :src="user.userAvatar"></el-avatar>
          <p style="position: absolute; top: 66%; left: 40%; transform: translate(-50%, -50%);">{{user.username}}</p>
          <div style="position: absolute; top: 80%; left: 40%; transform: translate(-50%, -50%);">
            <el-tag v-if="!user.userTags || (user.userTags?.length === 1 && user.userTags[0] === '')"></el-tag>
            <el-tag v-else v-for="(tag, index) in user.userTags" :key="tag">{{tag}}</el-tag>
          </div>
          <el-button
              type="primary"
              @click="toUserPage(user.uid)"
              style="position: absolute; top: 100%; left: 40%; transform: translate(-50%, -50%);"
              v-if="loginUser.uid !== user.uid"
          >
            查看主页
          </el-button>
        </div>
      </div>

      <el-button v-if="isCreator!==true && isMember===false && isFinished === false" class="sendJoinApply" @click="showDialog1(projectDetail.projectName, projectDetail.pid)">申请加入</el-button>
      <el-button v-if="isMember===true && isCreator===false && isFinished === false" type="danger" class="quitProject" @click="showDialog2(projectDetail.projectName, projectDetail.pid, teamDetail.tid)">退出项目</el-button>
      <el-button v-if="isCreator===true && isFinished === false" type="danger" class="breakProject" @click="showDialog3(projectDetail.projectName, projectDetail.pid, teamDetail.tid)">解散项目</el-button>
      <el-button v-if="isCreator===true && isFinished === false" type="primary" class="finishProject" @click="showDialog4(projectDetail.projectName, projectDetail.pid)">项目完成</el-button>

       <el-dialog v-model="dialogFormVisible1" :title='"确认向"+form.projectName+"发送加入请求"' width="500">
        <el-form :model="form">
          <el-form-item label="备注信息" :label-width="formLabelWidth" style="position: relative; left: -30px">
            <el-input v-model="form.desc" style="width: 300px" :rows="2" type="textarea" placeholder="请输入备注信息"/>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogFormVisible1 = false">取消</el-button>
            <el-button type="primary" @click="sendJoinAply(form.pid, form.remark)">确认</el-button>
          </div>
        </template>
      </el-dialog>
      <el-dialog v-model="dialogFormVisible2" :title='"确认退出"+form.projectName+"项目"' width="500">
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogFormVisible2 = false">取消</el-button>
            <el-button type="danger" @click="quitProject(form.pid, form.tid)">确认</el-button>
          </div>
        </template>
      </el-dialog>
      <el-dialog v-model="dialogFormVisible3" :title='"确认解散"+form.projectName+"项目"' width="500">
        <p style="color: #FF3B30">解散项目操作不可逆。</p>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogFormVisible3 = false">取消</el-button>
            <el-button type="danger" @click="breakProject(form.pid, form.tid)">确认</el-button>
          </div>
        </template>
      </el-dialog>
      <el-dialog v-model="dialogFormVisible4" :title='"确认"+form.projectName+"项目已完成"' width="500">
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogFormVisible4 = false">取消</el-button>
            <el-button type="primary" @click="finishProject(form.pid)">确认</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </el-main>

</template>

<style scoped>
  .teamAvatar {
    position: absolute;
    top: 40%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .projectName {
    position: absolute;
    top: 80%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 20px;
  }

  .toEditProjectInfo {
    position: absolute;
    top: 15%;
    left: 90%;
    transform: translate(-50%, -50%);
    border: 0px;
  }
  .toEditProjectInfo:hover {
    background: white;
  }

  .projectIsFinished {
    position: absolute;
    top: 10%;
    left: 90%;
    width: 130px;
    height: 30px;
    transform: translate(-50%, -50%);
  }

  .createUserAvatar {
    position: absolute;
    top: 40%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .users {
    position: relative;
    float: left;
    left: 10%;
    top: 13%;
    width: 200px;
    height: 250px;
  }

  .sendJoinApply {
    position: absolute;
    top: 10%;
    left: 90%;
  }

  .quitProject {
    position: absolute;
    top: 10%;
    left: 90%;
  }

  .breakProject {
    position: absolute;
    top: 10%;
    left: 85%;
  }

  .finishProject {
    position: absolute;
    top: 10%;
    left: 90%;
  }

</style>