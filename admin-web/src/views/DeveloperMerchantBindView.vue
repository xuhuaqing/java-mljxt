<template>
  <el-card class="bind-card" shadow="never">
    <template #header>
      <div class="title">开发绑定商家</div>
    </template>

    <div class="toolbar">
      <el-select v-model="form.developerId" clearable filterable placeholder="选择开发账号" class="developer-select" @change="onDeveloperChange">
        <el-option v-for="d in developers" :key="d.id" :label="`${d.name}(${d.phone})`" :value="d.id" />
      </el-select>
      <el-select
        v-model="form.merchantId"
        clearable
        filterable
        remote
        :remote-method="searchMerchants"
        placeholder="选择商家"
        class="merchant-select"
      >
        <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
      </el-select>
      <el-button type="primary" @click="bindNow">立即绑定</el-button>
      <el-button @click="loadList">刷新列表</el-button>
    </div>

    <el-alert
      v-if="!form.developerId"
      title="请先选择开发账号，再查看绑定列表"
      type="info"
      show-icon
      :closable="false"
      class="hint"
    />

    <el-table v-else :data="rows" border stripe>
      <el-table-column prop="bindId" label="绑定ID" width="90" />
      <el-table-column prop="merchantName" label="商家" width="130" />
      <el-table-column prop="merchantPhone" label="商家手机号" width="130" />
      <el-table-column prop="remainingUseCount" label="剩余次数" width="90" />
      <el-table-column prop="merchantTotalDeviceUsageCount" label="商家总使用次数" width="130" />
      <el-table-column prop="deviceName" label="设备名称" width="140" />
      <el-table-column prop="machineNo" label="设备编号" width="100" />
      <el-table-column label="设备状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.deviceStatus === 1 ? 'success' : 'info'">{{ row.deviceStatus === 1 ? "启用" : "停用" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="freeUseDeadline" label="免费截止时间" width="180" />
      <el-table-column prop="bindTime" label="绑定时间" width="180" />
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" @click="unbindNow(row)">解绑</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { bindDeveloperMerchant, fetchDeveloperBoundList, fetchMerchantOptions, fetchUsers, unbindDeveloperMerchant } from "../api/auth";

const developers = ref<Array<{ id: number; name: string; phone: string }>>([]);
const merchants = ref<Array<{ id: number; name: string }>>([]);
const rows = ref<Array<any>>([]);
const form = reactive<{ developerId?: number; merchantId?: number }>({});

async function loadDevelopers() {
  try {
    const data = await fetchUsers({ role: 4, pageNo: 1, pageSize: 100 });
    developers.value = (data.records || []).map((r) => ({ id: r.id, name: r.name, phone: r.phone }));
  } catch (e: any) {
    ElMessage.error(e?.message || "加载开发账号失败");
  }
}

async function searchMerchants(keyword = "") {
  merchants.value = await fetchMerchantOptions(keyword);
}

async function loadList() {
  if (!form.developerId) {
    rows.value = [];
    return;
  }
  try {
    rows.value = await fetchDeveloperBoundList(form.developerId);
  } catch (e: any) {
    ElMessage.error(e?.message || "加载绑定列表失败");
  }
}

function onDeveloperChange() {
  void loadList();
}

async function bindNow() {
  if (!form.developerId || !form.merchantId) {
    ElMessage.warning("请先选择开发和商家");
    return;
  }
  try {
    const result = await bindDeveloperMerchant({ developerId: form.developerId, merchantId: form.merchantId });
    ElMessage.success(result.alreadyBound ? "已存在绑定关系" : "绑定成功");
    await loadList();
  } catch (e: any) {
    ElMessage.error(e?.message || "绑定失败");
  }
}

async function unbindNow(row: { merchantId: number }) {
  if (!form.developerId) {
    ElMessage.warning("请先选择开发账号");
    return;
  }
  try {
    const result = await unbindDeveloperMerchant({ developerId: form.developerId, merchantId: row.merchantId });
    ElMessage.success(result.alreadyBound ? "当前未绑定该商家" : "解绑成功");
    await loadList();
  } catch (e: any) {
    ElMessage.error(e?.message || "解绑失败");
  }
}

onMounted(async () => {
  await Promise.all([loadDevelopers(), searchMerchants()]);
});
</script>

<style scoped>
.bind-card { border-radius: 14px; border: 1px solid #e8eef8; }
.title { font-size: 16px; font-weight: 600; }
.toolbar { display: flex; gap: 12px; margin-bottom: 14px; flex-wrap: wrap; }
.developer-select { width: 280px; }
.merchant-select { width: 240px; }
.hint { margin-bottom: 14px; }
</style>

