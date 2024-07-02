<script setup>
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import myAxios from "../../../plugins/myAxios.js";
import { useStore } from "vuex";
import { computed } from "vue";

const router = useRouter();
const store = useStore();

const currentUser = computed(() => store.state.currentUser);

let username = currentUser.value.username;
const userAvatar = currentUser.value.userAvatar;

if (!username) {
  username = 'null'
}

const logout = () => {
  myAxios.post('/user/logout', {
    uid: localStorage.getItem("uid")
  }).then(function (response) {
    if (response.data.code === 200) {
      ElMessage({
        showClose: true,
        message: '已退出登录',
        type: 'success',
        center: true,
        style: "width: 250px",
      })
      router.push('/')
    } else if (response.data.code === 400) {
      ElMessage({
        showClose: true,
        message: '退出登录失败',
        type: 'error',
        center: true,
        style: "width: 300px",
      })
    }
  }).catch(function (error) {
    console.log(error);
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
  <div class="HomeHeader">
    <div class="left">
      <router-link to="/home"><img class="logo" src="../../../assets/vue.svg"></router-link>
      <div>
        <router-link to="/home">
          <span class="home">首页</span>
        </router-link>
        <router-link to="/projects">
          <span class="projects">项目</span>
        </router-link>
        <router-link to="/friends">
          <span class="friends">好友</span>
        </router-link>
        <router-link to="/instances">
          <span class="instances">免费样例</span>
        </router-link>
        <router-link to="/about">
          <span class="about">如何使用</span>
        </router-link>
      </div>
    </div>
    <div style="position: absolute; left: 85%; width: 9.1%; height: 100%">
      <el-avatar
          :size="40"
          :src="userAvatar"
          class="avatar"
      />
      <el-dropdown class="myList">
        <span class="el-dropdown-link">
          {{username}}
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <!-- 跳转到 /user 并传递参数uid -->
            <router-link class="exitAndReset" :to="`/user?uid=${currentUser.uid}`"><el-dropdown-item class="el-dropdown-item-class">个人信息</el-dropdown-item></router-link>
            <router-link class="exitAndReset" to="/resetPassword"><el-dropdown-item class="el-dropdown-item-class">修改密码</el-dropdown-item></router-link>
            <router-link class="exitAndReset" to="/" @click="logout"><el-dropdown-item class="el-dropdown-item-class">退出登录</el-dropdown-item></router-link>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style>
.el-dropdown-item-class{
  color: black;
}
.el-dropdown-item-class:hover{
  background: rgba(141, 205, 234, 0.26) !important;
  color: rgb(53, 118, 229) !important;
}
</style>

<style scoped>
.exitAndReset{
  text-decoration: none;
  color: black;
}
.exitAndReset:link:active{
  color: black;
}

.HomeHeader{
  position: relative;
  float: left;
  height: 60px;
  width: 100%;
  border-bottom: 1px solid rgba(145, 145, 145, 0.34);
  //background: aqua;
}

.left{
  position: absolute;
  float: left;
  height: 100%;
  width: 85%;
  border-right: 1px solid rgba(183, 183, 183, 0.2);
}

.logo{
  position: absolute;
  height: 90%;
  left: 40px;
  cursor: pointer;
}

.home{
  position: absolute;
  left: 120px;
  width: 110px;
  height: 100%;
  color: black;
  text-decoration: none;
  line-height: 400%;
  text-align: center;
}
.home:hover{
  color: rgb(53, 118, 229);
  background: rgba(141, 205, 234, 0.26);
  border-bottom: 2px solid rgb(53, 118, 229,0.8);
}

.projects{
  position: absolute;
  left: 230px;
  width: 110px;
  height: 100%;
  color: black;
  text-decoration: none;
  line-height: 400%;
  text-align: center;
}
.projects:hover{
  color: rgb(53, 118, 229);
  background: rgba(141, 205, 234, 0.26);
  border-bottom: 2px solid rgb(53, 118, 229,0.8);
}

.friends{
  position: absolute;
  left: 340px;
  width: 110px;
  height: 100%;
  text-decoration: none;
  line-height: 400%;
  text-align: center;
  color: rgb(53, 118, 229);
  background: rgba(141, 205, 234, 0.26);
  border-bottom: 2px solid rgb(53, 118, 229,0.8);
}

.instances{
  position: absolute;
  left: 450px;
  width: 110px;
  height: 100%;
  color: black;
  text-decoration: none;
  line-height: 400%;
  text-align: center;
}
.instances:hover{
  color: rgb(53, 118, 229);
  background: rgba(141, 205, 234, 0.26);
  border-bottom: 2px solid rgb(53, 118, 229,0.8);
}

.about{
  position: absolute;
  left: 560px;
  width: 110px;
  height: 100%;
  color: black;
  text-decoration: none;
  line-height: 400%;
  text-align: center;
}
.about:hover{
  color: rgb(53, 118, 229);
  background: rgba(141, 205, 234, 0.26);
  border-bottom: 2px solid rgb(53, 118, 229,0.8);
}

.avatar {
  position: absolute;
  top: 10px;
  left: 30px;
}

.myList{
  position: absolute;
  left: 60%;
  width: 60%;
  height: 100%;
  font-size: 140%;
}

.el-dropdown-link {
  height: 100%;
  cursor: pointer;
  color: black;
  display: flex;
  text-align: center;
  align-items: center;
  transition-duration: 0.8s;
}
.el-dropdown-link:hover{
  cursor: pointer;
  color: rgb(53, 118, 229,0.8);
}
</style>