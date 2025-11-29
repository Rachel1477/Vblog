import request from './request'

// 认证相关
export const login = (data) => request.post('/api/user/login', data)
export const register = (data) => request.post('/api/user/register', data)
export const logout = () => request.post('/api/user/logout')
export const refreshToken = () => request.post('/api/user/refresh-token')
export const checkToken = () => request.get('/api/user/check-token')
export const parseToken = () => request.post('/api/user/parse-token')

// 用户信息
export const getCurrentUser = () => request.get('/api/user/current')
export const getUserByUsername = (username) => request.get(`/api/user/${encodeURIComponent(username)}`)
export const changePassword = (data) => request.post('/api/user/change-password', data)
