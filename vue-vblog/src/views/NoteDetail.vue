<template>
  <div class="page shell">
    <video class="bg-video" autoplay muted loop>
      <source src="@/assets/Background.mp4" type="video/mp4" />
    </video>
    <div class="header">
      <button @click="goBack" class="btn-back">← 返回</button>
      <h2>{{ note.title || '加载中...' }}</h2>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="note.id" class="content">
      <div class="meta">
        <small>作者ID：{{ note.userId }} | 浏览次数：{{ note.viewCount || 0 }} | 状态：{{ note.status === 1 ? '公开' : '私密' }}</small>
      </div>
      <div class="body">{{ note.content }}</div>
    </div>
    <div v-else class="error">笔记未找到</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNoteById } from '@/api/note'

const note = ref({})
const loading = ref(true)
const error = ref('')
const route = useRoute()
const router = useRouter()
const id = route.params.id

const fetchNote = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await getNoteById(id)
    if (res.data?.code === 200 && res.data.data) {
      note.value = res.data.data
    } else {
      error.value = res.data?.message || '笔记不存在或已被删除'
    }
  } catch (err) {
    console.error('获取笔记失败：', err)
    error.value = '获取笔记失败，请检查网络或后端'
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => fetchNote())
</script>

<style scoped>
.page { 
  max-width: 900px; 
  margin: 20px auto; 
  padding: 20px; 
  font-family: sans-serif;
  position: relative;
}

.bg-video {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.15;
  z-index: 0;
  pointer-events: none;
}

.shell { 
  background-color: rgba(236, 240, 243, 0.88); 
  box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #f9f9f9; 
  border-radius: 12px; 
  padding: 30px;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(4px);
}

.header { display: flex; align-items: center; gap: 20px; margin-bottom: 30px; }
.header h2 { margin: 0; flex: 1; }

.btn-back { 
  padding: 8px 16px; 
  border-radius: 6px; 
  border: none; 
  background-color: #666; 
  color: white; 
  cursor: pointer; 
  font-weight: bold; 
  transition: 0.2s;
}
.btn-back:hover { background-color: #555; }

.content { }
.meta { 
  padding: 12px; 
  background-color: rgba(0,0,0,0.05); 
  border-radius: 6px; 
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}
.body { 
  line-height: 1.8; 
  font-size: 16px; 
  white-space: pre-wrap; 
  word-wrap: break-word;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}

.loading { text-align: center; font-size: 16px; padding: 40px; color: #666; }
.error { text-align: center; font-size: 16px; padding: 40px; color: #d32f2f; }
</style>
