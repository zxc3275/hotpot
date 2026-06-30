import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/views/layout/Index.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/home/Index.vue'), meta: { title: '首页' } },
      { path: 'base-select', name: 'BaseSelect', component: () => import('@/views/base-select/Index.vue'), meta: { title: '选择锅底' } },
      { path: 'menu', name: 'Menu', component: () => import('@/views/menu/Index.vue'), meta: { title: '点菜' } },
      { path: 'cart', name: 'Cart', component: () => import('@/views/cart/Index.vue'), meta: { title: '购物车' } },
      { path: 'orders', name: 'Orders', component: () => import('@/views/order/Index.vue'), meta: { title: '我的订单' } },
      { path: 'order/:id', name: 'OrderDetail', component: () => import('@/views/order/Detail.vue'), meta: { title: '订单详情' } },
      { path: 'points', name: 'Points', component: () => import('@/views/points/Index.vue'), meta: { title: '积分商城' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/profile/Index.vue'), meta: { title: '个人中心' } }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Index.vue'),
    meta: { title: '登录' }
  }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) next('/login')
  else if (to.path === '/login' && token) next('/')
  else next()
})

export default router
