import request from './request'

export const login = (data) => request.post('/api/user/login', data)
export const register = (data) => request.post('/api/user/register', data)
export const getCurrentUser = () => request.get('/api/user/current')
