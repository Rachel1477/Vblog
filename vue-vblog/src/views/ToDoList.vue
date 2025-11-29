<template>
  <div class="page shell">
    <video class="bg-video" autoplay muted loop>
      <source src="@/assets/Background.mp4" type="video/mp4" />
    </video>
    <div class="container">
      <!-- 左侧日历 -->
      <div class="calendar-section">
        <div class="calendar-header">
          <button @click="prevMonth" class="month-btn">← 上个月</button>
          <h2>{{ monthYear }}</h2>
          <button @click="nextMonth" class="month-btn">下个月 →</button>
        </div>
        
        <div class="calendar">
          <!-- 星期头 -->
          <div class="weekdays">
            <div v-for="day in ['日', '一', '二', '三', '四', '五', '六']" :key="day" class="weekday">
              {{ day }}
            </div>
          </div>
          
          <!-- 日期 -->
          <div class="dates">
            <button 
              v-for="date in calendarDates" 
              :key="date.key"
              @click="selectDate(date.date)"
              :class="[
                'date-btn',
                { 'is-other-month': date.isOtherMonth },
                { 'is-today': isToday(date.date) },
                { 'is-selected': isSelected(date.date) },
                { 'has-todo': hasTodo(date.date) }
              ]"
            >
              {{ date.date.getDate() }}
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧待做事项 -->
      <div class="todo-section">
        <div class="todo-header">
          <h2>{{ selectedDateStr }}</h2>
        </div>

        <div class="todo-input-group">
          <input 
            v-model="newTodo" 
            type="text"
            placeholder="添加新的待做事项..."
            @keyup.enter="addTodo"
            class="todo-input"
          />
          <button @click="addTodo" class="btn-add">添加</button>
        </div>

        <div class="todo-list">
          <div 
            v-if="todosForSelectedDate.length === 0" 
            class="empty-tip"
          >
            暂无待做事项
          </div>

          <div 
            v-for="(todo, index) in todosForSelectedDate" 
            :key="index"
            class="todo-item"
            :class="{ completed: todo.completed }"
          >
            <div class="todo-content">
              <input 
                type="checkbox" 
                v-model="todo.completed"
                class="todo-checkbox"
              />
              <span class="todo-text">{{ todo.text }}</span>
            </div>
            <button @click="deleteTodo(index)" class="btn-delete">删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const currentDate = ref(new Date())
const selectedDate = ref(new Date())
const todos = ref({})
const newTodo = ref('')

// 初始化日期
onMounted(() => {
  const today = new Date()
  currentDate.value = new Date(today.getFullYear(), today.getMonth(), 1)
  selectedDate.value = new Date(today)
})

// 计算当前月份的年月字符串
const monthYear = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth() + 1
  const monthNames = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
  return `${year}年 ${monthNames[month - 1]}`
})

// 计算日历显示的所有日期
const calendarDates = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()
  
  // 该月第一天
  const firstDay = new Date(year, month, 1)
  // 该月最后一天
  const lastDay = new Date(year, month + 1, 0)
  
  // 该月第一天是星期几（0-6）
  const startDay = firstDay.getDay()
  // 上个月需要显示的天数
  const prevMonthDays = startDay
  // 总需要显示的天数（包括下月）
  const totalDays = 42 // 6 行 × 7 列
  
  const dates = []
  
  // 添加上个月的日期
  const prevMonth = new Date(year, month, 0)
  const prevMonthLastDay = prevMonth.getDate()
  for (let i = prevMonthDays - 1; i >= 0; i--) {
    dates.push({
      date: new Date(year, month - 1, prevMonthLastDay - i),
      isOtherMonth: true,
      key: `prev-${i}`
    })
  }
  
  // 添加当月的日期
  for (let i = 1; i <= lastDay.getDate(); i++) {
    dates.push({
      date: new Date(year, month, i),
      isOtherMonth: false,
      key: `current-${i}`
    })
  }
  
  // 添加下个月的日期
  const remainingDays = totalDays - dates.length
  for (let i = 1; i <= remainingDays; i++) {
    dates.push({
      date: new Date(year, month + 1, i),
      isOtherMonth: true,
      key: `next-${i}`
    })
  }
  
  return dates
})

// 检查是否是今天
const isToday = (date) => {
  const today = new Date()
  return date.getFullYear() === today.getFullYear() &&
         date.getMonth() === today.getMonth() &&
         date.getDate() === today.getDate()
}

// 检查是否是选中的日期
const isSelected = (date) => {
  return date.getFullYear() === selectedDate.value.getFullYear() &&
         date.getMonth() === selectedDate.value.getMonth() &&
         date.getDate() === selectedDate.value.getDate()
}

// 检查该日期是否有待做事项
const hasTodo = (date) => {
  const key = getDateKey(date)
  return todos.value[key] && todos.value[key].length > 0
}

// 获取日期的键
const getDateKey = (date) => {
  return `${date.getFullYear()}-${date.getMonth()}-${date.getDate()}`
}

// 选择日期
const selectDate = (date) => {
  selectedDate.value = new Date(date)
}

// 上个月
const prevMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1)
}

// 下个月
const nextMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1)
}

// 选中日期的字符串
const selectedDateStr = computed(() => {
  const date = selectedDate.value
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekDay = weekDays[date.getDay()]
  return `${year}年${month}月${day}日 ${weekDay}`
})

// 获取选中日期的待做事项
const todosForSelectedDate = computed(() => {
  const key = getDateKey(selectedDate.value)
  return todos.value[key] || []
})

// 添加待做事项
const addTodo = () => {
  if (!newTodo.value.trim()) return
  
  const key = getDateKey(selectedDate.value)
  if (!todos.value[key]) {
    todos.value[key] = []
  }
  
  todos.value[key].push({
    text: newTodo.value,
    completed: false,
    createdAt: new Date()
  })
  
  newTodo.value = ''
  
  // 保存到本地存储
  saveTodos()
}

// 删除待做事项
const deleteTodo = (index) => {
  const key = getDateKey(selectedDate.value)
  if (todos.value[key]) {
    todos.value[key].splice(index, 1)
    saveTodos()
  }
}

// 保存待做事项到本地存储
const saveTodos = () => {
  localStorage.setItem('todoList', JSON.stringify(todos.value))
}

// 从本地存储加载待做事项
const loadTodos = () => {
  const saved = localStorage.getItem('todoList')
  if (saved) {
    todos.value = JSON.parse(saved)
  }
}

// 页面加载时读取本地存储
onMounted(() => {
  loadTodos()
})
</script>

<style scoped>

*{
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.page { 
  height: calc(100vh - var(--nav-height));
  padding: 15px;
  font-family: sans-serif;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.bg-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 1;
  z-index: 0;
  pointer-events: none;
}
.shell { 
  background-color: transparent;
  box-shadow: none;
  border-radius: 0; 
  padding: 20px;
  flex: 1;
  display: flex;
  overflow: auto;
  position: relative;
  z-index: 1;
}

.container {
  display: grid;
  grid-template-columns: 0.9fr 1.1fr;
  gap: 20px;
  width: 100%;
  height: 100%;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

/* 左侧日历 */
.calendar-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: hidden;
  padding: 12px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(106, 64, 140, 0.06);
  backdrop-filter: blur(8px);
  height: 100%;
}

.calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 15px;
  flex-shrink: 0;
}

.calendar-header h2 {
  font-size: 18px;
  color: #ffffff;
  flex: 1;
  text-align: center;
}

.month-btn {
  padding: 8px 14px;
  border: 2px solid #fe0c0c;
  background: linear-gradient(135deg, #ffffff, #f5f0ff);
  color: #df0c44;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  font-size: 12px;
  box-shadow: 0 4px 8px rgba(174, 106, 199, 0.15);
}

.month-btn:hover {
  background: #f66c6c;
  color: #fff;
  box-shadow: 0 6px 12px rgba(174, 106, 199, 0.3);
}

.calendar {
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;
  gap: 6px;
  height: 100%;
}

.weekdays {
  position: sticky;
  top: 0;
  z-index: 20;
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
  flex-shrink: 0;
  background-color: #620202;
  padding: 6px;
  border-radius: 8px;
}

.weekday {
  text-align: center;
  font-weight: 700;
  color: #ffffff;
  background-color: #620202;
  font-size: 14px;
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  /* background: linear-gradient(135deg, #fbf8ff, #f6f2ff); */
  border-radius: 6px;
  letter-spacing: 0.5px;
}

.dates {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-template-rows: repeat(6, 1fr);
  gap: 4px;
  flex: 1;
  overflow: auto;
  margin-top: 15px;
}

.date-btn {
  aspect-ratio: 1;
  border: 2px solid #e6dff4;
  background: linear-gradient(135deg, rgba(255,255,255,0.8), rgba(251,248,255,0.85));
  color: #222;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 700;
  transition: all 0.18s ease;
  font-size: 16px;
  padding: 0;
  box-shadow: 0 2px 6px rgba(121, 78, 155, 0.06);
  min-width: 0;
  backdrop-filter: blur(4px);
}

.date-btn:hover {
  border-color: #c55b6b;
  box-shadow: 0 4px 8px rgba(199, 106, 114, 0.2);
  transform: translateY(-5px);
}

.date-btn.is-other-month {
  color: #bbb;
  background: #f8f8f8;
  border-color: #e8e8e8;
}

.date-btn.is-today {
  border: 3px solid #c76a6a;
  background: #782424;
  color: #fff;
  box-shadow: 0 0 16px rgba(199, 106, 106, 0.4), inset 0 1px 2px rgba(255, 255, 255, 0.3);
  font-weight: 700;
}

.date-btn.is-selected {
  background: #d03f3f;
  color: #fff;
  box-shadow: 0 4px 12px rgba(239, 48, 48, 0.3);
}

.date-btn.has-todo {
  background: linear-gradient(135deg, #ffb3b3, #ff99cc);
  border-color: #ff6969;
  color: #fff;
  font-weight: 700;
  box-shadow: 0 4px 8px rgba(255, 105, 105, 0.3);
}

.date-btn.has-todo.is-today {
  background: #870202;
  border-color: #c76a6a;
  color: #fff;
  font-weight: 700;
  box-shadow: 
    inset 0 0 0 3px #fff,
    0 0 16px rgba(199, 106, 106, 0.4);
}

/* 右侧待做事项 */
.todo-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.3);
  padding: 12px;
  border-radius: 12px;
  backdrop-filter: blur(8px);
  box-shadow: 0 6px 18px rgba(106, 64, 140, 0.06);
}

.todo-header {
  flex-shrink: 0;
}

.todo-header h2 {
  margin: 0;
  font-size: 16px;
  color: #fafafa;
}

.todo-input-group {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.todo-input {
  flex: 1;
  padding: 10px;
  border: 2px solid #d1d9e6;
  border-radius: 6px;
  font-size: 13px;
  transition: 0.2s;
  min-width: 0;
}

.todo-input:focus {
  outline: none;
  border-color: #c76a6a;
  box-shadow: 0 0 8px rgba(174, 106, 199, 0.2);
}

.btn-add {
  padding: 10px 16px;
  background-color: #890505;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: 0.2s;
  font-size: 13px;
  white-space: nowrap;
}

.btn-add:hover {
  background-color: #45a049;
}

.todo-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-right: 8px;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 20px;
  font-size: 13px;
}

.todo-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background-color: rgba(94, 4, 13, 0.6);
  border-radius: 8px;
  box-shadow: 2px 2px 8px rgba(0, 0, 0, 0.05);
  transition: 0.2s;
  gap: 12px;
  flex-shrink: 0;
  backdrop-filter: blur(4px);
  min-height: 60px;
}

.todo-item:hover {
  box-shadow: 4px 4px 12px rgba(0, 0, 0, 0.1);
}

.todo-item.completed {
  opacity: 0.6;
}

.todo-item.completed .todo-text {
  text-decoration: line-through;
  color: #999;
}

.todo-content {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.todo-checkbox {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: #c00f0f;
  flex-shrink: 0;
}

.todo-text {
  color: #ffffff;
  word-break: break-word;
  font-size: 16px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.btn-delete {
  padding: 8px 14px;
  background-color: #f44336;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: 0.2s;
  white-space: nowrap;
  flex-shrink: 0;
}

.btn-delete:hover {
  background-color: #da190b;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .container {
    grid-template-columns: 1fr;
  }
}

/* 滚动条美化 */
.calendar::-webkit-scrollbar,
.todo-list::-webkit-scrollbar {
  width: 6px;
}

.calendar::-webkit-scrollbar-track,
.todo-list::-webkit-scrollbar-track {
  background: transparent;
}

.calendar::-webkit-scrollbar-thumb,
.todo-list::-webkit-scrollbar-thumb {
  background: #d1d9e6;
  border-radius: 3px;
}

.calendar::-webkit-scrollbar-thumb:hover,
.todo-list::-webkit-scrollbar-thumb:hover {
  background: #ae6ac7;
}
</style>
