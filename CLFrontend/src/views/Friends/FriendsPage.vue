<script setup>
  import FriendsPageHeader from "./components/FriendsPageHeader.vue";
  import FriendsList from "./components/FriendsList.vue";

  import {onMounted, ref, reactive, computed} from "vue";
  import { useStore } from "vuex";
  import {useRouter} from "vue-router";
  import FriendsApplicationList from "./components/FriendsApplicationList.vue";
  import myAxios from "../../plugins/myAxios.js";
  import {ElMessage} from "element-plus";
  import FriendsChat from "./components/FriendsChat.vue";

  const router = useRouter();
  const store = useStore();

  const loginUser = computed(() => store.state.currentUser);
  let friendsApplicationFlag = ref(false);

  // 绑定el-menu-item的index
  const activeIndex = ref('1');
  // 切换el-menu-item的index
  const handleSelect = (index) => {
    activeIndex.value = index;
  }

  onMounted(async () => {
    if (!loginUser.value) {
      await router.push('/login');
    }

    await getFriendsApplicationFlag();
  });

  const getFriendsApplicationFlag = async () => {
    myAxios.post("/friends/getFriendsApplicationFlag", {
      uid: loginUser.value.uid
    }).then(function (response) {
      if (response.data.code === 200) {
        // 如果有好友申请
        if (response.data.data === 0) {
          friendsApplicationFlag.value = true;
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
    <FriendsPageHeader/>

    <div style="position: absolute; width: 100%">
      <el-menu
          default-active="1"
          style="position:absolute; top: 62px; width: 200px; height: 93.5vh"
      >
        <el-menu-item index="1" @click="handleSelect('1')">
          <span>好友列表</span>
        </el-menu-item>
        <el-menu-item index="2" @click="handleSelect('2')">
          <span>聊天</span>
        </el-menu-item>
        <el-menu-item index="3" @click="handleSelect('3')">
          <span>
            好友申请
            <div v-show="friendsApplicationFlag===true" class="friendsApplyFlag"></div>
          </span>
        </el-menu-item>
      </el-menu>

      <FriendsList v-if="activeIndex==='1'"/>
      <FriendsChat v-if="activeIndex==='2'"/>
      <FriendsApplicationList v-if="activeIndex==='3'"/>
    </div>
  </el-container>
</template>

<style scoped>
  .friendsApplyFlag {
    position: absolute;
    top: 21px;
    left: 80%;
    width: 14px;
    height: 14px;
    border-radius: 14px;
    background-color: #FF3B30;
  }
</style>