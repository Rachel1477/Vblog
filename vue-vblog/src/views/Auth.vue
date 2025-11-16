<template>
  <div class="container" :class="{ 'right-panel-active': isLoginActive }">


    <!-- 注册表单 -->
    <div class="form-container sign-in-container">
      <form @submit.prevent="handleRegister">
        <h1>创建账号</h1>
        <input v-model="regUsername" placeholder="用户名" />
        <input v-model="regPassword" type="password" placeholder="密码" />
        <button type="submit">SIGN UP</button>
      </form>
    </div>

    <!-- 登录表单 -->
    <div class="form-container sign-up-container">
      <form @submit.prevent="handleLogin">
        <h1>登入账号</h1>
        <input v-model="loginUsername" placeholder="用户名" />
        <input v-model="loginPassword" type="password" placeholder="密码" />
        <button type="submit">SIGN IN</button>
      </form>
    </div>

    

    <!-- 左右滑动 Panel -->
    <div class="overlay-container">
      <div class="overlay">
        <div class="overlay-panel overlay-left">
          <h1>Welcome Back！</h1>
          <p>没有账号？去注册一个账号！</p>
          <button class="ghost" @click="toggleForm ">SIGN UP</button>
        </div>

        <div class="overlay-panel overlay-right">
          <h1>Hello Friend!</h1>
          <p>已经有账号？去登录账号来进入奇妙世界吧！</p>
          <button class="ghost" @click="toggleForm ">SIGN IN</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { login, register } from '@/api/user'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const loginUsername = ref('')
const loginPassword = ref('')
const regUsername = ref('')
const regPassword = ref('')

// 当前状态：true = 登录界面
const isLoginActive = ref(true)
const isAnimating = ref(false)

const toggleForm = () => {
  isAnimating.value = true
  isLoginActive.value = !isLoginActive.value
  setTimeout(() => (isAnimating.value = false), 1250)
}

const handleLogin = async () => {
  if (!loginUsername.value.trim() || !loginPassword.value.trim()) {
    alert("用户名或密码不能为空")
    return
  }
  try {
    const res = await login({
      username: loginUsername.value,
      password: loginPassword.value
    })
    if (res.data?.code === 200) {
      const { token, user } = res.data.data
      userStore.setToken(token)
      userStore.setUser(user)
      router.push('/notes')
    } else {
      alert("登录失败：" + res.data?.message)
    }
  } catch (err) {
    alert("请求失败，请检查后端")
  }
}

const handleRegister = async () => {
  if (!regUsername.value.trim() || !regPassword.value.trim()) {
    alert("用户名或密码不能为空")
    return
  }

  try {
    const res = await register({
      username: regUsername.value,
      password: regPassword.value
    })
    if (res.data?.code !== 200) {
      alert("注册失败：" + res.data?.message)
      return
    }

    loginUsername.value = regUsername.value;
    loginPassword.value = regPassword.value;
  } catch (err) {
    alert("请求失败，请检查后端")
  }
}
</script>


<style scoped>
* {
  box-sizing: border-box;
}

.container {
  background: #eef2f7;
  box-shadow: 0 10px 30px #d4dce6;
  border-radius: 20px;
  position: relative;
  width: 900px;
  max-width: 100%;
  min-height: 600px;
  overflow: hidden;
  margin: 40px auto;
}

/* Panel 基础 */
.form-container {
  position: absolute;
  top: 0;
  height: 100%;
  transition: all 0.6s ease-in-out;
  display: flex;
  align-items: center;
  justify-content: center;
}

form {
  background: #eef3f9;
  display: flex;
  flex-direction: column;
  padding: 0 50px;
  width: 100%;
  max-width: 400px;
  text-align: center;
}

form input {
  margin: 10px 0;
  padding: 12px;
  border-radius: 10px;
  border: none;
  background: #f5f7fa;
  box-shadow: inset 2px 2px 5px #cfd6dd, inset -2px -2px 5px #fff;
}

form button {
  margin-top: 20px;
  padding: 12px;
  background: #4b70e2;
  color: #fff;
  border: none;
  border-radius: 20px;
  cursor: pointer;
}

/* 登录表单位于right */
.sign-in-container {
  left: 0;
  width: 50%;
  z-index: 2;
}

/* 注册表单位于left（初始隐藏） */
.sign-up-container {
  left: 0;
  width: 50%;
  opacity: 0;
  z-index: 1;
}

/* overlay 外层 */
.overlay-container {
  position: absolute;
  top: 0;
  left: 50%;
  width: 50%;
  height: 100%;
  overflow: hidden;
  transition: transform 0.6s ease-in-out;
  z-index: 100;
}

/* overlay 内容整体 */
.overlay {
  background: #4b70e2;
  color: #fff;
  position: relative;
  left: -100%;
  height: 100%;
  width: 200%;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
}

/* overlay 中两个面板 */
.overlay-panel {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 40px;
  text-align: center;
  top: 0;
  height: 100%;
  width: 50%;
  justify-content: center;
}

.overlay-left {
  transform: translateX(0);
}

.overlay-right {
  right: 0;
  transform: translateX(0);
}

/* 按钮 */
.ghost {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid #fff;
  padding: 10px 30px;
  border-radius: 20px;
}

/* 🟦 当切换到注册模式时的动画 */
.container.right-panel-active .sign-in-container {
  transform: translateX(100%);
}

.container.right-panel-active .sign-up-container {
  transform: translateX(100%);
  opacity: 1;
  z-index: 5;
}

.container.right-panel-active .overlay-container {
  transform: translateX(-100%);
}

.container.right-panel-active .overlay {
  transform: translateX(50%);
}

button:hover {
  transform: scale(1.05); /* 悬停放大 */
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3); /* 阴影增强 */
}

button:active{
  transform: scale(0.95); /* 点击缩小 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2); /* 阴影减弱 */
}
</style>

