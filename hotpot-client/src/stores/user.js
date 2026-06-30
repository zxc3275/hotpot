import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/api/request'

export const useUserStore = defineStore('user', () => {
  const user = ref({})
  const token = ref(localStorage.getItem('token') || '')

  const fetchUser = async () => {
    if (!token.value) return
    try {
      const res = await request.get('/auth/userinfo')
      user.value = res.data
    } catch { /* ignore */ }
  }

  const login = (t) => {
    token.value = t
    localStorage.setItem('token', t)
    fetchUser()
  }

  const logout = () => {
    token.value = ''
    user.value = {}
    localStorage.clear()
  }

  return { user, token, fetchUser, login, logout }
})
