-- ============================================================
-- 校园安防管理系统 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS campus_safety DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE campus_safety;

-- ============================================================
-- 1. 系统用户表
-- ============================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(64)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(64),
    phone       VARCHAR(20),
    email       VARCHAR(128),
    avatar      VARCHAR(255),
    user_type   TINYINT      DEFAULT 0 COMMENT '0管理员1教师2学生3安保',
    status      TINYINT      DEFAULT 0 COMMENT '0正常1禁用',
    create_by   VARCHAR(64),
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ============================================================
-- 2. 系统角色表
-- ============================================================
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(64)  NOT NULL,
    role_key    VARCHAR(64)  NOT NULL,
    sort        INT          DEFAULT 0,
    status      TINYINT      DEFAULT 0,
    remark      VARCHAR(255),
    create_by   VARCHAR(64),
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- ============================================================
-- 3. 系统菜单表
-- ============================================================
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    menu_name   VARCHAR(64)  NOT NULL,
    parent_id   BIGINT       DEFAULT 0,
    order_num   INT          DEFAULT 0,
    path        VARCHAR(200),
    component   VARCHAR(255),
    menu_type   CHAR(1)      COMMENT 'M目录 C菜单 F按钮',
    perms       VARCHAR(100),
    icon        VARCHAR(100),
    visible     TINYINT      DEFAULT 0 COMMENT '0显示1隐藏',
    status      TINYINT      DEFAULT 0,
    create_by   VARCHAR(64),
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

-- ============================================================
-- 4. 用户角色关联表
-- ============================================================
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ============================================================
-- 5. 角色菜单关联表
-- ============================================================
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ============================================================
-- 6. 操作日志表
-- ============================================================
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id             BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    module         VARCHAR(64),
    description    VARCHAR(255),
    method         VARCHAR(255),
    request_method VARCHAR(10),
    oper_url       VARCHAR(500),
    oper_ip        VARCHAR(50),
    oper_param     TEXT,
    json_result    TEXT,
    status         TINYINT      DEFAULT 0 COMMENT '0正常1异常',
    error_msg      TEXT,
    oper_time      DATETIME,
    oper_user      VARCHAR(64)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================================
-- 7. 访客记录表
-- ============================================================
DROP TABLE IF EXISTS visitor_record;
CREATE TABLE visitor_record (
    id                BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    visitor_name      VARCHAR(64),
    visitor_phone     VARCHAR(20),
    id_card           VARCHAR(20),
    reason            VARCHAR(255),
    visitee           VARCHAR(64),
    department        VARCHAR(64),
    visit_time        DATETIME,
    leave_time        DATETIME,
    actual_visit_time DATETIME,
    actual_leave_time DATETIME,
    status            TINYINT      DEFAULT 0 COMMENT '0待审批1已通过2已拒绝',
    remark            VARCHAR(500),
    create_by         VARCHAR(64),
    create_time       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_by         VARCHAR(64),
    update_time       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted           TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客记录表';

-- ============================================================
-- 8. 设备信息表
-- ============================================================
DROP TABLE IF EXISTS device_info;
CREATE TABLE device_info (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    device_name VARCHAR(64),
    device_code VARCHAR(64),
    device_type VARCHAR(64),
    brand       VARCHAR(64),
    model       VARCHAR(64),
    location    VARCHAR(255),
    ip_address  VARCHAR(50),
    status      TINYINT      DEFAULT 0 COMMENT '0正常1故障2维修3报废',
    online      TINYINT      DEFAULT 0 COMMENT '0离线1在线',
    remark      VARCHAR(500),
    create_by   VARCHAR(64),
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64),
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';

-- ============================================================
-- 9. 资产信息表
-- ============================================================
DROP TABLE IF EXISTS asset_info;
CREATE TABLE asset_info (
    id            BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    asset_name    VARCHAR(64),
    asset_code    VARCHAR(64),
    asset_type    VARCHAR(64),
    asset_value   DECIMAL(12,2),
    purchase_date DATE,
    location      VARCHAR(255),
    department    VARCHAR(64),
    responsible   VARCHAR(64),
    status        TINYINT       DEFAULT 0 COMMENT '0在用1闲置2报废3维修',
    remark        VARCHAR(500),
    create_by     VARCHAR(64),
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP,
    update_by     VARCHAR(64),
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT       DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产信息表';

-- ============================================================
-- 10. 预警记录表
-- ============================================================
DROP TABLE IF EXISTS warning_record;
CREATE TABLE warning_record (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(128),
    warning_type  VARCHAR(64),
    warning_level VARCHAR(20),
    content       TEXT,
    source        VARCHAR(64),
    location      VARCHAR(255),
    status        TINYINT      DEFAULT 0 COMMENT '0未处理1处理中2已处理3已关闭',
    handler       VARCHAR(64),
    handle_time   DATETIME,
    handle_result TEXT,
    remark        VARCHAR(500),
    create_by     VARCHAR(64),
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_by     VARCHAR(64),
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警记录表';

-- ============================================================
-- 11. 预警规则表
-- ============================================================
DROP TABLE IF EXISTS warning_rule;
CREATE TABLE warning_rule (
    id             BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    rule_name      VARCHAR(64),
    warning_type   INT,
    warning_level  INT,
    rule_condition TEXT,
    enabled        TINYINT      DEFAULT 0 COMMENT '0启用1禁用',
    remark         VARCHAR(500),
    create_by      VARCHAR(64),
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_by      VARCHAR(64),
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted        TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则表';

-- ============================================================
-- 初始数据插入
-- ============================================================

-- -----------------------------------------------------------
-- 用户数据
-- -----------------------------------------------------------
INSERT INTO sys_user (id, username, password, nickname, phone, email, user_type, status, create_by) VALUES
(1, 'admin',    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', '13800000000', 'admin@campus.edu.cn',    0, 0, 'system'),
(2, 'zhangsan', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三',       '13800000001', 'zhangsan@campus.edu.cn', 1, 0, 'admin'),
(3, 'lisi',     '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四',       '13800000002', 'lisi@campus.edu.cn',     2, 0, 'admin');

-- -----------------------------------------------------------
-- 角色数据
-- -----------------------------------------------------------
INSERT INTO sys_role (id, role_name, role_key, sort, status, remark, create_by) VALUES
(1, '超级管理员', 'admin', 1, 0, '拥有系统全部权限', 'system'),
(2, '普通用户',   'user',  2, 0, '普通用户权限',     'system');

-- -----------------------------------------------------------
-- 菜单数据
-- -----------------------------------------------------------
-- 一级目录 (menu_type = M)
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(1,  '系统管理', 0, 1, 'system',  NULL, 'M', NULL, 'setting',   0, 0, 'system'),
(2,  '访客管理', 0, 2, 'visitor', NULL, 'M', NULL, 'user',      0, 0, 'system'),
(3,  '设备管理', 0, 3, 'device',  NULL, 'M', NULL, 'monitor',   0, 0, 'system'),
(4,  '资产管理', 0, 4, 'asset',   NULL, 'M', NULL, 'component', 0, 0, 'system'),
(5,  '预警管理', 0, 5, 'warning', NULL, 'M', NULL, 'alert',     0, 0, 'system'),
(6,  '数据报表', 0, 6, 'report',  NULL, 'M', NULL, 'chart',     0, 0, 'system');

-- 系统管理 -> 二级菜单 (menu_type = C)
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(101, '用户管理', 1, 1, 'user', 'system/user/index', 'C', 'system:user:list', 'peoples',  0, 0, 'system'),
(102, '角色管理', 1, 2, 'role', 'system/role/index', 'C', 'system:role:list', 'peoples',  0, 0, 'system'),
(103, '菜单管理', 1, 3, 'menu', 'system/menu/index', 'C', 'system:menu:list', 'tree',     0, 0, 'system'),
(104, '日志管理', 1, 4, 'log',  'system/log/index',  'C', 'system:log:list',  'document', 0, 0, 'system');

-- 访客管理 -> 二级菜单
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(201, '访客记录', 2, 1, 'record', 'visitor/record/index', 'C', 'visitor:record:list', 'list', 0, 0, 'system');

-- 设备管理 -> 二级菜单
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(301, '设备列表', 3, 1, 'list', 'device/list/index', 'C', 'asset:device:list', 'server', 0, 0, 'system');

-- 资产管理 -> 二级菜单
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(401, '资产列表', 4, 1, 'list', 'asset/list/index', 'C', 'asset:asset:list', 'component', 0, 0, 'system');

-- 预警管理 -> 二级菜单
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(501, '预警记录', 5, 1, 'record', 'warning/record/index', 'C', 'warning:record:list', 'alert',    0, 0, 'system'),
(502, '预警规则', 5, 2, 'rule',   'warning/rule/index',   'C', 'warning:rule:list',   'edit',     0, 0, 'system');

-- 数据报表 -> 二级菜单
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(601, '数据看板', 6, 1, 'dashboard', 'report/dashboard/index', 'C', 'report:dashboard:view', 'chart', 0, 0, 'system');

-- 按钮权限 (menu_type = F)
-- 用户管理按钮
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(1011, '用户新增', 101, 1, '', NULL, 'F', 'system:user:add',    NULL, 0, 0, 'system'),
(1012, '用户修改', 101, 2, '', NULL, 'F', 'system:user:edit',   NULL, 0, 0, 'system'),
(1013, '用户删除', 101, 3, '', NULL, 'F', 'system:user:delete', NULL, 0, 0, 'system');

-- 角色管理按钮
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(1021, '角色新增', 102, 1, '', NULL, 'F', 'system:role:add',    NULL, 0, 0, 'system'),
(1022, '角色修改', 102, 2, '', NULL, 'F', 'system:role:edit',   NULL, 0, 0, 'system'),
(1023, '角色删除', 102, 3, '', NULL, 'F', 'system:role:delete', NULL, 0, 0, 'system');

-- 菜单管理按钮
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(1031, '菜单新增', 103, 1, '', NULL, 'F', 'system:menu:add',    NULL, 0, 0, 'system'),
(1032, '菜单修改', 103, 2, '', NULL, 'F', 'system:menu:edit',   NULL, 0, 0, 'system'),
(1033, '菜单删除', 103, 3, '', NULL, 'F', 'system:menu:delete', NULL, 0, 0, 'system');

-- 访客管理按钮
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(2011, '访客新增', 201, 1, '', NULL, 'F', 'visitor:record:add',    NULL, 0, 0, 'system'),
(2012, '访客修改', 201, 2, '', NULL, 'F', 'visitor:record:edit',   NULL, 0, 0, 'system'),
(2013, '访客删除', 201, 3, '', NULL, 'F', 'visitor:record:delete', NULL, 0, 0, 'system'),
(2014, '访客审批', 201, 4, '', NULL, 'F', 'visitor:record:audit',  NULL, 0, 0, 'system');

-- 设备管理按钮
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(3011, '设备新增', 301, 1, '', NULL, 'F', 'asset:device:add',    NULL, 0, 0, 'system'),
(3012, '设备修改', 301, 2, '', NULL, 'F', 'asset:device:edit',   NULL, 0, 0, 'system'),
(3013, '设备删除', 301, 3, '', NULL, 'F', 'asset:device:delete', NULL, 0, 0, 'system');

-- 资产管理按钮
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(4011, '资产新增', 401, 1, '', NULL, 'F', 'asset:asset:add',    NULL, 0, 0, 'system'),
(4012, '资产修改', 401, 2, '', NULL, 'F', 'asset:asset:edit',   NULL, 0, 0, 'system'),
(4013, '资产删除', 401, 3, '', NULL, 'F', 'asset:asset:delete', NULL, 0, 0, 'system');

-- 预警记录按钮
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(5011, '预警新增', 501, 1, '', NULL, 'F', 'warning:record:add',    NULL, 0, 0, 'system'),
(5012, '预警修改', 501, 2, '', NULL, 'F', 'warning:record:edit',   NULL, 0, 0, 'system'),
(5013, '预警删除', 501, 3, '', NULL, 'F', 'warning:record:delete', NULL, 0, 0, 'system'),
(5014, '预警处理', 501, 4, '', NULL, 'F', 'warning:record:handle', NULL, 0, 0, 'system');

-- 预警规则按钮
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, create_by) VALUES
(5021, '规则新增', 502, 1, '', NULL, 'F', 'warning:rule:add',    NULL, 0, 0, 'system'),
(5022, '规则修改', 502, 2, '', NULL, 'F', 'warning:rule:edit',   NULL, 0, 0, 'system'),
(5023, '规则删除', 502, 3, '', NULL, 'F', 'warning:rule:delete', NULL, 0, 0, 'system');

-- -----------------------------------------------------------
-- 用户角色关联
-- -----------------------------------------------------------
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 2);

-- -----------------------------------------------------------
-- 角色菜单关联
-- -----------------------------------------------------------
-- admin角色关联全部菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 101),
(1, 102),
(1, 103),
(1, 104),
(1, 201),
(1, 301),
(1, 401),
(1, 501),
(1, 502),
(1, 601),
(1, 1011),
(1, 1012),
(1, 1013),
(1, 1021),
(1, 1022),
(1, 1023),
(1, 1031),
(1, 1032),
(1, 1033),
(1, 2011),
(1, 2012),
(1, 2013),
(1, 2014),
(1, 3011),
(1, 3012),
(1, 3013),
(1, 4011),
(1, 4012),
(1, 4013),
(1, 5011),
(1, 5012),
(1, 5013),
(1, 5014),
(1, 5021),
(1, 5022),
(1, 5023);

-- user角色关联除系统管理外的菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 2),
(2, 3),
(2, 4),
(2, 5),
(2, 6),
(2, 201),
(2, 301),
(2, 401),
(2, 501),
(2, 502),
(2, 601),
(2, 2011),
(2, 2012),
(2, 2013),
(2, 2014),
(2, 3011),
(2, 3012),
(2, 3013),
(2, 4011),
(2, 4012),
(2, 4013),
(2, 5011),
(2, 5012),
(2, 5013),
(2, 5014),
(2, 5021),
(2, 5022),
(2, 5023);

-- -----------------------------------------------------------
-- 测试数据：访客记录
-- -----------------------------------------------------------
INSERT INTO visitor_record (id, visitor_name, visitor_phone, id_card, reason, visitee, department, visit_time, leave_time, status, remark, create_by) VALUES
(1, '王五',   '13900001111', '110101199001011234', '学术交流',   '张三', '计算机学院', '2025-04-10 09:00:00', '2025-04-10 17:00:00', 1, '已预约', 'admin'),
(2, '赵六',   '13900002222', '110101199502025678', '设备维修',   '李四', '后勤保障部', '2025-04-11 14:00:00', '2025-04-11 18:00:00', 0, '等待审批', 'admin');

-- -----------------------------------------------------------
-- 测试数据：设备信息
-- -----------------------------------------------------------
INSERT INTO device_info (id, device_name, device_code, device_type, brand, model, location, ip_address, status, online, remark, create_by) VALUES
(1, '东门高清摄像头',   'CAM-EAST-001',  '摄像头',   '海康威视', 'DS-2CD2T47', '东门入口',       '192.168.1.101', 0, 1, '2024年安装', 'admin'),
(2, '图书馆门禁',       'ACC-LIB-001',   '门禁设备', '中控智慧', 'ZKTeco-F22', '图书馆正门',     '192.168.1.102', 0, 1, '2024年安装', 'admin'),
(3, '操场烟感报警器',   'SMOKE-PLAY-001','烟感报警', '霍尼韦尔', 'HW-SD119',   '操场器材室',     '192.168.1.103', 0, 0, '待上线',     'admin');

-- -----------------------------------------------------------
-- 测试数据：资产信息
-- -----------------------------------------------------------
INSERT INTO asset_info (id, asset_name, asset_code, asset_type, asset_value, purchase_date, location, department, responsible, status, remark, create_by) VALUES
(1, '联想台式电脑',   'IT-PC-20240001',  '电子设备', 5500.00, '2024-03-15', '行政楼301', '信息中心', '张三', 0, '日常办公用', 'admin'),
(2, '投影仪',         'IT-PJ-20240002',  '教学设备', 8900.00, '2024-06-20', '教学楼A201','教务处',   '李四', 0, '教室使用',   'admin');

-- -----------------------------------------------------------
-- 测试数据：预警记录
-- -----------------------------------------------------------
INSERT INTO warning_record (id, title, warning_type, warning_level, content, source, location, status, handler, handle_time, handle_result, remark, create_by) VALUES
(1, '东门围墙异常攀爬', '周界入侵', '高',   '东门围墙区域检测到异常攀爬行为', '视频分析', '东门围墙',   2, 'admin', '2025-04-09 22:35:00', '已派安保人员现场处置，确认为学生翻墙，已批评教育', NULL, 'system'),
(2, '图书馆烟雾报警',   '消防报警', '紧急', '图书馆三楼烟雾传感器触发报警',   '烟感设备', '图书馆三楼', 0, NULL,    NULL,                  NULL,                                                 NULL, 'system');

-- -----------------------------------------------------------
-- 测试数据：预警规则
-- -----------------------------------------------------------
INSERT INTO warning_rule (id, rule_name, warning_type, warning_level, rule_condition, enabled, remark, create_by) VALUES
(1, '周界入侵检测规则', 1, 3, '{"deviceType":"摄像头","triggerCondition":"移动侦测","threshold":0.8,"duration":5}',  0, '当摄像头检测到围墙区域异常移动时触发高级预警', 'admin'),
(2, '消防烟雾报警规则', 2, 4, '{"deviceType":"烟感报警","triggerCondition":"烟雾浓度","threshold":0.5,"duration":3}', 0, '当烟感设备检测到烟雾浓度超标时触发紧急预警',   'admin');
