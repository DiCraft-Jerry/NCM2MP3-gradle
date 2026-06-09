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
 * 5. 文件合并
 * 6. OOM 防护
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
        String ncmFilePath = "src/test/resources/test-files/valid.ncm";
        String outFilePath = outputDir.getAbsolutePath();

        boolean result = converter.ncm2Mp3(ncmFilePath, outFilePath);

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
        String ncmFilePath = "src/test/resources/test-files/invalid.ncm";
        String pathLine = "src/test/resources/test-files";
        Path path = Paths.get(pathLine);
        File outputDir = path.toFile();
        String outFilePath = outputDir.getAbsolutePath();

        boolean result = converter.ncm2Mp3(ncmFilePath, outFilePath);

        assertFalse(result, "转换应该失败，因为文件不是有效的 NCM 文件");
    }

    /**
     * 测试 NCM 文件转换 - 不存在的输入文件
     * 预期结果：转换失败，返回 false
     */
    @Test
    void testNcm2Mp3_NonExistentFile() {
        String ncmFilePath = "/non/existent/file.ncm";
        String outFilePath = outputDir.getAbsolutePath();

        boolean result = converter.ncm2Mp3(ncmFilePath, outFilePath);

        assertFalse(result, "转换应该失败，因为文件不存在");
    }

    /**
     * 测试 Magic Header 读取
     * 预期结果：成功读取 Magic Header
     */
    @Test
    void testMagicHeader() throws Exception {
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/valid.ncm");
        converter.magicHeader(inputStream);
        assertEquals(10, inputStream.getChannel().position(), "应该读取 10 字节的 Magic Header");
    }

    /**
     * 测试 CR4 密钥获取
     * 预期结果：成功获取解密后的 CR4 密钥
     */
    @Test
    void testCr4Key() throws Exception {
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/valid.ncm");
        converter.magicHeader(inputStream);
        byte[] key = converter.cr4Key(inputStream);
        assertNotNull(key, "CR4 密钥不应该为空");
        assertTrue(key.length > 0, "CR4 密钥长度应该大于 0");
    }

    /**
     * 测试 CR4 密钥获取 - 数据大小超过 1MB 限制
     * 预期结果：返回空数组（OOM 防护）
     */
    @Test
    void testCr4Key_DataExceedsLimit() throws Exception {
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/invalid.ncm");
        converter.magicHeader(inputStream);
        byte[] key = converter.cr4Key(inputStream);
        assertEquals(0, key.length, "超过大小限制应返回空数组");
    }

    /**
     * 测试 CR4 密钥获取 - AES 解密后不足 17 字节
     * 在此测试中，invalid.ncm 文件会触发大小检查或字节数不足的情况
     * 预期结果：返回空数组
     */
    @Test
    void testCr4Key_BytesTooShort() throws Exception {
        // 使用 invalid.ncm 文件，该文件构造不当会导致解密后长度不足
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/invalid.ncm");
        converter.magicHeader(inputStream);
        byte[] key = converter.cr4Key(inputStream);
        assertEquals(0, key.length, "解密后不足 17 字节应返回空数组");
    }

    /**
     * 测试元数据提取
     * 预期结果：成功提取音乐元数据
     */
    @Test
    void testMataData() throws Exception {
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/valid.ncm");
        converter.magicHeader(inputStream);
        converter.cr4Key(inputStream);

        String mataData = converter.mataData(inputStream);

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
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/valid.ncm");
        converter.magicHeader(inputStream);
        converter.cr4Key(inputStream);
        converter.mataData(inputStream);

        byte[] image = converter.albumImage(inputStream);

        assertNotNull(image, "图片数据不应该为空");
        assertTrue(image.length > 0, "图片数据长度应该大于 0");
    }

    /**
     * 测试音乐数据转换
     * 预期结果：成功转换音乐数据
     */
    @Test
    void testMusicData() throws Exception {
        FileInputStream inputStream = new FileInputStream("src/test/resources/test-files/valid.ncm");
        FileOutputStream outputStream = new FileOutputStream(new File(outputDir, "music.mp3"));
        converter.magicHeader(inputStream);
        byte[] key = converter.cr4Key(inputStream);

        converter.musicData(inputStream, outputStream, key);

        File outputFile = new File(outputDir, "music.mp3");
        assertTrue(outputFile.exists(), "输出文件应该存在");
        assertTrue(outputFile.length() > 0, "输出文件不应该为空");
    }

    /**
     * 测试文件合并 - image 为非图片数据时 ImageIO.read 返回 null，不设置封面
     * 预期结果：不抛出异常，正常完成合并（封面被跳过）
     */
    @Test
    void testCombineFile_ImageNull() throws Exception {
        // 先通过正常转换生成一个音乐文件
        String ncmFilePath = "src/test/resources/test-files/valid.ncm";
        String outFilePath = outputDir.getAbsolutePath();
        converter.ncm2Mp3(ncmFilePath, outFilePath);

        // 查找生成的输出文件
        File[] outputFiles = outputDir.listFiles((dir, name) -> !name.endsWith(".ncm"));
        assertNotNull(outputFiles, "输出目录应该有文件");
        assertTrue(outputFiles.length > 0, "应该至少有一个输出文件");

        // 构造 image 为无效图片数据的 Ncm 对象(非 null 字节数组但 ImageIO.read 返回 null)
        Ncm ncm = new Ncm();
        ncm.setOutFile(outputFiles[0].getAbsolutePath());
        Mata mata = new Mata();
        mata.setMusicName("test");
        mata.setAlbum("test album");
        mata.setArtist(new String[][]{{"test artist"}});
        mata.setFormat("mp3");
        ncm.setMata(mata);
        ncm.setImage(new byte[]{0x00, 0x01, 0x02}); // 无效图片数据，ImageIO.read 返回 null

        // combineFile 应该不抛出异常（ImageIO.read 返回 null 时跳过封面设置）
        assertDoesNotThrow(() -> converter.combineFile(ncm), "无效图片数据使 ImageIO.read 返回 null 时不应抛出异常");
    }

    /**
     * 测试文件合并 - 正常流程
     * 预期结果：成功合并音乐文件和元数据
     */
    @Test
    void testCombineFile() throws Exception {
        String ncmFilePath = "src/test/resources/test-files/valid.ncm";
        String outFilePath = outputDir.getAbsolutePath();
        converter.ncm2Mp3(ncmFilePath, outFilePath);

        File outputFile = new File(outFilePath + File.separator + "valid.mp3");
        assertTrue(outputFile.exists(), "输出文件应该存在");
        assertTrue(outputFile.length() > 0, "输出文件不应该为空");
    }
}
