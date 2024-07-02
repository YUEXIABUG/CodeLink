<script setup lang="ts">
  import {computed, onMounted, ref} from "vue";
  import myAxios from "../../plugins/myAxios.js";
  import {useRouter} from "vue-router";
  import store from "../../store/store.js";
  import {ElMessage} from "element-plus";
  import TeamChatHeader from "./components/TeamChatHeader.vue";

  const loginUser = computed(() => store.state.currentUser);
  const router = useRouter();

  let tid = ref(0)
  let pid = ref(0)
  let projectDetail = ref([])
  let teamDetail = ref([])
  let projectCreator = ref([])
  let memberList = ref([])
  let teamChatRecord = ref([])
  let message =  ref('')

  onMounted(async () => {
    if (!loginUser.value) {
      router.push('/login');
      return;
    }

    tid = parseInt(router.currentRoute.value.query.tid, 10)
    pid = parseInt(router.currentRoute.value.query.pid, 10)

    await getProjectInfo()
    await getTeamChatRecord(true)
    await getTeamMemberList()

    // 将getFriendsChatRecord设置为定时任务，每隔1秒刷新一次聊天记录，实现实时聊天。检测当前路由是否为好友聊天页面，如果是则开启定时任务。
    setInterval(() => {
      if (router.currentRoute.value.path === '/friends/chat') {
        getTeamChatRecord(false)
      }
    }, 1000)
  })

  const getProjectInfo = async () => {
    myAxios.post('/project/getProjectDetail', {
      pid: pid,
    }).then(function (response) {
      if (response.data.code === 200) {
        projectDetail.value = response.data.data.project
        teamDetail.value = response.data.data.team
      } else {
        console.log(response.data)
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

  const getTeamChatRecord = async (scrollToBottom = false) => {
    myAxios.post("/chat/getTeamChatRecord", {
      uid: loginUser.value.uid,
      tid: tid
    }).then(function (response) {
      if (response.data.code === 200) {
        teamChatRecord.value = response.data.data

        // 如果需要滚动到底部，则定位滚动条到最底部
        if (scrollToBottom) {
          scrollToBottom1()
        }
      } else {
        console.log(response.data)
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

  const sendTeamChatMessage  = () => {
    if (message.value === '') {
      ElMessage({
        showClose: true,
        message: '发送内容不能为空！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
      return
    }

    myAxios.post("/chat/sendTeamChatMessage", {
      uid: loginUser.value.uid,
      tid: tid,
      text: message.value
    }).then(function (response) {
      if (response.data.code === 200) {
        getTeamChatRecord(true);
        message.value = ''
      } else {
        console.log(response.data)
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

  const scrollToBottom1 = () => {
    const chatMain = document.querySelector('.el-main');
    chatMain.scrollTop = chatMain.scrollHeight;
  };

  const getTeamMemberList = () => {
    myAxios.post('/project/getProjectUsers', {
      pid: pid,
      tid: tid
    }).then(function (response) {
      if (response.data.code === 200) {
        // 遍历response.data.data，将userRole为1的用户添加进projectCreator，其他用户添加进memberList
        response.data.data.forEach((item) => {
          if (item.userRole === 1) {
            projectCreator.value = item
          } else {
            memberList.value.push(item)
          }
        })

        console.log(projectCreator.value)
        console.log(memberList.value)
      } else {
        console.log(response.data)
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

  const toUserDetail = (uid) => {
    router.push({
      path: '/user',
      query: {
        uid: uid
      }
    })
  }


</script>

<template>
  <TeamChatHeader/>
  <el-card class="chartBackground">
    <el-container style="height: 800px">
      <el-header style="border-bottom: 1px solid rgba(133,131,131,0.41)">
        <el-avatar :size="50" :src="teamDetail.teamAvatar" style="position: relative; float: left"></el-avatar>
        <span style="position: relative; left: 2%; top: 20%">{{projectDetail.projectName}}</span>
      </el-header>
      <el-container>
        <el-container style="height: 80vh">
          <el-main style="border-bottom: 1px solid rgba(133,131,131,0.41); height: 80%">
            <el-empty v-if="teamChatRecord.length === 0" description="暂无聊天记录,开始聊天吧！"></el-empty>
            <div
                style="width: 100%;"
                v-for="chatMessage in teamChatRecord"
                key="chatMessage.id"
            >
              <div v-if="chatMessage.uid === loginUser.uid" class="chat-receiver">
                <div>
                  <img :size="50" :src="chatMessage.userAvatar" />
                </div>
                <div>{{chatMessage.username}}</div>
                <div>
                  <div class="chat-right_triangle"></div>
                  <span>{{chatMessage.text}}</span>
                </div>
              </div>

              <div v-else class="chat-sender">
                <div>
                  <img :size="50" :src="chatMessage.userAvatar" />
                </div>
                <div>{{chatMessage.username}}</div>
                <div>
                  <div class="chat-left_triangle"></div>
                  <span>{{chatMessage.text}}</span>
                </div>
              </div>
            </div>
          </el-main>
          <el-footer style="height: 25%">
            <el-input
                type="textarea"
                placeholder="请输入内容，回车换行"
                rows="5"
                resize="none"
                style="position: relative; top: 8%; width: 95%;"
                v-model="message"
            ></el-input>
            <el-button
                class="sendButton"
                type="primary"
                @click="sendTeamChatMessage"
            >
              发送
            </el-button>
          </el-footer>
        </el-container>
        <el-aside style="border-left: 1px solid rgba(133,131,131,0.41); width: 200px;">
          <div class="creator">
            <p style="position: absolute; left: 10px; top: 20px; color: rgba(131,131,131,0.76)">创建者</p>
            <div @click="toUserDetail(projectCreator.uid)" class="creatorDiv">
              <el-avatar :size="50" :src="projectCreator.userAvatar" style="position: absolute; left: 10px; top: 60px"></el-avatar>
              <span style="position: absolute; top: 75px; left: 40%">{{projectCreator.username}}</span>
            </div>
          </div>
          <div style="position: relative; float: left; width: 100%">
            <p style="position: absolute; left: 10px; color: rgba(131,131,131,0.76)">项目成员</p>
            <div
                v-for="user in memberList"
                key="user.uid"
                class="memberDiv"
            >
              <div class="member">
                <el-avatar class="memberAvatar" :size="50" :src="user.userAvatar" @click="toUserDetail(user.uid)"></el-avatar>
                <span class="memberName" @click="toUserDetail(user.uid)">{{user.username}}</span>
              </div>
            </div>
          </div>
        </el-aside>
      </el-container>
    </el-container>
  </el-card>
</template>

<style scoped>
  .chartBackground {
    position: absolute;
    left: 50%;
    top: 53%;
    transform: translate(-50%, -50%);
    width: 70%;
    height: 83%;
  }

  .sendButton {
    position: relative;
    top: 8%;
    left: 1%;
    width: 5%;
    height: 115px;
  }

  .creator {
    position: relative;
    float: left;
    width: 100%;
    height: 150px;
  }

  .creatorDiv:hover {
    cursor: pointer;
  }

  .memberDiv {
    position: relative;
    float: left;
    width: 100%;
    height: 60px;
    top: 30px;
  }

  .memberAvatar {
    position: absolute;
    left: 10px;
  }
  .memberAvatar:hover {
    cursor: pointer;
  }

  .memberName {
    position: absolute;
    left: 40%;
    top: 10px;
  }
  .memberName:hover {
    cursor: pointer;
  }

</style>

<style scoped src="../../style/chatDivStyle.css">
</style>