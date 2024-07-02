<script setup>
import {CirclePlus} from "@element-plus/icons-vue";
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import myAxios from "../../plugins/myAxios.js";
import { useStore } from "vuex";
import {computed, onMounted, reactive, ref} from "vue";

const loginUser = computed(() => store.state.currentUser);

const router = useRouter();
const store = useStore();

let projectList = ref([]);
let projectTags = ref([])
let needTags = ref([])

onMounted(async () => {
  if (!loginUser.value) {
    await router.push('/login');
  }

  await getMyProjectList()
})

async function getMyProjectList() {
  myAxios.post('/project/getMyProjectList', {
    uid: loginUser.value.uid
  }).then(function (response) {
    if (response.data.code === 200) {
      projectList.value = response.data.data
      console.log(projectList.value)
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

const toTeamChatPage = (tid, pid) => {
  router.push({
    path: '/team/chat',
    query: {
      tid: tid,
      pid: pid
    }
  })
}
</script>

<template>
  <el-main style="position: absolute; left: 200px; top: 61px; width: 88.43%; height: 93.84vh">
    <div style="position: relative">
      <el-main style="position: absolute; left: 10px; top: 19px; width: 95.23%; height: 86vh; background-color: #f0f2f5">
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
                  <el-button type="primary" plain style="position: relative; float: left; left: 39%" @click="toTeamChatPage(project.tid, project.pid)">聊天</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </div>
  </el-main>
</template>

<style scoped>
</style>