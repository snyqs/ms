<template>
  <div class="home">
    <h2>秒杀商品列表</h2>
    
    <div class="product-list">
      <el-card 
        v-for="product in products" 
        :key="product.id" 
        class="product-card"
        shadow="hover"
      >
        <div class="product-image">
          <el-image 
            :src="product.image" 
            fit="cover"
            style="width: 100%; height: 200px;"
          >
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
        
        <div class="product-info">
          <h3>{{ product.name }}</h3>
          <p class="product-desc">{{ product.description || '暂无描述' }}</p>
          
          <div class="price-section">
            <span class="original-price">原价: ¥{{ product.price }}</span>
            <span class="seckill-price">秒杀价: ¥{{ product.seckillPrice }}</span>
          </div>
          
          <div class="stock-info">
            库存: {{ product.seckillStock }} 件
          </div>
          
          <div class="time-info">
            <div>开始时间: {{ formatTime(product.startTime) }}</div>
            <div>结束时间: {{ formatTime(product.endTime) }}</div>
          </div>
          
          <el-button 
            type="primary" 
            :disabled="!isSeckillActive(product)"
            @click="goToDetail(product.id)"
            class="seckill-btn"
          >
            {{ getSeckillButtonText(product) }}
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { seckillApi } from '../api/seckill.js'
import { ElMessage } from 'element-plus'

export default {
  name: 'Home',
  data() {
    return {
      products: [],
      loading: false
    }
  },
  
  async mounted() {
    await this.loadProducts()
  },
  
  methods: {
    async loadProducts() {
      this.loading = true
      try {
        const response = await seckillApi.getProducts()
        if (response.success) {
          this.products = response.data
        } else {
          ElMessage.error('加载商品列表失败')
        }
      } catch (error) {
        console.error('加载商品列表失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    
    isSeckillActive(product) {
      const now = new Date()
      const startTime = new Date(product.startTime)
      const endTime = new Date(product.endTime)
      return now >= startTime && now <= endTime && product.seckillStock > 0
    },
    
    getSeckillButtonText(product) {
      const now = new Date()
      const startTime = new Date(product.startTime)
      const endTime = new Date(product.endTime)
      
      if (now < startTime) {
        return '即将开始'
      } else if (now > endTime) {
        return '已结束'
      } else if (product.seckillStock <= 0) {
        return '已售罄'
      } else {
        return '立即秒杀'
      }
    },
    
    goToDetail(productId) {
      this.$router.push(`/product/${productId}`)
    },
    
    formatTime(timeStr) {
      if (!timeStr) return '--'
      return new Date(timeStr).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

.home h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.product-card {
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-info {
  padding: 15px 0;
}

.product-info h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}

.product-desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
}

.price-section {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.original-price {
  text-decoration: line-through;
  color: #999;
}

.seckill-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
}

.stock-info {
  color: #67c23a;
  margin-bottom: 10px;
}

.time-info {
  font-size: 12px;
  color: #909399;
  margin-bottom: 15px;
}

.seckill-btn {
  width: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 200px;
  background: #f5f7fa;
  color: #909399;
}
</style>