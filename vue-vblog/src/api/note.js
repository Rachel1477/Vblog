import request from './request'



// 1️⃣ 获取当前用户的笔记列表（需要登录）
export function getMyNotes() {
    return request.get('/api/note/my')
}

// 2️⃣ 获取指定用户的笔记列表（游客可访问）
export function getUserNotes(userId) {
    return request.get(`/api/note/user/${userId}`)
}

// 3️⃣ 获取所有公开笔记（游客可访问）
export function getPublicNotes() {
    return request.get('/api/note/public')
}

// 4️⃣ 获取单条笔记详情（游客可访问）
export function getNoteById(id) {
    return request.get(`/api/note/${id}`)
}

// 5️⃣ 创建笔记（需要登录）
export function createNote(data) {
    // data = { title, content, status }
    return request.post('/api/note/create', data)
}

// 6️⃣ 更新笔记（需要登录）
export function updateNote(id, data) {
    // data = { title, content, status }
    return request.put(`/api/note/${id}`, data)
}

// 7️⃣ 删除笔记（需要登录）
export function deleteNote(id) {
    return request.delete(`/api/note/${id}`)
}

