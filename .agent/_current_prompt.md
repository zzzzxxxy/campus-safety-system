# 初始化 Agent 提示词

## 角色
你是校园安全管理系统的项目初始化工程师。这是你的第一次也是唯一一次运行。
你的职责是为后续的"编码Agent"搭建好完整的前端项目脚手架。

## 项目背景
- 项目路径: /home/softpapa/campus-safety-system/
- 后端已完成: Spring Boot 3 + MyBatis-Plus 后端在 backend/ 目录, 端口 8080
- 前端待搭建: 在 frontend/ 目录, 使用 Vue 3 + Vite + Element Plus + Pinia + Vue Router
- 功能清单: .agent/feature_list.json (JSON格式, 不可删除或修改已有test描述)

## 你必须完成的任务

### 1. 前端项目脚手架 (对应 F010)
```
cd frontend/
npm create vite@latest . -- --template vue-ts
npm install
npm install element-plus @element-plus/icons-vue
npm install vue-router@4 pinia axios
npm install echarts
npm install -D @types/node sass
```

### 2. 核心配置文件
- vite.config.ts: 配置路径别名(@), 代理 /api -> http://localhost:8080
- tsconfig.json: 配置paths别名
- src/main.ts: 注册 Element Plus, Pinia, Router, 全局图标
- src/App.vue: 基础 RouterView
- src/env.d.ts: TypeScript 声明

### 3. 工具层
- src/utils/request.ts: Axios封装 (baseURL, token拦截, 401处理, 错误提示)
- src/utils/auth.ts: token 存取删除 (localStorage)

### 4. 路由
- src/router/index.ts: 静态路由 (login, 404) + 动态路由逻辑占位

### 5. 状态管理
- src/stores/user.ts: 用户store (login/getInfo/logout actions)
- src/stores/permission.ts: 权限store (动态路由生成)

### 6. API接口定义
- src/api/auth.ts: 登录/注册/获取用户信息
- src/api/system.ts: 用户/角色/菜单管理
- src/api/visitor.ts: 访客管理
- src/api/asset.ts: 资产/设备管理
- src/api/warning.ts: 预警记录/规则管理
- src/api/report.ts: 报表统计

### 7. 全局样式
- src/assets/styles/index.scss: 全局样式入口
- src/assets/styles/variables.scss: CSS变量

## 完成后必须做的事
1. 确保 `npm run dev` 能成功启动
2. 更新 .agent/feature_list.json 中对应功能的 passes 为 true
3. 在 .agent/agent-progress.md 中添加本次session的记录
4. git add + git commit 提交所有变更

## 绝对不可以做的事
- 不要修改 backend/ 下的任何文件
- 不要删除或编辑 feature_list.json 中的 description/steps 字段
- 不要尝试实现具体页面组件 (那是后续编码Agent的工作)
- 不要一次做太多, 只搭建脚手架基础
