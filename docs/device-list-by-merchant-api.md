# 设备列表接口文档（按商家ID查询）

## 基本信息

- 接口名称：按商家查询设备列表
- 请求方式：`GET`
- 请求路径：`/api/device/list-by-merchant`
- 完整示例：`http://localhost:8877/api/device/list-by-merchant?merchantId=12`

## 业务说明

用于在前端根据商家ID拉取设备列表。  
仅返回该商家已绑定的设备数据。

字段 `freeUseDeadline` 表示设备免费使用截至时间（可为空）。

## 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| merchantId | number | 是 | 商家ID，必须大于0 |

请求示例：

```http
GET /api/device/list-by-merchant?merchantId=12
```

## 返回结构

统一返回体：

```json
{
  "code": "0",
  "msg": "success",
  "data": []
}
```

`data` 为数组，每项结构如下：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | number | 设备记录ID |
| machineNo | string | 设备编号（如 `0000`、`0001`） |
| deviceName | string | 设备名称 |
| status | number | 设备状态：`0` 停用，`1` 启用 |
| merchantId | number | 商家ID |
| freeUseDeadline | string/null | 免费使用截至时间，格式：`yyyy-MM-ddTHH:mm:ss`，为空表示未设置 |

## 成功响应示例

```json
{
  "code": "0",
  "msg": "success",
  "data": [
    {
      "id": 101,
      "machineNo": "0000",
      "deviceName": "门店A-1号机",
      "status": 1,
      "merchantId": 12,
      "freeUseDeadline": "2026-12-31T23:59:59"
    },
    {
      "id": 102,
      "machineNo": "0001",
      "deviceName": "门店A-2号机",
      "status": 1,
      "merchantId": 12,
      "freeUseDeadline": null
    }
  ]
}
```

## 失败响应示例

### 1) 参数错误（merchantId为空或<=0）

```json
{
  "code": "400",
  "msg": "merchantId不能为空且必须大于0",
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

export async function fetchDevicesByMerchantId(merchantId: number) {
  const res = await fetch(
    `${API_BASE}/api/device/list-by-merchant?merchantId=${merchantId}`
  );
  const result = await res.json();
  if (result.code !== "0") {
    throw new Error(result.msg || "获取设备列表失败");
  }
  return result.data as Array<{
    id: number;
    machineNo: string;
    deviceName: string;
    status: 0 | 1;
    merchantId: number;
    freeUseDeadline: string | null;
  }>;
}
```

## cURL 调试示例

```bash
curl "http://localhost:8877/api/device/list-by-merchant?merchantId=12"
```
