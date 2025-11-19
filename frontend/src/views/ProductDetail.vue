<template>
  <div class="product-detail" v-if="product">
    <el-button @click="$router.back()" type="primary" class="back-btn">
      <el-icon><ArrowLeft /></el-icon> 返回
    </el-button>
    
    <div class="detail-container">
      <div class="product-image-section">
        <el-image 
          :src="product.image" 
          fit="cover"
          class="product-image"
        >
          <template #error>
            <div class="image-placeholder">
              <el-icon><Picture /></el-icon>
              <span>暂无图片</span>
            </div>
          </template>
        </el-image>
      </div>
      
      <div class="product-info-section">
        <h1>{{ product.name }}</h1>
        <p class="product-description">{{ product.description || '暂无描述' }}</p>
        
        <div class="price-section">
          <div class="original-price">原价: ¥{{ product.price }}</div>
          <div class="seckill-price">秒杀价: ¥{{ product.seckillPrice }}</div>
        </div>
        
        <div class="stock-info">剩余库存: {{ product.seckillStock }} 件</div>
        
        <div class="time-info">
          <div>开始时间: {{ formatTime(product.startTime) }}</div>
          <div>结束时间: {{ formatTime(product.endTime) }}</div>
        </div>
        
        <div class="status-info">
          <el-tag :type="getStatusType(status)" size="large">
            {{ status }}
          </el-tag>
        </div>
        
        <el-button 
          type="danger" 
          size="large" 
          :disabled="!canSeckill"
          :loading="seckillLoading"
          @click="handleSeckill"
          class="seckill-btn"
        >
          {{ getSeckillButtonText() }}
        </el-button>
        
        <div v-if="seckillResult" class="result-message">
          <el-alert 
            :title="seckillResult.message" 
            :type="seckillResult.success ? 'success' : 'error'"
            show-icon
          />
          
          <div v-if="seckillResult.success && seckillResult.data" class="order-info">
            <h3>秒杀成功!</h3>
            <p>订单号: {{ seckillResult.data.orderNo }}</p>
            <p>秒杀时间: {{ formatTime(seckillResult.data.seckillTime) }}</p>
            <p>支付金额: ¥{{ seckillResult.data.totalAmount }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <div v-else class="loading-container">
    <el-skeleton animated :rows="10" />
  </div>
</template>

<script>
import { seckillApi } from '../api/seckill.js'
import { ElMessage } from 'element-plus'

export default {
  name: 'ProductDetail',
  data() {
    return {
      product: null,
      status: '',
      canSeckill: false,
      seckillLoading: false,
      seckillResult: null,
      // 模拟用户ID，实际项目中应该从登录信息获取
      userId: 10001
    }
  },
  
  computed: {
    productId() {
      return parseInt(this.$route.params.id)
    }
  },
  
  async mounted() {
    await this.loadProductDetail()
    await this.checkSeckillStatus()
  },
  
  methods: {
    async loadProductDetail() {
      try {
        const response = await seckillApi.getProduct(this.productId)
        if (response.success) {
          this.product = response.data
        } else {
          ElMessage.error('商品不存在')
          this.$router.push('/')
        }
      } catch (error) {
        console.error('加载商品详情失败:', error)
        ElMessage.error('网络错误，请稍后重试')
        this.$router.push('/')
      }
    },
    
    async checkSeckillStatus() {
      try {
        const response = await seckillApi.checkStatus(this.productId, this.userId)
        if (response.success) {
          this.status = response.status
          this.canSeckill = response.canSeckill
        }
      } catch (error) {
        console.error('检查秒杀状态失败:', error)
      }
    },
    
    async handleSeckill() {
      if (!this.canSeckill) return
      
      this.seckillLoading = true
      this.seckillResult = null
      
      try {
        const response = await seckillApi.executeSeckill(this.productId, this.userId)
        this.seckillResult = response
        
        if (response.success) {
          ElMessage.success('秒杀成功!')
          // 刷新状态
          await this.checkSeckillStatus()
          // 更新库存显示
          if (this.product && this.product.seckillStock > 0) {
            this.product.seckillStock -= 1
          }
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('秒杀失败:', error)
        ElMessage.error('网络错误，请稍后重试')
        this.seckillResult = {
          success: false,
          message: '网络错误，请稍后重试'
        }
      } finally {
        this.seckillLoading = false
      }
    },
    
    getStatusType(status) {
      if (status === '可以参与秒杀') return 'success'
      if (status === '秒杀活动尚未开始') return 'warning'
      if (status === '您已成功秒杀该商品') return 'info'
      return 'danger'
    },
    
    getSeckillButtonText() {
      if (this.seckillLoading) return '秒杀中...'
      if (this.canSeckill) return '立即秒杀'
      if (this.status === '秒杀活动尚未开始') return '等待开始'
      if (this.status === '您已成功秒杀该商品') return '已参与'
      if (this.status === '商品已售罄') return '已售罄'
      return '暂不可用'
    },
    
    formatTime(timeStr) {
      if (!timeStr) return '--'
      return new Date(timeStr).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.product-detail {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.back-btn {
  margin-bottom: 20px;
}

.detail-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: start;
}

.product-image-section {
  text-align: center;
}

.product-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.image-placeholder {
  width: 100%;
  height: 400px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  color: #909399;
  border-radius: 8px;
}

.image-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.product-info-section h1 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 28px;
}

.product-description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 30px;
}

.price-section {
  margin-bottom: 20px;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
  margin-bottom: 5px;
}

.seckill-price {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
}

.stock-info {
  font-size: 16px;
  color: #67c23a;
  margin-bottom: 15px;
}

.time-info {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.time-info div {
  margin-bottom: 5px;
  color: #606266;
}

.status-info {
  margin-bottom: 20px;
}

.seckill-btn {
  width: 200px;
  height: 50px;
  font-size: 18px;
  margin-bottom: 20px;
}

.result-message {
  margin-top: 20px;
}

.order-info {
  margin-top: 15px;
  padding: 15px;
  background: #f0f9ff;
  border-radius: 4px;
}

.order-info h3 {
  margin: 0 0 10px 0;
  color: #67c23a;
}

.order-info p {
  margin: 5px 0;
  color: #333;
}

.loading-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

@media (max-width: 768px) {
  .detail-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .product-image {
    height: 300px;
  }
}
</style>