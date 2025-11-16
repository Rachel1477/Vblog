<template>
  <div class="page shell">
    <h2>公开笔记</h2>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <div v-if="notes.length === 0">暂无公开笔记</div>
      <div class="note-list">
        <div v-for="note in notes" :key="note.id" class="note-card">
          <h3>{{ note.title }}</h3>
          <p>{{ note.content }}</p>
          <small>作者ID：{{ note.userId }} | 浏览次数：{{ note.viewCount }}</small>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const notes = ref([])
const loading = ref(true)

const fetchPublicNotes = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/note/public')
    if (res.data.code === 200) {
      notes.value = res.data.data
    } else {
      alert('获取公开笔记失败：' + (res.data.message || '未知错误'))
    }
  } catch (err) {
    console.error('获取公开笔记失败：', err)
    alert('获取公开笔记失败，请检查网络或后端')
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchPublicNotes())
</script>

<style scoped>
.page { max-width: 1000px; margin: 50px auto; padding: 20px; font-family: sans-serif; }
.shell { background-color: #ecf0f3; box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #f9f9f9; border-radius: 12px; padding: 30px; }

.note-list { display: flex; flex-wrap: wrap; gap: 20px; }
.note-card { flex: 1 1 300px; background-color: #ecf0f3; border-radius: 15px; padding: 20px;
  box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #f9f9f9; transition: 0.3s; }
.note-card:hover { box-shadow: 4px 4px 12px #d1d9e6, -4px -4px 12px #f9f9f9; }
h3 { margin-bottom: 10px; }
.loading { text-align: center; font-size: 16px; padding: 20px; }
</style>
