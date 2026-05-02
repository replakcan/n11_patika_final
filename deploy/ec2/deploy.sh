#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/opt/n11-patika}"
GIT_REF="${GIT_REF:-main}"
IMAGE_TAG_OVERRIDE="${IMAGE_TAG_OVERRIDE:-latest}"

cd "$APP_DIR"

git fetch --all --prune
git checkout "$GIT_REF"
git pull --ff-only || true

if [ -n "${IMAGE_TAG_OVERRIDE:-}" ]; then
  if grep -q '^IMAGE_TAG=' .env.compose.local; then
    sed -i "s/^IMAGE_TAG=.*/IMAGE_TAG=$IMAGE_TAG_OVERRIDE/" .env.compose.local
  else
    echo "IMAGE_TAG=$IMAGE_TAG_OVERRIDE" >> .env.compose.local
  fi
fi

docker compose --env-file .env.compose.local pull
docker compose --env-file .env.compose.local up -d --no-build
docker compose --env-file .env.compose.local --profile seed run --rm product-seeder || true
