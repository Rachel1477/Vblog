<template>
  <div class="page">
    <h2>登录</h2>
    <input v-model="username" placeholder="用户名" />
    <input v-model="password" type="password" placeholder="密码" />
    <button @click="handleLogin">登录</button>
    <p>没有账号？<router-link to="/register">注册</router-link></p>
  </div>

</template>

<script setup>
import { ref } from 'vue'
import { login, getCurrentUser } from '@/api/user'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const username = ref('')
const password = ref('')
const userStore = useUserStore()
const router = useRouter()

const handleLogin = async () => {
  if (!username.value || !password.value) {
    alert('用户名或密码不能为空')
    return
  }

  try {
    const res = await login({ username: username.value, password: password.value })
    console.log('后端返回：', res.data)

    // ✅ 正确处理后端返回 data
    if (res.data && res.data.code === 200 && res.data.data && res.data.data.token) {
      const token = res.data.data.token
      const user = res.data.data.user

      userStore.setToken(token)
      userStore.setUser(user)
      router.push('/notes') // 登录成功跳转
    } else {
      alert('登录失败：' + (res.data.message || '未知错误'))
    }
  } catch (err) {
    console.error('登录请求出错：', err)
    alert('登录失败，请检查后端或网络')
  }
}
</script>

<style scoped>
.page { max-width: 400px; margin: 50px auto; display: flex; flex-direction: column; }
input { margin-bottom: 10px; padding: 5px; }
button { padding: 5px; cursor: pointer; }





</style>
