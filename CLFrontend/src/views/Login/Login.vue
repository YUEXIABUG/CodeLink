<script lang="js" setup>
  import {useRouter} from "vue-router";
  import LeftArea from "./components/leftArea.vue";
  import {ElMessage} from "element-plus";
  import myAxios from "../../plugins/myAxios.js";
  import { useStore } from "vuex";
  import { computed } from "vue";

  const router = useRouter();
  const store = useStore();

  const toRegister = () => {
    router.push("/register");
  }

  const currentUser = computed(() => store.state.currentUser);

  // 登录
  const login = () =>{
    const userAccount = document.getElementById("userAccount").value;
    const userPassword = document.getElementById("userPassword").value;

    const data = {
      userAccount: userAccount,
      userPassword: userPassword
    }

    myAxios.post('/user/login', {
        userAccount: data.userAccount,
        userPassword: data.userPassword
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
            store.commit('setUserTags', response.data.data.userTags)
            store.commit('setGitHub', response.data.data.github)
            store.commit('setPersonalWeb', response.data.data.personalWeb)
            store.commit('setCsdn', response.data.data.csdn)
            store.commit('setUserEmail', response.data.data.userEmail)
            store.commit('setUserPhone', response.data.data.userPhone)

            // 路由跳转
            router.push('/home')
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
      } else if (response.data.code === 400) {
        ElMessage({
          showClose: true,
          message: '账号不存在',
          type: 'error',
          center: true,
          style: "width: 250px",
        })
      } else if (response.data.code === 401) {
        ElMessage({
          showClose: true,
          message: '账号或密码错误',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
      } else if (response.data.code === 500) {
        ElMessage({
          showClose: true,
          message: '登录失败',
          type: 'error',
          center: true,
          style: "width: 250px",
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
</script>

<template>
  <leftArea/>
  <div style="white-space: nowrap; position: relative; float: left; width: 50%; height: 100vh">
    <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 60%; height: 40%">
      <p style="font-size: 30px; position: absolute">登录</p><br/><br/><br/><br/>
      <p>账号</p>
      <input class="inputUserAccount" type="text" id="userAccount"/><br/><br/><br/>
      <p>密码</p>
      <input class="inputUserPassword" type="password" id="userPassword"/><br/><br/><br/>
      <button type="button" class="login" @click="login">登录</button><br/><br/><br/>
      <button type="button" class="login" @click="toRegister">注册</button>
    </div>
  </div>
</template>

<style scoped>
  .inputUserAccount {
    position: absolute;
    text-align: center;
    font-size: 16px;
    vertical-align: middle;
    width: 100%;
    height: 11%;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-bottom: 10px;
  }

  .inputUserPassword {
    position: absolute;
    text-align: center;
    font-size: 16px;
    vertical-align: middle;
    width: 100%;
    height: 11%;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-bottom: 10px;
  }

  .login {
    position: absolute;
    text-align: center;
    font-size: 18px;
    vertical-align: middle;
    width: 100%;
    height: 11%;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-bottom: 10px;
    background: white;
    transition-duration: 0.3s;
  }
  .login:hover {
    background: rgb(41,46,66,0.8);
    cursor: pointer;
    color: white;
    border: 0px solid black;
    box-shadow: rgba(0, 0, 0, 0.5) 2px 4px 10px;
  }
</style>