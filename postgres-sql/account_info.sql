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

 Date: 28/06/2025 17:48:04
*/


-- ----------------------------
-- Table structure for account_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."account_info";
CREATE TABLE "public"."account_info" (
  "id" int4 NOT NULL,
  "account_name" varchar(30) COLLATE "pg_catalog"."default",
  "up_current_count_5" int2,
  "up_character_5" int2,
  "up_current_count_4" int2,
  "up_character_4" int2,
  "up_count" int2,
  "permanent_count" int2,
  "permanent_current_count_5" int2,
  "permanent_current_count_4" int2
)
;
COMMENT ON COLUMN "public"."account_info"."up_character_5" IS '0-默认值；1-是；2-否';
COMMENT ON COLUMN "public"."account_info"."up_character_4" IS '0-默认值；1-是；2-否';

-- ----------------------------
-- Primary Key structure for table account_info
-- ----------------------------
ALTER TABLE "public"."account_info" ADD CONSTRAINT "account_info_pkey" PRIMARY KEY ("id");
