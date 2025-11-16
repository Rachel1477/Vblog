<template>
  <nav class="nav">
    <div class="nav-left">
      <router-link to="/" class="logo">MyVblog</router-link>
    </div>
    <div class="nav-right">
      <!-- 如果用户已登录，显示用户名和登出按钮 -->
      <template v-if="userStore.user">
        <span class="username">{{ userStore.user.username }}</span>
        <button @click="handleLogout" class="nav-btn">登出</button>
      </template>

      <!-- 如果未登录，显示登录/注册按钮 -->
      <template v-else>
        <router-link to="/auth" class="nav-btn">登录 / 注册</router-link>
      </template>
    </div>
  </nav>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

// 登出逻辑
const handleLogout = () => {
  userStore.setToken('')
  userStore.setUser(null)
  router.push('/auth')
}
</script>

<style scoped>
.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background-color: #4B70E2;
  color: #fff;
}

.logo {
  font-weight: bold;
  font-size: 20px;
  color: #fff;
  text-decoration: none;
}

.nav-right {
  display: flex;
  align-items: center;
}

.nav-btn {
  margin-left: 15px;
  padding: 6px 12px;
  background-color: #fff;
  color: #4B70E2;
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
</style>
