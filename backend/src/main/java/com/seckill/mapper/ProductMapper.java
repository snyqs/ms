package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    @Update("UPDATE product SET seckill_stock = seckill_stock - 1 WHERE id = #{productId} AND seckill_stock > 0")
    int reduceStock(Long productId);
}