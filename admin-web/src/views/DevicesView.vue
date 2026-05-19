<template>
  <div>
  <el-card class="devices-card" shadow="never">
    <template #header>
      <div class="header-title">设备管理列表</div>
    </template>
    <div class="toolbar">
      <el-select
        v-model="query.merchantId"
        clearable
        filterable
        remote
        :remote-method="searchMerchants"
        placeholder="按商家筛选"
        class="merchant-select"
      >
        <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
      </el-select>
      <el-input v-model="query.keyword" placeholder="设备编号/名称" class="kw-input" />
      <el-button type="primary" @click="load(1)">查询</el-button>
      <el-button @click="reset">重置</el-button>
      <el-button type="success" @click="openCreate">新增设备</el-button>
    </div>

    <el-table :data="rows" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="machineNo" label="设备编号" width="150" />
      <el-table-column prop="deviceName" label="设备名称" min-width="160" />
      <el-table-column prop="merchantName" label="所属商家" width="140">
        <template #default="{ row }">
          {{ row.merchantName || "—" }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "启用" : "停用" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="freeUseDeadline" label="免费使用截止时间" width="190" />
      <el-table-column label="操作" width="340" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDeadline(row)">设置截止时间</el-button>
          <el-button link type="primary" :disabled="!!row.merchantId" @click="openBind(row)">绑定商家</el-button>
          <el-button
            link
            type="warning"
            :disabled="!row.merchantId"
            @click="unbindMerchant(row)"
          >
            解绑商家
          </el-button>
          <el-button link type="danger" :disabled="row.status === 0" @click="stopDevice(row)">停用</el-button>
          <el-button link type="success" :disabled="row.status === 1" @click="startDevice(row)">启用</el-button>
        </template>
      </el-table-column>
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

  <div v-if="createVisible" class="modal-mask" @click.self="createVisible = false">
    <div class="modal-panel">
      <div class="modal-title">新增设备</div>
      <el-form :model="createForm" label-width="110px">
        <el-form-item label="设备编号">
          <el-input v-model="createForm.machineNo" />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="createForm.deviceName" />
        </el-form-item>
        <el-form-item label="商家">
          <el-select
            v-model="createForm.merchantId"
            filterable
            remote
            :remote-method="searchMerchants"
            :teleported="false"
            style="width: 100%"
          >
            <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="免费截止时间">
          <el-date-picker
            v-model="createForm.freeUseDeadline"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            :teleported="false"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <div class="modal-actions">
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">创建</el-button>
      </div>
    </div>
  </div>

  <div v-if="bindVisible" class="modal-mask" @click.self="bindVisible = false">
    <div class="modal-panel">
      <div class="modal-title">绑定商家</div>
      <el-form label-width="90px">
        <el-form-item label="商家">
          <el-select
            v-model="bindForm.merchantId"
            filterable
            remote
            :remote-method="searchMerchants"
            :teleported="false"
            placeholder="选择商家"
            style="width: 100%"
          >
            <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div class="modal-actions">
        <el-button @click="bindVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBind">确定</el-button>
      </div>
    </div>
  </div>

  <div v-if="deadlineVisible" class="modal-mask" @click.self="deadlineVisible = false">
    <div class="modal-panel deadline-panel">
      <div class="modal-title">设置免费使用截止时间</div>
      <el-date-picker
        v-model="deadlineForm.freeUseDeadline"
        type="datetime"
        value-format="YYYY-MM-DDTHH:mm:ss"
        :teleported="false"
        style="width: 100%"
      />
      <div class="modal-actions">
        <el-button @click="deadlineVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDeadline">保存</el-button>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { bindDeviceMerchant, createDevice, disableDevice, enableDevice, fetchDevices, fetchMerchantOptions, unbindDeviceMerchant, updateDeviceDeadline } from "../api/auth";

const rows = ref<Array<any>>([]);
const total = ref(0);
const merchants = ref<Array<{ id: number; name: string }>>([]);
const query = reactive({ merchantId: undefined as number | undefined, keyword: "", pageNo: 1, pageSize: 20 });

const createVisible = ref(false);
const createForm = reactive({ machineNo: "", deviceName: "", merchantId: undefined as number | undefined, freeUseDeadline: "" as string | null });

const bindVisible = ref(false);
const bindForm = reactive({ id: 0, merchantId: undefined as number | undefined });

const deadlineVisible = ref(false);
const deadlineForm = reactive({ id: 0, freeUseDeadline: "" });

async function searchMerchants(keyword = "") {
  merchants.value = await fetchMerchantOptions(keyword);
}

async function load(pageNo = query.pageNo) {
  query.pageNo = pageNo;
  const data = await fetchDevices(query);
  rows.value = data.records;
  total.value = data.total;
}

function reset() {
  query.merchantId = undefined;
  query.keyword = "";
  load(1);
}

function openCreate() {
  createForm.machineNo = "";
  createForm.deviceName = "";
  createForm.merchantId = undefined;
  createForm.freeUseDeadline = null;
  createVisible.value = true;
}

async function submitCreate() {
  if (!createForm.machineNo || !createForm.deviceName || !createForm.merchantId) {
    ElMessage.warning("请填写完整信息");
    return;
  }
  try {
    await createDevice(createForm as any);
    ElMessage.success("新增成功");
    createVisible.value = false;
    load(1);
  } catch (e: any) {
    ElMessage.error(e?.message || "新增失败");
  }
}

function openDeadline(row: any) {
  deadlineForm.id = row.id;
  deadlineForm.freeUseDeadline = row.freeUseDeadline || "";
  deadlineVisible.value = true;
}

async function submitDeadline() {
  if (!deadlineForm.freeUseDeadline) {
    ElMessage.warning("请选择截止时间");
    return;
  }
  try {
    await updateDeviceDeadline(deadlineForm.id, deadlineForm.freeUseDeadline);
    ElMessage.success("设置成功");
    deadlineVisible.value = false;
    load(query.pageNo);
  } catch (e: any) {
    ElMessage.error(e?.message || "设置失败");
  }
}

async function stopDevice(row: any) {
  if (row.status === 0) {
    ElMessage.info("该设备已是停用状态");
    return;
  }
  try {
    await disableDevice(row.id);
    ElMessage.success("停用成功");
    load(query.pageNo);
  } catch (e: any) {
    ElMessage.error(e?.message || "停用失败");
  }
}

async function startDevice(row: any) {
  if (row.status === 1) {
    ElMessage.info("该设备已是启用状态");
    return;
  }
  try {
    await enableDevice(row.id);
    ElMessage.success("启用成功");
    load(query.pageNo);
  } catch (e: any) {
    ElMessage.error(e?.message || "启用失败");
  }
}

function openBind(row: any) {
  if (row.merchantId) {
    ElMessage.info("该设备已绑定商家");
    return;
  }
  bindForm.id = row.id;
  bindForm.merchantId = undefined;
  bindVisible.value = true;
}

async function submitBind() {
  if (!bindForm.merchantId) {
    ElMessage.warning("请选择商家");
    return;
  }
  try {
    await bindDeviceMerchant(bindForm.id, bindForm.merchantId);
    ElMessage.success("绑定成功");
    bindVisible.value = false;
    load(query.pageNo);
  } catch (e: any) {
    ElMessage.error(e?.message || "绑定失败");
  }
}

async function unbindMerchant(row: any) {
  if (!row.merchantId) {
    ElMessage.info("当前设备未绑定商家");
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定将设备「${row.deviceName || row.machineNo}」与商家「${row.merchantName || ""}」解绑吗？解绑后需重新指定商家才可归属到商家名下。`,
      "解绑商家",
      { type: "warning", confirmButtonText: "解绑", cancelButtonText: "取消" }
    );
  } catch {
    return;
  }
  try {
    await unbindDeviceMerchant(row.id);
    ElMessage.success("解绑成功");
    load(query.pageNo);
  } catch (e: any) {
    ElMessage.error(e?.message || "解绑失败");
  }
}

onMounted(async () => {
  await searchMerchants();
  await load(1);
});
</script>

<style scoped>
.devices-card { border-radius: 14px; border: 1px solid #e8eef8; }
.header-title { font-size: 16px; font-weight: 600; }
.toolbar { display: flex; gap: 12px; margin-bottom: 14px; }
.merchant-select { width: 220px; }
.kw-input { width: 220px; }
.pager-wrap { margin-top: 14px; display: flex; justify-content: flex-end; }
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  z-index: 3000;
}
.modal-panel {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 520px;
  overflow: visible;
  background: #fff;
  border-radius: 12px;
  padding: 18px;
  box-shadow: 0 20px 50px rgba(2, 6, 23, 0.3);
}
.deadline-panel { width: 480px; }
.modal-title { font-size: 18px; font-weight: 600; margin-bottom: 14px; }
.modal-actions { margin-top: 14px; display: flex; justify-content: flex-end; gap: 10px; }
</style>
