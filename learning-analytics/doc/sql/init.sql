-- ============================================================
-- 学情数据统计分析平台 - 数据库初始化脚本
-- 数据库: PostgreSQL 16
-- ============================================================

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- ============================================================
-- 1. 组织架构 (4张表)
-- ============================================================

-- 1.1 租户表
CREATE TABLE IF NOT EXISTS tenant (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_name     VARCHAR(100)    NOT NULL                COMMENT '租户名称',
    contact_name    VARCHAR(50)     DEFAULT NULL            COMMENT '联系人姓名',
    contact_phone   VARCHAR(20)     DEFAULT NULL            COMMENT '联系电话',
    contact_email   VARCHAR(100)    DEFAULT NULL            COMMENT '联系邮箱',
    license_key     VARCHAR(200)    DEFAULT NULL            COMMENT '授权码',
    expire_time     TIMESTAMP       DEFAULT NULL            COMMENT '过期时间',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID(自身ID)',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tenant IS '租户表';
COMMENT ON COLUMN tenant.id IS '主键ID';
COMMENT ON COLUMN tenant.tenant_name IS '租户名称';
COMMENT ON COLUMN tenant.status IS '状态: 1-启用, 0-禁用';
COMMENT ON COLUMN tenant.tenant_id IS '租户ID(自身ID)';

-- 1.2 学校表
CREATE TABLE IF NOT EXISTS school (
    id              BIGSERIAL       PRIMARY KEY,
    school_name     VARCHAR(200)    NOT NULL                COMMENT '学校名称',
    school_code     VARCHAR(50)     DEFAULT NULL            COMMENT '学校编码',
    school_type     VARCHAR(20)     DEFAULT NULL            COMMENT '学校类型: primary/middle/high/university',
    province        VARCHAR(50)     DEFAULT NULL            COMMENT '省份',
    city            VARCHAR(50)     DEFAULT NULL            COMMENT '城市',
    district        VARCHAR(50)     DEFAULT NULL            COMMENT '区县',
    address         VARCHAR(300)    DEFAULT NULL            COMMENT '详细地址',
    principal_name  VARCHAR(50)     DEFAULT NULL            COMMENT '校长姓名',
    principal_phone VARCHAR(20)     DEFAULT NULL            COMMENT '校长电话',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE school IS '学校表';
COMMENT ON COLUMN school.school_type IS '学校类型: primary/middle/high/university';

-- 1.3 年级表
CREATE TABLE IF NOT EXISTS grade (
    id              BIGSERIAL       PRIMARY KEY,
    school_id       BIGINT          NOT NULL                COMMENT '学校ID',
    grade_name      VARCHAR(100)    NOT NULL                COMMENT '年级名称',
    grade_code      VARCHAR(30)     DEFAULT NULL            COMMENT '年级编码',
    grade_level     SMALLINT        DEFAULT NULL            COMMENT '年级级别(数字越大年级越高)',
    enrollment_year INTEGER         DEFAULT NULL            COMMENT '入学年份',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE grade IS '年级表';

-- 1.4 班级表
CREATE TABLE IF NOT EXISTS class (
    id              BIGSERIAL       PRIMARY KEY,
    school_id       BIGINT          NOT NULL                COMMENT '学校ID',
    grade_id        BIGINT          NOT NULL                COMMENT '年级ID',
    class_name      VARCHAR(100)    NOT NULL                COMMENT '班级名称',
    class_code      VARCHAR(30)     DEFAULT NULL            COMMENT '班级编码',
    teacher_id      BIGINT          DEFAULT NULL            COMMENT '班主任教师ID',
    classroom       VARCHAR(100)    DEFAULT NULL            COMMENT '教室',
    max_students    INTEGER         DEFAULT NULL            COMMENT '最大学生数',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE class IS '班级表';

-- ============================================================
-- 2. 用户权限 (7张表)
-- ============================================================

-- 2.1 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGSERIAL       PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL                COMMENT '用户名',
    password        VARCHAR(200)    NOT NULL                COMMENT '密码(加密)',
    real_name       VARCHAR(50)     DEFAULT NULL            COMMENT '真实姓名',
    avatar          VARCHAR(500)    DEFAULT NULL            COMMENT '头像URL',
    email           VARCHAR(100)    DEFAULT NULL            COMMENT '邮箱',
    phone           VARCHAR(20)     DEFAULT NULL            COMMENT '手机号',
    user_type       VARCHAR(20)     NOT NULL DEFAULT 'admin' COMMENT '用户类型: admin/teacher/student/parent',
    school_id       BIGINT          DEFAULT NULL            COMMENT '所属学校ID',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    last_login_time TIMESTAMP       DEFAULT NULL            COMMENT '最后登录时间',
    last_login_ip   VARCHAR(50)     DEFAULT NULL            COMMENT '最后登录IP',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_user_username UNIQUE (username, tenant_id)
);

COMMENT ON TABLE sys_user IS '系统用户表';
COMMENT ON COLUMN sys_user.user_type IS '用户类型: admin/teacher/student/parent';

-- 2.2 系统角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id              BIGSERIAL       PRIMARY KEY,
    role_name       VARCHAR(100)    NOT NULL                COMMENT '角色名称',
    role_code       VARCHAR(50)     NOT NULL                COMMENT '角色编码',
    data_scope      SMALLINT        DEFAULT 1               COMMENT '数据范围: 1-全部, 2-本学校, 3-本年级, 4-本班级, 5-本人',
    sort_order      INTEGER         DEFAULT 0               COMMENT '排序',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_role_code UNIQUE (role_code, tenant_id)
);

COMMENT ON TABLE sys_role IS '系统角色表';

-- 2.3 系统权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id              BIGSERIAL       PRIMARY KEY,
    parent_id       BIGINT          DEFAULT 0               COMMENT '父权限ID',
    permission_name VARCHAR(100)    NOT NULL                COMMENT '权限名称',
    permission_code VARCHAR(100)    DEFAULT NULL            COMMENT '权限编码',
    permission_type SMALLINT        NOT NULL                COMMENT '类型: 1-菜单, 2-按钮, 3-接口',
    path            VARCHAR(200)    DEFAULT NULL            COMMENT '路由路径',
    component       VARCHAR(200)    DEFAULT NULL            COMMENT '组件路径',
    icon            VARCHAR(100)    DEFAULT NULL            COMMENT '图标',
    sort_order      INTEGER         DEFAULT 0               COMMENT '排序',
    visible         SMALLINT        DEFAULT 1               COMMENT '是否可见: 1-是, 0-否',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_permission IS '系统权限表';
COMMENT ON COLUMN sys_permission.permission_type IS '类型: 1-菜单, 2-按钮, 3-接口';

-- 2.4 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL                COMMENT '用户ID',
    role_id         BIGINT          NOT NULL                COMMENT '角色ID',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';

-- 2.5 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id              BIGSERIAL       PRIMARY KEY,
    role_id         BIGINT          NOT NULL                COMMENT '角色ID',
    permission_id   BIGINT          NOT NULL                COMMENT '权限ID',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_role_permission IS '角色权限关联表';

-- 2.6 教师任课分配表
CREATE TABLE IF NOT EXISTS teacher_assignment (
    id              BIGSERIAL       PRIMARY KEY,
    teacher_id      BIGINT          NOT NULL                COMMENT '教师用户ID',
    school_id       BIGINT          NOT NULL                COMMENT '学校ID',
    grade_id        BIGINT          DEFAULT NULL            COMMENT '年级ID',
    class_id        BIGINT          DEFAULT NULL            COMMENT '班级ID',
    subject_id      BIGINT          NOT NULL                COMMENT '学科ID',
    academic_year   VARCHAR(20)     DEFAULT NULL            COMMENT '学年(如2025-2026)',
    semester        VARCHAR(10)     DEFAULT NULL            COMMENT '学期: first/second',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE teacher_assignment IS '教师任课分配表';

-- 2.7 家长学生关联表
CREATE TABLE IF NOT EXISTS parent_student (
    id              BIGSERIAL       PRIMARY KEY,
    parent_id       BIGINT          NOT NULL                COMMENT '家长用户ID',
    student_id      BIGINT          NOT NULL                COMMENT '学生用户ID',
    relation        VARCHAR(20)     DEFAULT NULL            COMMENT '关系: father/mother/guardian/other',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE parent_student IS '家长学生关联表';

-- ============================================================
-- 3. 学生信息 (1张表)
-- ============================================================

-- 3.1 学生档案表
CREATE TABLE IF NOT EXISTS student_profile (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL                COMMENT '关联用户ID',
    student_no      VARCHAR(50)     DEFAULT NULL            COMMENT '学号',
    school_id       BIGINT          NOT NULL                COMMENT '学校ID',
    grade_id        BIGINT          NOT NULL                COMMENT '年级ID',
    class_id        BIGINT          NOT NULL                COMMENT '班级ID',
    gender          SMALLINT        DEFAULT NULL            COMMENT '性别: 1-男, 2-女',
    birth_date      DATE            DEFAULT NULL            COMMENT '出生日期',
    admission_date  DATE            DEFAULT NULL            COMMENT '入学日期',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-在读, 0-休学, -1-退学',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE student_profile IS '学生档案表';

-- ============================================================
-- 4. 题库试卷 (5张表)
-- ============================================================

-- 4.1 学科表
CREATE TABLE IF NOT EXISTS subject (
    id              BIGSERIAL       PRIMARY KEY,
    subject_name    VARCHAR(100)    NOT NULL                COMMENT '学科名称',
    subject_code    VARCHAR(30)     DEFAULT NULL            COMMENT '学科编码',
    icon            VARCHAR(200)    DEFAULT NULL            COMMENT '图标',
    sort_order      INTEGER         DEFAULT 0               COMMENT '排序',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE subject IS '学科表';

-- 4.2 知识点表
CREATE TABLE IF NOT EXISTS knowledge_point (
    id              BIGSERIAL       PRIMARY KEY,
    parent_id       BIGINT          DEFAULT 0               COMMENT '父知识点ID',
    subject_id      BIGINT          NOT NULL                COMMENT '学科ID',
    point_name      VARCHAR(200)    NOT NULL                COMMENT '知识点名称',
    point_code      VARCHAR(100)    DEFAULT NULL            COMMENT '知识点编码',
    difficulty      SMALLINT        DEFAULT 1               COMMENT '难度: 1-简单, 2-中等, 3-困难',
    sort_order      INTEGER         DEFAULT 0               COMMENT '排序',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE knowledge_point IS '知识点表';

-- 4.3 题目表
CREATE TABLE IF NOT EXISTS question (
    id              BIGSERIAL       PRIMARY KEY,
    question_type   SMALLINT        NOT NULL                COMMENT '题型: 1-单选, 2-多选, 3-判断, 4-填空, 5-简答',
    subject_id      BIGINT          NOT NULL                COMMENT '学科ID',
    knowledge_ids   VARCHAR(500)    DEFAULT NULL            COMMENT '关联知识点ID(逗号分隔)',
    content         TEXT            NOT NULL                COMMENT '题目内容',
    options         TEXT            DEFAULT NULL            COMMENT '选项(JSON格式)',
    answer          TEXT            NOT NULL                COMMENT '正确答案',
    analysis        TEXT            DEFAULT NULL            COMMENT '解析',
    difficulty      SMALLINT        DEFAULT 1               COMMENT '难度: 1-简单, 2-中等, 3-困难',
    score           NUMERIC(6,2)    DEFAULT NULL            COMMENT '分值',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE question IS '题目表';
COMMENT ON COLUMN question.question_type IS '题型: 1-单选, 2-多选, 3-判断, 4-填空, 5-简答';

-- 4.4 试卷表
CREATE TABLE IF NOT EXISTS paper (
    id              BIGSERIAL       PRIMARY KEY,
    paper_name      VARCHAR(200)    NOT NULL                COMMENT '试卷名称',
    subject_id      BIGINT          NOT NULL                COMMENT '学科ID',
    total_score     NUMERIC(6,2)    NOT NULL DEFAULT 100    COMMENT '总分',
    pass_score      NUMERIC(6,2)    DEFAULT 60              COMMENT '及格分',
    duration        INTEGER         DEFAULT NULL            COMMENT '考试时长(分钟)',
    difficulty      SMALLINT        DEFAULT 1               COMMENT '难度: 1-简单, 2-中等, 3-困难',
    paper_type      SMALLINT        DEFAULT 1               COMMENT '试卷类型: 1-正式考试, 2-练习, 3-作业',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE paper IS '试卷表';

-- 4.5 试卷题目关联表
CREATE TABLE IF NOT EXISTS paper_question (
    id              BIGSERIAL       PRIMARY KEY,
    paper_id        BIGINT          NOT NULL                COMMENT '试卷ID',
    question_id     BIGINT          NOT NULL                COMMENT '题目ID',
    question_score  NUMERIC(6,2)    NOT NULL                COMMENT '题目分值',
    sort_order      INTEGER         DEFAULT 0               COMMENT '排序',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE paper_question IS '试卷题目关联表';

-- ============================================================
-- 5. 测评 (3张表)
-- ============================================================

-- 5.1 考试表
CREATE TABLE IF NOT EXISTS exam (
    id              BIGSERIAL       PRIMARY KEY,
    exam_name       VARCHAR(200)    NOT NULL                COMMENT '考试名称',
    paper_id        BIGINT          NOT NULL                COMMENT '试卷ID',
    subject_id      BIGINT          NOT NULL                COMMENT '学科ID',
    school_id       BIGINT          NOT NULL                COMMENT '学校ID',
    grade_id        BIGINT          DEFAULT NULL            COMMENT '年级ID',
    class_ids       VARCHAR(500)    DEFAULT NULL            COMMENT '参考班级ID(逗号分隔)',
    exam_type       SMALLINT        DEFAULT 1               COMMENT '考试类型: 1-期中, 2-期末, 3-月考, 4-随堂, 5-模拟',
    start_time      TIMESTAMP       DEFAULT NULL            COMMENT '开始时间',
    end_time        TIMESTAMP       DEFAULT NULL            COMMENT '结束时间',
    duration        INTEGER         DEFAULT NULL            COMMENT '考试时长(分钟)',
    creator_id      BIGINT          DEFAULT NULL            COMMENT '创建人ID',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 0-未开始, 1-进行中, 2-已结束, -1-已取消',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE exam IS '考试表';
COMMENT ON COLUMN exam.exam_type IS '考试类型: 1-期中, 2-期末, 3-月考, 4-随堂, 5-模拟';

-- 5.2 考试记录表
CREATE TABLE IF NOT EXISTS exam_record (
    id              BIGSERIAL       PRIMARY KEY,
    exam_id         BIGINT          NOT NULL                COMMENT '考试ID',
    student_id      BIGINT          NOT NULL                COMMENT '学生用户ID',
    paper_id        BIGINT          NOT NULL                COMMENT '试卷ID',
    total_score     NUMERIC(6,2)    DEFAULT NULL            COMMENT '总分',
    correct_count   INTEGER         DEFAULT 0               COMMENT '正确题数',
    wrong_count     INTEGER         DEFAULT 0               COMMENT '错误题数',
    unanswered_count INTEGER        DEFAULT 0               COMMENT '未答题数',
    duration        INTEGER         DEFAULT NULL            COMMENT '答题时长(秒)',
    submit_time     TIMESTAMP       DEFAULT NULL            COMMENT '提交时间',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 0-未提交, 1-已提交, 2-已批改',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE exam_record IS '考试记录表';

-- 5.3 考试答题明细表
CREATE TABLE IF NOT EXISTS exam_answer (
    id              BIGSERIAL       PRIMARY KEY,
    record_id       BIGINT          NOT NULL                COMMENT '考试记录ID',
    exam_id         BIGINT          NOT NULL                COMMENT '考试ID',
    question_id     BIGINT          NOT NULL                COMMENT '题目ID',
    student_answer  TEXT            DEFAULT NULL            COMMENT '学生答案',
    is_correct      SMALLINT        DEFAULT NULL            COMMENT '是否正确: 1-正确, 0-错误, NULL-未批改',
    score           NUMERIC(6,2)    DEFAULT NULL            COMMENT '得分',
    duration        INTEGER         DEFAULT NULL            COMMENT '答题时长(秒)',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE exam_answer IS '考试答题明细表';

-- ============================================================
-- 6. 证书 (1张表)
-- ============================================================

-- 6.1 证书表
CREATE TABLE IF NOT EXISTS certificate (
    id              BIGSERIAL       PRIMARY KEY,
    student_id      BIGINT          NOT NULL                COMMENT '学生用户ID',
    certificate_name VARCHAR(200)   NOT NULL                COMMENT '证书名称',
    certificate_no  VARCHAR(100)    DEFAULT NULL            COMMENT '证书编号',
    certificate_type SMALLINT       DEFAULT 1               COMMENT '证书类型: 1-学科竞赛, 2-技能认证, 3-荣誉证书',
    issue_org       VARCHAR(200)    DEFAULT NULL            COMMENT '颁发机构',
    issue_date      DATE            DEFAULT NULL            COMMENT '颁发日期',
    expire_date     DATE            DEFAULT NULL            COMMENT '过期日期',
    file_url        VARCHAR(500)    DEFAULT NULL            COMMENT '证书文件URL',
    description     TEXT            DEFAULT NULL            COMMENT '证书描述',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-有效, 0-无效',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE certificate IS '证书表';

-- ============================================================
-- 7. 学习数据接入 (2张表)
-- ============================================================

-- 7.1 学习行为表
CREATE TABLE IF NOT EXISTS learning_behavior (
    id              BIGSERIAL       PRIMARY KEY,
    student_id      BIGINT          NOT NULL                COMMENT '学生用户ID',
    subject_id      BIGINT          DEFAULT NULL            COMMENT '学科ID',
    behavior_type   VARCHAR(50)     NOT NULL                COMMENT '行为类型: login/logout/study/practice/exam/video/interaction',
    behavior_detail TEXT            DEFAULT NULL            COMMENT '行为详情(JSON)',
    duration        INTEGER         DEFAULT NULL            COMMENT '持续时间(秒)',
    source          VARCHAR(50)     DEFAULT NULL            COMMENT '来源: web/mobile/api',
    ip_address      VARCHAR(50)     DEFAULT NULL            COMMENT 'IP地址',
    user_agent      VARCHAR(500)    DEFAULT NULL            COMMENT '用户代理',
    behavior_time   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE learning_behavior IS '学习行为表';
COMMENT ON COLUMN learning_behavior.behavior_type IS '行为类型: login/logout/study/practice/exam/video/interaction';

-- 7.2 数据源表
CREATE TABLE IF NOT EXISTS data_source (
    id              BIGSERIAL       PRIMARY KEY,
    source_name     VARCHAR(200)    NOT NULL                COMMENT '数据源名称',
    source_type     VARCHAR(50)     NOT NULL                COMMENT '数据源类型: database/file/api/other',
    connection_info TEXT            DEFAULT NULL            COMMENT '连接信息(JSON,加密存储)',
    sync_frequency  VARCHAR(50)     DEFAULT NULL            COMMENT '同步频率: realtime/hourly/daily/weekly/manual',
    last_sync_time  TIMESTAMP       DEFAULT NULL            COMMENT '最后同步时间',
    sync_status     SMALLINT        DEFAULT 1               COMMENT '同步状态: 1-正常, 0-异常, -1-未配置',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE data_source IS '数据源表';

-- ============================================================
-- 8. 学情报告 (3张表)
-- ============================================================

-- 8.1 学生档案快照表
CREATE TABLE IF NOT EXISTS student_profile_snapshot (
    id              BIGSERIAL       PRIMARY KEY,
    student_id      BIGINT          NOT NULL                COMMENT '学生用户ID',
    snapshot_type   VARCHAR(50)     NOT NULL                COMMENT '快照类型: monthly/semester/yearly/custom',
    snapshot_date   DATE            NOT NULL                COMMENT '快照日期',
    total_score     NUMERIC(8,2)    DEFAULT NULL            COMMENT '综合得分',
    class_rank      INTEGER         DEFAULT NULL            COMMENT '班级排名',
    grade_rank      INTEGER         DEFAULT NULL            COMMENT '年级排名',
    class_size      INTEGER         DEFAULT NULL            COMMENT '班级总人数',
    grade_size      INTEGER         DEFAULT NULL            COMMENT '年级总人数',
    attendance_rate NUMERIC(5,2)    DEFAULT NULL            COMMENT '出勤率(%)',
    homework_rate   NUMERIC(5,2)    DEFAULT NULL            COMMENT '作业完成率(%)',
    avg_study_time  INTEGER         DEFAULT NULL            COMMENT '平均学习时长(分钟/天)',
    profile_data    JSONB           DEFAULT NULL            COMMENT '档案快照数据(JSON)',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE student_profile_snapshot IS '学生档案快照表';

-- 8.2 报告表
CREATE TABLE IF NOT EXISTS report (
    id              BIGSERIAL       PRIMARY KEY,
    report_name     VARCHAR(200)    NOT NULL                COMMENT '报告名称',
    report_type     VARCHAR(50)     NOT NULL                COMMENT '报告类型: student/class/grade/school/subject',
    template_id     BIGINT          DEFAULT NULL            COMMENT '报告模板ID',
    target_id       BIGINT          DEFAULT NULL            COMMENT '目标ID(学生/班级/年级/学校ID)',
    subject_id      BIGINT          DEFAULT NULL            COMMENT '学科ID(学科报告时)',
    period_start    DATE            DEFAULT NULL            COMMENT '统计开始日期',
    period_end      DATE            DEFAULT NULL            COMMENT '统计结束日期',
    report_data     JSONB           DEFAULT NULL            COMMENT '报告数据(JSON)',
    file_url        VARCHAR(500)    DEFAULT NULL            COMMENT '报告文件URL(PDF等)',
    creator_id      BIGINT          DEFAULT NULL            COMMENT '创建人ID',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 0-生成中, 1-已完成, -1-生成失败',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE report IS '报告表';

-- 8.3 报告模板表
CREATE TABLE IF NOT EXISTS report_template (
    id              BIGSERIAL       PRIMARY KEY,
    template_name   VARCHAR(200)    NOT NULL                COMMENT '模板名称',
    template_code   VARCHAR(50)     DEFAULT NULL            COMMENT '模板编码',
    report_type     VARCHAR(50)     NOT NULL                COMMENT '报告类型: student/class/grade/school/subject',
    template_config JSONB           DEFAULT NULL            COMMENT '模板配置(JSON,含图表、指标等)',
    cover_image     VARCHAR(500)    DEFAULT NULL            COMMENT '封面图片URL',
    description     TEXT            DEFAULT NULL            COMMENT '模板描述',
    sort_order      INTEGER         DEFAULT 0               COMMENT '排序',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE report_template IS '报告模板表';

-- ============================================================
-- 9. 系统管理 (4张表)
-- ============================================================

-- 9.1 审计日志表
CREATE TABLE IF NOT EXISTS sys_audit_log (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          DEFAULT NULL            COMMENT '操作用户ID',
    username        VARCHAR(50)     DEFAULT NULL            COMMENT '操作用户名',
    operation       VARCHAR(200)    NOT NULL                COMMENT '操作描述',
    method          VARCHAR(200)    DEFAULT NULL            COMMENT '请求方法',
    request_url     VARCHAR(500)    DEFAULT NULL            COMMENT '请求URL',
    request_method  VARCHAR(10)     DEFAULT NULL            COMMENT 'HTTP方法(GET/POST/PUT/DELETE)',
    request_params  TEXT            DEFAULT NULL            COMMENT '请求参数',
    response_data   TEXT            DEFAULT NULL            COMMENT '响应数据',
    ip_address      VARCHAR(50)     DEFAULT NULL            COMMENT 'IP地址',
    user_agent      VARCHAR(500)    DEFAULT NULL            COMMENT '用户代理',
    duration        INTEGER         DEFAULT NULL            COMMENT '执行时长(毫秒)',
    status          SMALLINT        DEFAULT 1               COMMENT '状态: 1-成功, 0-失败',
    error_msg       TEXT            DEFAULT NULL            COMMENT '错误信息',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_audit_log IS '审计日志表';

-- 9.2 字典表
CREATE TABLE IF NOT EXISTS sys_dict (
    id              BIGSERIAL       PRIMARY KEY,
    dict_name       VARCHAR(100)    NOT NULL                COMMENT '字典名称',
    dict_code       VARCHAR(50)     NOT NULL                COMMENT '字典编码',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_dict_code UNIQUE (dict_code, tenant_id)
);

COMMENT ON TABLE sys_dict IS '字典表';

-- 9.3 字典项表
CREATE TABLE IF NOT EXISTS sys_dict_item (
    id              BIGSERIAL       PRIMARY KEY,
    dict_id         BIGINT          NOT NULL                COMMENT '字典ID',
    item_label      VARCHAR(200)    NOT NULL                COMMENT '字典项标签',
    item_value      VARCHAR(200)    NOT NULL                COMMENT '字典项值',
    item_code       VARCHAR(100)    DEFAULT NULL            COMMENT '字典项编码',
    sort_order      INTEGER         DEFAULT 0               COMMENT '排序',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-启用, 0-禁用',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_dict_item IS '字典项表';

-- 9.4 系统消息表
CREATE TABLE IF NOT EXISTS sys_message (
    id              BIGSERIAL       PRIMARY KEY,
    sender_id       BIGINT          DEFAULT NULL            COMMENT '发送人ID',
    receiver_id     BIGINT          NOT NULL                COMMENT '接收人ID',
    message_type    VARCHAR(30)     NOT NULL                COMMENT '消息类型: system/notification/warning/reminder',
    title           VARCHAR(200)    NOT NULL                COMMENT '消息标题',
    content         TEXT            NOT NULL                COMMENT '消息内容',
    is_read         SMALLINT        DEFAULT 0               COMMENT '是否已读: 1-已读, 0-未读',
    read_time       TIMESTAMP       DEFAULT NULL            COMMENT '阅读时间',
    extra_data      JSONB           DEFAULT NULL            COMMENT '扩展数据(JSON)',
    status          SMALLINT        NOT NULL DEFAULT 1      COMMENT '状态: 1-正常, 0-删除',
    remark          VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    tenant_id       BIGINT          NOT NULL DEFAULT 0      COMMENT '租户ID',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_message IS '系统消息表';
COMMENT ON COLUMN sys_message.message_type IS '消息类型: system/notification/warning/reminder';

-- ============================================================
-- 创建索引
-- ============================================================

-- 组织架构索引
CREATE INDEX idx_school_tenant_id ON school(tenant_id);
CREATE INDEX idx_grade_school_id ON grade(school_id);
CREATE INDEX idx_class_school_id ON class(school_id);
CREATE INDEX idx_class_grade_id ON class(grade_id);

-- 用户权限索引
CREATE INDEX idx_sys_user_tenant_id ON sys_user(tenant_id);
CREATE INDEX idx_sys_user_user_type ON sys_user(user_type);
CREATE INDEX idx_sys_user_school_id ON sys_user(school_id);
CREATE INDEX idx_sys_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_sys_user_role_role_id ON sys_user_role(role_id);
CREATE INDEX idx_sys_role_permission_role_id ON sys_role_permission(role_id);
CREATE INDEX idx_teacher_assignment_teacher_id ON teacher_assignment(teacher_id);
CREATE INDEX idx_teacher_assignment_class_id ON teacher_assignment(class_id);
CREATE INDEX idx_parent_student_parent_id ON parent_student(parent_id);
CREATE INDEX idx_parent_student_student_id ON parent_student(student_id);

-- 学生信息索引
CREATE INDEX idx_student_profile_user_id ON student_profile(user_id);
CREATE INDEX idx_student_profile_class_id ON student_profile(class_id);
CREATE INDEX idx_student_profile_school_id ON student_profile(school_id);

-- 题库试卷索引
CREATE INDEX idx_knowledge_point_subject_id ON knowledge_point(subject_id);
CREATE INDEX idx_question_subject_id ON question(subject_id);
CREATE INDEX idx_paper_subject_id ON paper(subject_id);
CREATE INDEX idx_paper_question_paper_id ON paper_question(paper_id);
CREATE INDEX idx_paper_question_question_id ON paper_question(question_id);

-- 测评索引
CREATE INDEX idx_exam_subject_id ON exam(subject_id);
CREATE INDEX idx_exam_school_id ON exam(school_id);
CREATE INDEX idx_exam_record_exam_id ON exam_record(exam_id);
CREATE INDEX idx_exam_record_student_id ON exam_record(student_id);
CREATE INDEX idx_exam_answer_record_id ON exam_answer(record_id);
CREATE INDEX idx_exam_answer_question_id ON exam_answer(question_id);

-- 学习数据索引
CREATE INDEX idx_learning_behavior_student_id ON learning_behavior(student_id);
CREATE INDEX idx_learning_behavior_behavior_time ON learning_behavior(behavior_time);
CREATE INDEX idx_learning_behavior_behavior_type ON learning_behavior(behavior_type);

-- 学情报告索引
CREATE INDEX idx_student_profile_snapshot_student_id ON student_profile_snapshot(student_id);
CREATE INDEX idx_report_target_id ON report(target_id);
CREATE INDEX idx_report_report_type ON report(report_type);

-- 系统管理索引
CREATE INDEX idx_sys_audit_log_user_id ON sys_audit_log(user_id);
CREATE INDEX idx_sys_audit_log_created_at ON sys_audit_log(created_at);
CREATE INDEX idx_sys_dict_item_dict_id ON sys_dict_item(dict_id);
CREATE INDEX idx_sys_message_receiver_id ON sys_message(receiver_id);
CREATE INDEX idx_sys_message_is_read ON sys_message(is_read);
