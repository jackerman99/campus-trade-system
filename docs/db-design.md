\# 数据库设计（db-design）



\## 1. 数据库设计目标



本项目数据库设计服务于 V1 版本核心业务闭环：用户注册/登录 -> 浏览商品 -> 搜索商品 -> 发布商品 -> 管理员审核 -> 审核通过后商品在首页可见。



数据库设计遵循以下原则：



1\. 只围绕 V1 已冻结功能建表，不引入本次不实现的扩展模块

2\. 表结构尽量简洁，满足课程大作业演示、联调和上线要求

3\. 字段命名与接口协议保持一致，避免前后端和后端内部字段不统一

4\. 商品图片采用独立表设计，便于一对多管理

5\. 商品状态与审核状态分离，避免业务逻辑混乱



\## 2. 表清单



本项目 V1 版本数据库包含以下 5 张核心业务表：



1\. `users`：用户表

2\. `items`：商品表

3\. `item\_images`：商品图片表

4\. `categories`：商品分类表

5\. `audit\_logs`：商品审核记录表



\## 3. 表关系说明



各表关系如下：



1\. 一个用户可以发布多个商品  

&#x20;  `users.id` -> `items.seller\_id`



2\. 一个商品可以对应多张商品图片  

&#x20;  `items.id` -> `item\_images.item\_id`



3\. 一个分类下可以有多个商品  

&#x20;  `categories.id` -> `items.category\_id`



4\. 一个商品可以对应多条审核记录  

&#x20;  `items.id` -> `audit\_logs.item\_id`



5\. 一个管理员可以审核多个商品  

&#x20;  `users.id` -> `audit\_logs.admin\_id`



\## 4. 各表字段设计



\### 4.1 用户表 `users`



用于存储系统用户信息，包括普通用户与管理员。



| 字段名 | 类型 | 说明 |

|---|---|---|

| id | bigint | 主键 ID |

| username | varchar(50) | 用户名，唯一 |

| password\_hash | varchar(255) | 加密后的密码 |

| nickname | varchar(50) | 用户昵称 |

| email | varchar(100) | 邮箱 |

| phone | varchar(20) | 手机号 |

| avatar\_url | varchar(255) | 头像地址 |

| role | varchar(20) | 用户角色，取值 USER / ADMIN |

| status | varchar(20) | 用户状态，取值 ACTIVE / DISABLED |

| created\_at | datetime | 创建时间 |

| updated\_at | datetime | 更新时间 |

| deleted | tinyint | 逻辑删除标记，0 未删除，1 已删除 |



\### 4.2 商品表 `items`



用于存储商品主体信息。



| 字段名 | 类型 | 说明 |

|---|---|---|

| id | bigint | 主键 ID |

| seller\_id | bigint | 发布者用户 ID，对应 users.id |

| title | varchar(100) | 商品标题 |

| description | text | 商品描述 |

| price | decimal(10,2) | 商品价格 |

| category\_id | bigint | 分类 ID，对应 categories.id |

| condition\_level | int | 成色等级，1\~5，可选 |

| trade\_location | varchar(100) | 交易地点，可选 |

| cover\_image | varchar(255) | 商品封面图地址 |

| status | varchar(20) | 商品状态，取值 ON\_SALE / SOLD / OFFLINE |

| audit\_status | varchar(20) | 审核状态，取值 PENDING / APPROVED / REJECTED |

| audit\_reason | varchar(255) | 审核驳回原因，可为空 |

| publish\_time | datetime | 发布时间 |

| created\_at | datetime | 创建时间 |

| updated\_at | datetime | 更新时间 |

| deleted | tinyint | 逻辑删除标记，0 未删除，1 已删除 |



\### 4.3 商品图片表 `item\_images`



用于存储商品的多张图片信息。



| 字段名 | 类型 | 说明 |

|---|---|---|

| id | bigint | 主键 ID |

| item\_id | bigint | 商品 ID，对应 items.id |

| image\_url | varchar(255) | 图片地址 |

| sort\_no | int | 排序号 |

| created\_at | datetime | 创建时间 |



\### 4.4 分类表 `categories`



用于存储商品分类信息。



| 字段名 | 类型 | 说明 |

|---|---|---|

| id | bigint | 主键 ID |

| name | varchar(50) | 分类名称 |

| parent\_id | bigint | 父分类 ID，V1 可默认 0 |

| sort\_no | int | 排序号 |

| status | varchar(20) | 分类状态，取值 ENABLED / DISABLED |



\### 4.5 审核记录表 `audit\_logs`



用于记录管理员对商品的审核行为。



| 字段名 | 类型 | 说明 |

|---|---|---|

| id | bigint | 主键 ID |

| item\_id | bigint | 商品 ID，对应 items.id |

| admin\_id | bigint | 管理员用户 ID，对应 users.id |

| action | varchar(20) | 审核动作，取值 APPROVE / REJECT |

| reason | varchar(255) | 审核说明或驳回原因 |

| created\_at | datetime | 创建时间 |



\## 5. 状态字段设计



\### 5.1 用户角色

\- USER：普通用户

\- ADMIN：管理员



\### 5.2 用户状态

\- ACTIVE：正常

\- DISABLED：禁用



\### 5.3 商品状态

\- ON\_SALE：已上架

\- SOLD：已卖出

\- OFFLINE：已下架



\### 5.4 审核状态

\- PENDING：待审核

\- APPROVED：审核通过

\- REJECTED：审核驳回



\### 5.5 商品发布后的默认状态

用户新发布商品后，默认状态为：



\- `status = OFFLINE`

\- `audit\_status = PENDING`



管理员审核通过后，更新为：



\- `status = ON\_SALE`

\- `audit\_status = APPROVED`



管理员审核驳回后，更新为：



\- `status = OFFLINE`

\- `audit\_status = REJECTED`



\## 6. 索引设计



为满足商品查询、登录校验和后台审核等场景，V1 版本建议建立以下索引：



\### 6.1 users 表

\- `uk\_username`：用户名唯一索引

\- `idx\_role`：角色索引

\- `idx\_status`：状态索引



\### 6.2 items 表

\- `idx\_seller\_id`：发布者索引

\- `idx\_category\_id`：分类索引

\- `idx\_status`：商品状态索引

\- `idx\_audit\_status`：审核状态索引

\- `idx\_publish\_time`：发布时间索引



\### 6.3 item\_images 表

\- `idx\_item\_id`：商品 ID 索引



\### 6.4 audit\_logs 表

\- `idx\_item\_id`：商品 ID 索引

\- `idx\_admin\_id`：管理员 ID 索引



\## 7. 初始化数据设计



为便于前后端联调、功能测试和演示，初始化数据建议包括：



\### 7.1 用户数据

\- 1 个管理员账号

\- 2 个普通用户账号



\### 7.2 分类数据

建议初始化以下分类：

\- 数码产品

\- 图书教材

\- 生活用品

\- 服饰鞋包

\- 体育器材



\### 7.3 商品数据

建议初始化：

\- 6 条已审核通过并上架的商品

\- 2 条待审核商品

\- 1 条已驳回商品



\### 7.4 图片数据

每个商品至少初始化 1\~2 条图片记录，保证列表、详情和搜索页都能正常展示图片



\## 8. 设计约束说明



1\. 本次数据库设计仅服务于 V1 已冻结功能，不新增 AI、留言、报表、信誉分等扩展表

2\. 表名、字段名需与接口协议保持一致，避免后续联调和代码实现出现命名不统一

3\. 商品图片必须使用独立表设计，不采用字符串拼接方式存储多图

4\. 商品状态与审核状态必须分离，不允许合并为单一字段

5\. 后续 `schema.sql` 与 `data.sql` 必须严格按照本设计文档实现

