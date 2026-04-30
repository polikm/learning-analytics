-- ============================================================
-- 学情数据统计分析平台 - 演示数据初始化脚本
-- 数据库: PostgreSQL 16
-- 说明: 本脚本用于插入演示数据，请在执行 init.sql 之后运行
-- ============================================================

SET client_encoding = 'UTF8';

-- ============================================================
-- 1. 租户数据
-- ============================================================
INSERT INTO tenant (id, tenant_name, contact_name, contact_phone, status, tenant_id)
VALUES (1, '示范学校', '张管理', '13800138000', 1, 1);

-- ============================================================
-- 2. 学校数据
-- ============================================================
INSERT INTO school (id, school_name, school_code, school_type, province, city, district, status, tenant_id)
VALUES (1, '第一中学', 'SCH001', 'high', '广东省', '深圳市', '南山区', 1, 1);

-- ============================================================
-- 3. 年级数据
-- ============================================================
INSERT INTO grade (id, school_id, grade_name, grade_level, enrollment_year, status, tenant_id) VALUES
(1, 1, '高一', 10, 2025, 1, 1),
(2, 1, '高二', 11, 2024, 1, 1),
(3, 1, '高三', 12, 2023, 1, 1);

-- ============================================================
-- 4. 班级数据
-- ============================================================
INSERT INTO class (id, school_id, grade_id, class_name, class_code, status, tenant_id) VALUES
(1, 1, 1, '高一1班', 'G10-01', 1, 1),
(2, 1, 1, '高一2班', 'G10-02', 1, 1),
(3, 1, 2, '高二1班', 'G11-01', 1, 1),
(4, 1, 2, '高二2班', 'G11-02', 1, 1),
(5, 1, 3, '高三1班', 'G12-01', 1, 1),
(6, 1, 3, '高三2班', 'G12-02', 1, 1);

-- ============================================================
-- 5. 学科数据
-- ============================================================
INSERT INTO subject (id, subject_name, subject_code, sort_order, status, tenant_id) VALUES
(1, '语文', 'CHINESE', 1, 1, 1),
(2, '数学', 'MATH', 2, 1, 1),
(3, '英语', 'ENGLISH', 3, 1, 1),
(4, '物理', 'PHYSICS', 4, 1, 1);

-- ============================================================
-- 6. 知识点数据
-- ============================================================
-- 语文知识点
INSERT INTO knowledge_point (id, parent_id, subject_id, point_name, point_code, difficulty, sort_order, status, tenant_id) VALUES
(1, 0, 1, '基础知识', 'CH-BASIC', 1, 1, 1, 1),
(2, 0, 1, '阅读理解', 'CH-READING', 2, 2, 1, 1),
(3, 0, 1, '写作', 'CH-WRITING', 2, 3, 1, 1);

-- 数学知识点
INSERT INTO knowledge_point (id, parent_id, subject_id, point_name, point_code, difficulty, sort_order, status, tenant_id) VALUES
(4, 0, 2, '函数', 'MATH-FUNC', 2, 1, 1, 1),
(5, 0, 2, '几何', 'MATH-GEO', 2, 2, 1, 1),
(6, 0, 2, '概率统计', 'MATH-PROB', 3, 3, 1, 1);

-- 英语知识点
INSERT INTO knowledge_point (id, parent_id, subject_id, point_name, point_code, difficulty, sort_order, status, tenant_id) VALUES
(7, 0, 3, '词汇语法', 'EN-GRAMMAR', 1, 1, 1, 1),
(8, 0, 3, '阅读', 'EN-READING', 2, 2, 1, 1),
(9, 0, 3, '写作', 'EN-WRITING', 2, 3, 1, 1);

-- 物理知识点
INSERT INTO knowledge_point (id, parent_id, subject_id, point_name, point_code, difficulty, sort_order, status, tenant_id) VALUES
(10, 0, 4, '力学', 'PH-MECH', 2, 1, 1, 1),
(11, 0, 4, '电磁学', 'PH-ELEC', 3, 2, 1, 1);

-- ============================================================
-- 7. 用户数据
-- ============================================================
-- 密码说明: 所有用户密码统一为 '123456'
-- BCrypt加密值: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH
-- 注意: 此BCrypt值为示例，实际部署时请使用应用层生成正确的BCrypt哈希值
INSERT INTO sys_user (id, username, password, real_name, user_type, school_id, status, tenant_id) VALUES
(1,  'admin',    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin',   1, 1, 1),
(2,  'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王老师',     'teacher', 1, 1, 1),
(3,  'teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李老师',     'teacher', 1, 1, 1),
(4,  'parent1',  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张家长',     'parent',  1, 1, 1),
(5,  'parent2',  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '刘家长',     'parent',  1, 1, 1),
(6,  'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三',       'student', 1, 1, 1),
(7,  'student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四',       'student', 1, 1, 1),
(8,  'student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王五',       'student', 1, 1, 1),
(9,  'student4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵六',       'student', 1, 1, 1),
(10, 'student5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '钱七',       'student', 1, 1, 1);

-- ============================================================
-- 8. 角色数据
-- ============================================================
INSERT INTO sys_role (id, role_name, role_code, data_scope, sort_order, status, tenant_id) VALUES
(1, '超级管理员', 'SUPER_ADMIN',     1, 1, 1, 1),
(2, '学校管理员', 'SCHOOL_ADMIN',    2, 2, 1, 1),
(3, '教师',       'TEACHER',         4, 3, 1, 1),
(4, '家长/学生',  'PARENT_STUDENT',  5, 4, 1, 1);

-- ============================================================
-- 9. 用户角色关联
-- ============================================================
INSERT INTO sys_user_role (id, user_id, role_id, tenant_id) VALUES
(1,  1,  1, 1),   -- admin -> SUPER_ADMIN
(2,  2,  3, 1),   -- teacher1 -> TEACHER
(3,  3,  3, 1),   -- teacher2 -> TEACHER
(4,  4,  4, 1),   -- parent1 -> PARENT_STUDENT
(5,  5,  4, 1),   -- parent2 -> PARENT_STUDENT
(6,  6,  4, 1),   -- student1 -> PARENT_STUDENT
(7,  7,  4, 1),   -- student2 -> PARENT_STUDENT
(8,  8,  4, 1),   -- student3 -> PARENT_STUDENT
(9,  9,  4, 1),   -- student4 -> PARENT_STUDENT
(10, 10,  4, 1);  -- student5 -> PARENT_STUDENT

-- ============================================================
-- 10. 教师任课分配
-- ============================================================
-- teacher1(王老师) -> 数学, 班级[1,2,3]
INSERT INTO teacher_assignment (id, teacher_id, school_id, grade_id, class_id, subject_id, academic_year, semester, status, tenant_id) VALUES
(1, 2, 1, 1, 1, 2, '2025-2026', 'first', 1, 1),
(2, 2, 1, 1, 2, 2, '2025-2026', 'first', 1, 1),
(3, 2, 1, 2, 3, 2, '2025-2026', 'first', 1, 1);

-- teacher2(李老师) -> 语文, 班级[1,2]
INSERT INTO teacher_assignment (id, teacher_id, school_id, grade_id, class_id, subject_id, academic_year, semester, status, tenant_id) VALUES
(4, 3, 1, 1, 1, 1, '2025-2026', 'first', 1, 1),
(5, 3, 1, 1, 2, 1, '2025-2026', 'first', 1, 1);

-- ============================================================
-- 11. 家长学生关联
-- ============================================================
INSERT INTO parent_student (id, parent_id, student_id, relation, status, tenant_id) VALUES
(1, 4, 6, 'father', 1, 1),   -- parent1 -> student1(父亲)
(2, 5, 7, 'mother', 1, 1);   -- parent2 -> student2(母亲)

-- ============================================================
-- 12. 学生档案
-- ============================================================
INSERT INTO student_profile (id, user_id, student_no, school_id, grade_id, class_id, gender, admission_date, status, tenant_id) VALUES
(1, 6,  '2025010001', 1, 1, 1, 1, '2025-09-01', 1, 1),  -- 张三 -> 高一1班
(2, 7,  '2025010002', 1, 1, 2, 2, '2025-09-01', 1, 1),  -- 李四 -> 高一2班
(3, 8,  '2025010003', 1, 1, 1, 1, '2025-09-01', 1, 1),  -- 王五 -> 高一1班
(4, 9,  '2025010004', 1, 2, 3, 2, '2024-09-01', 1, 1),  -- 赵六 -> 高二1班
(5, 10, '2025010005', 1, 1, 2, 1, '2025-09-01', 1, 1);  -- 钱七 -> 高一2班

-- ============================================================
-- 13. 题目数据（数学学科）
-- ============================================================
-- 5道单选题 (question_type=1)
INSERT INTO question (id, question_type, subject_id, knowledge_ids, content, options, answer, analysis, difficulty, score, status, tenant_id) VALUES
(1, 1, 2, '4',
 '已知函数 f(x) = x^2 - 2x + 1，则 f(0) 的值为：',
 '[{"label":"A","content":"0"},{"label":"B","content":"1"},{"label":"C","content":"-1"},{"label":"D","content":"2"}]',
 'B',
 'f(0) = 0^2 - 2*0 + 1 = 1',
 1, 5.00, 1, 1),

(2, 1, 2, '4',
 '函数 y = 2^x 的定义域是：',
 '[{"label":"A","content":"x > 0"},{"label":"B","content":"x ≥ 0"},{"label":"C","content":"全体实数"},{"label":"D","content":"x ≠ 0"}]',
 'C',
 '指数函数 y = a^x (a>0, a≠1) 的定义域为全体实数',
 1, 5.00, 1, 1),

(3, 1, 2, '5',
 '在直角三角形中，两直角边分别为3和4，则斜边长为：',
 '[{"label":"A","content":"5"},{"label":"B","content":"6"},{"label":"C","content":"7"},{"label":"D","content":"12"}]',
 'A',
 '由勾股定理 c = √(3² + 4²) = √25 = 5',
 2, 8.00, 1, 1),

(4, 1, 2, '4',
 '函数 f(x) = ln(x) 的定义域是：',
 '[{"label":"A","content":"x > 0"},{"label":"B","content":"x ≥ 0"},{"label":"C","content":"x ≠ 0"},{"label":"D","content":"全体实数"}]',
 'A',
 '对数函数 ln(x) 要求真数 x > 0',
 2, 8.00, 1, 1),

(5, 1, 2, '6',
 '从1到10中随机取一个整数，取到偶数的概率是：',
 '[{"label":"A","content":"1/5"},{"label":"B","content":"1/2"},{"label":"C","content":"2/5"},{"label":"D","content":"3/5"}]',
 'B',
 '1到10中偶数为2,4,6,8,10共5个，概率为5/10=1/2',
 3, 10.00, 1, 1),

-- 3道多选题 (question_type=2)
(6, 2, 2, '4',
 '下列函数中，在区间 (0, +∞) 上单调递增的有：',
 '[{"label":"A","content":"y = x"},{"label":"B","content":"y = -x"},{"label":"C","content":"y = 2^x"},{"label":"D","content":"y = ln(x)"}]',
 'ACD',
 'y=x、y=2^x、y=ln(x) 在(0,+∞)上均单调递增，y=-x单调递减',
 2, 10.00, 1, 1),

(7, 2, 2, '5',
 '下列命题中，正确的有：',
 '[{"label":"A","content":"两条平行线被第三条直线所截，同位角相等"},{"label":"B","content":"三角形内角和为180°"},{"label":"C","content":"矩形一定是平行四边形"},{"label":"D","content":"菱形对角线互相垂直且相等"}]',
 'ABC',
 'D选项菱形对角线互相垂直但不一定相等（正方形才相等）',
 3, 10.00, 1, 1),

(8, 2, 2, '6',
 '下列事件中，属于随机事件的有：',
 '[{"label":"A","content":"抛一枚硬币正面朝上"},{"label":"B","content":"太阳从东方升起"},{"label":"C","content":"明天会下雨"},{"label":"D","content":"买彩票中奖"}]',
 'ACD',
 'B为必然事件，ACD均为随机事件',
 4, 12.00, 1, 1),

-- 2道判断题 (question_type=3)
(9, 3, 2, '4',
 '函数 y = x^2 在整个定义域上单调递增。',
 NULL,
 '0',
 'y = x^2 在(-∞, 0)上单调递减，在(0, +∞)上单调递增，不是在整个定义域上单调递增',
 1, 8.00, 1, 1),

(10, 3, 2, '5',
 '任意三角形的外角等于与之不相邻的两个内角之和。',
 NULL,
 '1',
 '这是三角形外角定理，任意三角形均成立',
 2, 8.00, 1, 1);

-- ============================================================
-- 14. 试卷数据
-- ============================================================
INSERT INTO paper (id, paper_name, subject_id, total_score, pass_score, duration, difficulty, paper_type, status, tenant_id)
VALUES (1, '高一数学期中测试', 2, 100.00, 60.00, 90, 2, 1, 1, 1);

-- ============================================================
-- 15. 试卷题目关联
-- ============================================================
INSERT INTO paper_question (id, paper_id, question_id, question_score, sort_order, tenant_id) VALUES
(1,  1, 1,  5.00,  1, 1),
(2,  1, 2,  5.00,  2, 1),
(3,  1, 3,  8.00,  3, 1),
(4,  1, 4,  8.00,  4, 1),
(5,  1, 5,  10.00, 5, 1),
(6,  1, 6,  10.00, 6, 1),
(7,  1, 7,  10.00, 7, 1),
(8,  1, 8,  12.00, 8, 1),
(9,  1, 9,  8.00,  9, 1),
(10, 1, 10, 8.00,  10, 1);

-- ============================================================
-- 16. 测评数据
-- ============================================================
INSERT INTO exam (id, exam_name, paper_id, subject_id, school_id, grade_id, class_ids, exam_type, start_time, end_time, duration, creator_id, status, tenant_id)
VALUES (1, '2025-2026学年第一学期期中考试', 1, 2, 1, 1, '1,2', 1,
        '2025-10-15 09:00:00', '2025-10-15 10:30:00', 90, 2, 2, 1);

-- ============================================================
-- 17. 测评记录和答题数据
-- ============================================================

-- ---------- student1 (张三) - 得分 85 ----------
INSERT INTO exam_record (id, exam_id, student_id, paper_id, total_score, correct_count, wrong_count, unanswered_count, duration, submit_time, status, tenant_id)
VALUES (1, 1, 6, 1, 85.00, 8, 2, 0, 4800, '2025-10-15 10:20:00', 2, 1);

INSERT INTO exam_answer (id, record_id, exam_id, question_id, student_answer, is_correct, score, duration, tenant_id) VALUES
(1,   1, 1, 1,  'B', 1, 5.00,   30, 1),
(2,   1, 1, 2,  'C', 1, 5.00,   25, 1),
(3,   1, 1, 3,  'A', 1, 8.00,   60, 1),
(4,   1, 1, 4,  'A', 1, 8.00,   45, 1),
(5,   1, 1, 5,  'B', 1, 10.00,  90, 1),
(6,   1, 1, 6,  'ACD', 1, 10.00, 120, 1),
(7,   1, 1, 7,  'ABCD', 0, 0.00, 150, 1),   -- 错误：多选了D
(8,   1, 1, 8,  'ACD', 1, 12.00, 180, 1),
(9,   1, 1, 9,  '0', 1, 8.00,   40, 1),
(10,  1, 1, 10, '1', 1, 8.00,   35, 1);

-- ---------- student2 (李四) - 得分 92 ----------
INSERT INTO exam_record (id, exam_id, student_id, paper_id, total_score, correct_count, wrong_count, unanswered_count, duration, submit_time, status, tenant_id)
VALUES (2, 1, 7, 1, 92.00, 9, 1, 0, 4200, '2025-10-15 10:10:00', 2, 1);

INSERT INTO exam_answer (id, record_id, exam_id, question_id, student_answer, is_correct, score, duration, tenant_id) VALUES
(11,  2, 1, 1,  'B', 1, 5.00,   20, 1),
(12,  2, 1, 2,  'C', 1, 5.00,   20, 1),
(13,  2, 1, 3,  'A', 1, 8.00,   50, 1),
(14,  2, 1, 4,  'A', 1, 8.00,   40, 1),
(15,  2, 1, 5,  'B', 1, 10.00,  80, 1),
(16,  2, 1, 6,  'ACD', 1, 10.00, 100, 1),
(17,  2, 1, 7,  'ABC', 1, 10.00, 120, 1),
(18,  2, 1, 8,  'ACD', 1, 12.00, 150, 1),
(19,  2, 1, 9,  '1', 0, 0.00,   30, 1),   -- 错误：判断为正确
(20,  2, 1, 10, '1', 1, 8.00,   25, 1);

-- ---------- student3 (王五) - 得分 67 ----------
INSERT INTO exam_record (id, exam_id, student_id, paper_id, total_score, correct_count, wrong_count, unanswered_count, duration, submit_time, status, tenant_id)
VALUES (3, 1, 8, 1, 67.00, 6, 4, 0, 5100, '2025-10-15 10:25:00', 2, 1);

INSERT INTO exam_answer (id, record_id, exam_id, question_id, student_answer, is_correct, score, duration, tenant_id) VALUES
(21,  3, 1, 1,  'B', 1, 5.00,   30, 1),
(22,  3, 1, 2,  'C', 1, 5.00,   30, 1),
(23,  3, 1, 3,  'B', 0, 0.00,   60, 1),   -- 错误
(24,  3, 1, 4,  'B', 0, 0.00,   50, 1),   -- 错误
(25,  3, 1, 5,  'A', 0, 0.00,   90, 1),   -- 错误
(26,  3, 1, 6,  'ACD', 1, 10.00, 120, 1),
(27,  3, 1, 7,  'ABC', 1, 10.00, 130, 1),
(28,  3, 1, 8,  'AC', 0, 0.00,  150, 1),   -- 错误：少选了D
(29,  3, 1, 9,  '0', 1, 8.00,   40, 1),
(30,  3, 1, 10, '1', 1, 8.00,   35, 1);

-- ---------- student4 (赵六) - 得分 78 ----------
INSERT INTO exam_record (id, exam_id, student_id, paper_id, total_score, correct_count, wrong_count, unanswered_count, duration, submit_time, status, tenant_id)
VALUES (4, 1, 9, 1, 78.00, 7, 3, 0, 4500, '2025-10-15 10:15:00', 2, 1);

INSERT INTO exam_answer (id, record_id, exam_id, question_id, student_answer, is_correct, score, duration, tenant_id) VALUES
(31,  4, 1, 1,  'B', 1, 5.00,   25, 1),
(32,  4, 1, 2,  'C', 1, 5.00,   25, 1),
(33,  4, 1, 3,  'A', 1, 8.00,   55, 1),
(34,  4, 1, 4,  'A', 1, 8.00,   45, 1),
(35,  4, 1, 5,  'C', 0, 0.00,   80, 1),   -- 错误
(36,  4, 1, 6,  'AC', 0, 0.00,  110, 1),   -- 错误：少选了D
(37,  4, 1, 7,  'ABC', 1, 10.00, 120, 1),
(38,  4, 1, 8,  'ACD', 1, 12.00, 140, 1),
(39,  4, 1, 9,  '0', 1, 8.00,   35, 1),
(40,  4, 1, 10, '0', 0, 0.00,   30, 1);   -- 错误

-- ---------- student5 (钱七) - 得分 95 ----------
INSERT INTO exam_record (id, exam_id, student_id, paper_id, total_score, correct_count, wrong_count, unanswered_count, duration, submit_time, status, tenant_id)
VALUES (5, 1, 10, 1, 95.00, 9, 1, 0, 3900, '2025-10-15 10:05:00', 2, 1);

INSERT INTO exam_answer (id, record_id, exam_id, question_id, student_answer, is_correct, score, duration, tenant_id) VALUES
(41,  5, 1, 1,  'B', 1, 5.00,   20, 1),
(42,  5, 1, 2,  'C', 1, 5.00,   20, 1),
(43,  5, 1, 3,  'A', 1, 8.00,   40, 1),
(44,  5, 1, 4,  'A', 1, 8.00,   35, 1),
(45,  5, 1, 5,  'B', 1, 10.00,  70, 1),
(46,  5, 1, 6,  'ACD', 1, 10.00, 100, 1),
(47,  5, 1, 7,  'ABC', 1, 10.00, 110, 1),
(48,  5, 1, 8,  'ACD', 1, 12.00, 140, 1),
(49,  5, 1, 9,  '0', 1, 8.00,   25, 1),
(50,  5, 1, 10, '1', 1, 8.00,   20, 1);

-- ============================================================
-- 18. 证书数据
-- ============================================================
INSERT INTO certificate (id, student_id, certificate_name, certificate_no, certificate_type, issue_org, issue_date, description, status, tenant_id) VALUES
(1, 6,  '全国数学竞赛省级一等奖', 'MATH-2025-GD-001', 1, '中国数学学会', '2025-06-15', '2025年全国高中数学联赛广东省赛区一等奖', 1, 1),
(2, 7,  '全国英语竞赛市级二等奖', 'ENG-2025-SZ-002',  1, '深圳市教育局',   '2025-05-20', '2025年全国中学生英语能力竞赛深圳市二等奖', 1, 1),
(3, 8,  '科技创新大赛校级一等奖', 'TECH-2025-SCH01',  2, '第一中学',       '2025-04-10', '第一中学2025年科技创新大赛一等奖', 1, 1);

-- ============================================================
-- 19. 数据字典
-- ============================================================
INSERT INTO sys_dict (id, dict_name, dict_code, status, remark, tenant_id) VALUES
(1, '用户类型', 'user_type',    1, '系统用户类型字典', 1),
(2, '考试状态', 'exam_status',  1, '考试状态字典',     1);

-- ============================================================
-- 20. 字典项
-- ============================================================
-- user_type 字典项
INSERT INTO sys_dict_item (id, dict_id, item_label, item_value, item_code, sort_order, status, tenant_id) VALUES
(1, 1, '管理员', 'admin',   'ADMIN',   1, 1, 1),
(2, 1, '教师',   'teacher', 'TEACHER', 2, 1, 1),
(3, 1, '家长',   'parent',  'PARENT',  3, 1, 1),
(4, 1, '学生',   'student', 'STUDENT', 4, 1, 1);

-- exam_status 字典项
INSERT INTO sys_dict_item (id, dict_id, item_label, item_value, item_code, sort_order, status, tenant_id) VALUES
(5, 2, '未开始', '0',  'NOT_STARTED', 1, 1, 1),
(6, 2, '进行中', '1',  'IN_PROGRESS', 2, 1, 1),
(7, 2, '已结束', '2',  'FINISHED',    3, 1, 1),
(8, 2, '已阅卷', '3',  'GRADED',      4, 1, 1);
