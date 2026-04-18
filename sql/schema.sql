-- =========================================================
-- 基于 Spring Cloud 的校园二手交易微服务系统
-- schema.sql
-- 说明：
-- 1. 本脚本用于创建 V1 版本核心业务表
-- 2. 采用 utf8mb4，支持完整中文与表情字符
-- 3. 当前版本使用“逻辑关联 + 索引”，暂不启用物理外键
-- 4. 执行前请确认已选择目标数据库
-- =========================================================

-- 可选：如果你还没建库，可以先手动建库后再执行本脚本
-- CREATE DATABASE IF NOT EXISTS campus_trade
--   DEFAULT CHARACTER SET utf8mb4
--   COLLATE utf8mb4_unicode_ci;
-- USE campus_trade;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- 按依赖顺序先删表（方便反复重建）
-- =========================================================
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS item_images;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- =========================================================
-- 1. 用户表 users
-- =========================================================
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名，唯一',
    password_hash VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    nickname VARCHAR(50) NOT NULL COMMENT '用户昵称',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    avatar_url VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色：USER/ADMIN',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态：ACTIVE/DISABLED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_role (role),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =========================================================
-- 2. 分类表 categories
-- =========================================================
CREATE TABLE categories (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类 ID，V1 默认 0',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT '分类状态：ENABLED/DISABLED',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_status (status),
    KEY idx_sort_no (sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- =========================================================
-- 3. 商品表 items
-- =========================================================
CREATE TABLE items (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    seller_id BIGINT NOT NULL COMMENT '发布者用户 ID，对应 users.id',
    title VARCHAR(100) NOT NULL COMMENT '商品标题',
    description TEXT NOT NULL COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    category_id BIGINT NOT NULL COMMENT '分类 ID，对应 categories.id',
    condition_level INT DEFAULT NULL COMMENT '成色等级，1~5，可选',
    trade_location VARCHAR(100) DEFAULT NULL COMMENT '交易地点，可选',
    cover_image VARCHAR(255) DEFAULT NULL COMMENT '商品封面图地址',
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE' COMMENT '商品状态：ON_SALE/SOLD/OFFLINE',
    audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
    audit_reason VARCHAR(255) DEFAULT NULL COMMENT '审核驳回原因，可为空',
    publish_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
    PRIMARY KEY (id),
    KEY idx_seller_id (seller_id),
    KEY idx_category_id (category_id),
    KEY idx_status (status),
    KEY idx_audit_status (audit_status),
    KEY idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- =========================================================
-- 4. 商品图片表 item_images
-- =========================================================
CREATE TABLE item_images (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    item_id BIGINT NOT NULL COMMENT '商品 ID，对应 items.id',
    image_url VARCHAR(255) NOT NULL COMMENT '图片地址',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- =========================================================
-- 5. 审核记录表 audit_logs
-- =========================================================
CREATE TABLE audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    item_id BIGINT NOT NULL COMMENT '商品 ID，对应 items.id',
    admin_id BIGINT NOT NULL COMMENT '管理员用户 ID，对应 users.id',
    action VARCHAR(20) NOT NULL COMMENT '审核动作：APPROVE/REJECT',
    reason VARCHAR(255) DEFAULT NULL COMMENT '审核说明或驳回原因',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_item_id (item_id),
    KEY idx_admin_id (admin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品审核记录表';

SET FOREIGN_KEY_CHECKS = 1;