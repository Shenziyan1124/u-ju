import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<any>(null)

  async function login(phone: string, password: string) {
    const res: any = await request.post('/admin/login', null, { params: { phone, password } })
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('token', res.data.token)
    return res.data
  }

  async function getUserInfo() {
    const res: any = await request.get('/users/me')
    userInfo.value = res.data
    return res.data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, login, getUserInfo, logout }
})
