import request from '@/utils/request'

export const adminApi = {
  login: (phone: string, password: string) =>
    request.post('/admin/login', null, { params: { phone, password } }),
  getDashboard: () => request.get('/admin/dashboard'),
  getUsers: (params: any) => request.get('/admin/users', { params }),
  updateUserStatus: (id: number, status: number) =>
    request.put(`/admin/users/${id}/status`, null, { params: { status } }),
  getCertifications: (params: any) => request.get('/admin/certifications', { params }),
  reviewCertification: (id: number, status: number, remark?: string) =>
    request.put(`/admin/certifications/${id}/review`, null, { params: { status, remark } }),
  getEvents: (params: any) => request.get('/admin/events', { params }),
  reviewEvent: (id: number, status: number) =>
    request.put(`/admin/events/${id}/review`, null, { params: { status } }),
  getOrders: (params: any) => request.get('/admin/orders', { params }),
  approveRefund: (id: number) => request.put(`/admin/orders/${id}/refund`),
  getRegistrations: (params: any) => request.get('/admin/registrations', { params }),
  reviewRegistration: (id: number, status: number, remark?: string) =>
    request.put(`/admin/registrations/${id}/review`, null, { params: { status, remark } }),
}

export const userApi = {
  getCurrentUser: () => request.get('/users/me'),
}

export const notificationApi = {
  getList: (params: any) => request.get('/notifications', { params }),
  getUnreadCount: () => request.get('/notifications/unread-count'),
  markRead: (id: number) => request.put(`/notifications/${id}/read`),
  markAllRead: () => request.put('/notifications/read-all'),
}

export const eventApi = {
  getList: (params: any) => request.get('/events', { params }),
  getDetail: (id: number) => request.get(`/events/${id}`),
}

export const orderApi = {
  getList: (params: any) => request.get('/orders/my', { params }),
}

export const fileApi = {
  upload: (formData: FormData) => request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
}

export const contentApi = {
  getBanners: () => request.get('/admin/content/banners'),
  saveBanners: (data: any) => request.post('/admin/content/banners', data),
  getAnnouncements: () => request.get('/admin/content/announcements'),
  saveAnnouncements: (data: any) => request.post('/admin/content/announcements', data),
  getConfig: () => request.get('/admin/content/config'),
  saveConfig: (data: any) => request.put('/admin/content/config', data),
}
