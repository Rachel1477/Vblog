import { createRouter, createWebHistory } from 'vue-router'
import Auth from '@/views/Auth.vue'
import Notes from '@/views/Notes.vue'
import PublicNotes from '@/views/PublicNotes.vue'
import { useUserStore } from '@/store/user'

const routes = [
  { path: '/auth', component: Auth },
  { path: '/notes', component: Notes, meta: { requireAuth: true } },
  { path: '/public', component: PublicNotes } // 游客可访问
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
