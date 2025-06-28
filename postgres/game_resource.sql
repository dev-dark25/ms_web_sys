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

 Date: 28/06/2025 17:48:14
*/


-- ----------------------------
-- Table structure for game_resource
-- ----------------------------
DROP TABLE IF EXISTS "public"."game_resource";
CREATE TABLE "public"."game_resource" (
  "id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(10) COLLATE "pg_catalog"."default",
  "name" varchar(30) COLLATE "pg_catalog"."default",
  "type" varchar(2) COLLATE "pg_catalog"."default",
  "time_limit" varchar(2) COLLATE "pg_catalog"."default",
  "up" varchar(2) COLLATE "pg_catalog"."default",
  "star" varchar(2) COLLATE "pg_catalog"."default",
  "time_limit_type" varchar(2) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."game_resource"."type" IS '0-角色；1-武器';
COMMENT ON COLUMN "public"."game_resource"."time_limit" IS '0-常驻；1-限定';
COMMENT ON COLUMN "public"."game_resource"."up" IS '0-否；1-是';
COMMENT ON COLUMN "public"."game_resource"."time_limit_type" IS '0- 限定池1；1-限定池2';

-- ----------------------------
-- Primary Key structure for table game_resource
-- ----------------------------
ALTER TABLE "public"."game_resource" ADD CONSTRAINT "resource_pkey" PRIMARY KEY ("id");
