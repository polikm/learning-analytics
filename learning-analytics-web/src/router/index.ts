import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils'

NProgress.configure({ showSpinner: false })

/** 公共路由 */
export const publicRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在', requiresAuth: false },
  },
]

/** 管理员路由 */
export const adminRoutes: RouteRecordRaw[] = [
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, roles: ['admin', 'ADMIN'] },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'Odometer' },
      },
      {
        path: 'tenants',
        name: 'AdminTenants',
        component: () => import('@/views/admin/tenant/index.vue'),
        meta: { title: '租户管理', icon: 'OfficeBuilding' },
      },
      {
        path: 'schools',
        name: 'AdminSchools',
        component: () => import('@/views/admin/school/index.vue'),
        meta: { title: '学校管理', icon: 'School' },
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' },
      },
      {
        path: 'roles',
        name: 'AdminRoles',
        component: () => import('@/views/admin/role/index.vue'),
        meta: { title: '角色管理', icon: 'Lock' },
      },
      {
        path: 'dicts',
        name: 'AdminDicts',
        component: () => import('@/views/admin/dict/index.vue'),
        meta: { title: '字典管理', icon: 'Collection' },
      },
      {
        path: 'datasource',
        name: 'AdminDatasource',
        component: () => import('@/views/admin/datasource/index.vue'),
        meta: { title: '数据源管理', icon: 'Coin' },
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('@/views/admin/settings/index.vue'),
        meta: { title: '系统设置', icon: 'Setting' },
      },
    ],
  },
]

/** 教师路由 */
export const teacherRoutes: RouteRecordRaw[] = [
  {
    path: '/teacher',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, roles: ['teacher', 'TEACHER'] },
    children: [
      {
        path: 'dashboard',
        name: 'TeacherDashboard',
        component: () => import('@/views/teacher/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'Odometer' },
      },
      {
        path: 'questions',
        name: 'TeacherQuestions',
        component: () => import('@/views/teacher/question/index.vue'),
        meta: { title: '题库管理', icon: 'Document' },
      },
      {
        path: 'papers',
        name: 'TeacherPapers',
        component: () => import('@/views/teacher/paper/index.vue'),
        meta: { title: '试卷管理', icon: 'Notebook' },
      },
      {
        path: 'exams',
        name: 'TeacherExams',
        component: () => import('@/views/teacher/exam/index.vue'),
        meta: { title: '测评管理', icon: 'EditPen' },
      },
      {
        path: 'certificates',
        name: 'TeacherCertificates',
        component: () => import('@/views/teacher/certificate/index.vue'),
        meta: { title: '证书管理', icon: 'Postcard' },
      },
      {
        path: 'reports',
        name: 'TeacherReports',
        component: () => import('@/views/teacher/report/index.vue'),
        meta: { title: '学情报告', icon: 'DataAnalysis' },
      },
      {
        path: 'exams/:id/analysis',
        name: 'TeacherExamAnalysis',
        component: () => import('@/views/teacher/exam/analysis/index.vue'),
        meta: { title: '成绩分析', icon: 'DataLine', hidden: true },
      },
      {
        path: 'class/:classId/profile',
        name: 'TeacherClassProfile',
        component: () => import('@/views/teacher/class/profile/index.vue'),
        meta: { title: '班级学情', icon: 'UserFilled', hidden: true },
      },
    ],
  },
]

/** 家长路由 */
export const parentRoutes: RouteRecordRaw[] = [
  {
    path: '/parent',
    component: () => import('@/layouts/MobileLayout.vue'),
    meta: { requiresAuth: true, roles: ['parent', 'PARENT'] },
    children: [
      {
        path: 'home',
        name: 'ParentHome',
        component: () => import('@/views/parent/home/index.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'profile',
        name: 'ParentProfile',
        component: () => import('@/views/parent/profile/index.vue'),
        meta: { title: '我的' },
      },
      {
        path: 'reports',
        name: 'ParentReports',
        component: () => import('@/views/parent/report/index.vue'),
        meta: { title: '学情报告' },
      },
    ],
  },
]

/** 学生路由 */
export const studentRoutes: RouteRecordRaw[] = [
  {
    path: '/student',
    component: () => import('@/layouts/MobileLayout.vue'),
    meta: { requiresAuth: true, roles: ['student', 'STUDENT'] },
    children: [
      {
        path: 'home',
        name: 'StudentHome',
        component: () => import('@/views/student/home/index.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'exam/:examId',
        name: 'StudentExam',
        component: () => import('@/views/student/exam/index.vue'),
        meta: { title: '测评详情' },
      },
      {
        path: 'exam/:examId/taking',
        name: 'StudentExamTaking',
        component: () => import('@/views/student/exam/taking.vue'),
        meta: { title: '在线测评', hidden: true },
      },
      {
        path: 'scores',
        name: 'StudentScores',
        component: () => import('@/views/student/score/index.vue'),
        meta: { title: '成绩查询' },
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/profile/index.vue'),
        meta: { title: '我的' },
      },
      {
        path: 'certificates',
        name: 'StudentCertificates',
        component: () => import('@/views/student/certificate/index.vue'),
        meta: { title: '我的证书' },
      },
    ],
  },
]

/** 数据大屏路由 */
export const dashboardRoutes: RouteRecordRaw[] = [
  {
    path: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'school',
        name: 'DashboardSchool',
        component: () => import('@/views/dashboard/school/index.vue'),
        meta: { title: '学校数据大屏' },
      },
      {
        path: 'grade/:gradeId',
        name: 'DashboardGrade',
        component: () => import('@/views/dashboard/grade/index.vue'),
        meta: { title: '年级数据大屏' },
      },
      {
        path: 'class/:classId',
        name: 'DashboardClass',
        component: () => import('@/views/dashboard/class/index.vue'),
        meta: { title: '班级数据大屏' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...publicRoutes,
    ...adminRoutes,
    ...teacherRoutes,
    ...parentRoutes,
    ...studentRoutes,
    ...dashboardRoutes,
    { path: '/', redirect: '/login' },
    { path: '/:pathMatch(.*)*', redirect: '/404' },
  ],
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  NProgress.start()
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 学情数据统计分析平台`
  }

  const token = getToken()

  if (to.meta.requiresAuth !== false && !token) {
    // 需要登录但没有token，跳转登录
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && token) {
    // 已登录访问登录页，跳转到首页
    next({ path: '/admin/dashboard' })
  } else {
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
