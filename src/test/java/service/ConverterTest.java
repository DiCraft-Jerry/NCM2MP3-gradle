package service;

import mime.Mata;
import mime.Ncm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 测试 NCM 文件转换功能
 * 主要测试 NCM 文件到 MP3 文件的转换过程，包括：
 * 1. 文件解密
 * 2. 元数据提取
 * 3. 封面图片提取
 * 4. 音乐数据转换
 */
public class ConverterTest {

    private Converter converter;
    private File outputDir;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        converter = new Converter();
        outputDir = tempDir.toFile();
    }

    /**
     * 测试 NCM 文件转换功能
     * 预期结果：成功将 NCM 文件转换为 MP3 文件
     */
    @Test
    void testNcm2Mp3_WithValidFile() {
        // 准备测试数据
        String ncmFilePath = "src/test/resources/test-files/valid.ncm";
        String outFilePath = outputDir.getAbsolutePath();

        // 执行转换
        boolean result = converter.ncm2Mp3(ncmFilePath, outFilePath);

        // 验证结果
        assertTrue(result, "转换应该成功");
        File outputFile = new File(outFilePath + File.separator + "valid.mp3");
        assertTrue(outputFile.exists(), "输出文件应该存在");
        assertTrue(outputFile.length() > 0, "输出文件不应该为空");
    }

    /**
     * 测试无效 NCM 文件转换
     * 预期结果：转换失败，返回 false
     */
    @Test
    void testNcm2Mp3_WithInvalidFile() {
        // 准备测试数据
        String ncmFilePath = "src/test/resources/test-files/invalid.ncm";
        String pathLine = "src/test/resources/test-files";
        Path path = Paths.get(pathLine);
        File outputDir = path.toFile();
        String outFilePath = outputDir.getAbsolutePath();
        
        // 执行转换
        boolean result = converter.ncm2Mp3(ncmFilePath, outFilePath);
        
        // 验证结果
        assertFalse(result, "转换应该失败，因为文件不是有效的 NCM 文件");
    }

    /**
     * 测试 Magic Header 读取
     * 预期结果：成功读取 Magic Header
     */
    @Test
    void testMagicHeader() throws Exception {
        // 准备测试数据
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/valid.ncm");

        // 执行测试
        converter.magicHeader(inputStream);

        // 验证结果
        // 由于 magicHeader 是私有方法，我们通过文件指针位置验证
        assertEquals(10, inputStream.getChannel().position(), "应该读取 10 字节的 Magic Header");
    }

    /**
     * 测试 CR4 密钥获取
     * 预期结果：成功获取解密后的 CR4 密钥
     */
    @Test
    void testCr4Key() throws Exception {
        // 准备测试数据
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/valid.ncm");
        converter.magicHeader(inputStream); // 跳过 Magic Header

        // 执行测试
        byte[] key = converter.cr4Key(inputStream);

        // 验证结果
        assertNotNull(key, "CR4 密钥不应该为空");
        assertTrue(key.length > 0, "CR4 密钥长度应该大于 0");
    }

    /**
     * 测试元数据提取
     * 预期结果：成功提取音乐元数据
     */
    @Test
    void testMataData() throws Exception {
        // 准备测试数据
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/valid.ncm");
        converter.magicHeader(inputStream); // 跳过 Magic Header
        converter.cr4Key(inputStream); // 跳过 CR4 密钥

        // 执行测试
        String mataData = converter.mataData(inputStream);

        // 验证结果
        assertNotNull(mataData, "元数据不应该为空");
        assertTrue(mataData.contains("musicName"), "元数据应该包含音乐名称");
        assertTrue(mataData.contains("artist"), "元数据应该包含艺术家信息");
    }

    /**
     * 测试专辑图片提取
     * 预期结果：成功提取专辑封面图片
     */
    @Test
    void testAlbumImage() throws Exception {
        // 准备测试数据
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/valid.ncm");
        converter.magicHeader(inputStream); // 跳过 Magic Header
        converter.cr4Key(inputStream); // 跳过 CR4 密钥
        converter.mataData(inputStream); // 跳过元数据

        // 执行测试
        byte[] image = converter.albumImage(inputStream);

        // 验证结果
        assertNotNull(image, "图片数据不应该为空");
        assertTrue(image.length > 0, "图片数据长度应该大于 0");
    }

    /**
     * 测试音乐数据转换
     * 预期结果：成功转换音乐数据
     */
    @Test
    void testMusicData() throws Exception {
        // 准备测试数据
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/valid.ncm");
        FileOutputStream outputStream = new FileOutputStream(new File(outputDir, "music.mp3"));
        converter.magicHeader(inputStream); // 跳过 Magic Header
        byte[] key = converter.cr4Key(inputStream); // 获取 CR4 密钥

        // 执行测试
        converter.musicData(inputStream, outputStream, key);

        // 验证结果
        File outputFile = new File(outputDir, "music.mp3");
        assertTrue(outputFile.exists(), "输出文件应该存在");
        assertTrue(outputFile.length() > 0, "输出文件不应该为空");
    }
}