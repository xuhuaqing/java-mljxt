<template>
  <el-card class="usage-card" shadow="never">
    <template #header>
      <div class="title">设备使用记录</div>
    </template>
    <div class="toolbar">
      <el-select
        v-model="query.merchantId"
        clearable
        filterable
        remote
        :remote-method="searchMerchants"
        placeholder="选择商家"
        class="merchant-select"
        @change="onMerchantChange"
      >
        <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
      </el-select>
      <el-select v-model="query.deviceId" clearable filterable placeholder="选择设备" class="device-select">
        <el-option v-for="d in devices" :key="d.id" :label="`${d.deviceName}(${d.machineNo})`" :value="d.id" />
      </el-select>
      <el-button type="primary" @click="load(1)">查询</el-button>
      <el-button @click="reset">重置</el-button>
      <el-button type="success" @click="download">导出Excel</el-button>
    </div>

    <el-table :data="rows" border stripe>
      <el-table-column prop="orderId" label="使用记录ID" width="100" />
      <el-table-column prop="merchantName" label="商家" width="130" />
      <el-table-column prop="deviceName" label="设备" width="130" />
      <el-table-column prop="userPhone" label="用户手机号" width="130" />
      <el-table-column prop="projectName" label="项目名称" min-width="160" />
      <el-table-column prop="usageCount" label="次数" width="80" />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
    </el-table>

    <div class="pager-wrap">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="total"
        :current-page="query.pageNo"
        :page-size="query.pageSize"
        @current-change="load"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { exportDeviceUsageRecords, fetchDeviceOptionsByMerchant, fetchDeviceUsageRecords, fetchMerchantOptions } from "../api/auth";

const rows = ref<Array<any>>([]);
const total = ref(0);
const merchants = ref<Array<{ id: number; name: string }>>([]);
const devices = ref<Array<{ id: number; deviceName: string; machineNo: string }>>([]);
const query = reactive({ merchantId: undefined as number | undefined, deviceId: undefined as number | undefined, pageNo: 1, pageSize: 20 });

async function searchMerchants(keyword = "") {
  merchants.value = await fetchMerchantOptions(keyword);
}

async function onMerchantChange() {
  devices.value = await fetchDeviceOptionsByMerchant(query.merchantId);
  query.deviceId = undefined;
}

async function load(pageNo = query.pageNo) {
  query.pageNo = pageNo;
  try {
    const data = await fetchDeviceUsageRecords(query);
    rows.value = data.records;
    total.value = data.total;
  } catch (e: any) {
    ElMessage.error(e?.message || "加载失败");
  }
}

function reset() {
  query.merchantId = undefined;
  query.deviceId = undefined;
  devices.value = [];
  load(1);
}

async function download() {
  try {
    await exportDeviceUsageRecords({ merchantId: query.merchantId, deviceId: query.deviceId });
    ElMessage.success("导出成功");
  } catch (e: any) {
    ElMessage.error(e?.message || "导出失败");
  }
}

onMounted(async () => {
  await searchMerchants();
  await load(1);
});
</script>

<style scoped>
.usage-card { border-radius: 14px; border: 1px solid #e8eef8; }
.title { font-size: 16px; font-weight: 600; }
.toolbar { display: flex; gap: 12px; margin-bottom: 14px; }
.merchant-select { width: 220px; }
.device-select { width: 240px; }
.pager-wrap { margin-top: 14px; display: flex; justify-content: flex-end; }
</style>
