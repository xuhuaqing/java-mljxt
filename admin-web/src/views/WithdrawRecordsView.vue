<template>
  <el-card class="withdraw-card" shadow="never">
    <template #header>
      <div class="title">提现明细列表</div>
    </template>
    <div class="toolbar">
      <el-select
        v-model="query.developerId"
        clearable
        filterable
        placeholder="按合伙人筛选"
        class="developer-select"
      >
        <el-option
          v-for="d in developers"
          :key="d.id"
          :label="`${d.name}(${d.phone})`"
          :value="d.id"
        />
      </el-select>
      <el-button type="primary" @click="load(1)">查询</el-button>
      <el-button @click="reset">重置</el-button>
    </div>

    <el-table :data="rows" border stripe>
      <el-table-column prop="withdrawRecordId" label="提现记录ID" width="110" />
      <el-table-column prop="developerId" label="合伙人ID" width="100" />
      <el-table-column prop="developerName" label="合伙人名称" width="130" />
      <el-table-column prop="developerPhone" label="合伙人手机号" width="130" />
      <el-table-column prop="usageCountSnapshot" label="提现时累计使用次数" width="160" />
      <el-table-column prop="createdAt" label="提现时间" width="180" />
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
import { fetchUsers, fetchWithdrawRecords } from "../api/auth";

const rows = ref<Array<{
  withdrawRecordId: number;
  developerId: number;
  developerName: string;
  developerPhone: string;
  usageCountSnapshot: number;
  createdAt: string;
}>>([]);
const total = ref(0);
const developers = ref<Array<{ id: number; name: string; phone: string }>>([]);
const query = reactive({
  developerId: undefined as number | undefined,
  pageNo: 1,
  pageSize: 20
});

async function loadDevelopers() {
  try {
    const data = await fetchUsers({ role: 4, pageNo: 1, pageSize: 100 });
    developers.value = (data.records || []).map((r) => ({
      id: r.id,
      name: r.name,
      phone: r.phone
    }));
  } catch (e: any) {
    ElMessage.error(e?.message || "加载合伙人列表失败");
  }
}

async function load(pageNo = query.pageNo) {
  query.pageNo = pageNo;
  try {
    const data = await fetchWithdrawRecords(query);
    rows.value = data.records;
    total.value = data.total;
  } catch (e: any) {
    ElMessage.error(e?.message || "加载失败");
  }
}

function reset() {
  query.developerId = undefined;
  load(1);
}

onMounted(async () => {
  await loadDevelopers();
  await load(1);
});
</script>

<style scoped>
.withdraw-card {
  border-radius: 14px;
  border: 1px solid #e8eef8;
}
.title {
  font-size: 16px;
  font-weight: 600;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
}
.developer-select {
  width: 280px;
}
.pager-wrap {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}
</style>
