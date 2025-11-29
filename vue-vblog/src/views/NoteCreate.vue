<template>
  <div class="note-create">
    <video class="bg-video" autoplay muted loop>
      <source src="@/assets/Background.mp4" type="video/mp4" />
    </video>
    <div class="page shell">
    
    <div class="header">
      <h2>{{ isEdit ? '编辑笔记' : '创建笔记' }}</h2>
    </div>

    <form @submit.prevent="handleSubmit" class="form-container">
      <div class="form-group">
        <label for="title">标题</label>
        <input 
          id="title"
          v-model="title" 
          type="text"
          placeholder="请输入笔记标题" 
          class="input-field"
          required
        />
      </div>

      <div class="form-group">
        <label for="content">内容</label>
        <textarea 
          id="content"
          v-model="content" 
          placeholder="请输入笔记内容" 
          class="textarea-field"
          rows="12"
          required
        ></textarea>
      </div>

      <div class="form-group">
        <label for="status">状态</label>
        <div class="radio-group">
          <label class="radio-label">
            <input type="radio" name="status" v-model.number="status" :value="0" /> 私密
          </label>
          <label class="radio-label">
            <input type="radio" name="status" v-model.number="status" :value="1" /> 公开
          </label>
        </div>
      </div>

      <div class="form-actions">
        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? '保存中...' : (isEdit ? '更新笔记' : '创建笔记') }}
        </button>
        <button type="button" @click="goBack" class="btn-cancel">取消</button>
      </div>
    </form>

    <div v-if="error" class="error-message">{{ error }}</div>
  </div>


  </div>
  
  
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { createNote, updateNote, getNoteById } from '@/api/note'
import { useRouter, useRoute } from 'vue-router'

const title = ref('')
const content = ref('')
const status = ref(1)
const loading = ref(false)
const error = ref('')
const router = useRouter()
const route = useRoute()
const isEdit = ref(false)
const noteId = ref(null)

const handleSubmit = async () => {
  if (!title.value.trim()) {
    error.value = '请输入笔记标题'
    return
  }
  if (!content.value.trim()) {
    error.value = '请输入笔记内容'
    return
  }

  loading.value = true
  error.value = ''
  try {
    const data = {
      title: title.value,
      content: content.value,
      status: status.value
    }

    if (isEdit.value && noteId.value) {
      const res = await updateNote(noteId.value, data)
      if (res.data?.code === 200) {
        alert('更新成功')
        router.push(`/note/${noteId.value}`)
      } else {
        error.value = res.data?.message || '更新失败'
      }
    } else {
      const res = await createNote(data)
      if (res.data?.code === 200) {
        alert('创建成功')
        router.push('/notes')
      } else {
        error.value = res.data?.message || '创建失败'
      }
    }
  } catch (err) {
    console.error('操作失败：', err)
    error.value = '操作失败，请检查网络或后端'
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

const fetchNoteForEdit = async () => {
  if (route.params.id) {
    loading.value = true
    try {
      const res = await getNoteById(route.params.id)
      if (res.data?.code === 200 && res.data.data) {
        const note = res.data.data
        title.value = note.title
        content.value = note.content
        status.value = note.status
        noteId.value = note.id
        isEdit.value = true
      } else {
        error.value = '笔记不存在'
      }
    } catch (err) {
      console.error('加载笔记失败：', err)
      error.value = '加载笔记失败'
    } finally {
      loading.value = false
    }
  }
}

onMounted(() => fetchNoteForEdit())
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
  opacity: 1;
  z-index: 0;
  pointer-events: none;
}

.shell { 
  background-color: rgba(236, 240, 243, 0.1); 
  box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #f9f9f9; 
  border-radius: 12px; 
  padding: 30px;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(4px);
}

.header { margin-bottom: 30px; }
.header h2 { margin: 0; font-size: 24px; color: #ffffff; }

.form-container { display: flex; flex-direction: column; gap: 20px; }

.form-group { display: flex; flex-direction: column; }
.form-group label { 
  font-weight: bold; 
  margin-bottom: 8px; 
  color: #ffffff;
  font-size: 14px;
}

.input-field,
.textarea-field,
.select-field {
  padding: 12px;
  border: 2px solid #d1d9e6;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  transition: 0.3s;
  color: #ffffff;
  background-color: rgba(71, 4, 4, 0.3);
}

.input-field:focus,
.textarea-field:focus,
.select-field:focus {
  outline: none;
  border-color: #7c0513;
  box-shadow: 0 0 8px rgba(130, 4, 36, 0.2);
}

.textarea-field {
  resize: vertical;
  min-height: 300px;
  line-height: 1.6;
}

.form-actions { 
  display: flex; 
  gap: 12px; 
  margin-top: 10px;
}

.btn-submit,
.btn-cancel {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.2s;
}

.btn-submit {
  background-color: #ad1442;
  color: white;
  flex: 1;
}
.btn-submit:hover:not(:disabled) {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(255, 2, 2, 0.3);
}
.btn-submit:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.btn-cancel {
  background-color: #999;
  color: white;
  flex: 1;
}
.btn-cancel:hover {
  transform: translateY(-5px);
  background-color: #888;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.error-message {
  padding: 12px;
  background-color: #ffebee;
  color: #d32f2f;
  border-radius: 8px;
  border-left: 4px solid #d32f2f;
  margin-top: 20px;
  font-size: 14px;
}

.radio-group { display: flex; gap: 16px; align-items: center; }
.radio-label { display: inline-flex; align-items: center; gap: 8px; font-size: 14px; color: #333; }
.radio-label input[type="radio"] { width: 16px; height: 16px; accent-color: #ff0000; }

</style>
