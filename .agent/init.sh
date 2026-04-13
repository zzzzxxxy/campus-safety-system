#!/bin/bash
# ============================================================
# 校园安全管理系统 - 开发环境初始化脚本
# 每个Agent session开始时执行此脚本
# ============================================================

set -e
PROJECT_DIR="/home/softpapa/campus-safety-system"
cd "$PROJECT_DIR"

echo "============================================"
echo "  校园安全管理系统 - 环境初始化"
echo "  $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================"

# 1. 检查后端是否运行
echo ""
echo "[1/5] 检查后端服务..."
BACKEND_PID=$(lsof -ti:8080 2>/dev/null || true)
if [ -n "$BACKEND_PID" ]; then
    echo "  ✓ 后端已在运行 (PID: $BACKEND_PID, port 8080)"
else
    echo "  ✗ 后端未运行"
    echo "  尝试启动后端..."
    if [ -f backend/pom.xml ]; then
        cd backend
        nohup mvn spring-boot:run -q > /tmp/campus-backend.log 2>&1 &
        echo "  → 后端启动中, 日志: /tmp/campus-backend.log"
        echo "  → 等待10秒..."
        sleep 10
        BACKEND_PID=$(lsof -ti:8080 2>/dev/null || true)
        if [ -n "$BACKEND_PID" ]; then
            echo "  ✓ 后端启动成功 (PID: $BACKEND_PID)"
        else
            echo "  ⚠ 后端可能还在启动, 请检查日志"
        fi
        cd "$PROJECT_DIR"
    else
        echo "  ⚠ 未找到 backend/pom.xml"
    fi
fi

# 2. 检查前端是否运行
echo ""
echo "[2/5] 检查前端服务..."
FRONTEND_PID=$(lsof -ti:5173 2>/dev/null || true)
if [ -n "$FRONTEND_PID" ]; then
    echo "  ✓ 前端已在运行 (PID: $FRONTEND_PID, port 5173)"
else
    echo "  ✗ 前端未运行"
    if [ -f frontend/package.json ]; then
        cd frontend
        nohup npm run dev > /tmp/campus-frontend.log 2>&1 &
        echo "  → 前端启动中, 日志: /tmp/campus-frontend.log"
        sleep 5
        FRONTEND_PID=$(lsof -ti:5173 2>/dev/null || true)
        if [ -n "$FRONTEND_PID" ]; then
            echo "  ✓ 前端启动成功 (PID: $FRONTEND_PID)"
        else
            echo "  ⚠ 前端可能还在启动或package.json不完整"
        fi
        cd "$PROJECT_DIR"
    else
        echo "  ⚠ 未找到 frontend/package.json (前端尚未初始化)"
    fi
fi

# 3. 检查MySQL
echo ""
echo "[3/5] 检查数据库..."
if command -v mysql &>/dev/null; then
    MYSQL_OK=$(mysql -u root -e "SELECT 1 FROM campus_safety.sys_user LIMIT 1" 2>/dev/null && echo "ok" || echo "fail")
    if [ "$MYSQL_OK" = "ok" ]; then
        echo "  ✓ MySQL连接正常, campus_safety数据库存在"
    else
        echo "  ⚠ 数据库可能未初始化, 请执行: mysql -u root < sql/init.sql"
    fi
else
    echo "  ⚠ mysql 命令不可用"
fi

# 4. 检查Redis
echo ""
echo "[4/5] 检查Redis..."
if command -v redis-cli &>/dev/null; then
    REDIS_OK=$(redis-cli ping 2>/dev/null || echo "fail")
    if [ "$REDIS_OK" = "PONG" ]; then
        echo "  ✓ Redis运行正常"
    else
        echo "  ⚠ Redis未运行, 请启动: sudo systemctl start redis"
    fi
else
    echo "  ⚠ redis-cli 不可用"
fi

# 5. 显示Git状态
echo ""
echo "[5/5] Git状态..."
if [ -d .git ]; then
    echo "  最近5条提交:"
    git log --oneline -5 2>/dev/null || echo "  (无提交记录)"
    echo ""
    echo "  工作区变更:"
    git status --short 2>/dev/null || echo "  (无变更)"
else
    echo "  ⚠ Git仓库未初始化"
fi

# 6. 显示功能进度
echo ""
echo "============================================"
echo "  功能进度统计"
echo "============================================"
if [ -f .agent/feature_list.json ]; then
    TOTAL=$(grep -c '"id":' .agent/feature_list.json 2>/dev/null || echo 0)
    PASSING=$(grep -c '"passes": true' .agent/feature_list.json 2>/dev/null || echo 0)
    FAILING=$(grep -c '"passes": false' .agent/feature_list.json 2>/dev/null || echo 0)
    echo "  总功能数: $TOTAL"
    echo "  已完成:   $PASSING"
    echo "  未完成:   $FAILING"
    if [ "$TOTAL" -gt 0 ]; then
        PCT=$((PASSING * 100 / TOTAL))
        echo "  完成率:   ${PCT}%"
    fi
else
    echo "  ⚠ 未找到 feature_list.json"
fi

echo ""
echo "============================================"
echo "  初始化完成, Agent可以开始工作"
echo "============================================"
