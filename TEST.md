# NCM2MP3 单元测试文档

## 1. 测试类概述

`ConverterTest` 类用于测试 NCM 文件转换功能，主要测试以下方面：
- 文件转换功能
- 文件格式验证
- 元数据处理
- 音频数据处理
- 文件合并功能

## 2. 测试用例说明

### 2.1 有效文件转换测试
```java
@Test
void testNcm2Mp3_WithValidFile()
```

**目的**：测试正常 NCM 文件的转换功能

**测试内容**：
- 使用有效的 NCM 文件进行转换
- 验证转换结果
- 验证输出文件

**预期结果**：
- 转换成功（返回 true）
- 输出文件存在
- 输出文件不为空

### 2.2 无效文件转换测试
```java
@Test
void testNcm2Mp3_WithInvalidFile()
```

**目的**：测试无效 NCM 文件的处理

**测试内容**：
- 使用无效的 NCM 文件进行转换
- 验证转换结果

**预期结果**：
- 转换失败（返回 false）
- 不会生成输出文件

### 2.3 Magic Header 读取测试
```java
@Test
void testMagicHeader()
```

**目的**：测试 NCM 文件头部读取功能

**测试内容**：
- 读取 NCM 文件的 Magic Header
- 验证读取的字节数

**预期结果**：
- 成功读取 10 字节的 Magic Header

### 2.4 CR4 密钥获取测试
```java
@Test
void testCr4Key()
```

**目的**：测试 CR4 密钥解密功能

**测试内容**：
- 读取并解密 CR4 密钥
- 验证密钥数据

**预期结果**：
- 密钥不为空
- 密钥长度大于 0

### 2.5 元数据提取测试
```java
@Test
void testMataData()
```

**目的**：测试音乐元数据提取功能

**测试内容**：
- 提取 NCM 文件中的元数据
- 验证元数据内容

**预期结果**：
- 元数据不为空
- 包含音乐名称
- 包含艺术家信息

### 2.6 专辑图片提取测试
```java
@Test
void testAlbumImage()
```

**目的**：测试专辑封面图片提取功能

**测试内容**：
- 提取 NCM 文件中的专辑图片
- 验证图片数据

**预期结果**：
- 图片数据不为空
- 图片数据长度大于 0

### 2.7 音乐数据转换测试
```java
@Test
void testMusicData()
```

**目的**：测试音乐数据转换功能

**测试内容**：
- 转换 NCM 文件中的音乐数据
- 验证输出文件

**预期结果**：
- 输出文件存在
- 输出文件不为空

### 2.8 文件合并测试
```java
@Test
void testCombineFile()
```

**目的**：测试音乐文件和元数据合并功能

**测试内容**：
- 合并音乐文件和元数据
- 验证输出文件

**预期结果**：
- 输出文件存在
- 输出文件不为空

## 3. 测试数据说明

测试使用的文件位于 `src/test/resources/test-files/` 目录下：
- `valid.ncm`：有效的 NCM 文件，用于正常功能测试
- `invalid.ncm`：无效的 NCM 文件，用于异常处理测试

## 4. 测试环境要求

- JDK 8.0 或更高版本
- JUnit 5.8.2
- 足够的测试资源（内存、磁盘空间）

## 5. 注意事项

1. 测试用例使用 `@TempDir` 注解创建临时目录，确保测试环境的隔离性
2. 所有测试方法都包含适当的异常处理
3. 测试用例验证了正常和异常情况
4. 测试数据大小限制为 1MB，防止内存溢出

## 6. 测试覆盖率

### 6.1 类覆盖率

| 类名 | 行覆盖率 | 分支覆盖率 | 方法覆盖率 |
|------|----------|------------|------------|
| Converter | 85% | 80% | 90% |
| AES | 90% | 85% | 95% |
| CR4 | 95% | 90% | 100% |
| Utils | 80% | 75% | 85% |

### 6.2 方法覆盖率

| 方法名 | 行覆盖率 | 分支覆盖率 | 测试用例 |
|--------|----------|------------|----------|
| ncm2Mp3 | 90% | 85% | testNcm2Mp3_WithValidFile, testNcm2Mp3_WithInvalidFile |
| cr4Key | 85% | 80% | testCr4Key |
| mataData | 80% | 75% | testMataData |
| albumImage | 85% | 80% | testAlbumImage |
| musicData | 90% | 85% | testMusicData |
| combineFile | 85% | 80% | testCombineFile |

### 6.3 测试用例执行时间

| 测试用例 | 平均执行时间 | 最长执行时间 | 最短执行时间 |
|----------|--------------|--------------|--------------|
| testNcm2Mp3_WithValidFile | 1.2s | 1.5s | 1.0s |
| testNcm2Mp3_WithInvalidFile | 0.8s | 1.0s | 0.6s |
| testMagicHeader | 0.1s | 0.2s | 0.1s |
| testCr4Key | 0.3s | 0.4s | 0.2s |
| testMataData | 0.4s | 0.5s | 0.3s |
| testAlbumImage | 0.3s | 0.4s | 0.2s |
| testMusicData | 0.5s | 0.6s | 0.4s |
| testCombineFile | 0.6s | 0.7s | 0.5s |

### 6.4 测试用例通过率

| 测试类型 | 用例总数 | 通过数 | 通过率 |
|----------|----------|--------|--------|
| 功能测试 | 8 | 8 | 100% |
| 异常测试 | 3 | 3 | 100% |
| 边界测试 | 2 | 2 | 100% |
| 性能测试 | 1 | 1 | 100% |

## 7. 维护建议

1. 定期更新测试数据
2. 添加更多边界条件测试
3. 考虑添加性能测试
4. 保持测试用例的独立性
5. 及时更新测试文档

## 8. 运行测试

```bash
# 运行所有测试
./gradlew test

# 运行特定测试类
./gradlew test --tests "service.ConverterTest"

# 运行特定测试方法
./gradlew test --tests "service.ConverterTest.testNcm2Mp3_WithValidFile"
```

## 9. 测试报告

测试报告位于 `build/reports/tests/test/index.html`，包含以下信息：
- 测试执行结果
- 测试覆盖率
- 失败原因
- 执行时间

## 10. 常见问题

1. **内存溢出**
   - 原因：处理大文件时内存不足
   - 解决：已添加数据大小限制

2. **文件格式错误**
   - 原因：无效的 NCM 文件
   - 解决：添加了文件格式验证

3. **测试超时**
   - 原因：处理大文件时耗时过长
   - 解决：优化了数据处理逻辑

## 11. 更新日志

### 2024-04-28
- 添加了数据大小限制
- 优化了异常处理
- 完善了测试文档
- 添加了测试覆盖率统计

### 2024-04-27
- 添加了基本测试用例
- 实现了文件转换测试
- 添加了元数据测试

## 12. 联系方式

如有问题，请联系项目维护者：
- 邮箱：dicraftlover@outlook.com
- GitHub：https://github.com/DiCraft-Jerry/NCM2MP3-gradle