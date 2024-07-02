<script lang="js" setup>
  import {useRouter} from "vue-router";
  import LeftArea from "./components/leftArea.vue";
  import axios from "axios";
  import {ElMessage} from "element-plus";
  import myAxios from "../../plugins/myAxios.js";

  const router = useRouter();

  const toLogin = () => {
    router.push("/");
  }

  // 注册
  const register = () => {
    const userAccount = document.getElementById("userAccount").value;
    const userPassword = document.getElementById("userPassword").value;
    const checkPassword = document.getElementById("checkPassword").value;

    if (userAccount.length < 6 || userAccount.length > 20) {
      alert("账号不得小于6位或大于20位");
    }
    if (userPassword.length < 8 || userPassword.length > 20) {
      alert("密码不得小于8位或大于20位");
    }
    if (userPassword !== checkPassword) {
      alert("两次密码需完全一致");
    }
    const data = {
      userAccount: userAccount,
      userPassword: userPassword,
      checkPassword: checkPassword
    }

    myAxios.post('/user/register', {
        userAccount: data.userAccount,
        userPassword: data.userPassword,
        checkPassword: data.checkPassword
    }).then(function (response){
      if (response.data.code === 200) {
        ElMessage({
          showClose: true,
          message: '注册成功',
          type: 'success',
          center: true,
          style: "width: 250px",
        })
        // 路由跳转
        router.push('/')
      } else if (response.data.code === 400 || response.data.code === 401 || response.data.code === 500) {
        ElMessage({
          showClose: true,
          message: '注册失败',
          type: 'error',
          center: true,
          style: "width: 250px",
        })
        // 清空输入框
        document.getElementById("userAccount").value = "";
        document.getElementById("userPassword").value = "";
        document.getElementById("checkPassword").value = "";
      } else if (response.data.code === 402) {
        ElMessage({
          showClose: true,
          message: '账号已存在',
          type: 'error',
          center: true,
          style: "width: 300px",
        })
        // 清空输入框
        document.getElementById("userAccount").value = "";
        document.getElementById("userPassword").value = "";
        document.getElementById("checkPassword").value = "";
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
    <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 60%;height: 450px">
      <p style="font-size: 30px; position: absolute">注册</p><br/><br/><br/><br/>
      <p>账号</p>
      <input class="inputUserAccount" type="text" placeholder="账号不得小于6位或大于20位，不得包含特殊字符" id="userAccount" autocomplete="off"/><br/><br/><br/>
      <p>密码</p>
      <input class="inputUserPassword" type="password" placeholder="密码不得小于8位或大于20位" id="userPassword" autocomplete="new-password"/><br/><br/><br/>
      <p>确认密码</p>
      <input class="inputUserPassword" type="password" placeholder="两次密码需完全一致" id="checkPassword" autocomplete="new-password"/><br/><br/><br/>
      <button type="button" class="register" @click="register">注册</button><br/><br/><br/>
      <button type="button" class="login" @click="toLogin">已有账号？返回登录</button>
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
  height: 40px;
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
  height: 40px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-bottom: 10px;
}

.register {
  position: absolute;
  text-align: center;
  font-size: 18px;
  vertical-align: middle;
  width: 100%;
  height: 40px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-bottom: 10px;
  background: white;
  transition-duration: 0.3s;
}
.register:hover {
  background: rgb(41,46,66,0.8);
  cursor: pointer;
  color: white;
  border: 0px solid black;
  box-shadow: rgba(0, 0, 0, 0.5) 2px 4px 10px;
}

.login {
  position: absolute;
  text-align: center;
  font-size: 18px;
  vertical-align: middle;
  width: 100%;
  height: 40px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-bottom: 10px;
  background: rgb(41,46,66,0.8);
  color: white;
  transition-duration: 0.3s;
}
.login:hover {
  cursor: pointer;
  border: 0px solid black;
  box-shadow: rgba(0, 0, 0, 0.5) 2px 4px 10px;
}
</style>