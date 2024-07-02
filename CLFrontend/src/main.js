import { createApp, ref } from 'vue'
import './style.css'
import App from './App.vue'
import routes from './config/router'
import * as VueRouter from 'vue-router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import store from "./store/store.js";

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

const router = VueRouter.createRouter({
    // 4. 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
    history: VueRouter.createWebHashHistory(),
    routes, // `routes: routes` 的缩写
})


app.use(router)

app.use(store)

app.mount('#app')