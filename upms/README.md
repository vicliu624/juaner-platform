# 用户权限管理微服务

## DDL

```sql
/*
 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : utf-8

 Date: 09/21/2019 11:37:35 AM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `tbl_permission_info`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_permission_info`;
CREATE TABLE `tbl_permission_info` (
  `id` bigint(18) NOT NULL COMMENT '权限编号',
  `perm_name` varchar(32) NOT NULL COMMENT '权限名称',
  `perm_type` int(9) NOT NULL COMMENT '权限类型 0-action',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '记录修改时间',
  `method` varchar(20) NOT NULL COMMENT 'http method',
  `description` varchar(500) DEFAULT NULL COMMENT '权限描述',
  `perm_url` varchar(64) NOT NULL COMMENT '权限url',
  `create_by` bigint(18) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(18) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_role_info`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_role_info`;
CREATE TABLE `tbl_role_info` (
  `id` bigint(18) NOT NULL COMMENT '角色编号',
  `role_name` varchar(16) NOT NULL COMMENT '角色名',
  `role_desc` varchar(255) NOT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_by` bigint(18) NOT NULL DEFAULT '0' COMMENT '创建者',
  `update_by` bigint(18) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_role_perm_map`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_role_perm_map`;
CREATE TABLE `tbl_role_perm_map` (
  `role_id` bigint(18) NOT NULL COMMENT '角色编号',
  `perm_id` bigint(18) NOT NULL COMMENT '权限编号',
  `create_by` bigint(18) NOT NULL DEFAULT '0' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(18) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`,`perm_id`) USING BTREE,
  KEY `index_query_by_role` (`role_id`) USING BTREE,
  KEY `perm_id` (`perm_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_user_info`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_user_info`;
CREATE TABLE `tbl_user_info` (
  `id` bigint(18) NOT NULL COMMENT '用户编号',
  `user_name` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(256) NOT NULL COMMENT '密码',
  `nick_name` varchar(10) DEFAULT NULL COMMENT '昵称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `account_non_expired` tinyint(1) NOT NULL COMMENT '帐号是否未过期 1-是 0-否',
  `credentials_non_expired` tinyint(1) NOT NULL COMMENT '登录凭据是否未过期 1-是 0-否',
  `account_non_locked` tinyint(1) NOT NULL COMMENT '帐号是否未锁定 1-是 0-否',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `create_by` bigint(18) DEFAULT '0' COMMENT '创建人',
  `update_by` bigint(18) DEFAULT '0' COMMENT '修改人',
  `enabled` tinyint(1) NOT NULL COMMENT '用户是否启用 1-启用 0-不启用',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_name` (`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_user_role_map`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_user_role_map`;
CREATE TABLE `tbl_user_role_map` (
  `user_id` bigint(18) NOT NULL COMMENT '用户编号',
  `role_id` bigint(18) NOT NULL COMMENT '角色编号',
  `create_by` bigint(18) NOT NULL DEFAULT '0' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(18) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE,
  KEY `index_query` (`user_id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;

```