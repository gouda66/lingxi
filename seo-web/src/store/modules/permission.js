import auth from '@/plugins/auth'
import { constantRoutes } from '@/router'

const usePermissionStore = defineStore(
  'permission',
  {
    state: () => ({
      routes: [],
      addRoutes: [],
      defaultRoutes: [],
      topbarRouters: [],
      sidebarRouters: []
    }),
    actions: {
      setRoutes(routes) {
        this.addRoutes = routes
        this.routes = constantRoutes.concat(routes)
      },
      setDefaultRoutes(routes) {
        this.defaultRoutes = constantRoutes.concat(routes)
      },
      setTopbarRoutes(routes) {
        this.topbarRouters = routes
      },
      setSidebarRouters(routes) {
        // 如果传入的是空数组，不更新
        if (!routes || routes.length === 0) {
          console.warn('[Permission Store] 尝试设置空的 sidebarRouters，已忽略')
          return
        }
        this.$patch({
          sidebarRouters: routes
        })
      }
    }
  })

export default usePermissionStore
