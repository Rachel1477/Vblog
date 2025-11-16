<template>
  <div class="page shell">
    <h2>我的笔记</h2>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <div v-if="notes.length === 0">暂无笔记</div>
      <div class="note-list">
        <div v-for="note in notes" :key="note.id" class="note-card">
          <h3>{{ note.title }}</h3>
          <p>{{ note.content }}</p>
          <small>浏览次数：{{ note.viewCount }}</small>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import axios from 'axios'

const userStore = useUserStore()
const router = useRouter()
const notes = ref([])
const loading = ref(true)

const fetchNotes = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/note/my', {
      headers: { Authorization: `Bearer ${userStore.token}` }
    })
    if (res.data.code === 200) {
      notes.value = res.data.data
    } else {
      alert('获取笔记失败：' + (res.data.message || '未知错误'))
    }
  } catch (err) {
    console.error('获取笔记失败：', err)
    alert('获取笔记失败，请检查网络或后端')
  } finally {
    loading.value = false
  }
}

const handleLogout = () => {
  userStore.clear()
  router.push('/login')
}

onMounted(() => fetchNotes())
</script>

<style scoped>
.page { max-width: 1000px; margin: 50px auto; padding: 20px; font-family: sans-serif; }
.shell { background-color: #ecf0f3; box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #f9f9f9; border-radius: 12px; padding: 30px; }
.logout-btn { padding: 6px 12px; border-radius: 5px; border: none; background-color: #f44336; color: white; cursor: pointer; margin-bottom: 20px; }

.note-list { display: flex; flex-wrap: wrap; gap: 20px; }
.note-card { flex: 1 1 300px; background-color: #ecf0f3; border-radius: 15px; padding: 20px;
  box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #f9f9f9; transition: 0.3s; }
.note-card:hover { box-shadow: 4px 4px 12px #d1d9e6, -4px -4px 12px #f9f9f9; }
h3 { margin-bottom: 10px; }
.loading { text-align: center; font-size: 16px; padding: 20px; }
</style>
