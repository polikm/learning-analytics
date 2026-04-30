import { get } from './request'

/** 测评统计信息 */
export interface ExamStatistics {
  examId: number
  examName: string
  averageScore: number
  maxScore: number
  minScore: number
  passRate: number
  excellentRate: number
  totalStudents: number
  submittedStudents: number
  scoreDistribution: { range: string; count: number }[]
}

/** 题目分析 */
export interface QuestionAnalysis {
  questionId: number
  content: string
  type: string
  correctRate: number
  averageScore: number
  fullScoreRate: number
  zeroScoreRate: number
  difficulty: number
  knowledgePoint: string
}

/** 学生成绩趋势 */
export interface StudentTrend {
  examId: number
  examName: string
  score: number
  rank: number
  classRank: number
  averageScore: number
  date: string
}

/** 班级排名 */
export interface ClassRanking {
  studentId: number
  studentName: string
  studentNo: string
  score: number
  rank: number
  classRank: number
  gradeRank: number
}

/** 获取测评统计 */
export function getExamStatistics(examId: number) {
  return get<ExamStatistics>(`/scores/exam/${examId}/statistics`)
}

/** 获取题目分析 */
export function getQuestionAnalysis(examId: number) {
  return get<QuestionAnalysis[]>(`/scores/exam/${examId}/question-analysis`)
}

/** 获取学生成绩趋势 */
export function getStudentTrend(studentId: number, subjectId?: number) {
  return get<StudentTrend[]>(`/scores/student/${studentId}/trend`, { subjectId })
}

/** 获取班级排名 */
export function getClassRanking(classId: number, examId: number) {
  return get<ClassRanking[]>(`/scores/class/${classId}/exam/${examId}/ranking`)
}
