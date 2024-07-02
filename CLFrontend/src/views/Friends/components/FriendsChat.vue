<script setup>
import {computed, onMounted, reactive, ref} from "vue";
import myAxios from "../../../plugins/myAxios.js";
import {useRouter} from "vue-router";
import store from "../../../store/store.js";
import {ElMessage, ElMessageBox} from "element-plus";

const loginUser = computed(() => store.state.currentUser);
const router = useRouter();

let userList = ref([])
let userTags = ref([]);

onMounted(async () => {
  await getFriendsList()
})

const getFriendsList = async () => {
  myAxios.post('/friends/getFriendsList', {
    uid: loginUser.value.uid
  }).then(function (response){
    if(response.data.code === 200){
      userList.value = response.data.data
      for (let i = 0; i < userList.value.length; i++) {
        if (!userList.value[i].username) {
          userList.value[i].username = 'null'
        }
      }

      // 将用户标签存入userTags
      for (let i = 0; i < userList.value.length; i++) {
        if(!userList.value[i].userTags){
          userList.value[i].userTags = []
        }
        userList.value[i].userTags = userList.value[i].userTags.split(',')
        // 查看每个标签，如果有'['或者']'就删除
        for (let j = 0; j < userList.value[i].userTags.length; j++) {
          if (userList.value[i].userTags[j].indexOf('[') !== -1 || userList.value[i].userTags[j].indexOf(']') !== -1) {
            userList.value[i].userTags[j] = userList.value[i].userTags[j].replace('[', '').replace(']', '')
          }
        }
        userTags.value.push(userList.value[i].userTags)
      }
    }
  }).catch(function (error){
    console.log(error.message)
  })
}

// 跳转到聊天页面
const toChatPage = (uid) => {
  router.push({
    path: '/friends/chat',
    query: {
      uid: uid
    }
  })
}


</script>

<template>
  <el-main style="position: absolute; left: 230px; top: 100px; width: 82%; height: 86vh; background-color: #f0f2f5">
    <el-empty v-if="userList.length === 0" description="暂未添加好友喔~"/>
    <el-row v-else :gutter="20" class="mb-4">
      <el-col
          v-for="(user, index) in userList"
          :key="user.uid"
          :span="4.5"
      >
        <el-card class="card" shadow="hover">
          <div  class="demo-image__preview">
            <el-image
                :src="user.userAvatar"
                fit="contain"
                :preview-src-list="user.userAvatar"
                :initial-index="4"
                style="height: 140px; width: 140px; margin: 0 auto; display: block;"
            />
          </div>
          <div style="position: relative; padding: 1px; width: 100%; height: 200px;">
            <!--居中显示username-->
            <span style="text-align: center; display: block">{{ user.username }}</span>
            <div style="position: absolute; top: 20%">
              <el-tag v-for="(tag, index) in user.userTags" class="ml-2" size="small">
                {{ tag }}
              </el-tag>
              <el-tag v-if="!user.userTags || user.userTags?.length === 0" class="ml-2" size="small" type="info">
                暂无标签
              </el-tag>
            </div>
            <div style="position: absolute; top: 60%; width: 100%;">
              <el-button type="primary" plain style="position: relative; float: left; left: 35%" @click="toChatPage(user.uid)">聊天</el-button>
            </div>
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