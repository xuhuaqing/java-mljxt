# 下单记录查询接口文档

## 基本信息

- 接口名称：下单记录查询
- 请求方式：`GET`
- 请求路径：`/api/order/order-records`
- 完整示例：`http://localhost:8877/api/order/order-records?phone=17612718888&pageNo=1&pageSize=10`

## 业务说明

用于查询“下单记录”（来源 `order_record` 表），不是设备实际使用记录。  
设备实际使用请调用：`/api/order/usage-records`（来源 `usage_record`）。

## 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| phone | string | 否 | 用户手机号（11位数字） |
| userId | number | 否 | 用户ID（>0） |
| merchantId | number | 否 | 商家ID（>0） |
| deviceId | number | 否 | 设备ID（>0） |
| pageNo | number | 否 | 页码，默认 `1` |
| pageSize | number | 否 | 每页条数，默认 `10`，最大 `100` |

## 返回结构

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "total": 21,
    "pageNo": 1,
    "pageSize": 10,
    "records": [
      {
        "orderId": 201,
        "userId": 16,
        "merchantId": 3,
        "deviceName": "王商家-2号设备",
        "projectName": "腰骶温养呵护",
        "projectDuration": 30,
        "usageCount": 10,
        "sportPerformance": 1,
        "createdAt": "2026-04-30T22:12:00"
      }
    ]
  }
}
```

## cURL 示例

```bash
curl "http://localhost:8877/api/order/order-records?phone=17612718888&pageNo=1&pageSize=10"
```

