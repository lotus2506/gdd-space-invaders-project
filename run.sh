#!/bin/sh
# Compiles and runs the game.
# Must run from the project root: image/audio paths in gdd.Global are
# relative to the working directory (e.g. "src/images/player.png").
set -e

cd "$(dirname "$0")"

# macOS ships stub javac/java binaries that exist but fail, so check it actually runs.
if ! javac -version > /dev/null 2>&1; then
    echo "No JDK found. Install one with:  brew install --cask temurin" >&2
    exit 1
fi

javac -d build $(find src -name "*.java")
java -cp build gdd.Main
