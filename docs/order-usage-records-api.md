# 使用记录查询接口文档

## 基本信息

- 接口名称：使用记录查询
- 请求方式：`GET`
- 请求路径：`/api/order/usage-records`
- 完整示例：`http://localhost:8877/api/order/usage-records?phone=13800138000&pageNo=1&pageSize=10`

## 业务说明

用于查询设备实际使用记录（来源 `usage_record` 表），支持按以下条件筛选：

- 用户手机号 `phone`
- 用户ID `userId`
- 设备ID `deviceId`

支持分页，按创建时间倒序展示：`created_at DESC, id DESC`。  
注意：该接口查询的是“实际使用记录”，不是下单记录。

## 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| phone | string | 否 | 用户手机号（11位数字） |
| userId | number | 否 | 用户ID |
| deviceId | number | 否 | 设备ID |
| pageNo | number | 否 | 页码，默认 `1` |
| pageSize | number | 否 | 每页条数，默认 `10`，最大 `100` |

参数规则：

- `phone` 传值时必须为11位数字
- `userId`、`deviceId` 传值时必须大于0

请求示例：

- 按手机号查询  
  `GET /api/order/usage-records?phone=13800138000&pageNo=1&pageSize=10`

- 按用户ID查询  
  `GET /api/order/usage-records?userId=12&pageNo=1&pageSize=10`

- 按设备ID查询  
  `GET /api/order/usage-records?deviceId=1&pageNo=1&pageSize=20`

- 组合查询（手机号 + 设备ID）  
  `GET /api/order/usage-records?phone=13800138000&deviceId=1&pageNo=1&pageSize=10`

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
| total | number | 总记录数 |
| pageNo | number | 当前页码 |
| pageSize | number | 每页条数 |
| records | array | 当前页记录 |

`records` 每项结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| orderId | number | 使用记录ID（`usage_record.id`） |
| userId | number | 用户ID |
| merchantId | number | 商家ID |
| deviceName | string/null | 设备名称 |
| projectName | string | 项目名称 |
| projectDuration | number | 项目时长（分钟） |
| usageCount | number | 使用次数 |
| sportPerformance | number | 运动表现（`0`经常、`1`偶尔、`2`从未） |
| createdAt | string | 创建时间，格式 `yyyy-MM-ddTHH:mm:ss` |

## 成功响应示例

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "total": 25,
    "pageNo": 1,
    "pageSize": 10,
    "records": [
      {
        "orderId": 1001,
        "userId": 12,
        "merchantId": 3,
        "deviceName": "王商家-1号设备",
        "projectName": "腰骶温养呵护",
        "projectDuration": 30,
        "usageCount": 10,
        "sportPerformance": 0,
        "createdAt": "2026-04-26T15:00:00"
      },
      {
        "orderId": 998,
        "userId": 12,
        "merchantId": 3,
        "deviceName": "王商家-2号设备",
        "projectName": "背脊通衡养护",
        "projectDuration": 20,
        "usageCount": 8,
        "sportPerformance": 1,
        "createdAt": "2026-04-26T14:40:00"
      }
    ]
  }
}
```

## 失败响应示例

### 1) 手机号格式错误

```json
{
  "code": "400",
  "msg": "手机号必须是11位数字",
  "data": null
}
```

### 2) ID参数非法

```json
{
  "code": "400",
  "msg": "deviceId必须大于0",
  "data": null
}
```

## 前端调用示例

```ts
const API_BASE = "http://localhost:8877";

type QueryOrderUsageParams = {
  phone?: string;
  userId?: number;
  deviceId?: number;
  pageNo?: number;
  pageSize?: number;
};

export async function queryUsageRecords(params: QueryOrderUsageParams) {
  const search = new URLSearchParams();
  if (params.phone) search.set("phone", params.phone);
  if (params.userId != null) search.set("userId", String(params.userId));
  if (params.deviceId != null) search.set("deviceId", String(params.deviceId));
  if (params.pageNo != null) search.set("pageNo", String(params.pageNo));
  if (params.pageSize != null) search.set("pageSize", String(params.pageSize));

  const res = await fetch(`${API_BASE}/api/order/usage-records?${search.toString()}`);
  const result = await res.json();
  if (result.code !== "0") {
    throw new Error(result.msg || "查询使用记录失败");
  }
  return result.data;
}
```

## cURL 调试示例

```bash
curl "http://localhost:8877/api/order/usage-records?phone=13800138000&pageNo=1&pageSize=10"
```
