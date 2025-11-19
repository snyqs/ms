package com.seckill.service;

import com.seckill.entity.Product;
import com.seckill.entity.SeckillOrder;

public interface SeckillService {
    
    /**
     * 执行秒杀
     */
    SeckillOrder executeSeckill(Long productId, Long userId);
    
    /**
     * 检查秒杀状态
     */
    String checkSeckillStatus(Long productId, Long userId);
    
    /**
     * 获取商品详情
     */
    Product getProductDetail(Long productId);
}