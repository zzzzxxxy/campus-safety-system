# 校园安全管理系统 - Agent 开发进度

## 项目概述
- 项目: 校园安全管理系统 (毕业设计)
- 后端: Spring Boot 3 + MyBatis-Plus + MySQL + Redis + JWT
- 前端: Vue 3 + Vite + Element Plus + Pinia + ECharts
- 项目路径: /home/softpapa/campus-safety-system/

## 当前状态
- 后端: 基本完成 (系统管理 + 访客 + 资产 + 设备 + 预警 + 报表)
- 前端: 仅有目录结构, 无实际代码文件
- 数据库: SQL初始化脚本已准备

## 已完成工作
| Session | Date | Feature IDs | Summary |
|---------|------|-------------|---------|
| init    | 2026-04-13 | F001-F004 | 后端全部模块已完成, 包含system/visitor/asset/warning/report |

## 下一步计划
- 优先级1: 前端项目脚手架 (F010-F015, F020, F022, F030-F032, F150, F152)
- 优先级2: 各管理页面CRUD (F050-F122, F140, F162)
- 优先级3: 增强功能 (F021, F033, F044, F080, F093, F123, F130-F133, F141-F142, F161, F163)
- 优先级4: 打磨 (F160)

## 技术约定
- 后端API统一前缀: /api
- 后端端口: 8080
- 前端端口: 5173 (Vite默认)
- 代理: /api -> http://localhost:8080
- 认证: JWT Bearer Token in Authorization header
- 分页参数: pageNum, pageSize
- 响应格式: { code: 200, msg: "success", data: ... }

## 已知问题
(无)

## 文件结构说明
```
campus-safety-system/
├── .agent/                    # Agent系统文件
│   ├── feature_list.json      # 功能清单 (JSON, 状态追踪)
│   ├── agent-progress.md      # 本文件 (进度日志)
│   ├── init.sh                # 环境初始化脚本
│   ├── initializer-prompt.md  # 初始化Agent提示词
│   ├── coding-agent-prompt.md # 编码Agent提示词
│   └── run-loop.sh            # 循环运行入口
├── backend/                   # Spring Boot后端
├── frontend/                  # Vue 3前端
├── sql/                       # SQL脚本
└── doc/                       # 文档
```
