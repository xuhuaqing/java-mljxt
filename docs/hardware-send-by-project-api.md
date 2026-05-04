# 使用仪器接口文档

## 基本信息

- 接口名称：按项目名称发送仪器指令
- 请求方式：`POST`
- 请求路径：`/api/hardware/send-by-project`
- 完整示例：`http://localhost:8877/api/hardware/send-by-project`

## 业务说明

该接口用于“真正使用仪器”（向设备下发 MQTT 指令）。  
调用成功后会写入 `usage_record`（使用记录），不是 `order_record`（下单记录）。

接口处理流程：

1. 根据 `projectName` 匹配项目字典并得到项目编码
2. 根据 `machineNo` 找设备，校验设备是否存在且未停用
3. 校验设备绑定商家是否存在
4. 判断设备是否在免费期
   - 在免费期：不校验商家剩余次数，不扣减次数
   - 不在免费期：校验商家 `remaining_use_count > 0`，成功后扣减 1
5. 校验用户手机号（`customerId`）是否存在，不存在则报错
6. 组帧并发送 MQTT
7. 写入 `usage_record` 使用记录（每次固定1条，`usage_count` 固定为 `1`）

## 请求体（JSON）

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| projectName | string | 是 | 项目名称（必须与项目字典完全一致） |
| customerId | string | 是 | 用户手机号，11位数字 |
| machineNo | number | 否 | 设备编号，`0-9999`，默认 `0` |
| gender | number | 否 | 性别：`0`男，`1`女；默认 `0` |
| age | number | 否 | 年龄：`0-255`；默认 `28` |
| height | number | 否 | 身高：`0-255`；默认 `170` |
| weight | number | 否 | 体重：`0-255`；默认 `60` |
| projectMinutes | number | 否 | 项目时长（分钟）：`0-255`；默认 `40` |
| sportPerformance | number | 否 | 运动表现：`0`经常、`1`偶尔、`2`从未；默认 `0` |
| usageCount | number | 否 | 使用次数：`0-127`；默认 `1` |

最小请求示例：

```json
{
  "projectName": "腰骶温养呵护",
  "customerId": "17612718888",
  "machineNo": 1
}
```

完整请求示例：

```json
{
  "projectName": "腰骶温养呵护",
  "customerId": "17612718888",
  "machineNo": 1,
  "gender": 1,
  "age": 28,
  "height": 165,
  "weight": 52,
  "projectMinutes": 30,
  "sportPerformance": 1,
  "usageCount": 10
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

字段说明：

| 字段 | 类型 | 说明 |
|---|---|---|
| serverCommand | string | MQTT 服务端连接信息 |
| subscribeCommand | string | 订阅命令示例 |
| publishCommand | string | 发布命令示例 |
| hexFrame | string | 最终发送的16进制报文 |
| projectCode | number | 项目编码 |
| projectName | string | 项目名称 |

## 常见错误

### 1) 项目名不存在

```json
{
  "code": "400",
  "msg": "未知项目名称: xxx",
  "data": null
}
```

### 2) 设备不存在或停用

```json
{
  "code": "400",
  "msg": "设备不存在: machineNo=0001",
  "data": null
}
```

```json
{
  "code": "400",
  "msg": "设备已停用",
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

### 4) 用户不存在

```json
{
  "code": "400",
  "msg": "用户不存在，请先登录",
  "data": null
}
```

## cURL 调试示例

```bash
curl -X POST "http://localhost:8877/api/hardware/send-by-project" \
  -H "Content-Type: application/json" \
  -d "{\"projectName\":\"腰骶温养呵护\",\"customerId\":\"17612718888\",\"machineNo\":1}"
```

