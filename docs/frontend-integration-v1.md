\# Campus Trade System 前后端联调说明 V1



\## 1. 联调地址



\### 统一访问入口

\- Gateway 地址：`http://localhost:8080`



\### 各服务直连地址

\- user-service：`http://localhost:8081`

\- item-service：`http://localhost:8082`

\- admin-service：`http://localhost:8083`



\### 注册中心

\- Nacos：`http://localhost:8848/nacos`



\---



\## 2. 当前可联调接口总览



\### 用户模块

\- `POST /api/auth/register` 注册

\- `POST /api/auth/login` 登录

\- `GET /api/users/me` 当前用户信息



\### 商品模块

\- `GET /api/items` 商品列表

\- `GET /api/items/{id}` 商品详情

\- `GET /api/search/items` 商品搜索

\- `POST /api/items` 发布商品

\- `GET /api/items/mine` 我的商品



\### 管理员审核模块

\- `GET /api/admin/items/pending` 待审核商品列表

\- `POST /api/admin/items/{id}/approve` 审核通过

\- `POST /api/admin/items/{id}/reject` 审核驳回



\### 文件模块

\- `POST /api/files/upload` 图片上传



\---



\## 3. 统一响应格式



所有接口统一返回：



```json

{

&#x20; "code": 0,

&#x20; "message": "OK",

&#x20; "data": {},

&#x20; "requestId": "xxx"

}

```



\### 说明

\- `code = 0`：成功

\- `code != 0`：失败

\- 常见错误码：

&#x20; - `400` 请求参数错误 / 业务校验失败

&#x20; - `401` 未登录 / token 无效

&#x20; - `403` 无权限

&#x20; - `404` 资源不存在

&#x20; - `500` 服务器内部错误



\---



\## 4. Token 规则



\### Header 格式

```text

Authorization: Bearer <token>

```



\### 当前权限规则



\#### 公开接口

这些接口目前不要求登录：

\- `POST /api/auth/register`

\- `POST /api/auth/login`

\- `GET /api/items`

\- `GET /api/items/{id}`

\- `GET /api/search/items`

\- `POST /api/files/upload`



\#### 需要登录

\- `GET /api/users/me`

\- `POST /api/items`

\- `GET /api/items/mine`



\#### 需要管理员权限

\- `GET /api/admin/items/pending`

\- `POST /api/admin/items/{id}/approve`

\- `POST /api/admin/items/{id}/reject`



\### 当前实现说明

\- 当前\*\*没有做 Gateway 统一鉴权过滤\*\*

\- 鉴权逻辑目前在各服务接口内完成

\- 管理员接口已校验 token 且要求 `role = ADMIN`



\---



\## 5. 测试账号



\### 普通用户账号

如果数据库未重置，当前可用测试账号建议：

\- 用户名：`test\_user\_01`

\- 密码：`123456`



\### 管理员测试账号

如果数据库未重置，当前管理员测试账号建议：

\- 用户名：`admin\_test\_01`

\- 密码：`123456`



\### 说明

\- 管理员测试账号是通过数据库将 `role` 改为 `ADMIN` 的

\- 如果后续你们重建数据库，需要重新插入或重新改角色



\---



\## 6. 用户模块接口



\### 6.1 注册



\#### 请求

\- 方法：`POST`

\- 地址：`/api/auth/register`



\#### 请求体

```json

{

&#x20; "username": "test\_user\_01",

&#x20; "password": "123456",

&#x20; "nickname": "测试用户01",

&#x20; "email": "test01@campus.com",

&#x20; "phone": "13812345678"

}

```



\#### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "注册成功",

&#x20; "data": {

&#x20;   "id": 4,

&#x20;   "username": "test\_user\_01",

&#x20;   "nickname": "测试用户01",

&#x20;   "email": "test01@campus.com",

&#x20;   "phone": "13812345678",

&#x20;   "role": "USER",

&#x20;   "status": "ACTIVE"

&#x20; },

&#x20; "requestId": "xxx"

}

```



\#### 前端注意

\- 注册成功后不会自动登录

\- 需要前端继续调用登录接口



\---



\### 6.2 登录



\#### 请求

\- 方法：`POST`

\- 地址：`/api/auth/login`



\#### 请求体

```json

{

&#x20; "username": "test\_user\_01",

&#x20; "password": "123456"

}

```



\#### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "登录成功",

&#x20; "data": {

&#x20;   "token": "xxxxx",

&#x20;   "tokenType": "Bearer",

&#x20;   "expiresIn": 7200,

&#x20;   "userInfo": {

&#x20;     "id": 4,

&#x20;     "username": "test\_user\_01",

&#x20;     "nickname": "测试用户01",

&#x20;     "avatarUrl": null,

&#x20;     "role": "USER",

&#x20;     "status": "ACTIVE"

&#x20;   }

&#x20; },

&#x20; "requestId": "xxx"

}

```



\#### 前端注意

\- token 字段名：`data.token`

\- token 类型字段：`data.tokenType`

\- 用户信息字段：`data.userInfo`

\- 后续请求头需要带：



```text

Authorization: Bearer <data.token>

```



\---



\### 6.3 当前用户信息



\#### 请求

\- 方法：`GET`

\- 地址：`/api/users/me`

\- 需要登录：是



\#### Header

```text

Authorization: Bearer <token>

```



\#### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "OK",

&#x20; "data": {

&#x20;   "id": 4,

&#x20;   "username": "test\_user\_01",

&#x20;   "nickname": "测试用户01",

&#x20;   "email": "test01@campus.com",

&#x20;   "phone": "13812345678",

&#x20;   "avatarUrl": null,

&#x20;   "role": "USER",

&#x20;   "status": "ACTIVE"

&#x20; },

&#x20; "requestId": "xxx"

}

```



\---



\## 7. 商品模块接口



\### 7.1 商品列表



\#### 请求

\- 方法：`GET`

\- 地址：`/api/items`



\#### 支持参数

\- `pageNum`

\- `page`

\- `pageSize`

\- `categoryId`



\#### 示例

```text

/api/items?pageNum=1\&pageSize=10

/api/items?pageNum=1\&pageSize=10\&categoryId=1

```



\#### 返回规则

当前前台列表只返回：

\- `deleted = 0`

\- `auditStatus = APPROVED`

\- `status = ON\_SALE`



\#### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "OK",

&#x20; "data": {

&#x20;   "total": 6,

&#x20;   "pageNum": 1,

&#x20;   "pageSize": 10,

&#x20;   "totalPages": 1,

&#x20;   "records": \[

&#x20;     {

&#x20;       "id": 101,

&#x20;       "sellerId": 2,

&#x20;       "title": "iPad 9 64G",

&#x20;       "price": 1800.00,

&#x20;       "categoryId": 1,

&#x20;       "categoryName": "数码产品",

&#x20;       "conditionLevel": 4,

&#x20;       "tradeLocation": "一校区宿舍楼下",

&#x20;       "coverImage": "http://localhost:8080/images/item101\_1.jpg",

&#x20;       "status": "ON\_SALE",

&#x20;       "auditStatus": "APPROVED",

&#x20;       "publishTime": "2026-04-18T15:00:01"

&#x20;     }

&#x20;   ]

&#x20; },

&#x20; "requestId": "xxx"

}

```



\---



\### 7.2 商品详情



\#### 请求

\- 方法：`GET`

\- 地址：`/api/items/{id}`



\#### 示例

```text

/api/items/101

```



\#### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "OK",

&#x20; "data": {

&#x20;   "id": 101,

&#x20;   "sellerId": 2,

&#x20;   "title": "iPad 9 64G",

&#x20;   "description": "九成新，正常使用，无拆修。",

&#x20;   "price": 1800.00,

&#x20;   "categoryId": 1,

&#x20;   "categoryName": "数码产品",

&#x20;   "conditionLevel": 4,

&#x20;   "tradeLocation": "一校区宿舍楼下",

&#x20;   "coverImage": "http://localhost:8080/images/item101\_1.jpg",

&#x20;   "status": "ON\_SALE",

&#x20;   "auditStatus": "APPROVED",

&#x20;   "auditReason": null,

&#x20;   "publishTime": "2026-04-18T15:00:01",

&#x20;   "images": \[

&#x20;     {

&#x20;       "id": 1001,

&#x20;       "imageUrl": "http://localhost:8080/images/item101\_1.jpg",

&#x20;       "sortNo": 1

&#x20;     },

&#x20;     {

&#x20;       "id": 1002,

&#x20;       "imageUrl": "http://localhost:8080/images/item101\_2.jpg",

&#x20;       "sortNo": 2

&#x20;     }

&#x20;   ]

&#x20; },

&#x20; "requestId": "xxx"

}

```



\---



\### 7.3 商品搜索



\#### 请求

\- 方法：`GET`

\- 地址：`/api/search/items`



\#### 支持参数

\- `q`

\- `keyword`

\- `pageNum`

\- `page`

\- `pageSize`

\- `categoryId`



\#### 示例

```text

/api/search/items?q=iPad\&pageNum=1\&pageSize=10

/api/search/items?q=机械\&categoryId=1\&pageNum=1\&pageSize=10

```



\#### 返回结构

和商品列表接口一致：



```json

{

&#x20; "code": 0,

&#x20; "message": "OK",

&#x20; "data": {

&#x20;   "total": 1,

&#x20;   "pageNum": 1,

&#x20;   "pageSize": 10,

&#x20;   "totalPages": 1,

&#x20;   "records": \[

&#x20;     {

&#x20;       "id": 101,

&#x20;       "sellerId": 2,

&#x20;       "title": "iPad 9 64G",

&#x20;       "price": 1800.00,

&#x20;       "categoryId": 1,

&#x20;       "categoryName": "数码产品",

&#x20;       "conditionLevel": 4,

&#x20;       "tradeLocation": "一校区宿舍楼下",

&#x20;       "coverImage": "http://localhost:8080/images/item101\_1.jpg",

&#x20;       "status": "ON\_SALE",

&#x20;       "auditStatus": "APPROVED",

&#x20;       "publishTime": "2026-04-18T15:00:01"

&#x20;     }

&#x20;   ]

&#x20; },

&#x20; "requestId": "xxx"

}

```



\---



\### 7.4 发布商品



\#### 请求

\- 方法：`POST`

\- 地址：`/api/items`

\- 需要登录：是



\#### Header

```text

Authorization: Bearer <token>

```



\#### 请求体

```json

{

&#x20; "title": "九成新台灯",

&#x20; "description": "宿舍自用台灯，功能正常，灯泡完好。",

&#x20; "price": 45.00,

&#x20; "categoryId": 3,

&#x20; "conditionLevel": 4,

&#x20; "tradeLocation": "一校区宿舍楼下",

&#x20; "coverImage": "http://localhost:8080/images/test\_lamp\_cover.jpg",

&#x20; "images": \[

&#x20;   "http://localhost:8080/images/test\_lamp\_cover.jpg",

&#x20;   "http://localhost:8080/images/test\_lamp\_2.jpg"

&#x20; ]

}

```



\#### 字段说明

\- `title`：标题

\- `description`：描述

\- `price`：价格

\- `categoryId`：分类 ID

\- `conditionLevel`：新旧程度，1\~5

\- `tradeLocation`：交易地点

\- `coverImage`：封面图 URL

\- `images`：图片 URL 列表，可为空



\#### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "发布成功，等待审核",

&#x20; "data": {

&#x20;   "id": 110,

&#x20;   "title": "九成新台灯",

&#x20;   "status": "OFFLINE",

&#x20;   "auditStatus": "PENDING",

&#x20;   "publishTime": "2026-04-18T21:30:00"

&#x20; },

&#x20; "requestId": "xxx"

}

```



\#### 当前业务规则

发布后默认：

\- `status = OFFLINE`

\- `auditStatus = PENDING`



所以：

\- \*\*不会立即出现在前台商品列表\*\*

\- 会出现在“我的商品”

\- 会出现在管理员待审核列表



\---



\### 7.5 我的商品



\#### 请求

\- 方法：`GET`

\- 地址：`/api/items/mine`

\- 需要登录：是



\#### Header

```text

Authorization: Bearer <token>

```



\#### 支持参数

\- `pageNum`

\- `page`

\- `pageSize`



\#### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "OK",

&#x20; "data": {

&#x20;   "total": 2,

&#x20;   "pageNum": 1,

&#x20;   "pageSize": 10,

&#x20;   "totalPages": 1,

&#x20;   "records": \[

&#x20;     {

&#x20;       "id": 111,

&#x20;       "title": "测试驳回商品",

&#x20;       "price": 66.00,

&#x20;       "categoryId": 3,

&#x20;       "categoryName": "生活用品",

&#x20;       "conditionLevel": 4,

&#x20;       "tradeLocation": "二校区食堂门口",

&#x20;       "coverImage": "http://localhost:8080/images/reject\_test\_cover.jpg",

&#x20;       "status": "OFFLINE",

&#x20;       "auditStatus": "REJECTED",

&#x20;       "auditReason": "商品描述不够完整，请补充细节后重新提交",

&#x20;       "publishTime": "2026-04-18T22:00:00"

&#x20;     }

&#x20;   ]

&#x20; },

&#x20; "requestId": "xxx"

}

```



\#### 前端注意

“我的商品”会返回：

\- 待审核商品

\- 已驳回商品

\- 已通过商品



所以前端可以直接根据：

\- `auditStatus`

\- `auditReason`

\- `status`



来展示不同状态。



\---



\## 8. 管理员接口



\### 8.1 待审核商品列表



\#### 请求

\- 方法：`GET`

\- 地址：`/api/admin/items/pending`

\- 需要管理员：是



\#### Header

```text

Authorization: Bearer <admin token>

```



\#### 支持参数

\- `pageNum`

\- `page`

\- `pageSize`



\#### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "OK",

&#x20; "data": {

&#x20;   "total": 3,

&#x20;   "pageNum": 1,

&#x20;   "pageSize": 10,

&#x20;   "totalPages": 1,

&#x20;   "records": \[

&#x20;     {

&#x20;       "id": 110,

&#x20;       "sellerId": 4,

&#x20;       "title": "九成新台灯",

&#x20;       "price": 45.00,

&#x20;       "categoryId": 3,

&#x20;       "categoryName": "生活用品",

&#x20;       "conditionLevel": 4,

&#x20;       "tradeLocation": "一校区宿舍楼下",

&#x20;       "coverImage": "http://localhost:8080/images/test\_lamp\_cover.jpg",

&#x20;       "status": "OFFLINE",

&#x20;       "auditStatus": "PENDING",

&#x20;       "publishTime": "2026-04-18T21:38:32"

&#x20;     }

&#x20;   ]

&#x20; },

&#x20; "requestId": "xxx"

}

```



\---



\### 8.2 审核通过



\#### 请求

\- 方法：`POST`

\- 地址：`/api/admin/items/{id}/approve`

\- 需要管理员：是



\#### Header

```text

Authorization: Bearer <admin token>

```



\#### 请求体

```json

{

&#x20; "auditorId": 1

}

```



\#### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "审核通过成功",

&#x20; "data": {

&#x20;   "id": 110,

&#x20;   "title": "九成新台灯",

&#x20;   "status": "ON\_SALE",

&#x20;   "auditStatus": "APPROVED",

&#x20;   "auditReason": null

&#x20; },

&#x20; "requestId": "xxx"

}

```



\#### 联动结果

\- 商品会从待审核列表消失

\- 商品会进入前台商品列表 / 搜索结果

\- `audit\_logs` 会写入 `APPROVE`



\---



\### 8.3 审核驳回



\#### 请求

\- 方法：`POST`

\- 地址：`/api/admin/items/{id}/reject`

\- 需要管理员：是



\#### Header

```text

Authorization: Bearer <admin token>

```



\#### 请求体

```json

{

&#x20; "auditorId": 1,

&#x20; "reason": "商品描述不够完整，请补充细节后重新提交"

}

```



\#### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "审核驳回成功",

&#x20; "data": {

&#x20;   "id": 111,

&#x20;   "title": "测试驳回商品",

&#x20;   "status": "OFFLINE",

&#x20;   "auditStatus": "REJECTED",

&#x20;   "auditReason": "商品描述不够完整，请补充细节后重新提交"

&#x20; },

&#x20; "requestId": "xxx"

}

```



\#### 联动结果

\- 商品会从待审核列表消失

\- 不会进入前台商品列表

\- 会保留在“我的商品”中

\- `audit\_logs` 会写入 `REJECT`



\---



\## 9. 图片上传接口



\### 请求

\- 方法：`POST`

\- 地址：`/api/files/upload`



\### 请求格式

\- `multipart/form-data`



\### 表单字段

\- 参数名必须是：`file`



\### 成功响应示例

```json

{

&#x20; "code": 0,

&#x20; "message": "上传成功",

&#x20; "data": {

&#x20;   "fileName": "abc123.jpg",

&#x20;   "url": "http://localhost:8080/images/abc123.jpg"

&#x20; },

&#x20; "requestId": "xxx"

}

```



\### 前端使用方式

上传成功后直接取：



```text

data.url

```



然后把这个 URL 填到：

\- `coverImage`

\- `images\[]`



里即可。



\---



\## 10. 前端对接建议



\### 登录后本地保存

建议保存：

\- `token`

\- `userInfo`

\- `role`



\### 页面接口映射建议



\#### 登录页

\- 调 `POST /api/auth/login`



\#### 注册页

\- 调 `POST /api/auth/register`



\#### 商品列表页

\- 调 `GET /api/items`



\#### 商品详情页

\- 调 `GET /api/items/{id}`



\#### 搜索结果页

\- 调 `GET /api/search/items`



\#### 发布商品页

1\. 先调 `POST /api/files/upload`

2\. 拿到图片 URL

3\. 再调 `POST /api/items`



\#### 个人中心页

\- 用户信息：`GET /api/users/me`

\- 我的商品：`GET /api/items/mine`



\#### 管理员审核页

\- 列表：`GET /api/admin/items/pending`

\- 通过：`POST /api/admin/items/{id}/approve`

\- 驳回：`POST /api/admin/items/{id}/reject`



\---



\## 11. 当前已完成 / 未完成说明



\### 已完成可联调

\- 用户注册

\- 用户登录

\- 当前用户信息

\- 商品列表

\- 商品详情

\- 商品搜索

\- 发布商品

\- 我的商品

\- 管理员待审核列表

\- 管理员审核通过

\- 管理员审核驳回

\- 图片上传



\### 当前未做

\- Gateway 统一鉴权过滤

\- 用户头像上传专用接口

\- 商品编辑接口

\- 商品删除接口

\- Jenkins 自动化流水线正式接入

\- OSS / 云存储上传

\- 生产环境部署



\---



\## 12. 当前联调结论



目前已经可以支撑以下完整真实链路：



1\. 注册

2\. 登录

3\. 获取当前用户信息

4\. 浏览商品列表

5\. 查看商品详情

6\. 搜索商品

7\. 上传图片

8\. 发布商品

9\. 个人中心查看自己发布的商品

10\. 管理员查看待审核商品

11\. 管理员审核通过 / 驳回

12\. 前台商品列表与个人中心联动展示状态变化

