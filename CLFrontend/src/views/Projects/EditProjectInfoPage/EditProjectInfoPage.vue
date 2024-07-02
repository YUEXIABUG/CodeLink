<script lang="ts" setup>
  import ProjectsHeader from "./components/ProjectsHeader.vue";
  import {useRouter} from "vue-router";
  import {ElMessage, ElInput} from "element-plus";
  import type{FormInstance, FormRules} from "element-plus"
  import { useStore } from "vuex";
  import {computed, nextTick, onMounted, reactive, ref, watch} from "vue";
  import myAxios from "../../../plugins/myAxios.js";

  const loginUser = computed(() => store.state.currentUser);

  const router = useRouter();
  const store = useStore();
  // 接收传参
  const pid = router.currentRoute.value.query.pid
  const tid = router.currentRoute.value.query.tid

  let projectDetail = ref({})
  let teamDetail = ref({})

  // 标签设置**************************************
  // 项目标签
  const inputVisible = ref(false)
  const InputRef = ref<InstanceType<typeof ElInput>>()
  const inputValue = ref('')
  let dynamicTags = ref([])
  // 项目所需标签
  const needInputVisible = ref(false)
  const needInputRef = ref<InstanceType<typeof ElInput>>()
  const needInputValue = ref('')
  let needDynamicTags = ref([])
  let options = ["Vue", "JavaScript", "SpringBoot", "HTML", "CSS", "AI", "Java", "JavaEE", "大一", "大二", "大三", "大四", "应届生", "研一", "研二", "研三", "博士"]
  let suggestions1 = ref([])
  let suggestions2 = ref([])

  const handleClose = (tag: string, flag: number) => {
    if (flag === 1) {
      dynamicTags.value.splice(dynamicTags.value.indexOf(tag), 1)
    } else if (flag === 2) {
      needDynamicTags.value.splice(needDynamicTags.value.indexOf(tag), 1)
    }
  }
  const showInput = (flag) => {
    if (flag === 1) {
      inputVisible.value = true
      nextTick(() => {
        InputRef.value!.input!.focus()
      })
    } else if (flag === 2) {
      needInputVisible.value = true
      nextTick(() => {
        needInputRef.value!.input!.focus()
      })
    }
  }
  const handleInputConfirm = (flag) => {
    if (flag === 1) {
      if (inputValue.value) {
        dynamicTags.value.push(inputValue.value)
      }
      inputVisible.value = false
      inputValue.value = ''
    } else if (flag === 2) {
      if (needInputValue.value) {
        needDynamicTags.value.push(needInputValue.value)
      }
      needInputVisible.value = false
      needInputValue.value = ''
    }
  }
  // 标签设置**************************************

  // 输入框校验
  const projectRuleFormRef = ref<FormInstance>()
  const projectRuleForm = reactive({
    projectName: '',
    projectDes: ''
  })
  const validateProjectName = (rule: any, value: any, callback: any) => {
    if (value === '') {
      callback(new Error('请输入项目名称！'))
    }
    if (value.length > 10) {
      callback(new Error('名称长度只能在10个字符以内！'))
    }

    callback()
  }
  const validateProjectDes = (rule: any, value: any, callback: any) => {
    if (value === '') {
      callback(new Error('请输入项目简介！'))
    } else {
      callback()
    }
  }
  const projectRules = reactive<FormRules<typeof projectRuleForm>>({
    projectName: [{validator: validateProjectName, trigger: 'blur'}],
    projectDes: [{validator: validateProjectDes, trigger: 'blur'}]
  })

  const teamRuleFormRef = ref<FormInstance>()
  const teamRuleForm = reactive({
    teamAvatar: 'https://s2.loli.net/2024/01/02/nwbmQvJphgfrAxz.png',
    isPersonal: false,
    teamPassword: null,
  })
  // 检查图像链接是否有效
  let checkImgExists = function(url) {
    return new Promise(function(resolve, reject) {
      let ImgObj = new Image();
      ImgObj.src = url;
      ImgObj.onload = function(res) {
        resolve(res);
      }
      ImgObj.onerror = function(err) {
        reject(err)
      }
    })
  }
  const validateTeamAvatar = (rule: any, value: any, callback: any) => {
    if (value === '') {
      callback(new Error('请输入队伍头像！'))
    } else {
      checkImgExists(value).then(()=>{
        callback()
      }).catch(()=>{
        callback(new Error('请输入正确的头像链接'))
      })
    }
  }
  const validateTeamPassword = (rule: any, value: any, callback: any) => {
    if (teamRuleForm.isPersonal){
      if (value == null) {
        callback(new Error('请输入密码！'))
      }
      if (value.length < 6 || value.length > 16) {
        callback(new Error('密码长度只能在6-16个字符之间！'))
      }
      callback()
    }
    callback()
  }
  const teamRules = reactive<FormRules<typeof teamRuleForm>>({
    teamAvatar: [{validator: validateTeamAvatar, trigger: 'blur'}],
    teamPassword: [{validator: validateTeamPassword, trigger: 'blur'}]
  })
  let isPersonal = 0



  onMounted(async () => {
    if (!loginUser.value) {
      await router.push('/login');
    }

    await getProjectDetail()
  })

  async function getProjectDetail() {
    myAxios.post('/project/getProjectDetail', {
      pid: pid,
    }).then(function (response) {
      if (response.data.code === 200) {
        projectDetail.value = response.data.data.project
        teamDetail.value = response.data.data.team

        // 将项目标签存入dynamicTags
        projectDetail.value.projectTags = projectDetail.value.projectTags.split(',')
        // 查看每个标签，如果有'['、']'或者是空格就删除
        for (let j = 0; j < projectDetail.value.projectTags.length; j++) {
          if (projectDetail.value.projectTags[j].indexOf('[') !== -1 || projectDetail.value.projectTags[j].indexOf(']') !== -1 || projectDetail.value.projectTags[j].indexOf(' ') !== -1) {
            projectDetail.value.projectTags[j] = projectDetail.value.projectTags[j].replace('[', '').replace(']', '').replace(' ', '')
          }
        }
        dynamicTags.value = projectDetail.value.projectTags
        console.log(dynamicTags.value)

        // 将所需标签存入needDynamicTags
        projectDetail.value.needTags = projectDetail.value.needTags.split(',')
        // 查看每个标签，如果有'['、']'或者是空格就删除
        for (let j = 0; j < projectDetail.value.needTags.length; j++) {
          if (projectDetail.value.needTags[j].indexOf('[') !== -1 || projectDetail.value.needTags[j].indexOf(']') !== -1 || projectDetail.value.needTags[j].indexOf(' ') !== -1) {
            projectDetail.value.needTags[j] = projectDetail.value.needTags[j].replace('[', '').replace(']', '').replace(' ', '')
          }
        }
        needDynamicTags.value = projectDetail.value.needTags

        projectRuleForm.projectName = projectDetail.value.projectName
        projectRuleForm.projectDes = projectDetail.value.projectDes
        teamRuleForm.teamAvatar = teamDetail.value.teamAvatar
        if(teamDetail.value.isPersonal === 0){
          teamRuleForm.isPersonal = false
        } else if (teamDetail.value.isPersonal === 1){
          teamRuleForm.isPersonal = true
        }
        teamRuleForm.teamPassword = teamDetail.value.teamPassword
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

  const updateProjectInfo = () =>{
    if (teamRuleForm.isPersonal === false){
      isPersonal = 0
    } else if (teamRuleForm.isPersonal === true){
      isPersonal = 1
    }
    myAxios.post("/project/updateProject", {
      uid: store.state.currentUser.uid,
      pid: pid,
      tid: tid,
      projectName: projectRuleForm.projectName,
      projectDes: projectRuleForm.projectDes,
      projectTags: dynamicTags.value,
      needTags: needDynamicTags.value,
      teamAvatar: teamRuleForm.teamAvatar,
      isPersonal: isPersonal,
      teamPassword: teamRuleForm.teamPassword
    }).then(function (response){
      if(response.data.code === 200){
        ElMessage({
          showClose: true,
          message: '修改成功',
          type: 'success',
          center: true,
          style: "width: 300px",
        })
        router.push({
          path: '/projects/projectDetail',
          query: {
            pid: pid,
            tid: tid
          }
        })
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

  // 自动补全
  const updateSuggestions1 = () => {
    // 使用filter方法根据用户输入过滤数据源
    suggestions1.value = options.filter(item => item.startsWith(inputValue.value))
  }
  const selectSuggestion1 = (suggestion) => {
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
    suggestions1.value = []
  }

  const updateSuggestions2 = () => {
    // 使用filter方法根据用户输入过滤数据源
    suggestions2.value = options.filter(item => item.startsWith(inputValue.value))
  }
  const selectSuggestion2 = (suggestion) => {
    if (suggestion) {
      if (needDynamicTags.value === null) {
        needDynamicTags.value = []
        needDynamicTags.value.push(suggestion)
      } else {
        needDynamicTags.value.push(suggestion)
      }
    }
    needInputVisible.value = false
    needInputValue.value = ''
    suggestions2.value = []
  }


</script>

<template>
  <ProjectsHeader/>
  <el-card style="position: absolute; left: 26.7%; top: 9%; width: 800px; height: 330px">
    <el-form
        label-width="auto"
        ref="projectRuleFormRef"
        :model="projectRuleForm"
        status-icon
        :rules="projectRules"
        style="position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%); width: 60%; height: 60%"
    >
      <el-form-item label="项目名称" prop="projectName">
        <el-input v-model="projectRuleForm.projectName" class="input" autocomplete="off" placeholder="项目名称长度在10个字符以内"></el-input>
      </el-form-item>
      <el-form-item label="项目描述" prop="projectDes">
        <el-input v-model="projectRuleForm.projectDes" type="textarea" class="input" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="项目标签">
        <el-tag
            v-for="tag in dynamicTags"
            :key="tag"
            closable
            :disable-transitions="false"
            @close="handleClose(tag, 1)"
        >
          {{ tag }}
        </el-tag>
        <el-input
            v-if="inputVisible"
            ref="InputRef"
            v-model="inputValue"
            style="width: 70px"
            size="small"
            @input="updateSuggestions1"
            @keyup.enter="handleInputConfirm(1)"
        />
        <el-button v-else class="button-new-tag" size="small" @click="showInput(1)">
          + 添加标签
        </el-button>
        <ul v-if="suggestions1.length">
          <li
              v-for="(suggestion, index) in suggestions1"
              :key="index"
              @click="selectSuggestion1(suggestion)"
          >{{ suggestion }}</li>
        </ul>
      </el-form-item>
      <el-form-item label="项目所需标签">
        <el-tag
            v-for="tag in needDynamicTags"
            :key="tag"
            closable
            :disable-transitions="false"
            @close="handleClose(tag, 2)"
        >
          {{ tag }}
        </el-tag>
        <el-input
            v-if="needInputVisible"
            ref="InputRef"
            v-model="needInputValue"
            style="width: 70px"
            size="small"
            @input="updateSuggestions2"
            @keyup.enter="handleInputConfirm(2)"
        />
        <el-button v-else class="button-new-tag" size="small" @click="showInput(2)">
          + 添加标签
        </el-button>
        <ul v-if="suggestions2.length">
          <li
              v-for="(suggestion, index) in suggestions2"
              :key="index"
              @click="selectSuggestion2(suggestion)"
          >{{ suggestion }}</li>
        </ul>
      </el-form-item>
    </el-form>
  </el-card>
  <!-- 队伍卡片 -->
  <el-card style="position: absolute; left: 26.7%; top: 44.5%; width: 800px; height: 400px">
    <el-form
        label-width="auto"
        ref="teamRuleFormRef"
        :model="teamRuleForm"
        status-icon
        :rules="teamRules"
        style="position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%); width: 60%; height: 70%"
    >
      <!-- 头像预览 -->
      <el-form-item>
        <el-avatar :size="100" :src="teamRuleForm.teamAvatar" shape="square" style="position: relative; left: 40%"></el-avatar>
      </el-form-item>
      <el-form-item label="队伍头像" prop="teamAvatar">
        <el-input v-model="teamRuleForm.teamAvatar" class="input" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="是否个人队伍" prop="isPersonal">
        <el-switch v-model="teamRuleForm.isPersonal"></el-switch>
      </el-form-item>
      <!-- 如果teamRuleForm.isPersonal是true，则此项编辑，否则不可编辑-->
      <el-form-item label="队伍密码" prop="teamPassword">
        <el-input v-model="teamRuleForm.teamPassword" class="input" autocomplete="off" :disabled="!teamRuleForm.isPersonal" placeholder="密码长度在6-16个字符之间"></el-input>
      </el-form-item>
    </el-form>
  </el-card>

  <el-button type="primary" @click="updateProjectInfo()" class="updateProject">修改项目</el-button>
</template>

<style scoped>

  .input {
    width: 400px;
  }

  .updateProject {
    position: absolute;
    top: 90%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  ul {
    list-style: none;
    padding: 0;
    margin: 0;
    max-height: 43px;
    overflow: scroll;
    width: 100px;
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