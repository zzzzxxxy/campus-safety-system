#!/usr/bin/env python3
"""
校园安全管理系统 - 无限运行 AI 开发系统
=========================================
基于 Anthropic "Effective Harnesses for Long-Running Agents" 论文

架构:
  1. 初始化Agent: 首次运行, 搭建前端脚手架
  2. 编码Agent: 循环运行, 每次完成1-2个功能
  3. 进度追踪: feature_list.json + agent-progress.md + git log
  4. 自动恢复: 每次session开始先验证环境, 修复已知问题

使用方法:
  python3 .agent/run_loop.py                    # 默认使用hermes
  python3 .agent/run_loop.py --agent "claude"    # 使用claude code
  python3 .agent/run_loop.py --max 10            # 最多10轮
  python3 .agent/run_loop.py --hermes-cron       # 通过hermes cron调度

依赖: 仅Python3标准库
"""

import json
import os
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path

# ============================================================
# 配置
# ============================================================
PROJECT_DIR = Path("/home/softpapa/campus-safety-system")
AGENT_DIR = PROJECT_DIR / ".agent"
FEATURE_FILE = AGENT_DIR / "feature_list.json"
PROGRESS_FILE = AGENT_DIR / "agent-progress.md"
LOG_DIR = AGENT_DIR / "logs"

# ============================================================
# 工具函数
# ============================================================

def load_features() -> dict:
    """加载功能清单"""
    with open(FEATURE_FILE, 'r', encoding='utf-8') as f:
        return json.load(f)

def save_features(data: dict):
    """保存功能清单"""
    with open(FEATURE_FILE, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

def get_progress() -> tuple:
    """返回 (passing, total, percentage)"""
    data = load_features()
    features = data['features']
    total = len(features)
    passing = sum(1 for f in features if f['passes'])
    pct = round(passing / total * 100, 1) if total > 0 else 0
    return passing, total, pct

def get_next_features(n=2) -> list:
    """获取接下来要做的n个功能 (按优先级+ID排序)"""
    data = load_features()
    pending = [f for f in data['features'] if not f['passes']]
    pending.sort(key=lambda x: (x.get('priority', 99), x['id']))
    return pending[:n]

def is_all_done() -> bool:
    """是否全部完成"""
    data = load_features()
    return all(f['passes'] for f in data['features'])

def is_first_run() -> bool:
    """是否前端未初始化"""
    return not (PROJECT_DIR / "frontend" / "package.json").exists()

def get_git_log(n=10) -> str:
    """获取最近的git log"""
    try:
        result = subprocess.run(
            ['git', 'log', '--oneline', f'-{n}'],
            capture_output=True, text=True, cwd=str(PROJECT_DIR)
        )
        return result.stdout.strip() if result.returncode == 0 else "(无git记录)"
    except:
        return "(git不可用)"

def read_file(path: Path) -> str:
    """安全读取文件"""
    try:
        return path.read_text(encoding='utf-8')
    except:
        return f"(无法读取 {path})"

def build_initializer_prompt() -> str:
    """构建初始化Agent的prompt"""
    return read_file(AGENT_DIR / "initializer-prompt.md")

def build_coding_prompt(session_num: int) -> str:
    """构建编码Agent的prompt"""
    base_prompt = read_file(AGENT_DIR / "coding-agent-prompt.md")
    
    # 获取状态信息
    passing, total, pct = get_progress()
    next_features = get_next_features(2)
    git_log = get_git_log(5)
    progress_content = read_file(PROGRESS_FILE)
    
    # 构建上下文
    next_desc = "\n".join([
        f"  - {f['id']} [优先级{f.get('priority', '?')}]: {f['description']}"
        for f in next_features
    ]) if next_features else "  所有功能已完成!"
    
    context = f"""
---
## 当前Session上下文 (自动注入, 不要修改此部分)

### Session信息
- Session编号: #{session_num}
- 当前时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
- 进度: {passing}/{total} ({pct}%)

### 最近Git提交
{git_log}

### 最近进度摘要
{progress_content[-2000:] if len(progress_content) > 2000 else progress_content}

### 建议的下一个功能 (按优先级排序)
{next_desc}

请按照规则开始工作: 先执行Step 1-3了解环境, 然后专注完成上面建议的第一个功能。
完成后执行结束步骤 (commit + 更新feature_list + 更新progress)。
"""
    return base_prompt + context

def run_agent(prompt: str, session_num: int, agent_cmd: str = "hermes") -> int:
    """运行一个Agent session"""
    LOG_DIR.mkdir(parents=True, exist_ok=True)
    log_file = LOG_DIR / f"session-{session_num:03d}-{datetime.now().strftime('%Y%m%d-%H%M%S')}.log"
    
    print(f"  日志文件: {log_file}")
    
    # 写prompt到临时文件
    prompt_file = AGENT_DIR / f"_current_prompt.md"
    prompt_file.write_text(prompt, encoding='utf-8')
    
    try:
        # 通过stdin传递prompt给agent
        proc = subprocess.Popen(
            [agent_cmd],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            cwd=str(PROJECT_DIR)
        )
        
        stdout, _ = proc.communicate(input=prompt, timeout=3600)  # 1小时超时
        
        # 写日志
        with open(log_file, 'w', encoding='utf-8') as f:
            f.write(f"=== Session #{session_num} ===\n")
            f.write(f"=== {datetime.now().isoformat()} ===\n")
            f.write(f"=== Agent: {agent_cmd} ===\n\n")
            f.write(stdout)
        
        # 输出到终端
        print(stdout[-3000:] if len(stdout) > 3000 else stdout)
        
        return proc.returncode
        
    except subprocess.TimeoutExpired:
        print(f"  ⚠ Session #{session_num} 超时 (1小时)")
        proc.kill()
        return -1
    except Exception as e:
        print(f"  ✗ 运行失败: {e}")
        return -1
    finally:
        prompt_file.unlink(missing_ok=True)

def print_banner():
    print()
    print("╔══════════════════════════════════════════════════════╗")
    print("║  校园安全管理系统 - 无限运行 AI 开发系统             ║")
    print("║  Based on Anthropic Long-Running Agent Harness       ║")
    print("║                                                      ║")
    print("║  架构:                                                ║")
    print("║    初始化Agent → 搭建脚手架                          ║")
    print("║    编码Agent   → 增量完成功能 (循环)                  ║")
    print("║    进度追踪    → feature_list.json + git             ║")
    print("╚══════════════════════════════════════════════════════╝")
    print()

def print_progress_bar(passing, total):
    """显示进度条"""
    width = 40
    filled = int(width * passing / total) if total > 0 else 0
    bar = '█' * filled + '░' * (width - filled)
    pct = passing / total * 100 if total > 0 else 0
    print(f"  [{bar}] {pct:.1f}% ({passing}/{total})")

# ============================================================
# 主程序
# ============================================================

def main():
    import argparse
    parser = argparse.ArgumentParser(description='无限运行AI开发系统')
    parser.add_argument('--agent', default='hermes', help='Agent命令 (hermes/claude)')
    parser.add_argument('--max', type=int, default=999, help='最大session数')
    parser.add_argument('--pause', type=int, default=15, help='session间暂停秒数')
    parser.add_argument('--dry-run', action='store_true', help='只显示prompt')
    parser.add_argument('--hermes-cron', action='store_true', help='通过hermes cron单次运行')
    args = parser.parse_args()

    print_banner()
    
    passing, total, pct = get_progress()
    print(f"  Agent:  {args.agent}")
    print(f"  最大轮: {args.max}")
    print(f"  进度:")
    print_progress_bar(passing, total)
    print()

    session_num = 0

    # ---- 首次运行: 初始化Agent ----
    if is_first_run():
        session_num = 0
        print("━" * 50)
        print(f"  🚀 首次运行 - 初始化Agent")
        print("━" * 50)
        
        prompt = build_initializer_prompt()
        
        if args.dry_run:
            print("\n--- DRY RUN: 初始化 Prompt ---")
            print(prompt[:2000])
            print("--- END ---")
            return
        
        result = run_agent(prompt, session_num, args.agent)
        
        if not (PROJECT_DIR / "frontend" / "package.json").exists():
            print("  ✗ 初始化可能失败, frontend/package.json 未创建")
            sys.exit(1)
        
        print(f"  ✓ 初始化完成!")
        time.sleep(args.pause)

    # ---- hermes-cron 模式: 只运行一个session ----
    if args.hermes_cron:
        args.max = 1

    # ---- 编码循环 ----
    while session_num < args.max:
        session_num += 1
        
        print()
        print("━" * 50)
        passing, total, pct = get_progress()
        print(f"  📝 Session #{session_num}")
        print_progress_bar(passing, total)
        
        if is_all_done():
            print("  🎉 所有功能已完成!")
            break
        
        next_features = get_next_features(1)
        if next_features:
            f = next_features[0]
            print(f"  → 下一个: {f['id']} - {f['description']}")
        print("━" * 50)
        
        prompt = build_coding_prompt(session_num)
        
        if args.dry_run:
            print(f"\n--- DRY RUN: Session #{session_num} ---")
            print(f"下一功能: {next_features[0]['id'] if next_features else 'N/A'}")
            print("--- END ---")
            if session_num >= min(3, args.max):
                print("(dry-run只预览前3轮)")
                break
            continue
        
        result = run_agent(prompt, session_num, args.agent)
        
        # 显示新进度
        new_passing, new_total, new_pct = get_progress()
        features_done = new_passing - passing
        print(f"\n  Session #{session_num} 结束:")
        print(f"  本轮完成: {features_done} 个功能")
        print_progress_bar(new_passing, new_total)
        
        if features_done == 0:
            print("  ⚠ 本轮没有新功能完成, 可能遇到了问题")
        
        # 暂停
        if session_num < args.max and not is_all_done():
            print(f"\n  暂停 {args.pause}s...")
            time.sleep(args.pause)

    # ---- 结束 ----
    print()
    print("╔══════════════════════════════════════════════════════╗")
    passing, total, pct = get_progress()
    print(f"║  开发循环结束                                        ║")
    print(f"║  总Sessions: {session_num:<40}║")
    print(f"║  最终进度:   {passing}/{total} ({pct}%){'':30}║"[:56] + "║")
    print("╚══════════════════════════════════════════════════════╝")

if __name__ == "__main__":
    main()
