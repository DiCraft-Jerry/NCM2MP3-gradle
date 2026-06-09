# 更新日志

### [v4.0.5] - 2025-06-10

#### 修复问题

- 修复 Converter.ncm2Mp3 文件流资源泄漏问题（改用 try-with-resources 自动关闭流）
- 修复 Interpreter.paramsFor 参数偏移错误（skip(0) → skip(1)，命令名不再错误传入处理器）
- 修复 Converter.mataData 缺少长度上限校验导致的 OOM 风险
- 修复 Converter.mataData 数组操作缺少边界检查的问题
- 修复 Utils.listAllFiles 使用 assert 做生产空值检查的问题（改为显式 if null 判断）
- 修复 Utils.waitForAllTask 吞掉 InterruptedException 未恢复中断状态的问题

#### 新增内容

- 新增 CommandTypeTest 测试类，覆盖所有命令类型匹配逻辑
- 新增 InterpreterTest 测试类，覆盖命令行参数解析与分发
- 新增 ConvertCommandTest 和 HelpCommandTest 测试类

#### 改动内容

- 扩展 UtilsTest，增加 waitForAllTask、getLength 边界值、albumImageMimeType 边界值测试
- 扩展 AsyncTaskExecutorTest，增加并发提交和 null 任务测试
- 扩展 ConvertTaskTest，增加有效文件转换成功路径测试
- 扩展 ConverterTest，增加 OOM 防护分支和 combineFile null image 分支测试
- test 从原有 23 个增加到 65 个（全部通过，0 跳过）
- 修正 AESTest、CR4Test、UtilsTest 在 Gradle 8.14 下静默未执行的问题
- 将 ConvertCommand 和 HelpCommand 中 System.exit(0) 替换为 return（修复 Java 17+ 兼容性）

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
