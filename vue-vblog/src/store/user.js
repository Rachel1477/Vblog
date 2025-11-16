import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        user: null
    }),
    actions: {
        setToken(token) {
            this.token = token
            localStorage.setItem('token', token)
        },
        setUser(user) {
            this.user = user
        },
        logout() {
            this.token = ''
            this.user = null
            localStorage.removeItem('token')
        }
    }
})
