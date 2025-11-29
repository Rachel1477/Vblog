<template>
    <div class="page">

    </div>
  <div class="profile-container">
    <h2>用户信息编辑</h2>

    <!-- 加载中 -->
    <div v-if="loading">正在加载用户信息...</div>

    <!-- 用户信息 -->
    <div v-else>
      <form @submit.prevent="saveProfile" class="form-block">
        <label>
          用户名:
          <input type="text" v-model="profile.nickname" />
        </label>

        <!-- <label>
          邮箱：
          <input type="email" v-model="profile.email" />
        </label> -->

        <button type="submit">保存资料</button>
      </form>

      <h3>修改密码</h3>
      <form @submit.prevent="submitPassword" class="form-block">
        <label>
          旧密码：
          <input type="password" v-model="password.oldPassword" required />
        </label>

        <label>
          新密码：
          <input type="password" v-model="password.newPassword" required />
        </label>

        <button type="submit">修改密码</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCurrentUser, changePassword } from '@/api/user' // 这里路径按你的项目调整

// 用户资料
const profile = ref({
  username: '',
  nickname: '',
  email: '',
})

// 修改密码用
const password = ref({
  oldPassword: '',
  newPassword: ''
})

const loading = ref(true)

// 获取当前用户信息
const loadUser = async () => {
  try {
    const res = await getCurrentUser()
    profile.value = res.data
  } catch (e) {
    alert('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

// 保存资料（如果你将来加入 updateUser API，这里可对接）
const saveProfile = async () => {
  alert('当前后台没有提供修改资料接口，请在 API 加上 /api/user/update 后再启用。')
}

// 修改密码
const submitPassword = async () => {
  try {
    await changePassword({
      oldPassword: password.value.oldPassword,
      newPassword: password.value.newPassword
    })
    alert('密码修改成功，请重新登录')
    localStorage.removeItem('token')
    window.location.href = '/login'
  } catch (err) {
    alert('修改失败：' + (err.response?.data?.message || '服务器错误'))
  }
}

onMounted(() => {
  loadUser()
})
</script>

<style scoped>
.profile-container {
  max-width: 480px;
  margin: 20px auto;
  padding: 20px;
}
.form-block {
  margin-bottom: 20px;
}
label {
  display: block;
  margin: 14px 0;
}
input {
  width: 100%;
  padding: 6px;
  margin-top: 4px;
}
button {
  margin-top: 10px;
  padding: 8px 14px;
  cursor: pointer;
}
</style>
