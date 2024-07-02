import Login from "../views/Login/Login.vue";
import register from "../views/Login/register.vue";
import Home from "../views/Home/Home.vue";
import UserPage from "../views/UserPage/UserPage.vue";
import EditUserInfo from "../views/UserPage/EditUserInfo.vue";
import InstancesPage from "../views/Instances/InstancesPage.vue";
import InstanceDetails from "../views/Instances/InstanceDetails.vue";
import FriendsPage from "../views/Friends/FriendsPage.vue";
import ProjectsPage from "../views/Projects/ProjectsPage.vue";
import NewProjectPage from "../views/Projects/NewProject/NewProjectPage.vue";
import ProjectDetailPage from "../views/Projects/ProjectDetailPage/ProjectDetailPage.vue";
import EditProjectInfoPage from "../views/Projects/EditProjectInfoPage/EditProjectInfoPage.vue";
import FriendsChatPage from "../views/Chat/FriendsChatPage.vue";
import TeamChatPage from "../views/Chat/TeamChatPage.vue";


const routes = [
    { path: '/', component: Login },
    { path: '/register', component: register },
    { path: '/home', component: Home },
    { path: '/user', component: UserPage },
    { path: '/user/editUserInfo', component: EditUserInfo },
    { path: '/friends', component: FriendsPage },
    { path: '/instances', component: InstancesPage },
    { path: '/instanceDetail', component: InstanceDetails },
    { path: '/projects', component: ProjectsPage },
    { path: '/projects/newProject', component: NewProjectPage },
    { path: '/projects/projectDetail', component: ProjectDetailPage },
    { path: '/projects/editProject', component: EditProjectInfoPage },
    { path: '/friends/chat', component: FriendsChatPage},
    { path: '/team/chat', component: TeamChatPage},
]

export default routes;