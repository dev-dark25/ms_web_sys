/*
 Navicat Premium Data Transfer

 Source Server         : ubuntu_docker_pg
 Source Server Type    : PostgreSQL
 Source Server Version : 150003 (150003)
 Source Host           : 192.168.172.2:5432
 Source Catalog        : postgres
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 150003 (150003)
 File Encoding         : 65001

 Date: 28/06/2025 17:48:23
*/


-- ----------------------------
-- Table structure for resource_record
-- ----------------------------
DROP TABLE IF EXISTS "public"."resource_record";
CREATE TABLE "public"."resource_record" (
  "account_id" int4,
  "game_resource_code" varchar(32) COLLATE "pg_catalog"."default",
  "create_time" date
)
;
