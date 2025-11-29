<template>
  <nav class="nav">
    <div class="nav-left">
      <SidebarMenu />
      <router-link to="/public" class="logo  btn-effect">Community</router-link>
    </div>
    <div class="nav-right">
      <!-- 如果用户已登录，显示用户名和登出按钮 -->
      <template v-if="userStore.user">
        <span class="username">{{ userStore.user.username }}</span>
        <button @click="handleLogout" class="nav-btn btn-animated">登出</button>
      </template>

      <!-- 如果未登录，显示登录/注册按钮 -->
      <template v-else>
        <router-link to="/auth" class="nav-btn btn-animated">登录 / 注册</router-link>
      </template>
    </div>
  </nav>
  <div class="nav-spacer" aria-hidden="true"></div>

  
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import '../assets/buttons.css'
import SidebarMenu from '@/components/SidebarMenu.vue'
import { logout } from '@/api/user'


const userStore = useUserStore()
const router = useRouter()

// 登出逻辑：调用后端登出接口再清理本地状态
const handleLogout = async () => {
  try {
    await logout()
  } catch (err) {
    // 忽略错误（仍然清理本地状态）
  }
  userStore.setToken('')
  userStore.setUser(null)
  router.push('/auth')
}
</script>

<style scoped>
*{
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
.nav {
  position: fixed; /* 固定在视口 */
  top: 0;          /* 顶部对齐 */
  left: 0;
  right: 0;
  width: 100%;     /* 横向铺满 */
  height: var(--nav-height); /* 使用 CSS 变量定义导航高度 */
  z-index: 1000;   /* 保证在最上层 */
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: var(--nav-height);
  padding: 0 30px; /* 左右内边距，垂直居中使用 height */
  background-color: #2c0303;
  color: #fff;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  font-weight: bold;
  font-size: 20px;
  color: #fff;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
}

.nav-right {
  display: flex;
  align-items: center;
}

.nav-btn {
  margin-left: 15px;
  padding: 6px 12px;
  background-color: #ff00957a;
  color: #ffff;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-weight: bold;
  text-decoration: none;
}

.nav-btn:hover {
  opacity: 0.8;
}

.username {
  font-weight: bold;
}

/* 占位：与 --nav-height 保持一致，避免固定导航遮挡页面 */
.nav-spacer {
  height: var(--nav-height);
}

/* 给 btn-effect 补充基础样式，确保 transform 可见且有过渡 */
.btn-effect {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transform-origin: center;
  transition: transform 200ms cubic-bezier(.2,.8,.2,1), box-shadow 200ms ease;
  will-change: transform, box-shadow;
}

.btn-effect:hover {
  transform:  scale(1.15);
}

.btn-effect:active{
  transform:  scale(0.95); /* 点击短暂回弹 */
}
</style>
