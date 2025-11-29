import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import { getCurrentUser } from '@/api/user'
import { useUserStore } from '@/store/user'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
app.use(router)

// If a token exists, fetch current user and populate the store
const token = localStorage.getItem('token')
if (token) {
	const userStore = useUserStore(pinia)
	getCurrentUser().then(res => {
		if (res?.data?.code === 200) {
			userStore.setUser(res.data.data)
		}
	}).catch(() => {
		// ignore errors here; user will be redirected to auth when necessary
	})
}

app.mount('#app')
