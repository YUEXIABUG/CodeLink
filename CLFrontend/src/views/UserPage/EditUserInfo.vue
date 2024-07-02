<script setup>
  import UserHeader from "./components/UserHeader.vue";
  import myAxios from "../../plugins/myAxios.js";
  import { useRouter } from "vue-router";
  import { ref, onMounted } from "vue";
  import { useStore } from "vuex";
  import { computed } from "vue";
  import {ElMessage} from "element-plus";

  const router = useRouter();
  const store = useStore();

  const currentUser = computed(() => store.state.currentUser);

  // 声明响应式变量
  let radio = ref(0)
  let userGender = 0
  let usernameInput = ref('')
  let userAvatarInput = ref('')
  let userAgeInput = ref(0)
  let userEmailInput = ref('')
  let userPhoneInput = ref('')
  let personalWebInput = ref('')
  let gitHubInput = ref('')
  let csdnInput = ref('')

  onMounted(() => {
    getUserInfo();
  });

  const getUserInfo = () => {
    console.log(currentUser.value.GitHub)
    usernameInput.value = currentUser.value.username
    userAvatarInput.value = currentUser.value.userAvatar
    userAgeInput.value = currentUser.value.userAge
    radio.value = currentUser.value.userGender
    userEmailInput.value = currentUser.value.userEmail
    userPhoneInput.value = currentUser.value.userPhone
    personalWebInput.value = currentUser.value.personalWeb
    gitHubInput.value = currentUser.value.GitHub
    csdnInput.value = currentUser.value.csdn
  };

  const updateUserInfo = async () => {
    if (radio.value === 0) {
      userGender = 0
    } else if (radio.value === 1) {
      userGender = 1
    } else if (radio.value === 2) {
      userGender = 2
    }

    myAxios.post('/user/updateUserInfo', {
      uid: currentUser.value.uid,
      username: usernameInput.value,
      userAvatar: userAvatarInput.value,
      userAge: userAgeInput.value,
      userGender: userGender,
      userEmail: userEmailInput.value,
      userPhone: userPhoneInput.value,
      github: gitHubInput.value,
      personalWeb: personalWebInput.value,
      csdn: csdnInput.value
    }).then(function (response){
      if(response.data.code === 200){
        // 获得用户的脱敏信息
        myAxios.post('/user/getUserInfo', {
          uid: response.data.data.uid
        }).then(function (response){
          if (response.data.code === 200) {
            store.commit('setUid', response.data.data.uid)
            store.commit('setUserAccount', response.data.data.userAccount)
            store.commit('setUsername', response.data.data.username)
            store.commit('setUserAvatar', response.data.data.userAvatar)
            store.commit('setUserGender', response.data.data.userGender)
            store.commit('setUserAge', response.data.data.userAge)
            store.commit('setGitHub', response.data.data.github)
            store.commit('setPersonalWeb', response.data.data.personalWeb)
            store.commit('setCsdn', response.data.data.csdn)
            store.commit('setUserEmail', response.data.data.userEmail)
            store.commit('setUserPhone', response.data.data.userPhone)

            ElMessage({
              showClose: true,
              message: '修改成功',
              type: 'success',
              center: true,
              style: "width: 250px",
            })

            // 路由跳转
            router.push({
              path: '/user',
              query: {
                uid: currentUser.value.uid
              }
            })
          } else {
            console.log(response)
            ElMessage({
              showClose: true,
              message: '网络异常，请稍后再试！',
              type: 'error',
              center: true,
              style: "width: 300px",
            })
          }
        }).catch(function (error){
          // 如果请求失败，则在控制台内显示错误信息，并进行弹窗提示
          console.log(error.message)
          ElMessage({
            showClose: true,
            message: '网络异常，请稍后再试！',
            type: 'error',
            center: true,
            style: "width: 300px",
          })
        })
      } else {
        console.log(error.message)
        ElMessage({
          showClose: true,
          message: '网络异常，请稍后再试！',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
      }
    }).catch(function (error){
      // 如果请求失败，则在控制台内显示错误信息，并进行弹窗提示
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

  onMounted(() => {
    if (!currentUser.value) {
      router.push('/login');
    }
  });

</script>

<template>
  <UserHeader />
  <div style="position: relative; float: left; width: 100%; height: 93.85vh; ">
    <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 40%; height: 80%; font-size: 120%">
      <p style="position: absolute; left: 17%; top: 5%; width: 100%">
        昵称 &emsp;&emsp;
        <el-input v-model="usernameInput" placeholder="请输入昵称" clearable style="width: 50%" id="username" maxlength="10" show-word-limit/>
      </p>
      <p style="position: absolute; left: 17%; top: 13%; width: 100%">
        头像 &emsp;&emsp;
        <el-input v-model="userAvatarInput" placeholder="请输入头像地址" clearable style="width: 50%" id="userAvatar"/>
      </p>
      <p style="position: absolute; left: 17%; top: 21%; width: 100%">
        年龄 &emsp;&emsp;
        <el-input v-model="userAgeInput" placeholder="请输入年龄" clearable style="width: 15%" id="userAge"/>
      </p>
      <p style="position: absolute; left: 17%; top: 29%; width: 100%">
        性别 &emsp;&emsp;
        <el-radio-group v-model="radio" id="userGender">
          <el-radio :label="0" >保密</el-radio>
          <el-radio :label="1">男</el-radio>
          <el-radio :label="2">女</el-radio>
        </el-radio-group>
      </p>
      <p style="position: absolute; left: 17%; top: 37%; width: 100%">
        邮箱 &emsp;&emsp;
        <el-input v-model="userEmailInput" placeholder="请输入邮箱" clearable style="width: 50%" id="userEmail"/>
      </p>
      <p style="position: absolute; left: 17%; top: 45%; width: 100%">
        电话 &emsp;&emsp;
        <el-input v-model="userPhoneInput" placeholder="请输入电话" clearable style="width: 50%" id="userPhone"/>
      </p>
      <p style="position: absolute; left: 17%; top: 53%; width: 100%">
        博客 &emsp;&emsp;
        <el-input v-model="personalWebInput" placeholder="请输入博客链接" clearable style="width: 50%" id="personalWeb"/>
      </p>
      <p style="position: absolute; left: 17%; top: 61%; width: 100%">
        GitHub &ensp;
        <el-input v-model="gitHubInput" placeholder="请输入GitHub链接" clearable style="width: 50%" id="gitHub"/>
      </p>
      <p style="position: absolute; left: 17%; top: 69%; width: 100%">
        CSDN &emsp;
        <el-input v-model="csdnInput" placeholder="请输入CSDN链接" clearable style="width: 50%" id="csdn"/>
      </p>

      <el-button
          type="primary"
          style="position: absolute; top: 80%; left: 50%; transform: translate(-50%, -50%);"
          @click="updateUserInfo"
      >
        确认修改
      </el-button>
    </div>
  </div>
</template>

<style scoped>

</style>