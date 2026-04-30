import { get } from './request'

/** 学生学情画像 */
export interface StudentProfile {
  studentId: number
  studentName: string
  studentNo: string
  className: string
  gender: number
  avatar?: string
  totalExams: number
  averageScore: number
  bestSubject: string
  worstSubject: string
  rankTrend: 'up' | 'down' | 'stable'
  recentExams: {
    examId: number
    examName: string
    subject: string
    score: number
    totalScore: number
    rank: number
    date: string
  }[]
}

/** 班级概览 */
export interface ClassOverview {
  classId: number
  className: string
  gradeName: string
  teacherName: string
  studentCount: number
  averageScore: number
  passRate: number
  excellentRate: number
  subjectAverages: { subject: string; average: number }[]
  topStudents: { studentId: number; name: string; score: number }[]
  warningStudents: { studentId: number; name: string; type: string; detail: string }[]
}

/** 年级概览 */
export interface GradeOverview {
  gradeId: number
  gradeName: string
  classCount: number
  studentCount: number
  averageScore: number
  passRate: number
  excellentRate: number
  classRanking: { classId: number; className: string; averageScore: number }[]
  subjectAverages: { subject: string; average: number }[]
}

/** 知识点掌握度 */
export interface KnowledgeMastery {
  knowledgePointId: number
  knowledgePointName: string
  subject: string
  masteryLevel: number
  questionCount: number
  correctCount: number
  correctRate: number
  lastPracticeDate: string
}

/** 预警信息 */
export interface WarningItem {
  id: number
  studentId: number
  studentName: string
  className: string
  type: 'score_decline' | 'absence' | 'fail' | 'inactivity'
  level: 'low' | 'medium' | 'high'
  message: string
  createdAt: string
}

/** 获取学生学情画像 */
export function getStudentProfile(studentId: number) {
  return get<StudentProfile>(`/profile/student/${studentId}`)
}

/** 获取班级概览 */
export function getClassOverview(classId: number) {
  return get<ClassOverview>(`/profile/class/${classId}`)
}

/** 获取年级概览 */
export function getGradeOverview(gradeId: number) {
  return get<GradeOverview>(`/profile/grade/${gradeId}`)
}

/** 获取知识点掌握度 */
export function getKnowledgeMastery(studentId: number) {
  return get<KnowledgeMastery[]>(`/profile/student/${studentId}/knowledge-mastery`)
}

/** 获取预警列表 */
export function getWarnings(studentId: number) {
  return get<WarningItem[]>(`/profile/student/${studentId}/warnings`)
}
