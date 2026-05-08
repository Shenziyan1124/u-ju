const BASE_URL = 'http://localhost:8080'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: any
}

function getToken(): string {
  return uni.getStorageSync('token') || ''
}

function request<T = any>(options: RequestOptions): Promise<T> {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        Authorization: getToken() ? `Bearer ${getToken()}` : '',
        ...options.header,
      },
      success: (res: any) => {
        const data = res.data
        if (data.code === 200) {
          resolve(data.data)
        } else if (data.code === 401) {
          uni.removeStorageSync('token')
          uni.navigateTo({ url: '/pages/user/login' })
          reject(data)
        } else {
          uni.showToast({ title: data.message || '请求失败', icon: 'none' })
          reject(data)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      },
    })
  })
}

export const get = <T = any>(url: string, data?: any) => request<T>({ url, method: 'GET', data })
export const post = <T = any>(url: string, data?: any) => request<T>({ url, method: 'POST', data })
export const put = <T = any>(url: string, data?: any) => request<T>({ url, method: 'PUT', data })
export const del = <T = any>(url: string, data?: any) => request<T>({ url, method: 'DELETE', data })

export default request
