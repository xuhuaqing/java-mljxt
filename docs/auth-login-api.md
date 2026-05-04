# 登录接口文档

## 基本信息

- 接口名称：用户登录
- 请求方式：`POST`
- 请求路径：`/api/auth/login`
- 完整示例：`http://localhost:8877/api/auth/login`
- `Content-Type`：`application/json`

## 业务说明

用户在前端选择身份后，使用手机号 + 密码登录。

- `1`：用户
- `2`：老师
- `3`：商家
- `4`：开发

## 请求参数

| 字段 | 类型 | 必填 | 说明 | 校验规则 |
|---|---|---|---|---|
| phone | string | 是 | 手机号 | 必须是 11 位数字 |
| password | string | 是 | 登录密码 | 不能为空 |
| role | number | 是 | 身份类型 | 只能是 1~4 |

请求体示例：

```json
{
  "phone": "13800138000",
  "password": "123456",
  "role": 1
}
```

## 响应结构

统一返回体：

```json
{
  "code": "0",
  "msg": "success",
  "data": {}
}
```

字段说明：

- `code`：业务状态码，字符串
- `msg`：提示信息
- `data`：业务数据对象，失败时一般为 `null`

## 成功响应示例

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "id": 1001,
    "phone": "13800138000",
    "role": 1,
    "roleName": "用户",
    "remainingUseCount": null,
    "token": "登录后访问后台API使用的Bearer Token"
  }
}
```

商家登录成功示例（`role=3`）：

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "id": 3001,
    "phone": "13900000001",
    "role": 3,
    "roleName": "商家",
    "remainingUseCount": 88,
    "token": "登录后访问后台API使用的Bearer Token"
  }
}
```

## 失败响应示例

参数校验失败：

```json
{
  "code": "400",
  "msg": "手机号必须是11位数字",
  "data": null
}
```

账号或身份不存在：

```json
{
  "code": "400",
  "msg": "手机号或身份不存在",
  "data": null
}
```

密码错误：

```json
{
  "code": "400",
  "msg": "密码错误",
  "data": null
}
```

## 前端调用示例

```ts
const API_BASE = "http://localhost:8877";

export async function login(phone: string, password: string, role: 1 | 2 | 3 | 4) {
  const res = await fetch(`${API_BASE}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ phone, password, role })
  });

  const result = await res.json();
  if (result.code !== "0") {
    throw new Error(result.msg || "登录失败");
  }
  return result.data;
}
```

## cURL 调试示例

```bash
curl -X POST "http://localhost:8877/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"phone\":\"13800138000\",\"password\":\"123456\",\"role\":1}"
```

