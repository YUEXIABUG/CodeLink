<script lang="ts" setup>
  import {useRouter} from "vue-router";
  import { ElMessage, ElInput } from "element-plus";
  import { useStore } from "vuex";
  import {computed, nextTick, onMounted, ref, watch} from "vue";
  import myAxios from "../../../plugins/myAxios.js";

  const router = useRouter();
  const store = useStore();

  // 获取用户信息
  const loginUser = computed(() => store.state.currentUser);
  let currentUser = ref({})
  let isLoginUser = 0
  let uid = 0
  // 用户性别：0-保密，1-男，2-女
  let userGender = '保密'
  const inputValue = ref('')
  let dynamicTags = ref([])
  let projectList = ref([]);
  let projectTags = ref([])
  let needTags = ref([])
  let finishedProjectList = ref([]);
  let finishedProjectTags = ref([])
  let finishedNeedTags = ref([])
  const inputVisible = ref(false)
  const InputRef = ref<InstanceType<typeof ElInput>>()
  // 定义一个变量，用来判断“修改标签”按钮是否可用，如果为true，则可用，否则不可用
  let isEditUserTags = ref(false);
  let selectedValue = ref('');
  let options = ["Vue", "JavaScript", "SpringBoot", "HTML", "CSS", "AI", "Java", "JavaEE", "大一", "大二", "大三", "大四", "应届生", "研一", "研二", "研三", "博士"]
  let suggestions = ref([])

  onMounted(async () => {
    if (!loginUser.value) {
      router.push('/login');
    }

    uid = parseInt(router.currentRoute.value.query.uid, 10)
    const currentUid = loginUser.value.uid

    if (uid == currentUid) {
      isLoginUser = 1
    }

    await getUserInfo()
    await getMyProjectList()
    await getFinishedProjectList()
  })

  // 当从同一个路由跳转过来时，由于参数变化，所以要刷新页面
  watch(() => router.currentRoute.value.query.uid, () => {
    uid = parseInt(router.currentRoute.value.query.uid, 10)
    const currentUid = loginUser.value.uid

    if (uid == currentUid) {
      isLoginUser = 1
    }

    getUserInfo()
  })

  // 获取用户信息
  async function getUserInfo () {
    myAxios.post('/user/getUserInfo', {
      uid: uid
    }).then(function (response) {
      if (response.data.code === 200) {
        currentUser.value = response.data.data
        dynamicTags.value = currentUser.value.userTags

        divisionTags()

        if (currentUser.value.userGender === 0) {
          userGender = '保密'
        } else if (currentUser.value.userGender === 1) {
          userGender = '男'
        } else {
          userGender = '女'
        }

        if (currentUser.value.username === null) {
          currentUser.value.username = 'null'
        }
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

  const toPersonalWeb = () => {
    if(loginUser.value.personalWeb === null) {
      ElMessage({
        showClose: true,
        message: '未绑定个人博客！',
        type: 'error',
        center: true,
        style: "width: 250px",
      })
    } else {
      window.open(loginUser.value.personalWeb)
    }
  }
  const toGitHub = () => {
    if(loginUser.value.GitHub === null) {
      ElMessage({
        showClose: true,
        message: '未绑定GitHub账号！',
        type: 'error',
        center: true,
        style: "width: 250px",
      })
    } else {
      window.open(loginUser.value.GitHub)
    }
  }
  const toCSDN = () => {
    if(loginUser.value.csdn === null) {
      ElMessage({
        showClose: true,
        message: '未绑定CSDN账号！',
        type: 'error',
        center: true,
        style: "width: 250px",
      })
    } else {
      window.open(loginUser.value.csdn)
    }
  }

  const toEditUserInfo = () => {
    router.push('/user/editUserInfo')
  }

  // 将用户标签分割成数组
  async function divisionTags () {
    if(dynamicTags.value === null){
      dynamicTags.value = []
    }
    // 如果 dynamicTags 为 空数组，则将其设置为 null
    if (dynamicTags.length <= 2) {
      dynamicTags.value = null
    } else {
      if (dynamicTags.value != null) {
        // 将dynamicTags变成数组的形式，以[]为分隔符，并不显示[]
        dynamicTags.value = dynamicTags.value.split(',')
        //检验dynamicTags是否有[、]或者是空格，如果有则去掉
        for (let i = 0; i < dynamicTags.value.length; i++) {
          if (dynamicTags.value[i].indexOf('[') !== -1) {
            dynamicTags.value[i] = dynamicTags.value[i].replace('[', '')
          }
          if (dynamicTags.value[i].indexOf(']') !== -1) {
            dynamicTags.value[i] = dynamicTags.value[i].replace(']', '')
          }
          if (dynamicTags.value[i].indexOf(' ') !== -1) {
            dynamicTags.value[i] = dynamicTags.value[i].replace(' ', '')
          }
        }
      }
      console.log(dynamicTags.value)
    }
  }

  const handleClose = (tag: string) => {
    dynamicTags.value.splice(dynamicTags.value.indexOf(tag), 1)
  }

  const showInput = () => {
    inputVisible.value = true
    nextTick(() => {
      InputRef.value!.input!.focus()
    })
  }

  const handleInputConfirm = () => {
    if (inputValue.value) {
      if (dynamicTags.value === null) {
        dynamicTags.value = []
        dynamicTags.value.push(inputValue.value)
      } else {
        dynamicTags.value.push(inputValue.value)
      }

    }
    inputVisible.value = false
    inputValue.value = ''
    suggestions.value = []
  }

  // 时刻监视 dynamicTags.value 与 currentUser.value.userTags 的变化，如果两者不一致，则将 isEditUserTags 设置为true
  watch(() => dynamicTags.value, (newValue) => {
    if (newValue !== currentUser.value.userTags) {
      isEditUserTags = true;
    } else {
      isEditUserTags = false;
    }
  })

  // 修改标签
  const editUserTags = () => {
    myAxios.post('/user/updateUserTags', {
      uid: loginUser.value.uid,
      userTags: dynamicTags.value
    }).then(function (response){
      if(response.data.code === 200){
        // 获得用户的脱敏信息
        myAxios.post('/user/getUserInfo', {
          uid: response.data.data.uid
        }).then(function (response) {
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

            ElMessage({
              showClose: true,
              message: '修改成功',
              type: 'success',
              center: true,
              style: "width: 250px",
            })

            router.push('/user')
          }
        })
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

  async function getMyProjectList() {
    myAxios.post('/project/getMyProjectList', {
      uid: uid
    }).then(function (response) {
      if (response.data.code === 200) {
        projectList.value = response.data.data
        // 将项目标签存入projectTags
        for (let i = 0; i < projectList.value.length; i++) {
          projectList.value[i].projectTags = projectList.value[i].projectTags.split(',')
          // 查看每个标签，如果有'['或者']'就删除
          for (let j = 0; j < projectList.value[i].projectTags.length; j++) {
            if (projectList.value[i].projectTags[j].indexOf('[') !== -1 || projectList.value[i].projectTags[j].indexOf(']') !== -1) {
              projectList.value[i].projectTags[j] = projectList.value[i].projectTags[j].replace('[', '').replace(']', '')
            }
          }
          projectTags.value.push(projectList.value[i].userTags)
        }
        // 将所需标签存入needTags
        for (let i = 0; i < projectList.value.length; i++) {
          projectList.value[i].needTags = projectList.value[i].needTags.split(',')
          // 查看每个标签，如果有'['或者']'就删除
          for (let j = 0; j < projectList.value[i].needTags.length; j++) {
            if (projectList.value[i].needTags[j].indexOf('[') !== -1 || projectList.value[i].needTags[j].indexOf(']') !== -1) {
              projectList.value[i].needTags[j] = projectList.value[i].needTags[j].replace('[', '').replace(']', '')
            }
          }
          needTags.value.push(projectList.value[i].needTags)
        }
      }
    })
  }

  async function getFinishedProjectList() {
    myAxios.post('/project/getFinishedProjectList', {
      uid: uid
    }).then(function (response) {
      if (response.data.code === 200) {
        finishedProjectList.value = response.data.data
        // 将项目标签存入projectTags
        for (let i = 0; i < finishedProjectList.value.length; i++) {
          finishedProjectList.value[i].projectTags = finishedProjectList.value[i].projectTags.split(',')
          // 查看每个标签，如果有'['或者']'就删除
          for (let j = 0; j < finishedProjectList.value[i].projectTags.length; j++) {
            if (finishedProjectList.value[i].projectTags[j].indexOf('[') !== -1 || finishedProjectList.value[i].projectTags[j].indexOf(']') !== -1) {
              finishedProjectList.value[i].projectTags[j] = finishedProjectList.value[i].projectTags[j].replace('[', '').replace(']', '')
            }
          }
          finishedProjectTags.value.push(finishedProjectList.value[i].userTags)
        }
        // 将所需标签存入needTags
        for (let i = 0; i < finishedProjectList.value.length; i++) {
          finishedProjectList.value[i].needTags = finishedProjectList.value[i].needTags.split(',')
          // 查看每个标签，如果有'['或者']'就删除
          for (let j = 0; j < finishedProjectList.value[i].needTags.length; j++) {
            if (finishedProjectList.value[i].needTags[j].indexOf('[') !== -1 || finishedProjectList.value[i].needTags[j].indexOf(']') !== -1) {
              finishedProjectList.value[i].needTags[j] = finishedProjectList.value[i].needTags[j].replace('[', '').replace(']', '')
            }
          }
          finishedNeedTags.value.push(finishedProjectList.value[i].needTags)
        }
      }
    })
  }

  const toProjectDetailPage = (pid, tid) => {
    router.push({
      path: '/projects/projectDetail',
      query: {
        pid: pid,
        tid: tid
      }
    })
  }

  // 自动补全
  const updateSuggestions = () => {
    // 使用filter方法根据用户输入过滤数据源
    suggestions.value = options.filter(item => item.startsWith(inputValue.value))
  }
  const selectSuggestion = (suggestion) => {
    if (suggestion) {
      if (dynamicTags.value === null) {
        dynamicTags.value = []
        dynamicTags.value.push(suggestion)
      } else {
        dynamicTags.value.push(suggestion)
      }
    }
    inputVisible.value = false
    inputValue.value = ''
    suggestions.value = []
  }
</script>

<template>
  <el-card
      style="position: relative; float: left; left: 7.9%; top: 30px; width: 25%; height: 400px;"
  >
    <el-avatar
        :size="150"
        :src="currentUser.userAvatar"
        class="userAvatar"
    />

    <p style="position: absolute; left: 50%; top: 54%; transform: translate(-50%, 0); font-size: 35px;">{{currentUser.username}}</p>

    <!--用户个人博客，github，csdn-->
    <div style="position: absolute; left: 50%; top: 80%; transform: translate(-50%, -50%); width: 60%;">
      <!--个人博客-->
      <a target="_blank" @click="toPersonalWeb">
        <div class="personalWeb">
          <p style="text-align: center; font-size: 30px">博</p>
        </div>
      </a>
      <!--github-->
      <a target="_blank" @click="toGitHub">
        <div class="github">
          <img src="https://github.githubassets.com/assets/GitHub-Mark-ea2971cee799.png" style="width: 68px; height: 68px; position: absolute; left: -9px; top: -9px"/>
        </div>
      </a>
      <!--csdn-->
      <a target="_blank" @click="toCSDN">
        <div class="csdn">
          <img src="https://img0.baidu.com/it/u=1281271954,3771372570&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500" style="width: 50px; height: 50px; position: absolute;"/>
        </div>
      </a>
    </div>

  </el-card>

  <el-card
      style="position: relative; float: left; left: 12%; top: 30px; width: 55%; height: 400px;"
  >
    <el-container style="height: 360px;">
      <el-main style="width: 40%; height: 100%; border-right: 1px solid rgba(145, 145, 145, 0.2)">
        <div style="color: #848484; padding: 20px; margin-top: 20px">
          <p style="line-height: 30px">uid：{{currentUser.uid}}</p>
          <p style="line-height: 30px">用户名：{{currentUser.userAccount}}</p>
          <p style="line-height: 30px">昵称：{{currentUser.username}}</p>
          <p style="line-height: 30px">性别：{{userGender}}</p>
          <p style="line-height: 30px">年龄：{{currentUser?.userAge}}</p>
          <p style="line-height: 30px">邮箱：{{currentUser?.userEmail}}</p>
          <p style="line-height: 30px">电话：{{currentUser?.userPhone}}</p>
        </div>
        <el-button
            v-show="isLoginUser === 1"
            class="toEditInfo"
            @click="toEditUserInfo"
        >
          编辑信息
        </el-button>
      </el-main>
      <el-aside>
        <div style="margin-left: 10px">
          <el-tag
                v-for="(item, index) in dynamicTags"
                :key="index"
                closable
                :disable-transitions="false"
                @close="handleClose(item)"
          >
            {{ item }}
          </el-tag>
          <el-input
                v-if="inputVisible"
                ref="InputRef"
                v-model="inputValue"
                style="width: 70px"
                size="small"
                @input="updateSuggestions"
                @keyup.enter="handleInputConfirm"
                type="text"
                autocomplete="on"
          />
          <el-button v-if="!inputVisible" class="button-new-tag" size="small" @click="showInput">
            + 添加标签
          </el-button>
          <ul v-if="suggestions.length">
            <li
                v-for="(suggestion, index) in suggestions"
                :key="index"
                @click="selectSuggestion(suggestion)"
            >{{ suggestion }}</li>
          </ul>
        </div>

        <!-- 修改标签按钮 -->
        <p style="color: #848484; position: absolute; left: 74%; bottom: 70px; width: 200px; height: 30px; font-size: 12px">首字母需大写，系统后台会去重</p>
        <el-button
            v-if="isEditUserTags === true && isLoginUser === 1"
            type="primary"
            style="position: absolute; left: 77.8%; bottom: 40px; width: 100px; height: 30px;"
            @click="editUserTags"
        >
          修改标签
        </el-button>
      </el-aside>
    </el-container>
  </el-card>

  <el-card style="position: relative; float: left; left: 50%; top: 80px; transform: translate(-50%, 0); width: 84.3%; height: 470px">
    <p style="line-height: 30px">正在进行的项目</p>
    <el-empty v-if="projectList.length === 0" description="还没有加入任何项目噢~"/>
    <el-row v-else :gutter="20" class="mb-4">
      <el-col
          v-for="(project, index) in projectList"
          :key="project.pid"
          :span="4.5"
      >
        <el-card class="card" shadow="hover">
          <div  class="demo-image__preview">
            <el-image
                :src="project.teamAvatar"
                fit="contain"
                :initial-index="4"
                style="height: 140px; width: 287px; margin: 0 auto; display: block;"
            />
          </div>
          <div style="position: relative; padding: 1px; width: 100%; height: 200px;">
            <!--居中显示username-->
            <span style="text-align: center; display: block">{{ project.projectName }}</span>
            <div style="position: absolute; top: 20%">
              <p>
                <span>项目标签</span>
                <el-tag v-if="!project.projectTags || (project.projectTags?.length === 1 && project.projectTags[0] === '')" class="ml-2" size="small" type="info">
                  暂无标签
                </el-tag>
                <el-tag v-for="(tag, index) in project.projectTags" class="ml-2" size="small" v-else>
                  {{ tag }}
                </el-tag>
              </p>
              <p>
                <span>所需标签</span>
                <el-tag v-if="!project.needTags || (project.needTags?.length === 1 && project.needTags[0] === '')" class="ml-2" size="small" type="info">
                  暂无标签
                </el-tag>
                <el-tag v-for="(tag, index) in project.needTags" class="ml-2" size="small" v-else>
                  {{ tag }}
                </el-tag>
              </p>
            </div>
            <div style="position: absolute; top: 70%; width: 100%;">
              <el-button type="primary" plain style="position: relative; float: left; left: 34%" @click="toProjectDetailPage(project.pid, project.tid)">查看项目</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </el-card>

  <el-card style="position: relative; float: left; left: 50%; top: 130px; transform: translate(-50%, 0); width: 84.3%; height: 470px">
    <p style="line-height: 30px">已完成的项目</p>
    <el-empty v-if="finishedProjectList.length === 0" description="还没有加入任何项目噢~"/>
    <el-row v-else :gutter="20" class="mb-4">
      <el-col
          v-for="(project, index) in finishedProjectList"
          :key="project.pid"
          :span="4.5"
      >
        <el-card class="card" shadow="hover">
          <div  class="demo-image__preview">
            <el-image
                :src="project.teamAvatar"
                fit="contain"
                :initial-index="4"
                style="height: 140px; width: 287px; margin: 0 auto; display: block;"
            />
          </div>
          <div style="position: relative; padding: 1px; width: 100%; height: 200px;">
            <!--居中显示username-->
            <span style="text-align: center; display: block">{{ project.projectName }}</span>
            <div style="position: absolute; top: 20%">
              <p>
                <span>项目标签</span>
                <el-tag v-if="!project.finishedProjectTags || (project.finishedProjectTags?.length === 1 && project.finishedProjectTags[0] === '')" class="ml-2" size="small" type="info">
                  暂无标签
                </el-tag>
                <el-tag v-for="(tag, index) in project.finishedProjectTags" class="ml-2" size="small" v-else>
                  {{ tag }}
                </el-tag>
              </p>
              <p>
                <span>所需标签</span>
                <el-tag v-if="!project.finishedNeedTags || (project.finishedNeedTags?.length === 1 && project.finishedNeedTags[0] === '')" class="ml-2" size="small" type="info">
                  暂无标签
                </el-tag>
                <el-tag v-for="(tag, index) in project.finishedNeedTags" class="ml-2" size="small" v-else>
                  {{ tag }}
                </el-tag>
              </p>
            </div>
            <div style="position: absolute; top: 70%; width: 100%;">
              <el-button type="primary" plain style="position: relative; float: left; left: 34%" @click="toProjectDetailPage(project.pid, project.tid)">查看项目</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<style scoped>
  .userAvatar {
    position: absolute;
    left: 50%;
    top: 25%;
    transform: translate(-50%, -40%);
  }

  .personalWeb {
    position: relative;
    float: left;
    left: 10%;
    width: 50px;
    height: 50px;
    border-radius: 50px;
    border: 1px solid rgba(145, 145, 145, 0.20);
    background-color: rgba(153, 227, 95, 0.84);
    transition-duration: 0.3s;
  }
  .personalWeb:hover {
    cursor: pointer;
    box-shadow: 1px 2px 10px rgba(145, 145, 145);
  }

  .github {
    position: relative;
    float: left;
    left: 20%;
    width: 50px;
    height: 50px;
    border-radius: 50px;
    border: 1px solid rgba(145, 145, 145, 0.20);
    overflow: hidden;
    transition-duration: 0.3s;
  }
  .github:hover {
    cursor: pointer;
    box-shadow: 1px 2px 10px rgba(145, 145, 145);
  }

  .csdn {
    position: relative;
    float: left;
    left: 30%;
    width: 50px;
    height: 50px;
    border-radius: 50px;
    border: 1px solid rgba(145, 145, 145, 0.20);
    overflow: hidden;
    transition-duration: 0.3s;
  }
  .csdn:hover {
    cursor: pointer;
    box-shadow: 1px 2px 10px rgba(145, 145, 145);
  }

  .toEditInfo {
    position: absolute;
    left: 54%;
    top: 10%;
    border: 0px;
    color: #848484;
  }
  .toEditInfo:hover {
    background: white;
  }

  .button-new-tag {
    margin-top: 10px;
    margin-bottom: 10px;
  }

  ul {
    list-style: none;
    padding: 0;
    margin: 0;
    max-height: 150px;
    overflow: scroll;
  }

  li {
    text-align: center;
    font-size: 15px;
    cursor: pointer;
    padding: 5px;
    background: #f0f0f0;
    border-bottom: 1px solid #eee;
  }
  li:hover {
    background: #e9e9e9;
  }

</style>