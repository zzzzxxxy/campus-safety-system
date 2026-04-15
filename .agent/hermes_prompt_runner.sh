#!/usr/bin/env bash
set -euo pipefail
PROJECT_DIR="/home/softpapa/campus-safety-system"
PROMPT_FILE="$PROJECT_DIR/.agent/_current_prompt.md"
if [[ ! -f "$PROMPT_FILE" ]]; then
  echo "Prompt file not found: $PROMPT_FILE" >&2
  exit 1
fi
cat "$PROMPT_FILE" | hermes chat -q -
