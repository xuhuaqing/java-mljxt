# 下单记录接口文档

## 基本信息

- 接口名称：创建下单记录
- 请求方式：`POST`
- 请求路径：`/api/order/create`
- 完整示例：`http://localhost:8877/api/order/create`

## 业务说明

该接口只负责创建“下单记录”（写入 `order_record`），不代表设备已实际执行。  
设备实际执行/消耗记录请使用“使用记录接口”（来源 `usage_record`）。

若请求中指定了 `deviceId`，且该设备当前处于使用中（已有用户通过 MQTT 下发且项目时长未结束），将拒绝下单，避免使用期间重复占用。

## 请求体（JSON）

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| phone | string | 是 | 用户手机号，11位数字 |
| name | string | 是 | 用户姓名，最长50字符 |
| gender | number | 是 | 性别：`0`男，`1`女 |
| age | number | 是 | 年龄：`1-120` |
| height | number | 是 | 身高（cm）：`50-250` |
| weight | number | 是 | 体重（kg）：`20-300` |
| sportPerformance | number | 是 | 运动表现：`0`经常、`1`偶尔、`2`从未 |
| projectName | string | 是 | 项目名称 |
| projectDuration | number | 是 | 项目时长（分钟）：`1-255` |
| merchantId | number | 是 | 商家ID |
| deviceId | number | 否 | 设备ID |
| usageCount | number | 是 | 使用次数：`1-127` |

请求示例：

```json
{
  "phone": "17612718888",
  "name": "张三",
  "gender": 1,
  "age": 28,
  "height": 165,
  "weight": 52,
  "sportPerformance": 1,
  "projectName": "腰骶温养呵护",
  "projectDuration": 30,
  "merchantId": 3,
  "deviceId": 2,
  "usageCount": 10
}
```

## 返回结构

统一返回体：

```json
{
  "code": "0",
  "msg": "success",
  "data": {}
}
```

`data` 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| orderId | number | 下单记录ID（`order_record.id`） |
| userId | number | 用户ID |
| userName | string | 用户姓名 |
| phone | string | 用户手机号 |
| merchantId | number | 商家ID |
| projectName | string | 项目名称 |
| projectDuration | number | 项目时长 |
| usageCount | number | 使用次数 |
| newUserCreated | boolean | 是否自动创建了用户 |
| initialPassword | string/null | 新建用户初始密码（手机号后4位） |

成功响应示例：

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "orderId": 201,
    "userId": 16,
    "userName": "张三",
    "phone": "17612718888",
    "merchantId": 3,
    "projectName": "腰骶温养呵护",
    "projectDuration": 30,
    "usageCount": 10,
    "newUserCreated": false,
    "initialPassword": null
  }
}
```

# 下单接口文档

## 基本信息

- 接口名称：创建订单
- 请求方式：`POST`
- 请求路径：`/api/order/create`
- 完整示例：`http://localhost:8877/api/order/create`

## 业务说明

用于创建一条下单记录。  
接口会根据手机号先查询用户：

- 若用户存在：直接使用该用户下单
- 若用户不存在：自动创建用户（姓名取请求 `name`，默认角色=1 用户，默认密码=手机号后四位），再下单
- 若用户已存在：同步更新 `user_account.name` 为本次请求的 `name`

## 请求参数

请求体 JSON：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| phone | string | 是 | 手机号（11位数字） |
| gender | number | 是 | 性别：`0` 男，`1` 女 |
| age | number | 是 | 年龄（1-120） |
| height | number | 是 | 身高 cm（50-250） |
| weight | number | 是 | 体重 kg（20-300） |
| sportPerformance | number | 是 | 运动表现：`0` 经常运动，`1` 偶尔运动，`2` 从未运动 |
| projectName | string | 是 | 项目名称 |
| projectDuration | number | 是 | 项目时长（分钟，1-255） |
| merchantId | number | 是 | 商家ID（必须存在且角色=商家） |
| usageCount | number | 是 | 使用次数（1-255） |

请求示例：

```json
{
  "phone": "13800138000",
  "gender": 1,
  "age": 28,
  "height": 165,
  "weight": 52,
  "sportPerformance": 0,
  "projectName": "腰骶温养呵护",
  "projectDuration": 30,
  "merchantId": 12,
  "usageCount": 10
}
```

## 返回结构

统一返回体：

```json
{
  "code": "0",
  "msg": "success",
  "data": {}
}
```

`data` 字段结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| orderId | number | 订单ID |
| userId | number | 用户ID |
| userName | string | 用户姓名 |
| phone | string | 用户手机号 |
| merchantId | number | 商家ID |
| projectName | string | 项目名称 |
| projectDuration | number | 项目时长（分钟） |
| usageCount | number | 使用次数 |
| newUserCreated | boolean | 是否新建用户 |
| initialPassword | string/null | 新建用户时返回初始密码（手机号后四位）；老用户返回 `null` |

## 成功响应示例（新用户自动创建）

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "orderId": 101,
    "userId": 208,
    "phone": "13800138000",
    "merchantId": 12,
    "projectName": "腰骶温养呵护",
    "projectDuration": 30,
    "usageCount": 10,
    "newUserCreated": true,
    "initialPassword": "8000"
  }
}
```

## 失败响应示例

### 1) 参数校验失败

```json
{
  "code": "400",
  "msg": "手机号必须是11位数字",
  "data": null
}
```

### 2) 商家不存在

```json
{
  "code": "400",
  "msg": "商家不存在",
  "data": null
}
```

## 前端调用示例

```ts
const API_BASE = "http://localhost:8877";

export type CreateOrderPayload = {
  phone: string;
  name: string;
  gender: 0 | 1;
  age: number;
  height: number;
  weight: number;
  sportPerformance: 0 | 1 | 2;
  projectName: string;
  projectDuration: number;
  merchantId: number;
  usageCount: number;
};

export async function createOrder(payload: CreateOrderPayload) {
  const res = await fetch(`${API_BASE}/api/order/create`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
  const result = await res.json();
  if (result.code !== "0") {
    throw new Error(result.msg || "下单失败");
  }
  return result.data;
}
```

## cURL 调试示例

```bash
curl -X POST "http://localhost:8877/api/order/create" \
  -H "Content-Type: application/json" \
  -d "{\"phone\":\"13800138000\",\"gender\":1,\"age\":28,\"height\":165,\"weight\":52,\"sportPerformance\":0,\"projectName\":\"腰骶温养呵护\",\"projectDuration\":30,\"merchantId\":12,\"usageCount\":10}"
```
