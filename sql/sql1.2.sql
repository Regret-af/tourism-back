-- =============================================================
-- Project: High-End Travel Platform
-- Version: 1.2 (MVP+ 可持续演进版)
-- Stack: SpringBoot + MySQL + Redis
-- Notes:
-- 1) 不使用外键约束（业务层保证一致性）
-- 2) 增加：点赞明细、收藏、状态字段、行为日志、关键索引
-- =============================================================

SET NAMES utf8mb4;
SET time_zone = '+00:00';

-- =========================
-- 1. 用户表
-- =========================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                       username VARCHAR(255) UNIQUE NOT NULL COMMENT '用户名',
                       email VARCHAR(255) UNIQUE NOT NULL COMMENT '用户邮箱',
                       password VARCHAR(255) NOT NULL COMMENT '用户密码(加密存储)',
                       avatar_url VARCHAR(255) COMMENT '用户头像URL',
                       bio TEXT COMMENT '用户个人简介',
                       status TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                       KEY idx_status_created (status, created_at) COMMENT '按状态与时间查询'
) COMMENT='用户表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 2. 景点表（新增 status + 索引）
-- =========================
DROP TABLE IF EXISTS attractions;
CREATE TABLE attractions (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '景点ID',
                             name VARCHAR(255) NOT NULL COMMENT '景点名称',
                             description TEXT COMMENT '景点描述',
                             location VARCHAR(255) COMMENT '景点位置',
                             image_url VARCHAR(255) COMMENT '主图URL',
                             gallery JSON COMMENT '图库: 存储多张图片URL数组',
                             rating DECIMAL(3,1) DEFAULT 5.0 COMMENT '综合评分: 0-5.0',
                             tags VARCHAR(255) COMMENT '标签: 如 "徒步,摄影,古镇"(逗号分隔)',
                             view_count INT DEFAULT 0 COMMENT '浏览热度(可Redis计数回写)',
                             price_level TINYINT DEFAULT 1 COMMENT '消费等级: 1-低,2-中,3-高',
                             status TINYINT DEFAULT 1 COMMENT '状态: 1-正常,2-待审核,3-屏蔽,0-删除',
                             longitude decimal(11, 8) NULL DEFAULT NULL COMMENT '景点经度坐标',
                             latitude decimal(10, 8) NULL DEFAULT NULL COMMENT '景点纬度坐标',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             KEY idx_status_created (status, created_at) COMMENT '按状态拉取最新',
                             KEY idx_price_time (price_level, created_at) COMMENT '按价位+时间筛选',
                             KEY idx_view (view_count) COMMENT '按热度排序',
                             KEY idx_rating (rating) COMMENT '按评分排序',
                             KEY idx_location (location) COMMENT '按地点筛选(简单索引)',
                             CONSTRAINT `chk_latitude` CHECK (`latitude` between -(90) and 90),
                             CONSTRAINT `chk_longitude` CHECK (`longitude` between -(180) and 180)
) COMMENT='景点表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 3. 旅行日记表（新增 status + 索引）
-- =========================
DROP TABLE IF EXISTS travel_diaries;
CREATE TABLE travel_diaries (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '旅行日记ID',
                                user_id BIGINT NOT NULL COMMENT '作者ID(users.id)',
                                title VARCHAR(255) NOT NULL COMMENT '日记标题',
                                summary VARCHAR(500) COMMENT '摘要: 首页卡片预览',
                                cover_image VARCHAR(255) COMMENT '日记封面图URL',
                                content LONGTEXT NOT NULL COMMENT '正文内容(富文本)',
                                is_featured TINYINT DEFAULT 0 COMMENT '是否推荐到首页: 1-是,0-否',
                                status TINYINT DEFAULT 1 COMMENT '状态: 1-正常,2-待审核,3-屏蔽,0-删除',
                                like_count INT DEFAULT 0 COMMENT '点赞数(建议Redis计数回写)',
                                view_count INT DEFAULT 0 COMMENT '查看次数(建议Redis计数回写)',
                                collect_count INT NULL DEFAULT 0 COMMENT '收藏数(建议Redis计数回写)',
                                comment_count INT NULL DEFAULT 0 COMMENT '评论数(建议Redis计数回写)',
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                KEY idx_user_time (user_id, created_at) COMMENT '作者主页日记流',
                                KEY idx_featured_time (is_featured, created_at) COMMENT '精选流',
                                KEY idx_status_time (status, created_at) COMMENT '按状态拉取最新',
                                KEY idx_like (like_count) COMMENT '按点赞排序',
                                KEY idx_view (view_count) COMMENT '按浏览排序'
) COMMENT='旅行日记表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 4. 评论表（新增 status + 关键联合索引）
-- =========================
DROP TABLE IF EXISTS comments;
CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
                          user_id BIGINT NOT NULL COMMENT '评论者ID(users.id)',
                          content TEXT NOT NULL COMMENT '评论内容',
                          star_rating TINYINT DEFAULT 5 COMMENT '单项评分: 1-5',
                          comment_type ENUM('attraction','diary') NOT NULL COMMENT '评论对象类型',
                          target_id BIGINT NOT NULL COMMENT '对应对象ID(attractions.id/diaries.id)',
                          status TINYINT DEFAULT 1 COMMENT '状态: 1-正常,3-屏蔽,0-删除',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          KEY idx_user_time (user_id, created_at) COMMENT '我的评论列表',
                          KEY idx_type_target_time (comment_type, target_id, created_at) COMMENT '对象评论流(核心)',
                          KEY idx_status_time (status, created_at) COMMENT '后台按状态筛选'
) COMMENT='评论表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 5. RBAC：角色/权限/关联表
-- =========================
DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
                       name VARCHAR(255) UNIQUE NOT NULL COMMENT '角色名称(如 ADMIN/MODERATOR)'
) COMMENT='角色表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS permissions;
CREATE TABLE permissions (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
                             name VARCHAR(255) UNIQUE NOT NULL COMMENT '权限标识(如 diary:delete, comment:moderate)'
) COMMENT='权限表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS user_roles;
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL COMMENT '用户ID',
                            role_id BIGINT NOT NULL COMMENT '角色ID',
                            PRIMARY KEY (user_id, role_id),
                            KEY idx_role (role_id) COMMENT '按角色找用户'
) COMMENT='用户角色关联表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS role_permissions;
CREATE TABLE role_permissions (
                                  role_id BIGINT NOT NULL COMMENT '角色ID',
                                  permission_id BIGINT NOT NULL COMMENT '权限ID',
                                  PRIMARY KEY (role_id, permission_id),
                                  KEY idx_perm (permission_id) COMMENT '按权限找角色'
) COMMENT='角色权限关联表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 6. v1.2 新增：日记点赞明细（支持去重/取消）
-- =========================
DROP TABLE IF EXISTS diary_likes;
CREATE TABLE diary_likes (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                             diary_id BIGINT NOT NULL COMMENT '日记ID(travel_diaries.id)',
                             user_id BIGINT NOT NULL COMMENT '用户ID(users.id)',
                             status TINYINT DEFAULT 1 COMMENT '状态: 1-有效点赞,0-取消点赞',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             UNIQUE KEY uk_diary_user (diary_id, user_id) COMMENT '同一用户对同一日记唯一',
                             KEY idx_user (user_id) COMMENT '按用户查点赞列表',
                             KEY idx_diary_status (diary_id, status) COMMENT '按日记统计点赞'
) COMMENT='旅行日记点赞明细表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 7. v1.2 新增：景点点赞明细（可用于“有用/喜欢”）
-- =========================
DROP TABLE IF EXISTS attraction_likes;
CREATE TABLE attraction_likes (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                  attraction_id BIGINT NOT NULL COMMENT '景点ID(attractions.id)',
                                  user_id BIGINT NOT NULL COMMENT '用户ID(users.id)',
                                  status TINYINT DEFAULT 1 COMMENT '状态: 1-有效点赞,0-取消点赞',
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  UNIQUE KEY uk_attr_user (attraction_id, user_id) COMMENT '同一用户对同一景点唯一',
                                  KEY idx_user (user_id) COMMENT '按用户查点赞列表',
                                  KEY idx_attr_status (attraction_id, status) COMMENT '按景点统计点赞'
) COMMENT='景点点赞明细表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 8. v1.2 新增：收藏/心愿单（景点/日记通用）
-- =========================
DROP TABLE IF EXISTS favorites;
CREATE TABLE favorites (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                           user_id BIGINT NOT NULL COMMENT '用户ID(users.id)',
                           fav_type ENUM('attraction','diary') NOT NULL COMMENT '收藏对象类型',
                           target_id BIGINT NOT NULL COMMENT '收藏对象ID',
                           status TINYINT DEFAULT 1 COMMENT '状态: 1-收藏,0-取消',
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           UNIQUE KEY uk_user_type_target (user_id, fav_type, target_id) COMMENT '同一对象唯一收藏',
                           KEY idx_target (fav_type, target_id) COMMENT '按对象统计收藏/查收藏用户',
                           KEY idx_user_time (user_id, created_at) COMMENT '我的收藏列表'
) COMMENT='收藏/心愿单表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 9. v1.2 新增：用户行为事件（为推荐/增长埋点）
-- =========================
DROP TABLE IF EXISTS user_events;
CREATE TABLE user_events (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                             user_id BIGINT COMMENT '用户ID(未登录可为空)',
                             event_type VARCHAR(50) NOT NULL COMMENT '事件类型:view/click/like/fav/comment/share',
                             target_type VARCHAR(50) NOT NULL COMMENT '目标类型:attraction/diary',
                             target_id BIGINT NOT NULL COMMENT '目标ID',
                             meta JSON COMMENT '扩展信息:来源、关键词、曝光位、停留时长等',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             KEY idx_user_time (user_id, created_at) COMMENT '按用户行为时间查询',
                             KEY idx_target_time (target_type, target_id, created_at) COMMENT '按目标聚合行为',
                             KEY idx_event_time (event_type, created_at) COMMENT '按事件类型统计'
) COMMENT='用户行为事件表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
