<script setup>
  import { onMounted, ref, reactive, computed } from "vue";
  import { useStore } from "vuex";
  import { useRouter } from "vue-router";
  import {ElMessage} from "element-plus";
  import myAxios from "../../../plugins/myAxios.js";

  const router = useRouter();
  const store = useStore();

  const loginUser = computed(() => store.state.currentUser);

  let userList = ref([])
  let userTags = ref([])
  let dialogVisible1 = ref(false)
  let dialogVisible2 = ref(false)
  const form = reactive({
    name: '',
    uid: 0,
    aid: 0,
    delivery: false,
    type: [],
    resource: '',
    desc: '',
  })

  onMounted(async () => {
    if (!loginUser.value) {
      await router.push('/login');
    }

    await getFriendsApplicationList();
  });

  const getFriendsApplicationList = async () => {
    myAxios.post("/friends/getFriendsApplicationList", {
      uid: loginUser.value.uid
    }).then(function (response) {
      if (response.data.code === 200) {
        userList.value = response.data.data

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

  const reject = (applyUserId, aid) => {
    dialogVisible1.value = true
    form.uid = applyUserId
    form.aid = aid
    myAxios.post('/friends/getRemark', {
      aid: aid
    }).then(function (response){
      if(response.data.code === 200){
        form.desc = response.data.data
      } else {
        ElMessage({
          showClose: true,
          message: '网络异常，请稍后再试！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
      }
    }).catch(function (error){
      ElMessage({
        showClose: true,
        message: '网络异常，请稍后再试！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    })
  }
  const agree = (applyUserId, aid) => {
    form.uid = applyUserId
    form.aid = aid
    myAxios.post('/friends/getRemark', {
      aid: aid
    }).then(function (response){
      if(response.data.code === 200){
        form.desc = response.data.data
      } else {
        ElMessage({
          showClose: true,
          message: '网络异常，请稍后再试！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
      }
    }).catch(function (error){
      ElMessage({
        showClose: true,
        message: '网络异常，请稍后再试！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    })

    dialogVisible2.value = true
  }

  const rejectFriendsApplication = (aid) => {
    myAxios.post('/friends/setApplyStatus', {
      aid: aid,
      applyStatus: 2
    }).then(function (response){
      if(response.data.code === 200){
        ElMessage({
          showClose: true,
          message: '拒绝好友申请成功！',
          type: 'success',
          center: true,
          style: "width: 300px",
        })
        getFriendsApplicationList()
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
    }).catch(function (error){
      ElMessage({
        showClose: true,
        message: '网络异常，请稍后再试！',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    })
  }
  const agreeFriendsApplication = (applyUserId, aid) => {
    myAxios.post('/friends/addFriends', {
      uid: loginUser.value.uid,
      applyUserId: applyUserId
    }).then(function (response){
      console.log(response.data)
      if(response.data.code === 200){
        // 设置好友申请状态
        myAxios.post('/friends/setApplyStatus', {
          aid: aid,
          applyStatus: 1
        }).then(function (response){
          if(response.data.code === 200){
            ElMessage({
              showClose: true,
              message: '同意好友申请成功！',
              type: 'success',
              center: true,
              style: "width: 300px",
            })
            getFriendsApplicationList()
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
        }).catch(function (error){
          ElMessage({
            showClose: true,
            message: '网络异常，请稍后再试！',
            type: 'error',
            center: true,
            style: "width: 300px",
          })
        })
        ///////////////////////////////
      } else {
        ElMessage({
          showClose: true,
          message: '网络异常，请稍后再试！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
      }
    }).catch(function (error){
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


</script>

<template>
  <el-main style="position: absolute; left: 230px; top: 100px; width: 82%; height: 86vh; background-color: #f0f2f5">
    <el-empty v-if="userList.length === 0" description="还没有申请信息~"/>
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
            <div style="position: absolute; top: 20%; width: 100%; height: 30%">
              <el-tag v-for="(tag, index) in user.userTags" class="ml-2" size="small">
                {{ tag }}
              </el-tag>
              <el-tag v-if="!user.userTags || user.userTags?.length === 0" class="ml-2" size="small" type="info">
                暂无标签
              </el-tag>
            </div>
            <div style="position: absolute; top: 60%; width: 100%;">
              <el-button type="primary" plain style="position: relative; float: left; left: 11%" @click="agree(user.uid, user.aid)" v-if="user.applyStatus===0">同意</el-button>
              <el-button type="danger" plain style="position: relative; float: left; left: 24%" @click="reject(user.uid, user.aid)" v-if="user.applyStatus===0">拒绝</el-button>
              <p v-if="user.applyStatus===1" style="color: rgba(30,157,30,0.75); position: absolute; left: 31%">已同意申请</p>
              <p v-if="user.applyStatus===2" style="color: rgba(238,35,35,0.69); position: absolute; left: 31%">已拒绝申请</p>
              <el-dialog
                  v-model="dialogVisible1"
                  title="拒绝好友申请"
                  width="500"
                  :before-close="handleClose1"
              >
                备注信息：
                <div style="left: -1px; background: rgba(243,239,239,0.42); width: 100%; height: 85%; border-radius: 5px">
                  <p style="padding: 5px">{{ form.desc }}</p>
                </div>
                <template #footer>
                  <div class="dialog-footer">
                    <el-button @click="dialogVisible1 = false">取消</el-button>
                    <el-button type="danger" @click="rejectFriendsApplication(form.aid)">拒绝</el-button>
                  </div>
                </template>
              </el-dialog>
              <el-dialog
                  v-model="dialogVisible2"
                  title="确定要同意好友申请吗"
                  width="500"
                  :before-close="handleClose2"
              >
                备注信息：
                <div style="left: -1px; background: rgba(243,239,239,0.42); width: 100%; height: 85%; border-radius: 5px">
                  <p style="padding: 5px">{{ form.desc }}</p>
                </div>
                <template #footer>
                  <div class="dialog-footer">
                    <el-button @click="dialogVisible2 = false">取消</el-button>
                    <el-button type="primary" @click="agreeFriendsApplication(form.uid, form.aid)">同意</el-button>
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