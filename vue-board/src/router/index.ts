import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/posts',
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/posts',
      name: 'PostList',
      component: () => import('@/views/PostListView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/posts/:id',
      name: 'PostDetail',
      component: () => import('@/views/PostDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/posts/new',
      name: 'PostCreate',
      component: () => import('@/views/PostCreateView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/posts/:id/edit',
      name: 'PostEdit',
      component: () => import('@/views/PostEditView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  if (requiresAuth && !authStore.isAuthenticated()) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated()) {
    next('/posts')
  } else {
    next()
  }
})

export default router
