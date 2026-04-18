# V1 接口协议（api-v1）

## 1. 通用约定

### 1.1 接口统一前缀

所有接口统一通过 Gateway 暴露，统一前缀为：

`/api`

### 1.2 数据格式

请求与响应统一使用 JSON，文件上传接口除外。

### 1.3 统一响应结构

所有接口统一返回以下结构：

```json
{
  "code": 0,
  "message": "OK",
  "data": {},
  "requestId": "uuid"
}
```

### 1.4 分页响应结构

分页接口统一返回以下结构：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "records": [],
    "page": 1,
    "size": 10,
    "total": 0,
    "pages": 0
  },
  "requestId": "uuid"
}
```

### 1.5 时间字段格式

时间字段统一采用字符串格式：

`yyyy-MM-dd HH:mm:ss`

---

## 2. 认证与权限约定

### 2.1 Token 传递方式

登录成功后，后续请求通过请求头传递 Token：

`Authorization: Bearer <token>`

### 2.2 公开接口

以下接口无需登录即可访问：

- 用户注册
- 用户登录
- 商品列表
- 商品详情
- 商品搜索

### 2.3 登录后可访问接口

以下接口需要普通用户登录后访问：

- 当前用户信息
- 商品发布
- 我的商品
- 图片上传

### 2.4 管理员接口

以下接口仅管理员可访问：

- 待审核商品列表
- 审核通过商品
- 审核驳回商品

---

## 3. 用户相关接口

### 3.1 用户注册

- 请求方式：`POST`
- 接口路径：`/api/auth/register`
- 是否需要登录：否

#### 请求参数

```json
{
  "username": "cai_daniel",
  "password": "123456",
  "nickname": "CaiDaniel",
  "email": "example@hit.edu.cn",
  "phone": "13800000000"
}
```

#### 参数说明

- `username`：用户名，唯一
- `password`：密码
- `nickname`：昵称
- `email`：邮箱
- `phone`：手机号

#### 成功响应示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "userId": 1,
    "username": "cai_daniel",
    "nickname": "CaiDaniel"
  },
  "requestId": "uuid"
}
```

### 3.2 用户登录

- 请求方式：`POST`
- 接口路径：`/api/auth/login`
- 是否需要登录：否

#### 请求参数

```json
{
  "username": "cai_daniel",
  "password": "123456"
}
```

#### 参数说明

- `username`：用户名
- `password`：密码

#### 成功响应示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "token": "jwt-token-string",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userInfo": {
      "id": 1,
      "username": "cai_daniel",
      "nickname": "CaiDaniel",
      "avatarUrl": "",
      "role": "USER"
    }
  },
  "requestId": "uuid"
}
```

### 3.3 当前用户信息

- 请求方式：`GET`
- 接口路径：`/api/users/me`
- 是否需要登录：是

#### 请求头

`Authorization: Bearer <token>`

#### 成功响应示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "id": 1,
    "username": "cai_daniel",
    "nickname": "CaiDaniel",
    "email": "example@hit.edu.cn",
    "phone": "13800000000",
    "avatarUrl": "",
    "role": "USER",
    "status": "ACTIVE"
  },
  "requestId": "uuid"
}
```

---

## 4. 商品相关接口

### 4.1 商品列表

- 请求方式：`GET`
- 接口路径：`/api/items`
- 是否需要登录：否

#### 查询参数

- `page`：页码，默认 1
- `size`：每页数量，默认 10
- `categoryId`：分类 ID，可选
- `keyword`：关键字，可选
- `sort`：排序方式，可选，支持 `latest`、`priceAsc`、`priceDesc`

#### 请求示例

`/api/items?page=1&size=10&categoryId=1&keyword=ipad&sort=latest`

#### 成功响应示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "records": [
      {
        "id": 101,
        "title": "iPad 9 64G",
        "coverImage": "http://localhost:8080/images/1.jpg",
        "price": 1800.00,
        "categoryId": 1,
        "categoryName": "数码产品",
        "sellerId": 1,
        "sellerNickname": "CaiDaniel",
        "status": "ON_SALE",
        "publishTime": "2026-04-18 13:00:00"
      }
    ],
    "page": 1,
    "size": 10,
    "total": 1,
    "pages": 1
  },
  "requestId": "uuid"
}
```

说明：`/api/items` 主要用于首页商品列表展示；`/api/search/items` 主要用于搜索结果页。两者返回结构保持一致，便于前端复用列表渲染逻辑。

### 4.2 商品详情

- 请求方式：`GET`
- 接口路径：`/api/items/{id}`
- 是否需要登录：否

#### 路径参数

- `id`：商品 ID

#### 成功响应示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "id": 101,
    "title": "iPad 9 64G",
    "description": "九成新，正常使用，无拆修。",
    "price": 1800.00,
    "categoryId": 1,
    "categoryName": "数码产品",
    "coverImage": "http://localhost:8080/images/1.jpg",
    "imageUrls": [
      "http://localhost:8080/images/1.jpg",
      "http://localhost:8080/images/2.jpg"
    ],
    "seller": {
      "id": 1,
      "nickname": "CaiDaniel"
    },
    "status": "ON_SALE",
    "auditStatus": "APPROVED",
    "publishTime": "2026-04-18 13:00:00"
  },
  "requestId": "uuid"
}
```

### 4.3 发布商品

- 请求方式：`POST`
- 接口路径：`/api/items`
- 是否需要登录：是

#### 请求头

`Authorization: Bearer <token>`

#### 请求参数

```json
{
  "title": "iPad 9 64G",
  "description": "九成新，正常使用，无拆修。",
  "price": 1800.00,
  "categoryId": 1,
  "coverImage": "http://localhost:8080/images/1.jpg",
  "imageUrls": [
    "http://localhost:8080/images/1.jpg",
    "http://localhost:8080/images/2.jpg"
  ]
}
```

#### 参数说明

- `title`：商品标题
- `description`：商品描述
- `price`：商品价格
- `categoryId`：分类 ID
- `coverImage`：封面图 URL
- `imageUrls`：商品图片 URL 列表

#### 成功响应示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "id": 101,
    "status": "OFFLINE",
    "auditStatus": "PENDING",
    "createdAt": "2026-04-18 13:00:00"
  },
  "requestId": "uuid"
}
```

### 4.4 我的商品

- 请求方式：`GET`
- 接口路径：`/api/items/mine`
- 是否需要登录：是

#### 请求头

`Authorization: Bearer <token>`

#### 查询参数

- `page`：页码，默认 1
- `size`：每页数量，默认 10

#### 成功响应示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "records": [
      {
        "id": 101,
        "title": "iPad 9 64G",
        "coverImage": "http://localhost:8080/images/1.jpg",
        "price": 1800.00,
        "status": "OFFLINE",
        "auditStatus": "PENDING",
        "publishTime": "2026-04-18 13:00:00"
      }
    ],
    "page": 1,
    "size": 10,
    "total": 1,
    "pages": 1
  },
  "requestId": "uuid"
}
```

---

## 5. 搜索相关接口

### 5.1 商品搜索

- 请求方式：`GET`
- 接口路径：`/api/search/items`
- 是否需要登录：否

#### 查询参数

- `page`：页码，默认 1
- `size`：每页数量，默认 10
- `keyword`：搜索关键字
- `categoryId`：分类 ID，可选
- `sort`：排序方式，可选，支持 `latest`、`priceAsc`、`priceDesc`

#### 请求示例

`/api/search/items?page=1&size=10&keyword=ipad&sort=latest`

#### 成功响应示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "records": [
      {
        "id": 101,
        "title": "iPad 9 64G",
        "coverImage": "http://localhost:8080/images/1.jpg",
        "price": 1800.00,
        "categoryId": 1,
        "categoryName": "数码产品",
        "sellerId": 1,
        "sellerNickname": "CaiDaniel",
        "status": "ON_SALE",
        "publishTime": "2026-04-18 13:00:00"
      }
    ],
    "page": 1,
    "size": 10,
    "total": 1,
    "pages": 1
  },
  "requestId": "uuid"
}
```

---

## 6. 文件上传接口

### 6.1 图片上传

- 请求方式：`POST`
- 接口路径：`/api/files/upload`
- 是否需要登录：是
- 请求类型：`multipart/form-data`

#### 请求参数

- `file`：图片文件

#### 成功响应示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "url": "http://localhost:8080/images/1.jpg"
  },
  "requestId": "uuid"
}
```

说明：响应中的 `url` 为图片可访问地址，文档中的 localhost 地址仅为示例，实际以运行环境返回为准。

---

## 7. 管理员审核接口

### 7.1 待审核商品列表

- 请求方式：`GET`
- 接口路径：`/api/admin/items/pending`
- 是否需要管理员：是

#### 查询参数

- `page`：页码，默认 1
- `size`：每页数量，默认 10

#### 成功响应示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "records": [
      {
        "id": 101,
        "title": "iPad 9 64G",
        "coverImage": "http://localhost:8080/images/1.jpg",
        "price": 1800.00,
        "sellerId": 1,
        "sellerNickname": "CaiDaniel",
        "auditStatus": "PENDING",
        "publishTime": "2026-04-18 13:00:00"
      }
    ],
    "page": 1,
    "size": 10,
    "total": 1,
    "pages": 1
  },
  "requestId": "uuid"
}
```

### 7.2 审核通过商品

- 请求方式：`POST`
- 接口路径：`/api/admin/items/{id}/approve`
- 是否需要管理员：是

#### 路径参数

- `id`：商品 ID

#### 成功响应示例

```json
{
  "code": 0,
  "message": "审核通过",
  "data": {
    "id": 101,
    "status": "ON_SALE",
    "auditStatus": "APPROVED"
  },
  "requestId": "uuid"
}
```

### 7.3 审核驳回商品

- 请求方式：`POST`
- 接口路径：`/api/admin/items/{id}/reject`
- 是否需要管理员：是

#### 路径参数

- `id`：商品 ID

#### 请求参数

```json
{
  "reason": "商品信息不完整"
}
```

#### 成功响应示例

```json
{
  "code": 0,
  "message": "审核驳回",
  "data": {
    "id": 101,
    "status": "OFFLINE",
    "auditStatus": "REJECTED"
  },
  "requestId": "uuid"
}
```

---

## 8. 状态码与错误响应约定

### 8.1 成功状态

- `code = 0`：请求成功

### 8.2 常见错误

- `code = 400`：请求参数错误
- `code = 401`：未登录或 Token 无效
- `code = 403`：无权限访问
- `code = 404`：资源不存在
- `code = 500`：服务器内部错误

### 8.3 错误响应示例

```json
{
  "code": 401,
  "message": "未登录或登录已过期",
  "data": null,
  "requestId": "uuid"
}
```