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

- Spring Data JPA
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
  AccountRepository.java
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

## Sprint 3: 接入 MySQL 与 JPA

目标：

- 从假数据切换到真实数据库

建议完成项：

- 本地启动 MySQL
- 配置数据源
- 创建 `account` 表
- 为 `account` 编写 JPA Entity 和 Repository
- 把账号模块改成真实 CRUD

建议优先学习：

- `application.yml` 数据源配置
- `@Entity`
- `JpaRepository`
- 简单查询方法命名
- 基本增删改查

完成标准：

- 账号接口已经真正写入和读取数据库

你会学到：

- Spring Boot 如何集成数据库
- JPA 的基本工作方式
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

建议你手打的关键部分：

- `Video` 实体里的 `author` 关联，重点看 `@ManyToOne`、`@JoinColumn(name = "author_id")`
- `VideoStatus` 这样的业务枚举，以及 `@Enumerated(EnumType.STRING)`
- `GET /videos` 的查询参数设计，例如 `authorId`、`status`、`page`、`size`
- `VideoRepository` 里的分页查询方法，例如 `findAllByAuthor_Id(...)`
- `VideoService` 里根据不同筛选条件决定走哪一个 Repository 方法
- `Page<Video>` 如何映射成 `Page<VideoSummaryResponse>`

这一 sprint 尽量不要重复 Sprint 4 的重点：

- 不要把主要精力又放回“注册/改资料/改密码”这种 account 里的业务规则
- 参数校验、统一返回、全局异常可以沿用已有写法，不必再把时间主要花在这里

可以串联起 `account` 和 `video` 的部分：

- 发布视频时先用 `AccountRepository.findById(authorId)` 确认作者存在
- 在 `Video` 中保存 `Account author`，让数据库落成 `author_id`
- 在视频详情和列表响应里复用账号摘要信息，例如作者 `id / username / nickname / avatarUrl`
- 删除视频时先校验“当前操作人是不是作者本人”，现在可以先用 `requesterAccountId` 模拟，Sprint 7 再替换成登录用户
- 你会开始感觉到：`account` 不再只是独立 CRUD，而是其他业务的身份来源

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

当前这一轮可以先这样落地 `comment` scaffold：

```text
feature/comment
  CommentController.java
  CommentService.java
  CommentRepository.java
  model/
    dto/
      CreateCommentRequest.java
    entity/
      Comment.java
    vo/
      CommentDetailResponse.java
      CommentSummaryResponse.java
```

建议先把骨架搭好，再把关键业务逻辑手打进去。

可以先固定 3 个接口：

- `POST /comments`
- `GET /comments?videoId=1&page=0&size=10`
- `DELETE /comments/{id}?requesterAccountId=1`

建议你手打的关键部分：

- `Comment` 实体里的 `video` 和 `author` 关联，重点看两个 `@ManyToOne`
- `CommentRepository` 里的分页查询方法，例如 `findAllByVideo_Id(...)`
- `CommentService.createComment(...)` 里如何同时关联 `Account` 和 `Video`
- `CommentService.getComments(...)` 里如何做 `Page<Comment>` 到 `Page<CommentSummaryResponse>` 的映射
- `CommentService.deleteComment(...)` 里如何校验“当前操作人是不是评论作者本人”

建议你保留为 TODO、自己完成的地方：

- `createComment()`
- `getComments()`
- `deleteComment()`

可以沿用前两个 sprint 的写法，不必把时间花在重复部分：

- 参数校验继续沿用 DTO + `@Valid`
- 成功/失败返回继续沿用统一 `ApiResponse`
- 删除先做硬删除，Sprint 7 再接入登录用户

你在这一 sprint 里最应该形成的感觉是：

- `comment` 不只是一个独立表，而是同时挂在 `video` 和 `account` 下面
- 查询评论列表时，重点不再是“查一条”，而是“按 `video_id` + 分页”
- 删除评论时，重点是“归属判断”，不是单纯 `deleteById`

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

如果你是接着当前这个项目往下做，我建议优先选 `Session` 方案，而不是一开始就上 `JWT`：

- 当前重点是先把“登录成功后如何拿到当前用户”这条链路跑通
- `Session` 更适合先理解认证、授权、过滤器链、`SecurityContext`
- `JWT` 会额外引入 token 签发、解析、过期、登出等概念，学习成本更高

这个项目里 Sprint 7 最关键的改动，不是“加上 Security 依赖”，而是“把用户身份真正接进现有业务”：

- `CreateVideoRequest` 里的 `authorId` 要去掉，改成从当前登录用户获取作者
- `CreateCommentRequest` 里的 `authorId` 要去掉，改成从当前登录用户获取作者
- 删除视频、删除评论时，不再让前端传 `requesterAccountId`
- 账户自己的资料、改密码、注销等接口，不应该继续依赖路径里的任意 `id`

建议你按这个顺序完成：

1. 先补登录能力
2. 再补当前登录用户获取
3. 再配置哪些接口公开、哪些接口需要登录
4. 最后回头改 `video`、`comment`、`account` 里的业务接口签名

可以先拆成这些小目标：

- 增加登录接口，例如 `POST /auth/login`
- 从 `accounts` 表加载用户信息
- 校验密码哈希
- 登录成功后，把身份放进 Spring Security 上下文
- 在 controller 或 service 中获取当前登录用户 id
- 保护 `/videos`、`/comments` 的写接口
- 保护“修改自己的资料 / 密码 / 注销自己账号”这类接口

建议你重点改造的接口有：

- `POST /videos`
- `DELETE /videos/{id}`
- `POST /comments`
- `DELETE /comments/{id}`
- 当前用户自己的资料接口
- 当前用户自己的密码修改接口
- 当前用户自己的注销接口

写到这里时，你最应该形成的感觉是：

- Sprint 7 的核心不是“多一个配置类”，而是“业务代码以后不能再信任前端传来的用户 id”
- 真正的作者、评论者、删除者，都应该来自当前登录态
- 鉴权不只是“有没有登录”，还包括“是不是资源本人”

建议你顺手一起检查这些边界：

- 未登录用户访问写接口时，应该直接被拦住
- 登录用户只能修改自己的资源，不能修改别人的资源
- 作者本人是否允许查看自己的草稿视频，要提前定好规则
- 评论列表是否允许看到未发布视频下的评论，也要和视频可见性规则保持一致

如果你想把 Sprint 7 做成一个完整的小闭环，至少补这些测试：

- 未登录访问受保护接口时失败
- 登录后创建视频成功，且不需要传 `authorId`
- 登录后创建评论成功，且不需要传 `authorId`
- 登录用户删除自己的视频 / 评论成功
- 登录用户删除别人的视频 / 评论失败
- 修改资料、改密码、注销时，只能操作自己

这个 sprint 里还有一个很典型的检查点：

- 如果代码里还需要手动传 `authorId`、`requesterAccountId`，通常说明 Sprint 7 还没真正完成

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
5. 数据库与 JPA
6. Account 模块
7. Video 模块
8. Comment 模块
9. Security
10. Payment
11. Docker Compose 与部署
12. 测试与重构

