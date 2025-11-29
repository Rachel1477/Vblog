import request from './request'

// 测试 API
export const testGet = () => request.get('/api/test')
export const testPost = (data) => request.post('/api/test', data)
