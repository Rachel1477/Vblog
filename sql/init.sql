-- 创建 vblog 数据库
CREATE DATABASE IF NOT EXISTS vblog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE vblog;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(64) NOT NULL COMMENT '密码（SHA-256加密）',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试用户（密码：123456，SHA-256加密后的值）
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `status`) 
VALUES ('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'admin@example.com', '管理员', 1);

-- 笔记表
CREATE TABLE IF NOT EXISTS `note` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '笔记ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '笔记标题',
    `content` TEXT NOT NULL COMMENT '笔记内容',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-草稿，1-发布，2-私密',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='笔记表';

-- 图片资源表
CREATE TABLE IF NOT EXISTS `image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `user_id` BIGINT NOT NULL COMMENT '上传用户ID',
    `url` VARCHAR(255) NOT NULL COMMENT '访问URL（/uploads/xxx）',
    `path` VARCHAR(500) NOT NULL COMMENT '服务器存储路径',
    `size` BIGINT DEFAULT 0 COMMENT '文件大小（字节）',
    `content_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME 类型',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    PRIMARY KEY (`id`),
    KEY `idx_image_user_id` (`user_id`),
    KEY `idx_image_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片资源表';

-- 插入测试笔记
INSERT INTO `note` (`user_id`, `title`, `content`, `status`) VALUES
(1, '我的第一篇笔记', '这是一篇测试笔记，欢迎使用Vblog笔记系统！', 1),
(1, 'Spring Boot学习笔记', 'Spring Boot是一个优秀的Java框架，可以快速搭建应用...', 1),
(1, '私密笔记', '这是一篇私密笔记，只有作者可以看到。', 2);
