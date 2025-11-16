<template>
  <div class="page">
    <h2>注册</h2>
    <input v-model="username" placeholder="用户名" />
    <input v-model="password" type="password" placeholder="密码" />
    <button @click="handleRegister">注册并登录</button>
    <p>已有账号？<router-link to="/login">登录</router-link></p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { register, login } from '@/api/user'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const username = ref('')
const password = ref('')
const userStore = useUserStore()
const router = useRouter()

const handleRegister = async () => {
  if (!username.value || !password.value) {
    alert('用户名或密码不能为空')
    return
  }

  const payload = { username: username.value, password: password.value }
  console.log('发送注册请求：', payload)

  try {
    // 1️⃣ 注册用户
    const res = await register(payload)
    console.log('注册返回：', res.data)

    if (res.data && res.data.code !== 200) {
      alert('注册失败：' + (res.data.message || '未知错误'))
      return
    }

    // 2️⃣ 注册成功后立即登录
    const loginRes = await login(payload)
    console.log('登录返回：', loginRes.data)

    if (loginRes.data && loginRes.data.code === 200 && loginRes.data.data?.token) {
      const token = loginRes.data.data.token
      const user = loginRes.data.data.user

      userStore.setToken(token)
      userStore.setUser(user)
      router.push('/notes') // 登录成功跳转
    } else {
      alert('登录失败：' + (loginRes.data.message || '未知错误'))
    }

  } catch (err) {
    console.error('注册或登录请求出错：', err)
    alert('操作失败，请检查后端或网络')
  }
}
</script>

<style scoped>
.page { max-width: 400px; margin: 50px auto; display: flex; flex-direction: column; }
input { margin-bottom: 10px; padding: 5px; }
button { padding: 5px; cursor: pointer; }
</style>
