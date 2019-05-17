import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/worker',
      name: 'worker',
      component: () => import(/* webpackChunkName: "about" */ './views/Worker.vue')
    },
    {
      path: '/',
      name: 'shift',
      component: () => import(/* webpackChunkName: "about" */ './views/Shift.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import(/* webpackChunkName: "about" */ './views/Admin.vue')
    }
  ]
})
