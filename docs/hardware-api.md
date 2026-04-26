# 硬件控制接口文档

## 基本信息

- 服务地址：`http://localhost:8877`
- 接口前缀：`/api/hardware`
- 请求类型：`application/json`

## 1) 获取MQTT配置指令

- 方法：`GET`
- 路径：`/api/hardware/commands`
- 作用：返回当前后端使用的 MQTT 服务器/订阅/发布配置（联调用）

响应示例：

```json
{
  "mqttServerCommand": "AT+SETSTDMQTT:IP=203.55.129.83,PORT=21883,username=admin,password=public",
  "mqttSubscribeCommand": "AT+mqttSubscribeset:Subscribe=dtm_beautydy/00031",
  "mqttPublishCommand": "AT+mqttPublishset:Publish=dtm_beautyfb/00031",
  "payloadHex": null,
  "resolvedProjectCode": null,
  "resolvedProjectName": null
}
```

## 2) 获取项目列表（分组版，前端推荐）

- 方法：`GET`
- 路径：`/api/hardware/projects`
- 作用：返回项目分组数据，直接用于前端分类渲染

响应结构：

- `categoryName`：分类名称
- `projects`：当前分类下的项目数组
  - `code`：项目编码（十进制）
  - `codeHex`：项目编码（十六进制展示）
  - `name`：项目名称（发送时使用该名称）

完整响应示例（当前后端返回）：

```json
[
  {
    "categoryName": "日常训练",
    "projects": [
      { "code": 1, "codeHex": "0x01", "name": "肩颈深度解压" },
      { "code": 2, "codeHex": "0x02", "name": "背脊通衡养护" },
      { "code": 3, "codeHex": "0x03", "name": "腰骶温养呵护" },
      { "code": 4, "codeHex": "0x04", "name": "胸肋呼吸舒展" },
      { "code": 5, "codeHex": "0x05", "name": "腹区温蕴舒压" },
      { "code": 6, "codeHex": "0x06", "name": "臀腿活力焕新" },
      { "code": 7, "codeHex": "0x07", "name": "小腿轻盈舒缓" },
      { "code": 8, "codeHex": "0x08", "name": "足踝稳泰调理" },
      { "code": 9, "codeHex": "0x09", "name": "上臂肱桡释能" },
      { "code": 10, "codeHex": "0x0A", "name": "前臂腕指松解" }
    ]
  },
  {
    "categoryName": "塑形紧致",
    "projects": [
      { "code": 11, "codeHex": "0x0B", "name": "直角肩养成" },
      { "code": 12, "codeHex": "0x0C", "name": "天鹅臂精雕" },
      { "code": 13, "codeHex": "0x0D", "name": "手臂纤细雕刻" },
      { "code": 14, "codeHex": "0x0E", "name": "美背塑形" },
      { "code": 15, "codeHex": "0x0F", "name": "腰际线精雕" },
      { "code": 16, "codeHex": "0x10", "name": "马甲线雕刻" },
      { "code": 17, "codeHex": "0x11", "name": "蜜桃臀塑造" },
      { "code": 18, "codeHex": "0x12", "name": "大腿内侧紧致" },
      { "code": 19, "codeHex": "0x13", "name": "小腿线条优化" },
      { "code": 20, "codeHex": "0x14", "name": "跟腱显现雕刻" }
    ]
  },
  {
    "categoryName": "运动表现",
    "projects": [
      { "code": 21, "codeHex": "0x15", "name": "力量重塑训练" },
      { "code": 22, "codeHex": "0x16", "name": "爆发力激活训练" },
      { "code": 23, "codeHex": "0x17", "name": "耐力强化训练" },
      { "code": 24, "codeHex": "0x18", "name": "协调敏捷训练" },
      { "code": 25, "codeHex": "0x19", "name": "稳定柔韧训练" }
    ]
  }
]
```

## 3) 获取项目列表（扁平版，兼容）

- 方法：`GET`
- 路径：`/api/hardware/projects-flat`
- 作用：返回单层数组，兼容旧前端
- 响应项结构：`{ code, codeHex, name }`

## 4) 只生成协议帧（不发送）

- 方法：`POST`
- 路径：`/api/hardware/frame`
- 作用：按字段组装设备协议帧，返回 `payloadHex`，不推送 MQTT

请求示例：

```json
{
  "customerId": "13148862532",
  "gender": 0,
  "height": 170,
  "age": 28,
  "weight": 60,
  "projectCode": 3,
  "projectMinutes": 40,
  "sportPerformance": 0,
  "usageCount": 10
}
```

## 5) 按编码发送到设备

- 方法：`POST`
- 路径：`/api/hardware/send`
- 作用：按协议组帧后直接发布到 MQTT
- 入参与 `/frame` 一样

## 6) 按项目名称发送到设备（前端推荐）

- 方法：`POST`
- 路径：`/api/hardware/send-by-project`
- 作用：前端点击项目名时调用，后端自动把项目名解析成编码并发送 MQTT

请求字段：

- 必填：
  - `projectName`：项目名称（必须与项目列表中的 `name` 完全一致）
  - `customerId`：11位手机号
- 可选（不传使用默认值）：
  - `gender`：默认 `0`
  - `height`：默认 `170`
  - `age`：默认 `28`
  - `weight`：默认 `60`
  - `projectMinutes`：默认 `40`
  - `sportPerformance`：默认 `0`
  - `usageCount`：默认 `1`

请求示例：

```json
{
  "projectName": "腰骶温养呵护",
  "customerId": "13148862532",
  "age": 40,
  "weight": 56,
  "projectMinutes": 40,
  "usageCount": 10
}
```

成功响应示例：

```json
{
  "mqttServerCommand": "AT+SETSTDMQTT:IP=203.55.129.83,PORT=21883,username=admin,password=public",
  "mqttSubscribeCommand": "AT+mqttSubscribeset:Subscribe=dtm_beautydy/00031",
  "mqttPublishCommand": "AT+mqttPublishset:Publish=dtm_beautyfb/00031",
  "payloadHex": "AA BB 14 31 33 31 34 38 38 36 32 35 33 32 30 AA 28 38 03 28 30 0A 61",
  "resolvedProjectCode": 3,
  "resolvedProjectName": "腰骶温养呵护"
}
```

失败示例（项目名不存在）：

```json
{
  "timestamp": "2026-04-25T08:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "未知项目名称: XXX",
  "path": "/api/hardware/send-by-project"
}
```

## 前端调用示例（fetch）

```ts
const API_BASE = "http://localhost:8877";

export async function getProjectGroups() {
  const res = await fetch(`${API_BASE}/api/hardware/projects`);
  if (!res.ok) throw new Error("获取项目列表失败");
  return res.json();
}

export async function sendByProject(projectName: string, customerId: string) {
  const res = await fetch(`${API_BASE}/api/hardware/send-by-project`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ projectName, customerId })
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}
```

