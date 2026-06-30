import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/api/request'

export const useUserStore = defineStore('user', () => {
  const user = ref({})
  const token = ref(localStorage.getItem('token') || '')
  const menus = ref([])

  const fetchUser = async () => {
    if (!token.value) return
    try {
      const res = await request.get('/auth/userinfo')
      user.value = res.data
    } catch { /* ignore */ }
  }

  const login = (t, u, m) => {
    token.value = t
    user.value = u
    menus.value = m || []
    localStorage.setItem('token', t)
    localStorage.setItem('username', u?.nickname || u?.username || '')
  }

  const logout = () => {
    token.value = ''
    user.value = {}
    menus.value = []
    localStorage.clear()
  }

  return { user, token, menus, fetchUser, login, logout }
})
