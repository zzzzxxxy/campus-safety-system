# Hermes Agent 运行指南

## 方式1: 直接在对话中运行一轮

在 Hermes 对话中说:
```
帮我运行一轮校园安全系统的开发，按照 /home/softpapa/campus-safety-system/.agent/coding-agent-prompt.md 的规则
```

## 方式2: 通过 Hermes Cron Job 自动循环

设置定时任务，每30分钟运行一次Agent session:

```
帮我设置一个cron job，每30分钟运行一次，按照 
/home/softpapa/campus-safety-system/.agent/coding-agent-prompt.md 
的规则，对校园安全系统进行增量开发。

每次运行时:
1. 读取 .agent/feature_list.json 了解进度
2. 读取 .agent/agent-progress.md 了解历史
3. 选择下一个 passes=false 的功能
4. 实现功能并测试
5. 更新 feature_list.json 和 agent-progress.md
6. git commit
```

## 方式3: 使用 delegate_task 并行开发

```
帮我用 delegate_task 并行3个子Agent:
- Agent 1: 搭建前端脚手架 (F010-F015)
- Agent 2: 写登录页和布局 (F020-F032)  
- Agent 3: 写API接口定义 (所有api/*.ts文件)
```

## 方式4: Shell 脚本循环 (后台运行)

```bash
cd /home/softpapa/campus-safety-system
nohup python3 .agent/run_loop.py --agent hermes --max 50 --pause 30 > .agent/logs/loop.log 2>&1 &
```

## 查看进度

```bash
# 查看功能完成度
python3 -c "
import json
with open('.agent/feature_list.json') as f:
    data = json.load(f)
total = len(data['features'])
done = sum(1 for f in data['features'] if f['passes'])
print(f'进度: {done}/{total} ({done/total*100:.1f}%)')
for f in data['features']:
    status = '✓' if f['passes'] else '✗'
    print(f'  {status} {f[\"id\"]} [{f.get(\"priority\",\"?\")}] {f[\"description\"][:50]}')
"
```
