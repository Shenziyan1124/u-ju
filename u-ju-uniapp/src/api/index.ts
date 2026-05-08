import { get, post, put } from '@/utils/request'

export const userApi = {
  register: (data: any) => post('/users/register', data),
  login: (data: any) => post('/users/login', data),
  getMe: () => get('/users/me'),
  updateProfile: (data: any) => put('/users/me', data),
  updatePassword: (oldPassword: string, newPassword: string) =>
    put(`/users/password?oldPassword=${oldPassword}&newPassword=${newPassword}`),
  updateAvatar: (avatarUrl: string) =>
    post(`/users/avatar?avatarUrl=${encodeURIComponent(avatarUrl)}`),
}

export const eventApi = {
  getList: (params: any) => get('/events', params),
  getDetail: (id: number) => get(`/events/${id}`),
  getMyEvents: (params: any) => get('/events/my', params),
  create: (data: any) => post('/events', data),
  update: (id: number, data: any) => put(`/events/${id}`, data),
  updateStatus: (id: number, status: number) =>
    put(`/events/${id}/status?status=${status}`),
  getTickets: (eventId: number) => get(`/events/${eventId}/tickets`),
  createTicket: (eventId: number, data: any) =>
    post(`/events/${eventId}/tickets`, data),
}

export const registrationApi = {
  create: (data: any) => post('/registrations', data),
  getMyList: (params: any) => get('/registrations/my', params),
  getDetail: (id: number) => get(`/registrations/${id}`),
  cancel: (id: number) => put(`/registrations/${id}/cancel`),
  getEventRegistrations: (eventId: number, params: any) =>
    get(`/registrations/event/${eventId}`, params),
  review: (id: number, status: number) =>
    put(`/registrations/${id}/review?status=${status}`),
}

export const orderApi = {
  create: (registrationId: number, ticketId?: number) =>
    post(`/orders?registrationId=${registrationId}${ticketId ? '&ticketId=' + ticketId : ''}`),
  getMyList: (params: any) => get('/orders/my', params),
  getDetail: (id: number) => get(`/orders/${id}`),
  pay: (id: number, payMethod: number) => post(`/orders/${id}/pay?payMethod=${payMethod}`),
  cancel: (id: number) => post(`/orders/${id}/cancel`),
  refund: (id: number) => post(`/orders/${id}/refund`),
}

export const certificationApi = {
  submit: (data: any) => post('/certifications', data),
  getMine: () => get('/certifications/me'),
}

export const notificationApi = {
  getList: (params: any) => get('/notifications', params),
  getUnreadCount: () => get('/notifications/unread-count'),
  markRead: (id: number) => put(`/notifications/${id}/read`),
  markAllRead: () => put('/notifications/read-all'),
}

export const favoriteApi = {
  toggle: (eventId: number) => post(`/events/${eventId}/favorite`),
  getList: (params: any) => get('/events/favorites', params),
}

export const fileApi = {
  upload: (filePath: string) => {
    return new Promise((resolve, reject) => {
      const token = uni.getStorageSync('token')
      uni.uploadFile({
        url: `${uni.getStorageSync('baseUrl') || 'http://localhost:8080'}/api/files/upload`,
        filePath,
        name: 'file',
        header: token ? { Authorization: `Bearer ${token}` } : {},
        success: (res) => {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data)
          } else {
            reject(data)
          }
        },
        fail: (err) => reject(err),
      })
    })
  },
}
