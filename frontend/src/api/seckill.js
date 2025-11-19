import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

// API接口
export const seckillApi = {
  // 获取商品列表
  getProducts() {
    return api.get('/seckill/products')
  },
  
  // 获取商品详情
  getProduct(id) {
    return api.get(`/seckill/product/${id}`)
  },
  
  // 检查秒杀状态
  checkStatus(productId, userId) {
    return api.get(`/seckill/status/${productId}/${userId}`)
  },
  
  // 执行秒杀
  executeSeckill(productId, userId) {
    return api.post(`/seckill/execute/${productId}/${userId}`)
  }
}

export default api