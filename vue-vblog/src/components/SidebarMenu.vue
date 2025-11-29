<template>
  <div class="sidebar-root" ref="root">
    <button class="hamburger " @click="open = !open" :aria-expanded="open" aria-label="打开侧边菜单">
      <span class="bar"></span>
      <span class="bar"></span>
      <span class="bar"></span>
    </button>

    <div class="sidebar" :class="{ open }" @click.self="open = false">
      <div class="sidebar-inner">
        <h3 class="title">菜单</h3>
        <button class="menu-item" :disabled="!isLogged" @click="onMyNotes">我的笔记</button>
        <button class="menu-item" :disabled="!isLogged" @click="onCreateNote">创建笔记</button>
        <button class="menu-item" :disabled="!isLogged" @click="onTodoList">To-do List</button>
        <!-- <button class="menu-item" :disabled="!isLogged" @click="onProfile">我的资料</button> -->
        <div class="spacer"></div>
        <div class="hint">登录后可使用所有功能</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const open = ref(false)
const root = ref(null)
const router = useRouter()
const userStore = useUserStore()

function handleDocumentClick(e) {
  // if click is outside the root element, close the sidebar
  if (!root.value) return
  if (open.value && !root.value.contains(e.target)) {
    open.value = false
  }
}

function handleKeyDown(e) {
  if (e.key === 'Escape' && open.value) open.value = false
}

onMounted(() => {
  document.addEventListener('click', handleDocumentClick)
  document.addEventListener('keydown', handleKeyDown)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleDocumentClick)
  document.removeEventListener('keydown', handleKeyDown)
})

const isLogged = computed(() => !!userStore.token)

function onMyNotes() {
  if (!isLogged.value) return
  router.push('/notes')
  open.value = false
}

function onCreateNote() {
  if (!isLogged.value) return
  router.push('/create')
  open.value = false
}

function onTodoList() {
  if (!isLogged.value) return
  router.push('/todo')
  open.value = false
}


function onProfile() {
  if (!isLogged.value) return
  router.push('/Profile')
  open.value = false
}
</script>

<style scoped>
.sidebar-root { display: inline-block; }
.hamburger { /* small reset */
  background: transparent;  padding: 6px; display: inline-flex; flex-direction: column; gap: 3px; cursor: pointer;
  transition: transform 160ms cubic-bezier(.2,.8,.2,1);
  will-change: transform;
  border:#9aa0b4 solid 2px;
  border-radius: 6px;
}
.hamburger:hover {
  transform: scale(1.12);
}
.hamburger:active {
  transform: scale(0.94);
}
.hamburger .bar { 
    width: 20px; 
    height: 4px; 
    background: #fff; 
    border-radius: 1px; 
}

.sidebar {
  position: fixed;
  border-radius: 0 20px px 0;
  top: var(--nav-height);
  left: 0;
  height: calc(100vh - var(--nav-height));
  width: 320px; /* fixed width for predictable layout */
  transform: translateX(-100%); /* off-canvas by default */
  overflow: hidden;
  background: rgba(59, 1, 1, 0.75);
  color: #fff;
  z-index: 1200;
  transition: transform 220ms cubic-bezier(.2,.9,.25,1);
  will-change: transform;
  backface-visibility: hidden;
  box-shadow: 2px 0 12px rgba(0,0,0,0.4);
  pointer-events: none; /* avoid interactions while hidden */
}
.sidebar.open {
  transform: translateX(0);
  pointer-events: auto;
}

.sidebar-inner { display: flex; flex-direction: column; padding: 20px; height: 100%; }
.title { margin: 0 0 12px 0; font-size: 18px; }
.menu-item { display: block; width: 100%; padding: 10px; margin-bottom: 8px; text-align: left; border-radius: 6px; border: none; cursor: pointer; background: rgba(255,255,255,0.04); color: #fff; }
.menu-item:disabled { background: rgba(255,255,255,0.02); color: #9aa0b4; cursor: not-allowed; }
.spacer { flex: 1 1 auto }
.hint { font-size: 12px; color: #9aa0b4 }

/* bars defined above; no duplicate rules */
</style>
