# NCM2MP3

网易云 ncm 音乐格式转换为 mp3 音乐格式工具

## 环境准备

- JDK 8.0 或更高版本
- Gradle 7.6 或更高版本
- 集成开发环境 IDEA（推荐使用，插件支持：Lombok）

## 快速开始

### 使用可执行 JAR
1. 下载最新的发布版本
2. 在命令行中执行：
```bash
java -jar NCM2MP3-[对应版本号].jar
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
java -jar NCM2MP3.jar [command]

命令列表：
-v, --view     : 打开图形界面（默认命令）
-c, --convert  : 转换指定路径的 NCM 文件到 ./output 目录
-h, --help     : 显示帮助信息
```

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
NCM格式是网易云音乐特有的音乐格式,这种音乐格式用到AES,RC4的加密算法对普通的音乐格式(如MP3,FLAC)进行加密,若要了解该加密过程,最好的方法就是知道起格式图,以及加密的原理(可以参考笔记`密码学.md`).


## 加密解密流程
NCM 格式使用了 AES 和 RC4 加密算法，解密过程包括：

| 信息 | 大小 | 说明 |
|------|------|------|
| Magic Header | 10 bytes | 跳过 |
| KEY Length | 4 bytes | 用 AES128 加密 RC4 密钥后的长度（小端字节排序，无符号整型） |
| KEY From AES128 Decode | KEY Length | 用 AES128 加密的 RC4 密钥（注意：1.按字节对 0x64 异或 2.AES 解密 3.去除前面 'neteasecloudmusic' 17 个字节） |
| Mata Length | 4 bytes | Mata 信息的长度（小端字节排序，无符号整型） |
| Mata Data(JSON) | Mata Length | JSON 格式的 Mata 信息（注意：1.按字节对 0x63 异或 2.去除前面 '163 key(Don't modify):' 22 个字节 3.Base64 解码 4.AES 解密 5.去除前面 'music:' 6 个字节后获得 JSON） |
| CRC 校验码 | 4 bytes | 跳过 |
| Gap | 5 bytes | 跳过 |
| Image Size | 4 bytes | 图片大小 |
| Image | Image Size | 图片数据 |
| Music Data | - | RC4-KSA 生成 s 盒，RC4-PRGA 解密 |

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


## 贡献指南
欢迎提交 Issue 和 Pull Request。在提交代码前，请确保：
1. 代码符合项目编码规范
2. 添加必要的测试
3. 更新相关文档

## 许可证
本项目采用 GNU General Public License v3.0 许可证。详见 [LICENSE](LICENSE) 文件。

## 致谢
感谢所有为项目做出贡献的开发者。<br/>
原作者: charlotte-xiao<br/>
原作者邮箱: sa.xiao@thoughtworks.com <br/>
原项目地址: https://github.com/charlotte-xiao/NCM2MP3

## 更多信息
- 密码学相关知识请参考 [密码学.md](密码学.md)

## 更新日志

### [v4.0.0] - 2025-04-07

#### 新增功能

- 完成了从 Maven 到 Gradle 的迁移，支持更高效的构建流程
- 添加了 Gradle Wrapper 支持，简化本地开发环境配置

#### 移除内容

- 移除了对 Maven 构建系统的支持，替换为 Gradle 构建文件

#### 修复问题

- 修复了迁移过程中部分依赖未正确解析的问题
- 保持所有依赖和版本不变
- 配置 Java 8 兼容性
- 设置可执行 JAR 生成


### [v4.0.1] - 2025-04-07

#### 修复问题
- 修复了编译过程中出现的未经检查的操作警告。
  - 启用 `-Xlint:unchecked` 参数以获取详细警告信息。
  - 修改代码以正确使用泛型，避免未经检查的操作。
  - 提高了代码的安全性和可维护性。

#### 改动内容
- 调整了部分代码结构以符合最佳实践。

### [v4.0.2] - 2025-04-07

#### 修复问题
无
#### 改动内容
- 优化处理音乐转化时的线程池配置
  - 根据系统 CPU 核心数动态设置线程池参数
  - 使用无界队列存储待执行的任务
- gradle引入junit依赖以便于后续的测试
- gitignore文件增加排除文件信息
- 版本号更新到 v4.0.2
- 调整了部分代码结构以符合最佳实践。

### [v4.0.3] - 2024-04-27
#### 改动内容
- 添加了基本测试用例
- 实现了文件转换测试
- 添加了元数据测试

### [v4.0.4] - 2024-04-28
#### 修复问题
- 修复读取到异常文件是分配过大内存导致oom的问题
#### 改动内容
- 添加了数据大小限制
- 优化了异常处理
- 完善了测试文档
- 添加了测试覆盖率统计
