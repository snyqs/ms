package com.seckill.controller;

import com.seckill.entity.Product;
import com.seckill.entity.SeckillOrder;
import com.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/seckill")
public class SeckillController {
    
    @Autowired
    private SeckillService seckillService;
    
    /**
     * 获取商品详情
     */
    @GetMapping("/product/{id}")
    public Map<String, Object> getProduct(@PathVariable("id") Long productId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Product product = seckillService.getProductDetail(productId);
            if (product != null) {
                result.put("success", true);
                result.put("data", product);
            } else {
                result.put("success", false);
                result.put("message", "商品不存在");
            }
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            result.put("success", false);
            result.put("message", "获取商品详情失败");
        }
        return result;
    }
    
    /**
     * 检查秒杀状态
     */
    @GetMapping("/status/{productId}/{userId}")
    public Map<String, Object> checkStatus(@PathVariable Long productId, @PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            String status = seckillService.checkSeckillStatus(productId, userId);
            result.put("success", true);
            result.put("status", status);
            result.put("canSeckill", "可以参与秒杀".equals(status));
        } catch (Exception e) {
            log.error("检查秒杀状态失败", e);
            result.put("success", false);
            result.put("message", "检查秒杀状态失败");
        }
        return result;
    }
    
    /**
     * 执行秒杀
     */
    @PostMapping("/execute/{productId}/{userId}")
    public Map<String, Object> executeSeckill(@PathVariable Long productId, @PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            SeckillOrder order = seckillService.executeSeckill(productId, userId);
            result.put("success", true);
            result.put("data", order);
            result.put("message", "秒杀成功");
        } catch (Exception e) {
            log.error("秒杀失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取所有秒杀商品
     */
    @GetMapping("/products")
    public Map<String, Object> getProducts() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里暂时返回模拟数据，实际应该从数据库查询
            Map<String, Object> product1 = new HashMap<>();
            product1.put("id", 1);
            product1.put("name", "iPhone 15 Pro");
            product1.put("price", 7999.00);
            product1.put("seckillPrice", 6999.00);
            product1.put("seckillStock", 100);
            product1.put("image", "/images/iphone15.jpg");
            product1.put("startTime", "2024-01-01 10:00:00");
            product1.put("endTime", "2024-01-01 12:00:00");
            
            Map<String, Object> product2 = new HashMap<>();
            product2.put("id", 2);
            product2.put("name", "MacBook Pro");
            product2.put("price", 12999.00);
            product2.put("seckillPrice", 9999.00);
            product2.put("seckillStock", 50);
            product2.put("image", "/images/macbook.jpg");
            product2.put("startTime", "2024-01-01 14:00:00");
            product2.put("endTime", "2024-01-01 16:00:00");
            
            result.put("success", true);
            result.put("data", new Object[]{product1, product2});
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            result.put("success", false);
            result.put("message", "获取商品列表失败");
        }
        return result;
    }
}