import { createRouter, createWebHistory } from 'vue-router'
import Auth from '@/views/Auth.vue'
import Notes from '@/views/Notes.vue'
import PublicNotes from '@/views/PublicNotes.vue'
import ToDoList from '@/views/ToDoList.vue'
import NoteDetail from '@/views/NoteDetail.vue'
import PublicNoteDetail from '@/views/PublicNoteDetail.vue'
import { useUserStore } from '@/store/user'

const routes = [
  { path: '/', component: () => import('@/views/Home.vue') },
  { path: '/auth', component: Auth },
  { path: '/notes', component: Notes, meta: { requireAuth: true } },
  { path: '/public', component: PublicNotes }, // 游客可访问
  { path: '/todo', component: ToDoList, meta: { requireAuth: true } }, // 待做事项
  { path: '/create', component: ()=> import('@/views/NoteCreate.vue'), meta: { requireAuth: true } }, // 兼容旧路由
  { path: '/note/create', component: ()=> import('@/views/NoteCreate.vue'), meta: { requireAuth: true } },
  { path: '/note/edit/:id', component: ()=> import('@/views/NoteCreate.vue'), meta: { requireAuth: true } },
  { path: '/note/:id', component: NoteDetail }, // 笔记详情（用于私密笔记，游客也可查看公开笔记）
  { path: '/public-note/:id', component: PublicNoteDetail }, // 公开笔记详情
  { path: '/profile', component: ()=> import('@/views/UserProfile.vue'), meta: { requireAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requireAuth && !userStore.token) {
    next('/auth')
  } else {
    next()
  }
})

export default router
