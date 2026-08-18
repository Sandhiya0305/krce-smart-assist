#!/usr/bin/env bash
set -e
cd "$(dirname "$0")/backend"
command -v mvn >/dev/null 2>&1 || { echo "Maven was not found."; exit 1; }
mvn spring-boot:run
