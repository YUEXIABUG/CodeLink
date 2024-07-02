<script setup>
  import HomeHeader from "./components/HomeHeader.vue";
  import UserRecommend from "./components/UserRecommend.vue";

  import {onMounted, ref, reactive, computed} from "vue";
  import { useStore } from "vuex";
  import {useRouter} from "vue-router";
  import ProjectRecommend from "./components/ProjectRecommend.vue";
  import AllProjects from "./components/AllProjects.vue";

  const router = useRouter();
  const store = useStore();

  const loginUser = computed(() => store.state.currentUser);

  // 绑定el-menu-item的index
  const activeIndex = ref('1');
  // 切换el-menu-item的index
  const handleSelect = (index) => {
    activeIndex.value = index;
  }

  onMounted(() => {
    if (!loginUser.value) {
      router.push('/login');
    }
  });
</script>

<template>
  <el-container style="width: 100%; height: 100vh;">
    <HomeHeader/>
    <div style="position: absolute; width: 100%">
      <el-menu
          default-active="1"
          style="position:absolute; top: 62px; width: 200px; height: 93.5vh"
      >
        <el-menu-item index="1" @click="handleSelect('1')">
          <span>用户推荐</span>
        </el-menu-item>
        <el-menu-item index="2" @click="handleSelect('2')">
          <span>项目推荐</span>
        </el-menu-item>
        <el-menu-item index="3" @click="handleSelect('3')">
          <span>所有项目</span>
        </el-menu-item>
      </el-menu>


      <UserRecommend v-show="activeIndex==='1'"/>
      <ProjectRecommend v-show="activeIndex==='2'"/>
      <AllProjects v-show="activeIndex==='3'"/>
    </div>
  </el-container>


</template>

<style scoped>

</style>