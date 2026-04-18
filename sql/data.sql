-- =========================================================
-- 基于 Spring Cloud 的校园二手交易微服务系统
-- data.sql
-- 说明：
-- 1. 本脚本用于初始化 V1 版本测试数据
-- 2. 当前密码字段 password_hash 先使用占位字符串
-- 3. 后续接入真实登录逻辑时，再替换为 BCrypt 密文
-- 4. 执行前请确认 schema.sql 已经执行成功
-- =========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- 1. 初始化用户数据
-- =========================================================
INSERT INTO users (id, username, password_hash, nickname, email, phone, avatar_url, role, status, created_at, updated_at, deleted)
VALUES
(1, 'admin', 'admin123_hash', '系统管理员', 'admin@campus.com', '13800000001', '', 'ADMIN', 'ACTIVE', NOW(), NOW(), 0),
(2, 'cai_daniel', 'user123_hash', 'CaiDaniel', 'cai@campus.com', '13800000002', '', 'USER', 'ACTIVE', NOW(), NOW(), 0),
(3, 'yu_nikita', 'user123_hash', 'YuNikita', 'yu@campus.com', '13800000003', '', 'USER', 'ACTIVE', NOW(), NOW(), 0);

-- =========================================================
-- 2. 初始化分类数据
-- =========================================================
INSERT INTO categories (id, name, parent_id, sort_no, status)
VALUES
(1, '数码产品', 0, 1, 'ENABLED'),
(2, '图书教材', 0, 2, 'ENABLED'),
(3, '生活用品', 0, 3, 'ENABLED'),
(4, '服饰鞋包', 0, 4, 'ENABLED'),
(5, '体育器材', 0, 5, 'ENABLED');

-- =========================================================
-- 3. 初始化商品数据
-- 状态说明：
-- 已上架：status = ON_SALE, audit_status = APPROVED
-- 待审核：status = OFFLINE, audit_status = PENDING
-- 已驳回：status = OFFLINE, audit_status = REJECTED
-- =========================================================
INSERT INTO items (
    id, seller_id, title, description, price, category_id,
    condition_level, trade_location, cover_image,
    status, audit_status, audit_reason,
    publish_time, created_at, updated_at, deleted
)
VALUES
-- 已审核通过并上架（6 条）
(101, 2, 'iPad 9 64G', '九成新，正常使用，无拆修。', 1800.00, 1, 4, '一校区宿舍楼下', 'http://localhost:8080/images/item101_1.jpg', 'ON_SALE', 'APPROVED', NULL, NOW(), NOW(), NOW(), 0),
(102, 2, '机械键盘', '茶轴机械键盘，自用半年。', 180.00, 1, 4, '教学楼门口', 'http://localhost:8080/images/item102_1.jpg', 'ON_SALE', 'APPROVED', NULL, NOW(), NOW(), NOW(), 0),
(103, 3, '高等数学教材', '教材有少量笔记，不影响使用。', 25.00, 2, 3, '图书馆附近', 'http://localhost:8080/images/item103_1.jpg', 'ON_SALE', 'APPROVED', NULL, NOW(), NOW(), NOW(), 0),
(104, 3, '保温杯', '不锈钢保温杯，保温效果正常。', 35.00, 3, 4, '食堂门口', 'http://localhost:8080/images/item104_1.jpg', 'ON_SALE', 'APPROVED', NULL, NOW(), NOW(), NOW(), 0),
(105, 2, '运动鞋', '尺码 42，穿过几次。', 120.00, 4, 3, '操场门口', 'http://localhost:8080/images/item105_1.jpg', 'ON_SALE', 'APPROVED', NULL, NOW(), NOW(), NOW(), 0),
(106, 3, '羽毛球拍', '双拍出售，适合日常娱乐。', 90.00, 5, 4, '体育馆门口', 'http://localhost:8080/images/item106_1.jpg', 'ON_SALE', 'APPROVED', NULL, NOW(), NOW(), NOW(), 0),

-- 待审核（2 条）
(107, 2, '蓝牙耳机', '功能正常，续航还可以。', 150.00, 1, 3, '一校区快递站', 'http://localhost:8080/images/item107_1.jpg', 'OFFLINE', 'PENDING', NULL, NOW(), NOW(), NOW(), 0),
(108, 3, '线性代数教材', '八成新，适合复习使用。', 18.00, 2, 3, '主楼附近', 'http://localhost:8080/images/item108_1.jpg', 'OFFLINE', 'PENDING', NULL, NOW(), NOW(), NOW(), 0),

-- 已驳回（1 条）
(109, 2, '未知商品', '描述过于简单。', 50.00, 3, 2, '未知地点', 'http://localhost:8080/images/item109_1.jpg', 'OFFLINE', 'REJECTED', '商品信息不完整', NOW(), NOW(), NOW(), 0);

-- =========================================================
-- 4. 初始化商品图片数据
-- 每个商品至少 1~2 张图
-- =========================================================
INSERT INTO item_images (id, item_id, image_url, sort_no, created_at)
VALUES
(1001, 101, 'http://localhost:8080/images/item101_1.jpg', 1, NOW()),
(1002, 101, 'http://localhost:8080/images/item101_2.jpg', 2, NOW()),

(1003, 102, 'http://localhost:8080/images/item102_1.jpg', 1, NOW()),
(1004, 102, 'http://localhost:8080/images/item102_2.jpg', 2, NOW()),

(1005, 103, 'http://localhost:8080/images/item103_1.jpg', 1, NOW()),
(1006, 103, 'http://localhost:8080/images/item103_2.jpg', 2, NOW()),

(1007, 104, 'http://localhost:8080/images/item104_1.jpg', 1, NOW()),
(1008, 104, 'http://localhost:8080/images/item104_2.jpg', 2, NOW()),

(1009, 105, 'http://localhost:8080/images/item105_1.jpg', 1, NOW()),
(1010, 105, 'http://localhost:8080/images/item105_2.jpg', 2, NOW()),

(1011, 106, 'http://localhost:8080/images/item106_1.jpg', 1, NOW()),
(1012, 106, 'http://localhost:8080/images/item106_2.jpg', 2, NOW()),

(1013, 107, 'http://localhost:8080/images/item107_1.jpg', 1, NOW()),
(1014, 107, 'http://localhost:8080/images/item107_2.jpg', 2, NOW()),

(1015, 108, 'http://localhost:8080/images/item108_1.jpg', 1, NOW()),
(1016, 108, 'http://localhost:8080/images/item108_2.jpg', 2, NOW()),

(1017, 109, 'http://localhost:8080/images/item109_1.jpg', 1, NOW());

-- =========================================================
-- 5. 初始化审核记录数据
-- =========================================================
INSERT INTO audit_logs (id, item_id, admin_id, action, reason, created_at)
VALUES
(2001, 101, 1, 'APPROVE', '商品信息完整，审核通过', NOW()),
(2002, 102, 1, 'APPROVE', '商品信息完整，审核通过', NOW()),
(2003, 103, 1, 'APPROVE', '商品信息完整，审核通过', NOW()),
(2004, 104, 1, 'APPROVE', '商品信息完整，审核通过', NOW()),
(2005, 105, 1, 'APPROVE', '商品信息完整，审核通过', NOW()),
(2006, 106, 1, 'APPROVE', '商品信息完整，审核通过', NOW()),
(2007, 109, 1, 'REJECT', '商品信息不完整', NOW());

SET FOREIGN_KEY_CHECKS = 1;