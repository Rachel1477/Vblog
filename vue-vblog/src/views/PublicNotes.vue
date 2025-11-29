<template>
  <div class="public-page">

    <video class="bg-video" autoplay muted loop>
      <source src="@/assets/Background.mp4" type="video/mp4" />
    </video>
    <div class="page shell">
    
    <h2>公开笔记</h2>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <div v-if="notes.length === 0" class="empty">暂无公开笔记</div>
      <div v-else class="note-list">
        <div v-for="note in notes" :key="note.id" class="note-card" @click="goToDetail(note.id)">
          <div class="card-content">
            <h3>{{ note.title }}</h3>
            <p class="preview">{{ note.content?.substring(0, 100) }}...</p>
            <small>作者：{{ note.author?.username || note.username || `用户 ${note.userId}` }}  | 浏览次数：{{ note.viewCount || 0 }} | 创建时间：{{ formatRelativeTime(note.createTime) }}</small>
          </div>
        </div>
      </div>
    </div>
  </div>
  </div>
  
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPublicNotes } from '@/api/note'
import { formatRelativeTime } from '@/utils/dateFormat'

const router = useRouter()
const notes = ref([])
const loading = ref(true)

const fetchPublicNotes = async () => {
  loading.value = true
  try {
    const res = await getPublicNotes()
    if (res.data?.code === 200) {
      notes.value = res.data.data || []
    } else {
      alert('获取公开笔记失败：' + (res.data?.message || '未知错误'))
    }
  } catch (err) {
    console.error('获取公开笔记失败：', err)
    alert('获取公开笔记失败，请检查网络或后端')
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/public-note/${id}`)
}

onMounted(() => fetchPublicNotes())
</script>

<style scoped>

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.public-page {
  display: flex;
  align-items: flex-start; /* 顶部对齐 */
  justify-content: center; /* 水平居中 */
  min-height: calc(100vh - var(--nav-height)); /* 减去导航高度的可视高度 */
  padding: 20px; /* 小屏幕留白 */
  position: relative;
  z-index: 1;
}
.page {
  width: 90%;
  margin: 0 auto; /* 水平居中，顶对齐由父容器控制 */
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
  opacity: 1;
  z-index: 0;
  pointer-events: none;
}

.shell { 
  color: #ffff;
  background-color: rgba(255, 255, 255, 0.1);
  box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #f9f9f9;
  border-radius: 12px;
  padding: 30px;
  position: relative;
  z-index: 10;
  backdrop-filter: blur(4px);
}

.note-list { display: flex; flex-direction: column; gap: 20px; margin-top: 40px; }
.note-card { 
  color: #ffff;
  background-color: rgba(136, 46, 46, 0.6);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #f9f9f9;
  transition: 0.3s;
  cursor: pointer;
  height: 150px;
  transition: all 0.3s ease;
}
.note-card:hover { 
  height: 200px;
  padding: 30px;
  box-shadow: 4px 4px 12px #d1d9e6, -4px -4px 12px #f9f9f9;
  transform: translateY(-5px);
}

.card-content h3 { margin: 0 0 10px 0; }
.card-content .preview { color: #666; margin: 10px 0; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.card-content small { color: #999; }

.loading { text-align: center; font-size: 16px; padding: 20px; }
.empty { text-align: center; color: #999; padding: 40px; font-size: 16px; }
</style>
