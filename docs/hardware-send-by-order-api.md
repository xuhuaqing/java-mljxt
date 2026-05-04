# 使用仪器接口文档（按订单ID）

## 基本信息

- 接口名称：按订单ID发送仪器指令
- 请求方式：`POST`
- 请求路径：`/api/hardware/send-by-order`
- 完整示例：`http://localhost:8877/api/hardware/send-by-order`

## 业务说明

该接口用于“已下单后直接使用仪器”，只需要传 `orderId`。  
后端会自动读取 `order_record` 里的用户与参数组帧并发送到设备。

处理流程：

1. 按 `orderId` 查询下单记录
2. 解析订单项目名对应的项目编码
3. 取设备：
   - 若传了 `machineNo`，优先用传入机器号
   - 否则使用订单 `deviceId` 对应的设备
4. 校验设备状态、商家状态与剩余次数（非免费期）
5. 发送 MQTT
6. 写入 `usage_record` 使用记录（`usage_count` 固定为 `1`）
7. 非免费期扣减商家 `remaining_use_count`
8. 扣减订单 `order_record.usage_count`（减1，减到0后不可再用）

## 请求体（JSON）

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| orderId | number | 是 | 下单记录ID（`order_record.id`） |
| machineNo | number | 否 | 机器编号 `0-9999`，用于覆盖订单设备 |

最小请求示例：

```json
{
  "orderId": 201
}
```

覆盖设备号示例：

```json
{
  "orderId": 201,
  "machineNo": 1
}
```

## 返回结构

成功返回：

```json
{
  "serverCommand": "xxx",
  "subscribeCommand": "xxx",
  "publishCommand": "xxx",
  "hexFrame": "AA5501...",
  "projectCode": 10,
  "projectName": "腰骶温养呵护"
}
```

## 常见错误

### 1) 订单不存在

```json
{
  "code": "400",
  "msg": "订单不存在",
  "data": null
}
```

### 2) 订单未绑定设备且未传 machineNo

```json
{
  "code": "400",
  "msg": "请传machineNo，订单未绑定设备",
  "data": null
}
```

### 3) 商家剩余次数不足（仅非免费期）

```json
{
  "code": "400",
  "msg": "商家剩余次数不足",
  "data": null
}
```

## cURL 调试示例

```bash
curl -X POST "http://localhost:8877/api/hardware/send-by-order" \
  -H "Content-Type: application/json" \
  -d "{\"orderId\":201}"
```

