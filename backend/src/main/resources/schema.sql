-- 创建数据库
CREATE DATABASE IF NOT EXISTS seckill_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE seckill_db;

-- 秒杀商品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '原价',
    seckill_price DECIMAL(10,2) NOT NULL COMMENT '秒杀价',
    stock INT NOT NULL DEFAULT 0 COMMENT '总库存',
    seckill_stock INT NOT NULL DEFAULT 0 COMMENT '秒杀库存',
    image VARCHAR(500) COMMENT '商品图片',
    status INT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常 0-下架',
    start_time DATETIME NOT NULL COMMENT '秒杀开始时间',
    end_time DATETIME NOT NULL COMMENT '秒杀结束时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_time (start_time, end_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀商品表';

-- 秒杀订单表
CREATE TABLE IF NOT EXISTS seckill_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    price DECIMAL(10,2) NOT NULL COMMENT '秒杀价格',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status INT NOT NULL DEFAULT 1 COMMENT '状态: 1-成功 0-失败',
    seckill_time DATETIME NOT NULL COMMENT '秒杀时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_product (user_id, product_id),
    INDEX idx_order_no (order_no),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀订单表';

-- 插入测试数据
INSERT INTO product (name, description, price, seckill_price, stock, seckill_stock, image, status, start_time, end_time) VALUES
('iPhone 15 Pro', '苹果最新旗舰手机', 7999.00, 6999.00, 1000, 100, '/images/iphone15.jpg', 1, '2024-01-01 10:00:00', '2024-12-31 23:59:59'),
('MacBook Pro M3', '苹果最新笔记本电脑', 12999.00, 9999.00, 500, 50, '/images/macbook.jpg', 1, '2024-01-01 14:00:00', '2024-12-31 23:59:59'),
('AirPods Pro 2', '无线降噪耳机', 1899.00, 1299.00, 2000, 200, '/images/airpods.jpg', 1, '2024-01-01 16:00:00', '2024-12-31 23:59:59'),
('iPad Pro 12.9', '专业级平板电脑', 8999.00, 6999.00, 300, 30, '/images/ipad.jpg', 1, '2024-01-01 18:00:00', '2024-12-31 23:59:59');