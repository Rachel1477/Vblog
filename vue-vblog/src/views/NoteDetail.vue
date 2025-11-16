<template>
  <div class="note-detail">
    <h2>{{ note.title }}</h2>
    <p>{{ note.content }}</p>
    <p>状态：{{ note.status === 1 ? '公开' : '私密' }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getNoteById } from '@/api/note'

const note = ref({})
const route = useRoute()
const id = route.params.id

const fetchNote = async () => {
  const res = await getNoteById(id)
  note.value = res.data
}

onMounted(fetchNote)
</script>

<style scoped>
.note-detail {
  max-width: 600px;
  margin: 20px auto;
}
</style>
