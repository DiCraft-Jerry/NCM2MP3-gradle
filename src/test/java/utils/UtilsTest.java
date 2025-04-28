package utils;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试工具类功能
 * 主要测试通用工具方法，包括：
 * 1. 长度计算
 * 2. 图片类型判断
 * 3. 文件列表获取
 */
public class UtilsTest {

    /**
     * 测试长度计算
     * 预期结果：正确计算字节数组长度
     */
    @Test
    void testGetLength() {
        // 准备测试数据
        byte[] bytes = new byte[]{0x01, 0x00, 0x00, 0x00}; // 小端序 1
        
        // 执行测试
        int length = Utils.getLength(bytes);
        
        // 验证结果
        assertEquals(1, length, "长度应该为 1");
    }

    /**
     * 测试图片类型判断 - PNG
     * 预期结果：正确识别 PNG 图片
     */
    @Test
    void testAlbumImageMimeType_PNG() {
        // 准备测试数据
        byte[] pngHeader = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        };
        
        // 执行测试
        String mimeType = Utils.albumImageMimeType(pngHeader);
        
        // 验证结果
        assertEquals("image/png", mimeType, "应该识别为 PNG 图片");
    }

    /**
     * 测试图片类型判断 - JPG
     * 预期结果：正确识别 JPG 图片
     */
    @Test
    void testAlbumImageMimeType_JPG() {
        // 准备测试数据
        byte[] jpegHeader = new byte[] {
                (byte) 0xFF, (byte) 0xD8, // SOI (Start of Image)
                (byte) 0xFF, (byte) 0xE0, // APP0 Marker
                (byte) 0x00, (byte) 0x10, // Length of APP0 segment (16 bytes)
                (byte) 0x4A, (byte) 0x46, (byte) 0x49, (byte) 0x46, (byte) 0x00, // "JFIF" identifier
                (byte) 0x01, (byte) 0x01, // JFIF version (1.1)
                (byte) 0x00,             // Pixel density units (0 = no units)
                (byte) 0x00, (byte) 0x01, // Horizontal pixel density
                (byte) 0x00, (byte) 0x01, // Vertical pixel density
                (byte) 0x00, (byte) 0x00  // Thumbnail width and height (0x0)
        };
        
        // 执行测试
        String mimeType = Utils.albumImageMimeType(jpegHeader);
        
        // 验证结果
        assertEquals("image/jpg", mimeType, "应该识别为 JPG 图片");
    }

    /**
     * 测试文件列表获取
     * 预期结果：正确获取 NCM 文件列表
     */
    @Test
    void testListAllFiles() {
        // 准备测试数据
        ArrayList<File> files = new ArrayList<>();
        File testDir = new File("src/test/resources/test-files");
        
        // 执行测试
        Utils.listAllFiles(files, testDir);
        
        // 验证结果
        assertFalse(files.isEmpty(), "文件列表不应该为空");
        files.forEach(file -> {
            assertTrue(file.getName().toLowerCase().endsWith(".ncm"), 
                "文件应该是 NCM 格式");
        });
    }
}
