import Vue from 'vue'
import Router from 'vue-router'
import Worker from './views/Worker.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/worker',
      name: 'worker',
      component: Worker
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
