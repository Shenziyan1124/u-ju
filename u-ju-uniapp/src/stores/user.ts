import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref<any>(null)

  async function login(phone: string, password: string) {
    const data: any = await userApi.login({ phone, password })
    token.value = data.token
    userInfo.value = data.user
    uni.setStorageSync('token', data.token)
    return data
  }

  async function register(data: any) {
    const res: any = await userApi.register(data)
    token.value = res.token
    userInfo.value = res.user
    uni.setStorageSync('token', res.token)
    return res
  }

  async function getUserInfo() {
    const data = await userApi.getMe()
    userInfo.value = data
    return data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
  }

  function isLoggedIn() {
    return !!token.value
  }

  return { token, userInfo, login, register, getUserInfo, logout, isLoggedIn }
})
