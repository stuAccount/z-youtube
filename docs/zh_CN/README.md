# z-youtube

[English](/README.md) | 简体中文 | [More](/docs/README.md)

一个用于练习 Spring Boot 的 YouTube-ish 内容平台后端项目。当前范围刻意聚焦在内容系统基础能力：认证、账号、视频、评论、参数校验、持久化和访问控制。

## 当前状态

主学习链路已经具备：

- `account`：注册、查看自己的资料、查看公开资料、修改资料、修改密码、注销
- `auth`：登录、登出、获取当前用户
- `video`：创建、编辑、发布、下线、删除、公开视频详情、公开视频列表
- `comment`：创建评论、分页查询、删除自己的评论
- 基础设施：`ApiResponse`、全局异常处理、参数校验、Session 登录态

这个仓库已经不只是一个骨架，而是一个适合继续深化内容平台后端能力的基础盘。

## 技术栈

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- MySQL
- H2 测试环境

## 运行方式

启动 MySQL：

```bash
docker compose up -d
```

启动后端：

```bash
cd back
./mvnw spring-boot:run
```

本地默认配置：

- 应用端口：`6969`
- MySQL：`localhost:3306`
- 数据库名：`zyoutube`
- 用户名：`root`
- 密码：`root`

健康检查：

```text
GET /ping
```

## 仓库结构

```text
.
├── back/
│   ├── pom.xml
│   └── src/main/java/com/zyoutube/
├── docker-compose.yml
└── README.md
```

## 适合继续练的方向

这个仓库最适合继续深化内容平台后端能力：

- 分层设计和领域边界
- JPA 实体建模和查询设计
- Spring Security 与资源归属校验
- 非交易型业务下的事务处理
- 面向读多写少平台的 Redis / MQ 场景

## 建议下一步

### 工程性收尾

- 为认证失败补专门异常处理，避免直接落成 `500` ✅ 
- 修正登录响应字段映射问题 ✅
- 补上视频删除和评论外键之间的处理逻辑 ✅
- 明确并实现作者查看草稿/私密视频的规则 ✅
- 明确并实现 `UNLISTED` 可见性规则 ✅
- 为 `auth`、`account`、`video`、`comment` 补核心集成测试 ✅

### 最小 MVP 功能

- 创作者后台最小闭环：我的视频列表、我的草稿详情、私密视频详情 ✅
- 点赞 / 点踩 ✅
- 简单的 `viewCount`
- 基于现有公开视频列表的最新 feed
- `videoUrl`、`coverUrl` 等播放基础字段

### 适合在本仓库里练的中间件场景

- Redis 排行榜
- 热点视频缓存
- feed 拉模式 / 推模式
- 未读计数
- 评论列表缓存
- 异步通知
- MQ 解耦

## 当前暂不关注

这个仓库当前聚焦内容平台主链路，支付、订单、库存、秒杀等强交易型主题暂不作为当前仓库的目标范围。
