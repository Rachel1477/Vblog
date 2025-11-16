import axios from 'axios'
import { getActivePinia } from 'pinia'
import { useUserStore } from '@/store/user'

const service = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000
})

// 请求拦截器：动态获取 store
service.interceptors.request.use(config => {
    const pinia = getActivePinia()
    if (pinia) {
        const userStore = useUserStore(pinia)
        if (userStore.token) {
            config.headers.Authorization = `Bearer ${userStore.token}`
        }
    }
    return config
}, error => {
    return Promise.reject(error)
})

export default service
