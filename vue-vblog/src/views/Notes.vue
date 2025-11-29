<template>
  <div class="note-page">

    <video class="bg-video" autoplay muted loop>
      <source src="@/assets/Background.mp4" type="video/mp4" />
    </video>
    <div class="page shell">
    
      <div class="header">
        <h2>我的笔记</h2>
        <button @click="goToCreate" class="btn-primary">+ 创建笔记</button>
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else>
        <div v-if="notes.length === 0" class="empty">暂无笔记</div>
        <div v-else class="note-list">
          <div v-for="note in notes" :key="note.id" class="note-card">
            <div class="card-content">
              <h3>{{ note.title }}</h3>
              <p class="preview">{{ note.content?.substring(0, 100) }}...</p>
              <small>浏览次数：{{ note.viewCount || 0 }}</small>
            </div>
            <div class="card-actions">
              <button @click="goToEdit(note.id)" class="btn-edit">编辑</button>
              <button @click="confirmDelete(note.id)" class="btn-delete">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { getMyNotes, deleteNote } from '@/api/note'

const userStore = useUserStore()
const router = useRouter()
const notes = ref([])
const loading = ref(true)

const fetchNotes = async () => {
  loading.value = true
  try {
    const res = await getMyNotes()
    if (res.data?.code === 200) {
      notes.value = res.data.data || []
    } else {
      alert('获取笔记失败：' + (res.data?.message || '未知错误'))
    }
  } catch (err) {
    console.error('获取笔记失败：', err)
    alert('获取笔记失败，请检查网络或后端')
  } finally {
    loading.value = false
  }
}

const goToCreate = () => {
  router.push('/note/create')
}

const goToEdit = (id) => {
  router.push(`/note/edit/${id}`)
}

const confirmDelete = async (id) => {
  if (!confirm('确定删除这条笔记吗？')) return
  try {
    const res = await deleteNote(id)
    if (res.data?.code === 200) {
      alert('删除成功')
      fetchNotes() // 重新加载列表
    } else {
      alert('删除失败：' + (res.data?.message || '未知错误'))
    }
  } catch (err) {
    console.error('删除失败：', err)
    alert('删除失败，请检查网络或后端')
  }
}

onMounted(() => fetchNotes())
</script>

<style scoped>
.note-page {
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
  max-width: 1000px;
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
  background-color: rgba(255, 255, 255, 0.1); 
  box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #f9f9f9; 
  border-radius: 12px; 
  padding: 30px;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(4px);
}

.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.header h2 { margin: 0; color: #fdfdfd;}

.btn-primary { 
  padding: 8px 16px; 
  border-radius: 6px; 
  border: none; 
  background-color: #d10815; 
  color: white; 
  cursor: pointer; 
  font-weight: bold; 
  transition: 0.2s;
}
.btn-primary:hover { transform: translateY(-5px); }

.note-list { display: flex; flex-direction: column; gap: 20px; }
.note-card { 
  color: #ffff;
  background-color: rgba(136, 46, 46, 0.6);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #f9f9f9;
  transition: 0.3s;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  backdrop-filter: blur(2px);
  transition: all 0.3s ease;
}
.note-card:hover {
  box-shadow: 4px 4px 12px #d1d9e6, -4px -4px 12px #f9f9f9;
  transform: translateY(-5px); 
}

.card-content h3 { margin: 0 0 10px 0; }
.card-content .preview { color: #666; margin: 10px 0; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.card-content small { color: #999; }
.card-content { flex: 1; }

.card-actions { display: flex; flex-direction: column; gap: 8px; margin-left: 20px; }
.btn-edit, .btn-delete { 
  padding: 6px 12px; 
  border-radius: 5px; 
  border: none; 
  cursor: pointer; 
  font-size: 14px;
  font-weight: bold;
  transition: 0.2s;
  width: 60px;
}
.btn-edit { background-color: #c616ed; color: white; }
.btn-edit:hover { background-color: #da0bd05d; }
.btn-delete { background-color: #f44336; color: white; }
.btn-delete:hover { background-color: #da190b; }

h3 { margin-bottom: 10px; }
.loading { text-align: center; font-size: 16px; padding: 20px; }
.empty { text-align: center; color: #999; padding: 40px; font-size: 16px; }
</style>
