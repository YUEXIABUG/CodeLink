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
  let dialogVisible = ref(false)
  const form = reactive({
    name: '',
    uid: 0,
    delivery: false,
    type: [],
    resource: '',
    desc: '',
  })

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

  // 跳转到用户主页
  const toUserPage = (uid) => {
    router.push({
      path: '/user',
      query: {
        uid: uid
      }
    })
  }

  const showDialog = (username, uid) => {
    dialogVisible.value = true
    form.name = username
    form.uid = uid
  }

  const handleClose = () => {
    dialogVisible.value = false
  }

  const deleteFriends = (uid) => {
    myAxios.post('/friends/deleteFriends', {
      uid: loginUser.value.uid,
      friendUid: uid
    }).then(function (response){
      if(response.data.code === 200){
        ElMessage({
          showClose: true,
          message: '删除好友成功！',
          type: 'success',
          center: true,
          style: "width: 300px",
        })
        getFriendsList()
        dialogVisible.value = false
      }
    }).catch(function (error){
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
              <el-button type="primary" plain style="position: relative; float: left; left: 3%" @click="toUserPage(user.uid)">查看主页</el-button>
              <el-button type="danger" plain style="position: relative; float: left; left: 8%" @click="showDialog(user.username, user.uid)">删除好友</el-button>
              <el-dialog
                  v-model="dialogVisible"
                  title="删除好友"
                  width="500"
                  :before-close="handleClose"
              >
                <span>确定要删除好友吗</span>
                <template #footer>
                  <div class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="danger" @click="deleteFriends(form.uid)">删除</el-button>
                  </div>
                </template>
              </el-dialog>
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