package com.seckill.service.impl;

import com.seckill.entity.Product;
import com.seckill.entity.SeckillOrder;
import com.seckill.mapper.ProductMapper;
import com.seckill.mapper.SeckillOrderMapper;
import com.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Transactional
    public SeckillOrder executeSeckill(Long productId, Long userId) {
        // 1. 检查是否已经秒杀过
        SeckillOrder existOrder = seckillOrderMapper.findByUserIdAndProductId(userId, productId);
        if (existOrder != null) {
            throw new RuntimeException("您已经参与过本次秒杀");
        }
        
        // 2. 检查商品是否存在且秒杀时间内
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(product.getStartTime()) || now.isAfter(product.getEndTime())) {
            throw new RuntimeException("秒杀活动未开始或已结束");
        }
        
        // 3. 减库存（使用Redis分布式锁保证原子性）
        String lockKey = "seckill:lock:" + productId;
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", java.time.Duration.ofSeconds(10));
        
        try {
            if (lockAcquired != null && lockAcquired) {
                // 检查库存
                if (product.getSeckillStock() <= 0) {
                    throw new RuntimeException("秒杀商品已售罄");
                }
                
                // 减库存
                int result = productMapper.reduceStock(productId);
                if (result == 0) {
                    throw new RuntimeException("秒杀失败，库存不足");
                }
                
                // 4. 创建订单
                SeckillOrder order = new SeckillOrder();
                order.setUserId(userId);
                order.setProductId(productId);
                order.setOrderNo(generateOrderNo());
                order.setPrice(product.getSeckillPrice());
                order.setQuantity(1);
                order.setTotalAmount(product.getSeckillPrice());
                order.setStatus(1); // 1-成功
                order.setSeckillTime(now);
                order.setCreateTime(now);
                order.setUpdateTime(now);
                
                seckillOrderMapper.insert(order);
                
                log.info("用户{}秒杀商品{}成功，订单号：{}", userId, productId, order.getOrderNo());
                return order;
            } else {
                throw new RuntimeException("系统繁忙，请稍后重试");
            }
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }
    
    @Override
    public String checkSeckillStatus(Long productId, Long userId) {
        // 检查是否已经秒杀过
        SeckillOrder existOrder = seckillOrderMapper.findByUserIdAndProductId(userId, productId);
        if (existOrder != null) {
            return "您已成功秒杀该商品";
        }
        
        // 检查商品状态
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return "商品不存在";
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(product.getStartTime())) {
            return "秒杀活动尚未开始";
        }
        
        if (now.isAfter(product.getEndTime())) {
            return "秒杀活动已结束";
        }
        
        if (product.getSeckillStock() <= 0) {
            return "商品已售罄";
        }
        
        return "可以参与秒杀";
    }
    
    @Override
    public Product getProductDetail(Long productId) {
        return productMapper.selectById(productId);
    }
    
    private String generateOrderNo() {
        return "SK" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}