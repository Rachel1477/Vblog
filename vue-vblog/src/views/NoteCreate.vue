<template>
  <div class="page">
    <h2>创建笔记</h2>
    <input v-model="title" placeholder="标题" />
    <textarea v-model="content" placeholder="内容"></textarea>
    <button @click="handleCreate">创建</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { createNote } from '@/api/note'
import { useRouter } from 'vue-router'

const title = ref('')
const content = ref('')
const router = useRouter()

const handleCreate = async () => {
  await createNote({ title: title.value, content: content.value, status: 1 })
  alert('创建成功')
  router.push('/notes')
}
</script>

<style scoped>
.page { max-width: 600px; margin: 20px auto; display: flex; flex-direction: column; }
input, textarea { margin-bottom: 10px; padding: 5px; }
button { padding: 5px; cursor: pointer; }
</style>
