<script setup>
  import {onMounted, ref, watch, reactive, computed} from "vue";
  import {useRouter} from "vue-router";
  import myAxios from "../../../plugins/myAxios.js";
  import {ElMessage} from "element-plus";
  import store from "../../../store/store.js";

  const loginUser = computed(() => store.state.currentUser);

  const router = useRouter();
  let userList = ref([]);
  let userTags = ref([]);
  let dialogFormVisible = ref(false)
  let formLabelWidth = '140px'
  const form = reactive({
    name: '',
    uid: 0,
    delivery: false,
    type: [],
    resource: '',
    desc: '',
  })


  // 页面初始化
  onMounted(async () => {
    await getRecommendUsers()
  })

  // 获取推荐用户
  async function getRecommendUsers () {
    myAxios.post('/user/recommendUsers', {}).then(function (response) {
      userList.value = response.data.data

      // 查看每个用户的username，如果为null，则改为'null'
      for (let i = 0; i < userList.value.length; i++) {
        if (!userList.value[i].username) {
          userList.value[i].username = 'null'
        }
      }

      // 将用户标签存入userTags
      for (let i = 0; i < userList.value.length; i++) {
        userList.value[i].userTags = userList.value[i].userTags.split(',')
        // 查看每个标签，如果有'['或者']'就删除
        for (let j = 0; j < userList.value[i].userTags.length; j++) {
          if (userList.value[i].userTags[j].indexOf('[') !== -1 || userList.value[i].userTags[j].indexOf(']') !== -1) {
            userList.value[i].userTags[j] = userList.value[i].userTags[j].replace('[', '').replace(']', '')
          }
        }
        userTags.value.push(userList.value[i].userTags)
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

  // 跳转到用户主页
  const toUserPage = (uid) => {
    router.push({
      path: '/user',
      query: {
        uid: uid
      }
    })
  }

  // 显示对话框
  const showDialog = (username, uid) => {
    dialogFormVisible.value = true
    form.name = username
    form.uid = uid
  }

  // 添加好友
  const addFriends = (applyUserid) => {
    dialogFormVisible.value = false

    myAxios.post('/friends/sendFriendsApplication', {
      uid: loginUser.value.uid,
      applyUserid: applyUserid,
      remark: form.desc
    }).then(function (response) {
      console.log(response.data)
      if (response.data.code === 200) {
        ElMessage({
          showClose: true,
          message: '好友请求发送成功！',
          type: 'success',
          center: true,
          style: "width: 300px",
        })
      } else {
        ElMessage({
          showClose: true,
          message: response.data.message,
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
</script>

<template>
  <el-main style="position: absolute; left: 230px; top: 100px; width: 82%; height: 86vh; background-color: #f0f2f5">
    <el-empty v-if="userList.length === 0" description="找不到符合要求的伙伴噢"/>
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
                :initial-index="4"
                style="height: 140px; width: 140px; margin: 0 auto; display: block;"
            />
          </div>
          <div style="position: relative; padding: 1px; width: 100%; height: 200px;">
            <!--居中显示username-->
            <span style="text-align: center; display: block">{{ user.username }}</span>
            <div style="position: absolute; top: 20%">
              <el-tag v-if="!user.userTags || user.userTags?.length === 0" class="ml-2" size="small" type="info">
                暂无标签
              </el-tag>
              <el-tag v-else v-for="(tag, index) in user.userTags" class="ml-2" size="small">
                {{ tag }}
              </el-tag>
            </div>
            <div style="position: absolute; top: 60%; width: 100%;">
              <el-button type="primary" plain style="position: relative; float: left; left: 3%" @click="toUserPage(user.uid)">查看主页</el-button>
              <el-button type="primary" plain style="position: relative; float: left; left: 8%" @click="showDialog(user.username, user.uid)">添加好友</el-button>
              <el-dialog v-model="dialogFormVisible" :title='"确认向"+form.name+"发送好友请求"' width="500">
                <el-form :model="form">
                  <el-form-item label="备注信息" :label-width="formLabelWidth" style="position: relative; left: -30px">
                    <el-input v-model="form.desc" style="width: 300px" :rows="2" type="textarea" placeholder="请输入备注信息"/>
                  </el-form-item>
                </el-form>
                <template #footer>
                  <div class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">取消</el-button>
                    <el-button type="primary" @click="addFriends(form.uid)">确认</el-button>
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