<script setup>
  import ProjectsHeader from "./components/ProjectsHeader.vue";
  import MyProjects from "./MyProjects.vue";

  import {onMounted, ref, computed} from "vue";
  import { useStore } from "vuex";
  import {useRouter} from "vue-router";
  import FinishedProjects from "./FinishedProjects.vue";
  import ProjectsMessage from "./ProjectsMessage.vue";
  import myAxios from "../../plugins/myAxios.js";
  import {ElMessage} from "element-plus";
  import TeamChat from "./TeamChat.vue";

  const router = useRouter();
  const store = useStore();

  const loginUser = computed(() => store.state.currentUser);

  // 绑定el-menu-item的index
  const activeIndex = ref('1');
  // 切换el-menu-item的index
  let projectMessagesFlag = ref(false);

  const handleSelect = (index) => {
    activeIndex.value = index;
  }

  onMounted(async () => {
    if (!loginUser.value) {
      router.push('/login');
    }

    await getProjectMessagesFlag();
  });

  const getProjectMessagesFlag = async () => {
    myAxios.post("/teamMessages/getTeamMessagesFlag", {
      uid: loginUser.value.uid
    }).then(function (response) {
      if (response.data.code === 200) {
        // 如果有好友申请
        if (response.data.data === 0) {
          projectMessagesFlag.value = true;
        } else if (response.data.data === 1) {
          projectMessagesFlag.value = false;
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
  <el-container style="width: 100%; height: 100vh;">
    <ProjectsHeader/>
    <div style="position: absolute; width: 100%">
      <el-menu
          default-active="1"
          style="position:absolute; top: 62px; width: 200px; height: 93.5vh"
      >
        <el-menu-item index="1" @click="handleSelect('1')">
          <span>我的项目</span>
        </el-menu-item>
        <el-menu-item index="2" @click="handleSelect('2')">
          <span>聊天</span>
        </el-menu-item>
        <el-menu-item index="3" @click="handleSelect('3')">
          <span>已完成的项目</span>
        </el-menu-item>
        <el-menu-item index="4" @click="handleSelect('4')">
          <span>项目消息</span>
          <div v-show="projectMessagesFlag===true" class="projectMessagesFlag"></div>
        </el-menu-item>
      </el-menu>


      <MyProjects v-show="activeIndex==='1'"/>
      <TeamChat v-show="activeIndex==='2'"/>
      <FinishedProjects v-show="activeIndex==='3'"/>
      <ProjectsMessage v-show="activeIndex==='4'"/>
    </div>
  </el-container>

</template>

<style scoped>
  .projectMessagesFlag {
    position: absolute;
    top: 21px;
    left: 80%;
    width: 14px;
    height: 14px;
    border-radius: 14px;
    background-color: #FF3B30;
  }
</style>