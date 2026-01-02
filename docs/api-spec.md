## 前台接口汇总（User Side）`/api/v1`

| 模块 | 接口名       | Method | Path                         | 是否登录 | 主要用途/说明（含注意事项）                                                     |
| -- | --------- | -----: | ---------------------------- | ---: | ------------------------------------------------------------------ |
| 认证 | 用户注册      |   POST | `/auth/register`             |    否 | username/email 唯一；建议密码强度校验                                         |
| 认证 | 用户登录      |   POST | `/auth/login`                |    否 | account=用户名或邮箱；禁用用户返回 403（USER_DISABLED）                           |
| 用户 | 获取我的信息    |    GET | `/users/me`                  |    是 | 返回当前登录用户；禁用用户应拒绝                                                   |
| 用户 | 更新我的资料    |    PUT | `/users/me`                  |    是 | 更新头像/简介；一般不允许改 username/email                                      |
| 用户 | 用户公开信息    |    GET | `/users/{id}`                |    否 | 作者卡片/主页；不返回敏感字段                                                    |
| 景点 | 景点列表      |    GET | `/attractions`               |    否 | 支持分页/筛选/排序；默认按创建时间倒序；预留 algo/scene                                 |
| 景点 | 景点详情      |    GET | `/attractions/{id}`          |    否 | 仅展示可见状态（如 status=1）                                                |
| 景点 | 景点浏览+1    |   POST | `/attractions/{id}/view`     |    否 | 高频写建议 Redis 计数器 + 回写；可写埋点                                          |
| 景点 | 点赞景点（幂等）  |   POST | `/attractions/{id}/likes`    |    是 | 幂等：重复点赞不加计数；建议 status=1/0 逻辑删除                                     |
| 景点 | 取消点赞景点    | DELETE | `/attractions/{id}/likes`    |    是 | 建议 status=0，不物理删除                                                  |
| 景点 | 我是否点赞景点   |    GET | `/attractions/{id}/likes/me` |    是 | 详情页点赞态                                                             |
| 日记 | 日记列表流     |    GET | `/diaries`                   |    否 | 支持作者页 userId、精选 featured、搜索 q、排序 sort、预留 algo/scene                |
| 日记 | 发布日记      |   POST | `/diaries`                   |    是 | 富文本建议 XSS 清洗；可预留审核 status=2                                        |
| 日记 | 日记详情      |    GET | `/diaries/{id}`              |    否 | 默认仅展示可见状态                                                          |
| 日记 | 编辑日记（作者）  |    PUT | `/diaries/{id}`              |    是 | 必须校验作者；若启用审核可编辑后转待审                                                |
| 日记 | 删除日记（软删）  | DELETE | `/diaries/{id}`              |    是 | 建议 status=0 软删                                                     |
| 日记 | 日记浏览+1    |   POST | `/diaries/{id}/view`         |    否 | 同景点：Redis 计数 + 回写                                                  |
| 日记 | 点赞日记（幂等）  |   POST | `/diaries/{id}/likes`        |    是 | 幂等：重复点赞不加计数                                                        |
| 日记 | 取消点赞日记    | DELETE | `/diaries/{id}/likes`        |    是 | 建议 status=0                                                        |
| 日记 | 我是否点赞日记   |    GET | `/diaries/{id}/likes/me`     |    是 | 详情页点赞态                                                             |
| 评论 | 评论列表（按对象） |    GET | `/comments`                  |    否 | `commentType` + `targetId`；建议索引(comment_type,target_id,created_at) |
| 评论 | 发表评论      |   POST | `/comments`                  |    是 | 无外键：必须校验 target 存在且可见；评分 1~5                                       |
| 评论 | 删除评论      | DELETE | `/comments/{id}`             |    是 | 作者/管理员；建议软删 status=0                                               |
| 收藏 | 我的收藏列表    |    GET | `/favorites`                 |    是 | 可按 favType 过滤；返回最小卡片字段                                             |
| 收藏 | 收藏（幂等）    |   POST | `/favorites`                 |    是 | 幂等：重复收藏不重复写/不重复计数                                                  |
| 收藏 | 取消收藏      | DELETE | `/favorites`                 |    是 | 建议 status=0，不物理删除                                                  |
| 收藏 | 我是否收藏对象   |    GET | `/favorites/me`              |    是 | `favType` + `targetId`                                             |
| 埋点 | 行为事件上报    |   POST | `/events`                    |    否 | 允许未登录；meta 扩展字段用于推荐/统计                                             |
| 推荐 | 统一推荐流     |    GET | `/feed`                      |    否 | `type=attraction/diary/mix`；当前 time_desc，未来可接算法                    |

---

## 管理端接口汇总（Admin Side）`/api/v1/admin`

| 模块   | 接口名           | Method | Path                          | 权限建议                  | 主要用途/说明（含注意事项）                  |
| ---- | ------------- | -----: | ----------------------------- | --------------------- | ------------------------------- |
| 用户管理 | 用户列表          |    GET | `/users`                      | `user:read`           | q 模糊查(username/email)、status 筛选 |
| 用户管理 | 启用/禁用用户       |    PUT | `/users/{id}/status`          | `user:ban`            | 禁用后建议所有鉴权接口返回 USER_DISABLED     |
| RBAC | 角色列表          |    GET | `/roles`                      | `rbac:read`           | 查看角色                            |
| RBAC | 创建角色          |   POST | `/roles`                      | `rbac:write`          | name 唯一（ADMIN/MODERATOR）        |
| RBAC | 权限列表          |    GET | `/permissions`                | `rbac:read`           | 查看权限                            |
| RBAC | 创建权限          |   POST | `/permissions`                | `rbac:write`          | 建议 `resource:action` 命名         |
| RBAC | 设置角色权限（覆盖式）   |    PUT | `/roles/{roleId}/permissions` | `rbac:write`          | 覆盖式：先清空再插入                      |
| RBAC | 查询用户角色        |    GET | `/users/{userId}/roles`       | `rbac:read`           | 查看用户角色                          |
| RBAC | 设置用户角色（覆盖式）   |    PUT | `/users/{userId}/roles`       | `rbac:write`          | 覆盖式：先清空再插入                      |
| 景点运维 | 景点列表（含status） |    GET | `/attractions`                | `attraction:read`     | 后台可看全部状态（0/1/2/3）               |
| 景点运维 | 新增景点          |   POST | `/attractions`                | `attraction:write`    | gallery/tags 推荐数组输入，服务端序列化落库    |
| 景点运维 | 编辑景点          |    PUT | `/attractions/{id}`           | `attraction:write`    | 常规编辑                            |
| 景点运维 | 删除景点          | DELETE | `/attractions/{id}`           | `attraction:delete`   | 建议软删（status=0）                  |
| 景点运维 | 修改景点状态        |    PUT | `/attractions/{id}/status`    | `attraction:moderate` | 审核/屏蔽/恢复/软删（按状态约定）              |
| 日记运维 | 日记列表（含status） |    GET | `/diaries`                    | `diary:read`          | 可筛 status、featured              |
| 日记运维 | 修改日记状态        |    PUT | `/diaries/{id}/status`        | `diary:moderate`      | 审核/屏蔽/恢复/软删                     |
| 日记运维 | 设置精选          |    PUT | `/diaries/{id}/featured`      | `diary:feature`       | 首页推荐位                           |
| 日记运维 | 删除日记          | DELETE | `/diaries/{id}`               | `diary:delete`        | 建议软删                            |
| 评论运维 | 评论列表          |    GET | `/comments`                   | `comment:read`        | 可筛 commentType/targetId/status  |
| 评论运维 | 修改评论状态        |    PUT | `/comments/{id}/status`       | `comment:moderate`    | 屏蔽/恢复/软删                        |
| 评论运维 | 删除评论          | DELETE | `/comments/{id}`              | `comment:delete`      | 建议软删                            |
| 数据看板 | 看板汇总          |    GET | `/dashboard/summary`          | `dashboard:read`      | 建议 Redis 缓存 30~300s             |
| 数据看板 | 看板趋势          |    GET | `/dashboard/trends`           | `dashboard:read`      | days 建议 1~60；建议缓存               |

---

## 统一错误码/响应（建议后端对齐）

| HTTP | 场景          | 建议业务码 | 建议 message           |
| ---: | ----------- | ----: | -------------------- |
|  400 | 参数错误/校验失败   | 40001 | `PARAM_INVALID`      |
|  401 | 未登录/Token无效 | 40101 | `UNAUTHORIZED`       |
|  403 | 无权限         | 40301 | `FORBIDDEN`          |
|  403 | 用户被禁用       | 40302 | `USER_DISABLED`      |
|  404 | 资源不存在/被软删   | 40002 | `RESOURCE_NOT_FOUND` |
|  500 | 服务端异常       | 50000 | `INTERNAL_ERROR`     |
