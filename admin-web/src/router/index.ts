import { createRouter, createWebHashHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import AdminLayout from "../layouts/AdminLayout.vue";
import DashboardView from "../views/DashboardView.vue";
import ForbiddenView from "../views/ForbiddenView.vue";
import UsersView from "../views/UsersView.vue";
import DevicesView from "../views/DevicesView.vue";
import DeviceUsageRecordsView from "../views/DeviceUsageRecordsView.vue";
import DeveloperMerchantBindView from "../views/DeveloperMerchantBindView.vue";
import { useAuthStore } from "../stores/auth";

const routes = [
  { path: "/login", component: LoginView },
  {
    path: "/",
    component: AdminLayout,
    children: [
      { path: "", redirect: "/dashboard" },
      { path: "dashboard", component: DashboardView },
      { path: "403", component: ForbiddenView },
      { path: "users", component: UsersView },
      { path: "devices", component: DevicesView },
      { path: "device-usage-records", component: DeviceUsageRecordsView },
      { path: "developer-merchant-bind", component: DeveloperMerchantBindView }
    ]
  }
];

const router = createRouter({
  history: createWebHashHistory("/admin/"),
  routes
});

router.beforeEach(async (to) => {
  if (to.path === "/login") return true;
  const token = localStorage.getItem("admin_token");
  if (!token) return "/login";

  const auth = useAuthStore();
  if (!auth.me) {
    try {
      await auth.loadProfile();
    } catch {
      auth.logout();
      return "/login";
    }
  }
  const allowPaths = auth.menus.map((m) => m.path);
  if (!allowPaths.includes(to.path) && to.path !== "/dashboard") {
    return "/403";
  }
  return true;
});

export default router;
