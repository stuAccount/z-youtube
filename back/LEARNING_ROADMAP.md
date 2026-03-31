# Spring Boot Learning Roadmap

这份路线图面向当前这个 `YouTube-ish` 后端项目，目标不是一次性把功能堆满，而是按 sprint 逐步学习 Spring Boot 的核心开发过程。

建议原则：

- 先学主流程，再加复杂度
- 先跑通接口，再接数据库
- 先做业务，再做认证授权
- 先做 `account / video / comment`，最后再做 `payment`

## 推荐初始化配置

项目创建建议：

- Name: `back`
- Location: `z-youtube/`
- Group: `com.zyoutube`
- Artifact: `back`
- Package name: `com.zyoutube`
- Packaging: `Jar`

Java 建议：

- 想更稳：`Java 17`
- 想直接用新 LTS：`Java 21`

对学习目的来说，两者都可以。若你不想多考虑环境兼容，优先选 `17`。若这是你完全自己掌控的新项目，也可以直接选 `21`。

## 依赖添加顺序

### 第一阶段

- Spring Web
- Validation
- Lombok
- Spring Boot DevTools

### 第二阶段

- MyBatis Framework
- MySQL Driver

### 第三阶段

- Spring Security

不要一开始就把所有依赖都加上。学习时最重要的是知道每个依赖解决了什么问题。

## 推荐包结构

```text
src/main/java/com/zyoutube
  ZYoutubeApplication.java
  config/
  common/
  feature/
    account/
    video/
    comment/
    payment/
```

每个 feature 内部再按职责拆分：

```text
feature/account
  AccountController.java
  AccountService.java
  AccountMapper.java
  dto/
  entity/
  vo/
```

## Sprint 0: 项目初始化

目标：

- 创建 Spring Boot 项目
- 跑通本地启动
- 确认目录结构和包结构

建议完成项：

- 创建 `back/` 工程
- 保持主启动类在 `com.zyoutube`
- 新增一个最简单的健康检查接口，例如 `/ping`
- 使用 `application.yml`

完成标准：

- 项目可以启动
- 浏览器或 Postman 访问 `/ping` 返回成功

你会学到：

- Spring Boot 项目结构
- 启动流程
- 包扫描的基本规则

## Sprint 1: Web 基础分层

目标：

- 理解 Controller、Service 的基本协作方式

建议完成项：

- 建立 `feature/account`
- 写一个 `AccountController`
- 写一个 `AccountService`
- 先不用数据库，使用内存假数据

建议接口：

- `POST /accounts`
- `GET /accounts/{id}`
- `PUT /accounts/{id}`
- `DELETE /accounts/{id}`

完成标准：

- 能完成一个最简单的账号 CRUD 流程
- 代码已经分到 `controller/service/dto`

你会学到：

- 请求映射
- 路径参数、查询参数、请求体
- 分层设计

## Sprint 2: 参数校验与统一返回

目标：

- 让接口有基本的输入校验和错误处理能力

建议完成项：

- 使用 `@Valid`
- 在 DTO 上加：
  - `@NotBlank`
  - `@Email`
  - `@Size`
- 建立统一返回体
- 建立全局异常处理

建议增加目录：

```text
common/api
common/exception
```

完成标准：

- 参数不合法时，接口能返回清晰错误信息
- 成功和失败响应格式统一

你会学到：

- Bean Validation
- `@RestControllerAdvice`
- Spring Boot 的异常处理入口

## Sprint 3: 接入 MySQL 与 MyBatis

目标：

- 从假数据切换到真实数据库

建议完成项：

- 本地启动 MySQL
- 配置数据源
- 创建 `account` 表
- 为 `account` 编写 MyBatis Mapper
- 把账号模块改成真实 CRUD

建议优先学习：

- `application.yml` 数据源配置
- Mapper 接口
- XML SQL 映射
- 基本增删改查

完成标准：

- 账号接口已经真正写入和读取数据库

你会学到：

- Spring Boot 如何集成数据库
- MyBatis 的基本工作方式
- DTO、Entity、VO 的职责区别

## Sprint 4: 完成 Account 模块

目标：

- 把账号模块做成一个相对完整的 feature

建议完成项：

- 注册
- 查询个人信息
- 更新昵称、头像、简介
- 软删除或硬删除
- 检查邮箱或用户名唯一性

建议目录：

```text
feature/account
  dto/
  entity/
  vo/
```

完成标准：

- 账号模块具备基本业务意义
- 接口命名、错误处理、数据校验开始稳定

你会学到：

- 业务规则如何落到 Service 层
- 如何把简单 CRUD 往真实业务推进

## Sprint 5: Video 模块

目标：

- 开始做 YouTube-ish 的核心主业务

建议先做“视频元数据”，暂时不做文件上传。

建议完成项：

- 发布视频信息
- 查询视频详情
- 查询视频列表
- 删除视频

视频表可以先包含：

- `id`
- `title`
- `description`
- `author_id`
- `status`
- `created_at`

完成标准：

- 已经形成 `account -> video` 的业务关联

你会学到：

- 业务主实体建模
- 列表查询
- 条件筛选
- 基础分页

## Sprint 6: Comment 模块

目标：

- 学习一对多关系与分页查询

建议完成项：

- 对视频发表评论
- 查询视频评论列表
- 删除评论

建议重点：

- 按 `video_id` 查询评论
- 分页参数设计
- 评论归属校验

完成标准：

- 视频和评论之间的关系已经跑通

你会学到：

- 关联数据设计
- 分页接口设计
- 删除权限的业务判断

## Sprint 7: Security 登录与鉴权

目标：

- 在已有业务系统上增加认证授权

注意：

- 不建议在前几个 sprint 就引入 Security
- 先把用户、数据库、异常处理、业务接口做明白，再加它

建议完成项：

- 用户登录
- 密码加密
- 鉴权过滤
- 获取当前登录用户
- 受保护接口访问控制

建议路线：

- 先理解 Spring Security 基本概念
- 再决定用 Session 还是 JWT
- 学习如何保护 `/videos`、`/comments` 的写接口

完成标准：

- 未登录用户不能访问受保护接口
- 已登录用户可以访问自己的资源

你会学到：

- 认证与授权的区别
- 过滤器链
- 用户身份在 Spring Boot 中如何传递

## Sprint 8: Payment 模块

目标：

- 最后再学习支付场景的工程复杂度

为什么放最后：

- 支付会引入状态流转
- 需要处理幂等
- 可能涉及第三方回调
- 对基础能力要求更高

建议完成项：

- 创建订单
- 模拟支付成功
- 更新订单状态
- 处理重复请求

完成标准：

- 能理解支付流程不是简单 CRUD

你会学到：

- 状态机思维
- 幂等设计
- 回调处理的基本模式

## 每个 Sprint 的固定输出

建议每个 sprint 至少交付这些内容：

- 可以运行的代码
- 几个可用接口
- 对应的表结构或假数据
- README 中的接口说明
- 遇到的问题和总结

## 学习时的开发顺序

按这个顺序推进最稳：

1. 项目初始化
2. Web 分层
3. 参数校验
4. 统一返回与全局异常
5. 数据库与 MyBatis
6. Account 模块
7. Video 模块
8. Comment 模块
9. Security
10. Payment
11. Docker Compose 与部署
12. 测试与重构

## 当前项目建议的近期目标

如果你现在刚开始，我建议只做这 3 个 sprint：

1. Sprint 0
2. Sprint 1
3. Sprint 2

也就是：

- 先让项目跑起来
- 先写 `account` 的内存版 CRUD
- 先把参数校验和异常处理建立起来

这样你会先真正理解 Spring Boot 的主流程，再进数据库和安全。

## 一个简单的里程碑判断

当你做到下面这些时，就说明基础已经比较扎实了：

- 你可以自己定义一个 feature 包
- 你知道请求如何流到 Controller 和 Service
- 你会写 DTO 并做参数校验
- 你会做统一响应和全局异常处理
- 你会把假数据替换成 MyBatis + MySQL
- 你能解释为什么 Security 应该后加

## 最后建议

这条路线不是为了“最快把项目做完”，而是为了“最清楚地理解 Spring Boot 是怎么工作的”。

如果你发现某个 sprint 太大，可以继续拆小，比如：

- Sprint 3A: 接数据源
- Sprint 3B: 建表
- Sprint 3C: 接 Mapper
- Sprint 3D: 改造 account CRUD

这也是正常的。学习型项目最重要的是每一步都知道自己正在掌握什么。
