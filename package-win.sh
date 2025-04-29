#!/bin/bash
# windows打包可执行文件脚本
# 设置版本号
VERSION="4.0.4"
APP_NAME="NCM2MP3"
MAIN_CLASS="main"
JAR_FILE="build/libs/${APP_NAME}-${VERSION}.jar"
ICON_FILE="src/main/resources/icon.ico"
OUTPUT_DIR="build/windows"

# 确保资源目录存在
mkdir -p src/main/resources

# 清理之前的构建
echo "Cleaning previous build..."
rm -rf build
rm -rf dist

# 生成 JAR 文件
echo "Building JAR file..."
./gradlew clean build

# 检查 JAR 文件是否存在
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    exit 1
fi

# 创建输出目录
mkdir -p "$OUTPUT_DIR"

# 创建临时目录
TEMP_DIR="build/temp"
mkdir -p "$TEMP_DIR"

# 复制必要的文件到临时目录
cp "$JAR_FILE" "$TEMP_DIR/"
cp "$ICON_FILE" "$TEMP_DIR/" 2>/dev/null || echo "Warning: Icon file not found"

# 使用 javapackager 打包
echo "Creating Windows executable..."
javapackager \
    -deploy \
    -native exe \
    -outdir "$OUTPUT_DIR" \
    -outfile "$APP_NAME" \
    -srcdir "$TEMP_DIR" \
    -srcfiles "$(basename "$JAR_FILE")" \
    -appclass "$MAIN_CLASS" \
    -name "$APP_NAME" \
    -title "$APP_NAME" \
    -vendor "DiCraft" \
    -Bruntime="$JAVA_HOME" \
    -Bicon="$ICON_FILE" \
    -BappVersion="$VERSION" \
    -Bwin.menuGroup="NCM2MP3" \
    -Bwin.shortcutHint=true \
    -Bwin.menuHint=true \
    -Bwin.dirChooser=true \
    -Bwin.perUserInstall=true

# 检查是否成功
if [ -f "$OUTPUT_DIR/${APP_NAME}.exe" ]; then
    echo "Successfully created ${APP_NAME}.exe"
else
    echo "Error: Failed to create executable"
    exit 1
fi

# 清理临时文件
rm -rf "$TEMP_DIR"

echo "Packaging completed successfully!" 