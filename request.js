import axios from 'axios'
import { mockProducts } from '../mock/products.js'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

function notifyError(message) {
  if (typeof window !== 'undefined' && typeof window.alert === 'function') {
    window.alert(message)
    return
  }
  console.error(message)
}

function getToken() {
  if (typeof localStorage === 'undefined') return ''
  return localStorage.getItem('token') || ''
}

function sortProducts(list, sortBy) {
  const sorted = [...list]
  switch (sortBy) {
    case 'price_asc':
      return sorted.sort((a, b) => (a.promotionPrice || a.price) - (b.promotionPrice || b.price))
    case 'price_desc':
      return sorted.sort((a, b) => (b.promotionPrice || b.price) - (a.promotionPrice || a.price))
    case 'sales':
      return sorted.sort((a, b) => (b.sales || 0) - (a.sales || 0))
    default:
      return sorted.sort((a, b) => a.id - b.id)
  }
}

function buildMockProducts(params = {}) {
  const page = Number(params.page || 1)
  const size = Number(params.size || 8)
  const category = (params.category || '').trim()
  const sortBy = params.sortBy || 'default'

  const filtered = mockProducts.filter(item => !category || item.category === category)
  const sorted = sortProducts(filtered, sortBy)
  const total = sorted.length
  const pages = Math.max(1, Math.ceil(total / size))
  const current = Math.min(Math.max(page, 1), pages)
  const startIndex = (current - 1) * size
  const records = sorted.slice(startIndex, startIndex + size)

  return {
    code: 200,
    message: '操作成功',
    data: records,
    page: {
      total,
      pages,
      current,
      size
    }
  }
}

function buildMockProductDetail(url) {
  const productId = Number(String(url).split('/').pop())
  const product = mockProducts.find(item => item.id === productId)

  if (!product) {
    return {
      code: 404,
      message: '商品不存在',
      data: null
    }
  }

  return {
    code: 200,
    message: '操作成功',
    data: product
  }
}

function createMockAdapter(config) {
  return async () => {
    await new Promise(resolve => setTimeout(resolve, 350))

    let payload = {
      code: 404,
      message: '未找到对应的 Mock 接口',
      data: null
    }

    if (config.method === 'get' && config.url === '/products') {
      payload = buildMockProducts(config.params)
    } else if (config.method === 'get' && /^\/products\/\d+$/.test(config.url)) {
      payload = buildMockProductDetail(config.url)
    }

    return {
      data: payload,
      status: 200,
      statusText: 'OK',
      headers: {},
      config,
      request: { mock: true }
    }
  }
}

// 请求拦截器
request.interceptors.request.use(
  config => {
    if (config.mock) {
      config.adapter = createMockAdapter(config)
      return config
    }

    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      notifyError(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      if (typeof localStorage !== 'undefined') {
        localStorage.removeItem('token')
      }
      if (typeof window !== 'undefined') {
        window.location.href = '/login'
      }
    }
    notifyError(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
