#!/bin/bash

# 스크립트 실행 중 오류 발생 시 즉시 중단 (set -e)
set -e

# --- 1. FE(React) 빌드 ---
echo "--- 1. FE(React) 빌드를 시작합니다. (frontend-react-src) ---"

# FE 소스 디렉토리로 이동
cd frontend-react-src

echo "FE 의존성 설치 (npm install)..."
npm install

echo "FE 빌드 (npm run build)..."
npm run build

# 다시 프로젝트 루트로 이동
cd ..

echo "--- 2. 빌드 결과물(FE)을 백엔드(BE)로 복사합니다. ---"

# 변수 정의
FE_SOURCE_DIR="./frontend-react-src/build"
BE_TARGET_DIR="./src/main/resources/static/app"

# 기존 백엔드 타겟 디렉토리 삭제
echo "기존 타겟 디렉토리 삭제: $BE_TARGET_DIR"
rm -rf $BE_TARGET_DIR

# 타겟 디렉토리 생성 (존재하지 않아도 오류 없음)
echo "새 타겟 디렉토리 생성: $BE_TARGET_DIR"
mkdir -p $BE_TARGET_DIR

# FE 빌드 결과물(*_를 BE 타겟 디렉토리로 복사
echo "빌드 결과물 복사: $FE_SOURCE_DIR/* -> $BE_TARGET_DIR/"
cp -R $FE_SOURCE_DIR/* $BE_TARGET_DIR/

echo ""
echo "--- ✅ 완료: FE 빌드 및 백엔드 복사 성공 ---"