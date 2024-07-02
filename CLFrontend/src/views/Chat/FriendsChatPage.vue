<script setup lang="ts">
  import {computed, onMounted, ref} from "vue";
  import myAxios from "../../plugins/myAxios.js";
  import {useRouter} from "vue-router";
  import store from "../../store/store.js";
  import {ElMessage} from "element-plus";
  import FriendsChatHeader from "./components/FriendsChatHeader.vue";

  const loginUser = computed(() => store.state.currentUser);
  const router = useRouter();

  let uid = ref(0)
  let friendInfo = ref([])
  let friendsChatRecord = ref([])
  let message =  ref('')

  onMounted(async () => {
    if (!loginUser.value) {
      router.push('/login');
      return;
    }

    uid = parseInt(router.currentRoute.value.query.uid, 10)

    await getUserInfo()
    await getFriendsChatRecord(true)

    // 将getFriendsChatRecord设置为定时任务，每隔1秒刷新一次聊天记录，实现实时聊天。检测当前路由是否为好友聊天页面，如果是则开启定时任务。
    setInterval(() => {
      if (router.currentRoute.value.path === '/friends/chat') {
        getFriendsChatRecord(false)
      }
    }, 1000)
  })

  const getUserInfo = async () => {
    myAxios.post("/user/getUserInfo", {
      uid: uid
    }).then(function (response) {
      if (response.data.code === 200) {
        friendInfo.value = response.data.data
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
      ElMessage({
        showClose: true,
        message: '网络异常，请稍后再试！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    })
  }

  const getFriendsChatRecord = async (scrollToBottom = false) => {
    myAxios.post("/chat/getFriendsChatRecord", {
      uid: loginUser.value.uid,
      friendUid: uid
    }).then(function (response) {
      if (response.data.code === 200) {
        friendsChatRecord.value = response.data.data

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

  const sendFriendsChatMessage = () => {
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

    myAxios.post("/chat/sendFriendsChatMessage", {
      uid: loginUser.value.uid,
      friendUid: uid,
      text: message.value
    }).then(function (response) {
      if (response.data.code === 200) {
        getFriendsChatRecord(true);
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
</script>

<template>
  <FriendsChatHeader/>
  <el-card class="chartBackground">
    <el-header style="border-bottom: 1px solid rgba(133,131,131,0.41)">
      <el-avatar :size="50" :src="friendInfo.userAvatar" style="position: relative; float: left"></el-avatar>
      <span style="position: relative; left: 2%; top: 20%">{{friendInfo.username}}</span>
    </el-header>
    <el-main style="border-bottom: 1px solid rgba(133,131,131,0.41); height: 600px; overflow-y: auto">
      <el-empty v-if="friendsChatRecord.length === 0" description="暂无聊天记录,开始聊天吧！"></el-empty>
      <div
          style="width: 100%;"
          v-for="chatMessage in friendsChatRecord"
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
    <el-footer style="height: 300px;">
      <el-input
        type="textarea"
        placeholder="请输入内容，回车换行"
        rows="5"
        resize="none"
        style="position: relative; top: 2%; width: 95%;"
        v-model="message"
      ></el-input>
      <el-button
          class="sendButton"
          type="primary"
          @click="sendFriendsChatMessage"
      >
        发送
      </el-button>
    </el-footer>
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
    top: 2%;
    left: 2%;
    width: 5%;
    height: 115px;
  }
</style>

<style scoped src="../../style/chatDivStyle.css">
</style>