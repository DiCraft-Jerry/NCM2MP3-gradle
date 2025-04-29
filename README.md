# NCM2MP3

[![Java Version](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)
[![Gradle Version](https://img.shields.io/badge/Gradle-7.6%2B-green.svg)](https://gradle.org/releases/)
[![License](https://img.shields.io/github/license/DiCraft-Jerry/NCM2MP3-gradle)](https://github.com/DiCraft-Jerry/NCM2MP3-gradle/blob/master/LICENSE)
[![Last Commit](https://img.shields.io/github/last-commit/DiCraft-Jerry/NCM2MP3-gradle)](https://github.com/DiCraft-Jerry/NCM2MP3-gradle/commits/master)
[![Issues](https://img.shields.io/github/issues/DiCraft-Jerry/NCM2MP3-gradle)](https://github.com/DiCraft-Jerry/NCM2MP3-gradle/issues)
[![Stars](https://img.shields.io/github/stars/DiCraft-Jerry/NCM2MP3-gradle)](https://github.com/DiCraft-Jerry/NCM2MP3-gradle/stargazers)
[![Downloads](https://img.shields.io/github/downloads/DiCraft-Jerry/NCM2MP3-gradle/total)](https://github.com/DiCraft-Jerry/NCM2MP3-gradle/releases)
[![Release](https://img.shields.io/github/v/release/DiCraft-Jerry/NCM2MP3-gradle)](https://github.com/DiCraft-Jerry/NCM2MP3-gradle/releases)
[![Code Size](https://img.shields.io/github/languages/code-size/DiCraft-Jerry/NCM2MP3-gradle)](https://github.com/DiCraft-Jerry/NCM2MP3-gradle)

网易云 ncm 音乐格式转换为 mp3 音乐格式工具

## 目录

- [环境准备](#环境准备)
- [快速开始](#快速开始)
  - [使用可执行 JAR](#使用可执行-jar)
  - [从源代码构建](#从源代码构建)
  - [命令行使用](#命令行使用)
- [打包说明](#打包说明)
  - [环境要求](#环境要求)
  - [macOS 打包](#macos-打包)
  - [Windows 打包](#windows-打包)
  - [打包文件说明](#打包文件说明)
  - [打包注意事项](#打包注意事项)
- [项目结构](#项目结构)
  - [构成说明](#构成说明)
- [技术实现](#技术实现)
- [原理说明](#原理说明)
- [加密解密流程](#加密解密流程)
- [效果展示](#效果展示)
- [运行说明](#运行说明)
- [使用说明](#使用说明)
- [常见问题](#常见问题)
- [贡献指南](#贡献指南)
- [许可证](#许可证)
- [联系方式](#联系方式)
- [致谢](#致谢)
- [更多信息](#更多信息)

## 环境准备

- JDK 8.0 或更高版本
- Gradle 7.6 或更高版本
- 集成开发环境 IDEA（推荐使用，插件支持：Lombok）

## 快速开始

### 使用可执行 JAR

1. 下载最新的发布版本
2. 在命令行中执行：

```bash
java -jar NCM2MP3-${VERSION}.jar
```

### 从源代码构建

1. 克隆项目

```bash
git clone https://github.com/DiCraft-Jerry/NCM2MP3-gradle.git
cd NCM2MP3-gradle
```

2. 使用 Gradle 构建

```bash
# 构建项目
./gradlew build

# 运行项目
./gradlew run

# 生成可执行 JAR
./gradlew jar
```

### 命令行使用

```bash
java -jar NCM2MP3-${VERSION}.jar [command]

命令列表：
-v, --view     : 打开图形界面（默认命令）
-c, --convert  : 转换指定路径的 NCM 文件到 ./output 目录
-h, --help     : 显示帮助信息
```

## 打包说明

### 环境要求

- JDK 8 或更高版本
- macOS 10.12 或更高版本（用于 macOS 版本）
- Windows 7 或更高版本（用于 Windows 版本）

### macOS 打包

使用 `package-mac.sh` 脚本创建 macOS DMG 安装包：

```bash
chmod +x package-mac.sh
./package-mac.sh
```

打包完成后，DMG 文件将生成在 `build` 目录下。

### Windows 打包

使用 `package-win.sh` 脚本创建 Windows 可执行文件：

```bash
chmod +x package-win.sh
./package-win.sh
```

注意：Windows 打包需要在 Windows 环境下运行，或者使用 Docker 容器。

### 打包文件说明

- `package-mac.sh`: macOS 打包脚本，用于创建 DMG 安装包
- `package-win.sh`: Windows 打包脚本，用于创建可执行文件
- `build/`: 打包输出目录
  - `NCM2MP3-${VERSION}.dmg`: macOS 安装包
  - `NCM2MP3-${VERSION}.exe`: Windows 安装程序

### 打包注意事项

1. 确保已安装所需的开发工具和依赖
2. 确保有足够的磁盘空间
3. 确保有适当的文件权限
4. Windows 打包需要在 Windows 环境下进行
5. 打包前请确保所有测试通过

## 项目结构

```
src/main/java/
├── executor/          # 任务执行管理
│   ├── ConvertTask.java
│   └── AsyncTaskExecutor.java
├── service/           # 核心业务逻辑
│   ├── Converter.java
│   └── Interpreter/
│       ├── ConvertCommand.java
│       ├── HelpCommand.java
│       └── ViewCommand.java
├── mime/             # 数据模型
│   ├── MATA.java
│   └── NCM.java
├── utils/            # 工具类
│   ├── AES.java
│   ├── RC4.java
│   └── Utils.java
├── view/             # 用户界面
│   └── view.java
└── main.java         # 程序入口
```

### 构成说明

- executor:控制管理
  - ConvertTask.java 对应每一个音乐转换的任务(消费者)
  - AsyncTaskExecutor.java 线程池,双空判断懒加载模式,核心线程10个，最大线程数20个，队列长度为100
- service:音乐格式转换核心功能实现
  - Converter.java 将NCM音乐解密拆分(==如果想快速看懂这个项目:建议从这个类开始看==), 将分析的各个数据整合到一起
  - Interpreter: 命令行参数解析器(策略模式分配命令处理)
    - ConvertCommand,HelpCommand,ViewCommand: 现在支持的3种命令
- mime 封装的数据类型
  - MATA.java 音乐头部信息
  - NCM.java 音乐输入输出信息等基本信息
- utils 工具类
  - AES.java AES解密的实现(ECB加密模式,PKCS5Padding填充模式)
  - RC4.java RC4解密的实现(这算法本质就是打乱顺序,需要注意的byte是1个字节且无符号的,以及对其取整操作(&0xff))
  - Utils.java 杂七杂八的工具(有注释说明)
- View 视图
  - view.java 用的Swing做的视图(Flatlaf这个jar包中有皮肤,所以看起来还不错.以后有机会学学javaFX..)
- main.java

## 技术实现

- 使用 Java 8 开发
- 采用 Gradle 构建系统
- 使用 Swing 构建图形界面，FlatLaf 提供现代化皮肤
- 实现多线程处理，使用线程池管理转换任务

## 原理说明

NCM格式是网易云音乐特有的音乐格式,这种音乐格式用到AES,RC4的加密算法对普通的音乐格式(如MP3,FLAC)进行加密,若要了解该加密过程,最好的方法就是知道起格式图,以及加密的原理(可以参考笔记 `密码学.md`).

## 加密解密流程

NCM 格式使用了 AES 和 RC4 加密算法，解密过程包括：

| 信息                   | 大小        | 说明                                                                                                                                                               |
| ---------------------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Magic Header           | 10 bytes    | 跳过                                                                                                                                                               |
| KEY Length             | 4 bytes     | 用 AES128 加密 RC4 密钥后的长度（小端字节排序，无符号整型）                                                                                                        |
| KEY From AES128 Decode | KEY Length  | 用 AES128 加密的 RC4 密钥（注意：1.按字节对 0x64 异或 2.AES 解密 3.去除前面 'neteasecloudmusic' 17 个字节）                                                        |
| Mata Length            | 4 bytes     | Mata 信息的长度（小端字节排序，无符号整型）                                                                                                                        |
| Mata Data(JSON)        | Mata Length | JSON 格式的 Mata 信息（注意：1.按字节对 0x63 异或 2.去除前面 '163 key(Don't modify):' 22 个字节 3.Base64 解码 4.AES 解密 5.去除前面 'music:' 6 个字节后获得 JSON） |
| CRC 校验码             | 4 bytes     | 跳过                                                                                                                                                               |
| Gap                    | 5 bytes     | 跳过                                                                                                                                                               |
| Image Size             | 4 bytes     | 图片大小                                                                                                                                                           |
| Image                  | Image Size  | 图片数据                                                                                                                                                           |
| Music Data             | -           | RC4-KSA 生成 s 盒，RC4-PRGA 解密                                                                                                                                   |

## 效果展示

- 打开界面

![](image/picture1.png)

- 准备转换

![](image/picture2.png)

- 转换成功

![](image/picture3.png)

## 运行说明

```text
1. 使用NCM2MP3.jar运行图形界面(只需要准备jdk环境便可以):命令行中在该jar包的目录下执行`java -jar NCM2MP3.jar`
2. 用源代码运行:在环境配置好后,执行入口为`main.java`
3. 命令行相关操作`java -jar NCM2MP3.jar [command]`
Usage: java -jar NCM2MP3.jar [command]
If don't add command, there will open NCM2MP3 GUI directly
[Command List]
-v,-view                      : open NCM View GUI(default command)
-c,--convert [path] ...       : convert NCM File in path to ./output directory
-h,-help                      : Help about any command
```

## 使用说明

1. 启动应用程序
2. 点击"选择文件"按钮选择 NCM 文件，或直接将文件拖放到应用程序窗口
3. 选择输出目录
4. 点击"开始转换"按钮
5. 等待转换完成

## 常见问题

如果您在使用过程中遇到问题，请查看 [FAQ.md](FAQ.md) 文件获取常见问题的解决方案。

## 贡献指南

欢迎提交 Issue 和 Pull Request。在提交代码前，请确保：

1. 代码符合项目编码规范
2. 添加必要的测试
3. 更新相关文档

## 许可证

本项目采用 GNU General Public License v3.0 许可证。详见 [LICENSE](LICENSE) 文件。

## 联系方式

- 项目主页：[GitHub](https://github.com/DiCraft-Jerry/NCM2MP3-gradle)
- 问题反馈：[Issues](https://github.com/DiCraft-Jerry/NCM2MP3-gradle/issues)

## 致谢

感谢所有为项目做出贡献的开发者。<br/>
原作者: charlotte-xiao <br/>
原作者邮箱: sa.xiao@thoughtworks.com<br/>
原项目地址: https://github.com/charlotte-xiao/NCM2MP3

## 更多信息

- 密码学相关知识请参考 [密码学.md](密码学.md)
- 更新日志请参考 [CHANGELOG.md](CHANGELOG.md)
- 常见问题请参考 [FAQ.md](FAQ.md)