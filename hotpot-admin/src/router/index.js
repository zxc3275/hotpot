import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/layout/Index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: { title: '仪表盘', icon: 'DataAnalysis' }
      },
      {
        path: 'dish',
        name: 'Dish',
        component: () => import('@/views/dish/Index.vue'),
        meta: { title: '菜品管理', icon: 'Dish' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/order/Index.vue'),
        meta: { title: '订单管理', icon: 'Document' }
      },
      {
        path: 'points',
        name: 'Points',
        component: () => import('@/views/points/Index.vue'),
        meta: { title: '积分商城', icon: 'Present' }
      },
      {
        path: 'guide',
        name: 'Guide',
        component: () => import('@/views/guide/Index.vue'),
        meta: { title: '火锅指南', icon: 'Guide' }
      },
      {
        path: 'system',
        name: 'System',
        component: () => import('@/views/system/Index.vue'),
        meta: { title: '系统管理', icon: 'Setting' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Index.vue'),
        meta: { title: '个人中心', icon: 'User' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
