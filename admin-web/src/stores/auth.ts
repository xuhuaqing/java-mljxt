import { defineStore } from "pinia";
import { fetchMe, fetchMenus, login, type LoginPayload } from "../api/auth";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    me: null as null | { id: number; phone: string; role: number; roleName: string },
    menus: [] as Array<{ path: string; name: string }>
  }),
  actions: {
    async doLogin(payload: LoginPayload) {
      const data = await login(payload);
      localStorage.setItem("admin_token", data.token);
      this.me = { id: data.id, phone: data.phone, role: data.role, roleName: data.roleName };
    },
    async loadProfile() {
      this.me = await fetchMe();
      this.menus = await fetchMenus();
    },
    logout() {
      localStorage.removeItem("admin_token");
      this.me = null;
      this.menus = [];
    }
  }
});
