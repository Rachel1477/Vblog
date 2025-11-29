/**
 * 格式化日期时间
 * @param {number|string|Date} timestamp - 时间戳或日期字符串
 * @param {string} format - 格式化模式，默认 'YYYY-MM-DD HH:mm'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(timestamp, format = 'YYYY-MM-DD HH:mm') {
  if (!timestamp) return '未知'

  const date = typeof timestamp === 'number' ? new Date(timestamp) : new Date(timestamp)
  
  if (isNaN(date.getTime())) {
    return '未知'
  }

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 获取相对时间（如"2小时前"）
 * @param {number|string|Date} timestamp - 时间戳或日期字符串
 * @returns {string} 相对时间字符串
 */
export function formatRelativeTime(timestamp) {
  if (!timestamp) return '未知'

  const date = typeof timestamp === 'number' ? new Date(timestamp) : new Date(timestamp)
  
  if (isNaN(date.getTime())) {
    return '未知'
  }

  const now = new Date()
  const diff = now - date
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return formatDate(timestamp)
}
