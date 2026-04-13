#!/bin/bash
# ============================================================
# 校园安全管理系统 - 无限运行 AI 开发循环
#
# 基于 Anthropic "Effective Harnesses for Long-Running Agents"
# 架构: 初始化Agent (首次) + 编码Agent (循环)
#
# 用法:
#   bash .agent/run-loop.sh                    # 使用默认hermes
#   bash .agent/run-loop.sh --agent claude      # 使用claude code
#   bash .agent/run-loop.sh --max-sessions 10   # 最多运行10轮
#   bash .agent/run-loop.sh --dry-run           # 只显示prompt不执行
# ============================================================

set -euo pipefail

PROJECT_DIR="/home/softpapa/campus-safety-system"
AGENT_DIR="$PROJECT_DIR/.agent"
FEATURE_FILE="$AGENT_DIR/feature_list.json"
PROGRESS_FILE="$AGENT_DIR/agent-progress.md"
LOG_DIR="$AGENT_DIR/logs"

# 默认参数
AGENT_CMD="hermes"
MAX_SESSIONS=999
DRY_RUN=false
PAUSE_BETWEEN=10  # 每轮之间暂停秒数
SESSION_NUM=0
NOTIFY_FEISHU=false

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        --agent)     AGENT_CMD="$2"; shift 2 ;;
        --max-sessions) MAX_SESSIONS="$2"; shift 2 ;;
        --dry-run)   DRY_RUN=true; shift ;;
        --pause)     PAUSE_BETWEEN="$2"; shift 2 ;;
        --notify)    NOTIFY_FEISHU=true; shift ;;
        *)           echo "Unknown option: $1"; exit 1 ;;
    esac
done

# 创建日志目录
mkdir -p "$LOG_DIR"

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# ============================================================
# 工具函数
# ============================================================

log() {
    echo -e "${BLUE}[$(date '+%H:%M:%S')]${NC} $1"
}

success() {
    echo -e "${GREEN}[$(date '+%H:%M:%S')] ✓${NC} $1"
}

warn() {
    echo -e "${YELLOW}[$(date '+%H:%M:%S')] ⚠${NC} $1"
}

error() {
    echo -e "${RED}[$(date '+%H:%M:%S')] ✗${NC} $1"
}

# 计算完成度
get_progress() {
    if [ ! -f "$FEATURE_FILE" ]; then
        echo "0/0"
        return
    fi
    local total=$(grep -c '"id":' "$FEATURE_FILE" 2>/dev/null || echo 0)
    local passing=$(grep -c '"passes": true' "$FEATURE_FILE" 2>/dev/null || echo 0)
    echo "$passing/$total"
}

# 检查是否全部完成
is_all_done() {
    if [ ! -f "$FEATURE_FILE" ]; then
        return 1
    fi
    local failing=$(grep -c '"passes": false' "$FEATURE_FILE" 2>/dev/null || echo 1)
    [ "$failing" -eq 0 ]
}

# 获取下一个待做功能
get_next_feature() {
    if [ ! -f "$FEATURE_FILE" ]; then
        echo "unknown"
        return
    fi
    # 获取第一个 passes: false 的功能 (按优先级排序)
    python3 -c "
import json
with open('$FEATURE_FILE') as f:
    data = json.load(f)
features = [f for f in data['features'] if not f['passes']]
features.sort(key=lambda x: (x.get('priority', 99), x['id']))
if features:
    f = features[0]
    print(f'{f[\"id\"]}: {f[\"description\"]}')
else:
    print('ALL_DONE')
" 2>/dev/null || echo "unknown"
}

# 检查是否首次运行 (前端未初始化)
is_first_run() {
    [ ! -f "$PROJECT_DIR/frontend/package.json" ]
}

# 生成session日志文件名
get_log_file() {
    echo "$LOG_DIR/session-$(printf '%03d' $SESSION_NUM)-$(date '+%Y%m%d-%H%M%S').log"
}

# ============================================================
# 主循环
# ============================================================

echo ""
echo "╔══════════════════════════════════════════════════╗"
echo "║  校园安全管理系统 - 无限运行 AI 开发系统         ║"
echo "║  Based on Anthropic Long-Running Agent Harness   ║"
echo "╚══════════════════════════════════════════════════╝"
echo ""
log "Agent命令:    $AGENT_CMD"
log "最大轮数:     $MAX_SESSIONS"
log "轮间暂停:     ${PAUSE_BETWEEN}s"
log "当前进度:     $(get_progress)"
log "Dry Run:      $DRY_RUN"
echo ""

# 首次运行: 使用初始化Agent
if is_first_run; then
    SESSION_NUM=0
    log "========== 首次运行: 初始化Agent =========="
    log "前端项目尚未初始化, 将执行初始化Agent"

    INIT_PROMPT=$(cat "$AGENT_DIR/initializer-prompt.md")

    if [ "$DRY_RUN" = true ]; then
        echo "--- DRY RUN: 初始化 Prompt ---"
        echo "$INIT_PROMPT"
        echo "--- END ---"
        exit 0
    fi

    LOG_FILE=$(get_log_file)
    log "Session日志: $LOG_FILE"

    echo "$INIT_PROMPT" | $AGENT_CMD 2>&1 | tee "$LOG_FILE"

    if [ -f "$PROJECT_DIR/frontend/package.json" ]; then
        success "初始化完成! 前端项目已创建"
    else
        error "初始化可能失败, 前端package.json未找到"
        warn "请检查日志: $LOG_FILE"
        exit 1
    fi

    log "暂停 ${PAUSE_BETWEEN}s 后开始编码循环..."
    sleep "$PAUSE_BETWEEN"
fi

# 编码循环
while [ $SESSION_NUM -lt $MAX_SESSIONS ]; do
    SESSION_NUM=$((SESSION_NUM + 1))

    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    log "Session #$SESSION_NUM 开始"
    log "当前进度: $(get_progress)"

    # 检查是否全部完成
    if is_all_done; then
        success "🎉 所有功能已完成!"
        log "最终进度: $(get_progress)"
        break
    fi

    NEXT=$(get_next_feature)
    log "下一个功能: $NEXT"

    # 构建编码Agent的prompt
    CODING_PROMPT=$(cat "$AGENT_DIR/coding-agent-prompt.md")

    # 追加当前session上下文
    FULL_PROMPT="$CODING_PROMPT

---
## 当前Session信息
- Session编号: #$SESSION_NUM
- 当前时间: $(date '+%Y-%m-%d %H:%M:%S')
- 当前进度: $(get_progress)
- 建议下一个功能: $NEXT

请按照上述规则开始工作。首先执行 Step 1-3 了解环境,然后专注完成建议的功能。"

    if [ "$DRY_RUN" = true ]; then
        echo "--- DRY RUN: Session #$SESSION_NUM Prompt ---"
        echo "下一个功能: $NEXT"
        echo "--- END ---"
        continue
    fi

    LOG_FILE=$(get_log_file)
    log "Session日志: $LOG_FILE"

    # 运行Agent
    echo "$FULL_PROMPT" | $AGENT_CMD 2>&1 | tee "$LOG_FILE"

    RESULT=$?
    if [ $RESULT -ne 0 ]; then
        warn "Session #$SESSION_NUM 以非零退出码 $RESULT 结束"
    fi

    # 显示session后的进度
    NEW_PROGRESS=$(get_progress)
    success "Session #$SESSION_NUM 完成, 进度: $NEW_PROGRESS"

    # 通知 (可选)
    if [ "$NOTIFY_FEISHU" = true ]; then
        log "发送飞书通知..."
        # 这里可以集成飞书webhook
    fi

    # 暂停
    if [ $SESSION_NUM -lt $MAX_SESSIONS ] && ! is_all_done; then
        log "暂停 ${PAUSE_BETWEEN}s..."
        sleep "$PAUSE_BETWEEN"
    fi
done

echo ""
echo "╔══════════════════════════════════════════════════╗"
echo "║  开发循环结束                                    ║"
echo "║  总Sessions: $SESSION_NUM"
echo "║  最终进度:   $(get_progress)"
echo "╚══════════════════════════════════════════════════╝"
