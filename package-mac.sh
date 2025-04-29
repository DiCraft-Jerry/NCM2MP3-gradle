#!/bin/bash
# macOS打包可执行文件脚本
# 设置版本号
VERSION="4.0.4"
APP_NAME="NCM2MP3"
MAIN_CLASS="main"
JAR_FILE="build/libs/${APP_NAME}-${VERSION}.jar"
ICON_FILE="src/main/resources/icon.icns"
DMG_NAME="${APP_NAME}-${VERSION}.dmg"
APP_DIR="build/${APP_NAME}.app"

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

# 创建应用程序目录结构
mkdir -p "${APP_DIR}/Contents/MacOS"
mkdir -p "${APP_DIR}/Contents/Resources"
mkdir -p "${APP_DIR}/Contents/Java"
mkdir -p "${APP_DIR}/Contents/PlugIns/jre"

# 复制 JAR 文件
cp "$JAR_FILE" "${APP_DIR}/Contents/Java/"
cp "$ICON_FILE" "${APP_DIR}/Contents/Resources/" 2>/dev/null || echo "Warning: Icon file not found"

# 复制 JRE
echo "Copying JRE..."
JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
if [ -z "$JAVA_HOME" ]; then
    echo "Error: Java 8 not found"
    exit 1
fi

# 复制 JRE 文件
cp -R "$JAVA_HOME"/* "${APP_DIR}/Contents/PlugIns/jre/"

# 创建启动脚本
cat > "${APP_DIR}/Contents/MacOS/JavaAppLauncher" << 'EOF'
#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
"$DIR/../PlugIns/jre/bin/java" -jar "$DIR/../Java/NCM2MP3.jar"
EOF

chmod +x "${APP_DIR}/Contents/MacOS/JavaAppLauncher"

# 创建 Info.plist
cat > "${APP_DIR}/Contents/Info.plist" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleIconFile</key>
    <string>icon.icns</string>
    <key>CFBundleIdentifier</key>
    <string>com.github.dicraft.ncm2mp3</string>
    <key>CFBundleName</key>
    <string>NCM2MP3</string>
    <key>CFBundleDisplayName</key>
    <string>NCM2MP3</string>
    <key>CFBundleVersion</key>
    <string>${VERSION}</string>
    <key>CFBundleShortVersionString</key>
    <string>${VERSION}</string>
    <key>CFBundlePackageType</key>
    <string>APPL</string>
    <key>CFBundleSignature</key>
    <string>????</string>
    <key>CFBundleExecutable</key>
    <string>JavaAppLauncher</string>
    <key>JVMMainClassName</key>
    <string>${MAIN_CLASS}</string>
    <key>JVMOptions</key>
    <array>
        <string>-Xmx512m</string>
    </array>
</dict>
</plist>
EOF

# 创建 DMG 文件
echo "Creating DMG file..."
hdiutil create -volname "${APP_NAME}" -srcfolder "${APP_DIR}" -ov -format UDZO "build/${DMG_NAME}"

# 检查打包是否成功
if [ -f "build/${DMG_NAME}" ]; then
    echo "Successfully created ${DMG_NAME}"
else
    echo "Error: Failed to create DMG file"
    exit 1
fi

echo "Packaging completed successfully!" 