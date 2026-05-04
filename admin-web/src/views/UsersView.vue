<template>
  <div>
    <el-card class="users-card" shadow="never">
    <template #header>
      <div class="card-header">
        <div>
          <div class="header-title">角色用户列表</div>
          <div class="header-sub">支持按角色、姓名、手机号快速筛选</div>
        </div>
      </div>
    </template>
    <div class="toolbar">
      <el-select v-model="query.role" clearable placeholder="角色" class="role-select">
        <el-option :value="1" label="用户" />
        <el-option :value="2" label="老师" />
        <el-option :value="3" label="商家" />
        <el-option :value="4" label="开发" />
      </el-select>
      <el-input v-model="query.keyword" placeholder="姓名或手机号" class="kw-input" />
      <el-button type="primary" @click="load(1)">查询</el-button>
      <el-button @click="reset">重置</el-button>
      <el-button type="success" @click="openCreate">新增用户</el-button>
    </div>
    <el-table :data="rows" border stripe>
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="roleName" label="角色" width="100" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "启用" : "禁用" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remainingUseCount" label="商家剩余次数" width="140" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" :disabled="row.status === 0" @click="remove(row)">停用</el-button>
          <el-button link type="success" :disabled="row.status === 1" @click="enable(row)">启用</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无匹配数据" />
      </template>
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

    <div v-if="dialogVisible" class="modal-mask" @click.self="dialogVisible = false">
      <div class="modal-panel" :style="modalStyle">
        <div class="modal-title" @mousedown="startDrag">{{ editingId ? "编辑用户" : "新增用户" }}</div>
        <el-form :model="form" label-width="90px">
          <el-form-item label="姓名">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" show-password />
          </el-form-item>
          <el-form-item label="角色">
            <el-checkbox-group v-model="selectedRoles" @change="onRoleCheckChange">
              <el-checkbox :label="1">用户</el-checkbox>
              <el-checkbox :label="2">老师</el-checkbox>
              <el-checkbox :label="3">商家</el-checkbox>
              <el-checkbox :label="4">开发</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item v-if="form.role === 3" label="剩余次数">
            <el-input-number v-model="form.remainingUseCount" :min="0" :step="1" style="width: 100%" />
          </el-form-item>
        </el-form>
        <div class="modal-actions">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit">{{ editingId ? "保存" : "创建" }}</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from "vue";
import { createUser, deleteUser, enableUser, fetchUsers, updateUser, type AdminUserForm } from "../api/auth";
import { ElMessage } from "element-plus";

const rows = ref<Array<any>>([]);
const total = ref(0);
const query = reactive({ role: undefined as number | undefined, keyword: "", pageNo: 1, pageSize: 20 });
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const selectedRoles = ref<number[]>([1]);
const modalWidth = 520;
const modalPos = reactive({ left: 0, top: 0 });
const drag = reactive({ active: false, offsetX: 0, offsetY: 0 });
const form = reactive<AdminUserForm>({
  name: "",
  phone: "",
  password: "",
  role: 1,
  remainingUseCount: 0
});

const modalStyle = computed(() => ({
  width: `${modalWidth}px`,
  left: `${modalPos.left}px`,
  top: `${modalPos.top}px`
}));

async function load(pageNo = query.pageNo) {
  query.pageNo = pageNo;
  try {
    const data = await fetchUsers(query);
    rows.value = data.records;
    total.value = data.total;
  } catch (e: any) {
    ElMessage.error(e?.message || "加载失败");
  }
}

function reset() {
  query.role = undefined;
  query.keyword = "";
  load(1);
}

function openCreate() {
  editingId.value = null;
  form.name = "";
  form.phone = "";
  form.password = "";
  form.role = 1;
  selectedRoles.value = [1];
  form.remainingUseCount = 0;
  centerModal();
  dialogVisible.value = true;
}

function openEdit(row: any) {
  editingId.value = row.id;
  form.name = row.name || "";
  form.phone = row.phone || "";
  form.password = row.password || "";
  form.role = row.role;
  selectedRoles.value = [row.role];
  form.remainingUseCount = row.remainingUseCount ?? 0;
  centerModal();
  dialogVisible.value = true;
}

function centerModal() {
  const vw = window.innerWidth;
  const vh = window.innerHeight;
  modalPos.left = Math.max((vw - modalWidth) / 2, 12);
  modalPos.top = Math.max((vh - 480) / 2, 12);
}

function startDrag(e: MouseEvent) {
  drag.active = true;
  drag.offsetX = e.clientX - modalPos.left;
  drag.offsetY = e.clientY - modalPos.top;
  document.addEventListener("mousemove", onDragging);
  document.addEventListener("mouseup", stopDrag);
}

function onDragging(e: MouseEvent) {
  if (!drag.active) return;
  const maxLeft = window.innerWidth - modalWidth - 12;
  const maxTop = window.innerHeight - 80;
  modalPos.left = Math.min(Math.max(e.clientX - drag.offsetX, 12), Math.max(maxLeft, 12));
  modalPos.top = Math.min(Math.max(e.clientY - drag.offsetY, 12), Math.max(maxTop, 12));
}

function stopDrag() {
  drag.active = false;
  document.removeEventListener("mousemove", onDragging);
  document.removeEventListener("mouseup", stopDrag);
}

function onRoleCheckChange(values: Array<number | string>) {
  if (!values.length) {
    form.role = 1;
    selectedRoles.value = [1];
    return;
  }
  const last = Number(values[values.length - 1]);
  form.role = last as 1 | 2 | 3 | 4;
  selectedRoles.value = [last];
}

async function submit() {
  if (!form.name || !form.phone || !form.password) {
    ElMessage.warning("请填写完整信息");
    return;
  }
  try {
    const payload: AdminUserForm = {
      name: form.name,
      phone: form.phone,
      password: form.password,
      role: form.role,
      remainingUseCount: form.role === 3 ? form.remainingUseCount ?? 0 : 0
    };
    if (editingId.value) {
      await updateUser(editingId.value, payload);
      ElMessage.success("更新成功");
    } else {
      await createUser(payload);
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    await load(query.pageNo);
  } catch (e: any) {
    ElMessage.error(e?.message || "操作失败");
  }
}

async function remove(row: any) {
  if (row.status === 0) {
    ElMessage.info("该用户已是禁用状态");
    return;
  }
  try {
    await deleteUser(row.id);
    ElMessage.success("停用成功");
    await load(1);
  } catch (e: any) {
    ElMessage.error(e?.message || "停用失败");
  }
}

async function enable(row: any) {
  if (row.status === 1) {
    ElMessage.info("该用户已是启用状态");
    return;
  }
  try {
    await enableUser(row.id);
    ElMessage.success("启用成功");
    await load(1);
  } catch (e: any) {
    ElMessage.error(e?.message || "启用失败");
  }
}

onMounted(() => load(1));
onBeforeUnmount(() => stopDrag());
</script>

<style scoped>
.users-card {
  border-radius: 14px;
  border: 1px solid #e8eef8;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
}

.header-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #94a3b8;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
}

.role-select {
  width: 160px;
}

.kw-input {
  width: 260px;
}

.pager-wrap {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}

.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 3000;
  background: rgba(15, 23, 42, 0.45);
}

.modal-panel {
  position: fixed;
  background: #fff;
  border-radius: 12px;
  padding: 18px 18px 12px;
  box-shadow: 0 20px 50px rgba(2, 6, 23, 0.3);
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 14px;
  cursor: move;
  user-select: none;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
