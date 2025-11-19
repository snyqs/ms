package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.entity.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
    
    @Select("SELECT * FROM seckill_order WHERE user_id = #{userId} AND product_id = #{productId}")
    SeckillOrder findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}