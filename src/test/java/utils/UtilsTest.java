package utils;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试工具类功能
 * 主要测试通用工具方法，包括：
 * 1. 长度计算
 * 2. 图片类型判断
 * 3. 文件列表获取
 * 4. 异步任务等待
 */
public class UtilsTest {

    /**
     * 测试长度计算 - 值 1
     * 预期结果：正确计算字节数组长度
     */
    @Test
    void testGetLength_Value1() {
        byte[] bytes = new byte[]{0x01, 0x00, 0x00, 0x00}; // 小端序 1
        int length = Utils.getLength(bytes);
        assertEquals(1, length, "长度应该为 1");
    }

    /**
     * 测试长度计算 - 值 0
     * 预期结果：正确计算为 0
     */
    @Test
    void testGetLength_Value0() {
        byte[] bytes = new byte[]{0x00, 0x00, 0x00, 0x00};
        int length = Utils.getLength(bytes);
        assertEquals(0, length, "长度应该为 0");
    }

    /**
     * 测试长度计算 - 最大值
     * 预期结果：正确计算 0xFFFFFFFF（由于 Java int 是有符号的，值为 -1，但位模式正确）
     */
    @Test
    void testGetLength_MaxValue() {
        byte[] bytes = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        int length = Utils.getLength(bytes);
        // 0xFFFFFFFF 作为有符号 int 是 -1，但位模式是正确的
        assertEquals(-1, length, "0xFFFFFFFF 作为int为 -1");
    }

    /**
     * 测试长度计算 - 较大值
     * 预期结果：正确计算多字节值
     */
    @Test
    void testGetLength_LargeValue() {
        byte[] bytes = new byte[]{(byte) 0xFF, (byte) 0xFF, 0x00, 0x00}; // 小端序 0x0000FFFF = 65535
        int length = Utils.getLength(bytes);
        assertEquals(65535, length, "长度应该为 65535");
    }

    /**
     * 测试图片类型判断 - PNG
     * 预期结果：正确识别 PNG 图片
     */
    @Test
    void testAlbumImageMimeType_PNG() {
        byte[] pngHeader = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        };
        String mimeType = Utils.albumImageMimeType(pngHeader);
        assertEquals("image/png", mimeType, "应该识别为 PNG 图片");
    }

    /**
     * 测试图片类型判断 - JPG
     * 预期结果：正确识别 JPG 图片
     */
    @Test
    void testAlbumImageMimeType_JPG() {
        byte[] jpegHeader = new byte[] {
                (byte) 0xFF, (byte) 0xD8,
                (byte) 0xFF, (byte) 0xE0,
                (byte) 0x00, (byte) 0x10,
                (byte) 0x4A, (byte) 0x46, (byte) 0x49, (byte) 0x46, (byte) 0x00,
                (byte) 0x01, (byte) 0x01,
                (byte) 0x00,
                (byte) 0x00, (byte) 0x01,
                (byte) 0x00, (byte) 0x01,
                (byte) 0x00, (byte) 0x00
        };
        String mimeType = Utils.albumImageMimeType(jpegHeader);
        assertEquals("image/jpg", mimeType, "应该识别为 JPG 图片");
    }

    /**
     * 测试图片类型判断 - 恰好 8 字节
     * 预期结果：正确识别 PNG 图片
     */
    @Test
    void testAlbumImageMimeType_Exactly8Bytes() {
        byte[] pngHeader = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        };
        String mimeType = Utils.albumImageMimeType(pngHeader);
        assertEquals("image/png", mimeType, "恰好 8 字节的 PNG 头应该识别为 PNG");
    }

    /**
     * 测试图片类型判断 - 小于 8 字节
     * 预期结果：默认为 image/png
     */
    @Test
    void testAlbumImageMimeType_LessThan8Bytes() {
        byte[] shortData = new byte[]{0x01, 0x02, 0x03};
        String mimeType = Utils.albumImageMimeType(shortData);
        assertEquals("image/png", mimeType, "小于 8 字节默认为 PNG");
    }

    /**
     * 测试文件列表获取
     * 预期结果：正确获取 NCM 文件列表
     */
    @Test
    void testListAllFiles() {
        ArrayList<File> files = new ArrayList<>();
        File testDir = new File("src/test/resources/test-files");
        Utils.listAllFiles(files, testDir);
        assertFalse(files.isEmpty(), "文件列表不应该为空");
        files.forEach(file -> {
            assertTrue(file.getName().toLowerCase().endsWith(".ncm"),
                "文件应该是 NCM 格式");
        });
    }

    /**
     * 测试文件列表获取 - 空目录
     * 预期结果：返回空列表
     */
    @Test
    void testListAllFiles_EmptyDirectory() throws Exception {
        ArrayList<File> files = new ArrayList<>();
        File emptyDir = new File("build/tmp/test_empty_dir");
        emptyDir.mkdirs();
        try {
            Utils.listAllFiles(files, emptyDir);
            assertTrue(files.isEmpty(), "空目录应该返回空列表");
        } finally {
            emptyDir.delete();
        }
    }

    /**
     * 测试文件列表获取 - 单个 NCM 文件
     * 预期结果：正确识别和添加 NCM 文件
     */
    @Test
    void testListAllFiles_SingleNcmFile() {
        ArrayList<File> files = new ArrayList<>();
        File testFile = new File("src/test/resources/test-files/valid.ncm");
        Utils.listAllFiles(files, testFile);
        assertEquals(1, files.size(), "应该包含一个文件");
        assertEquals("valid.ncm", files.get(0).getName(), "文件名应该匹配");
    }

    /**
     * 测试 waitForAllTask - 全部成功
     * 预期结果：正常完成，不抛出异常
     */
    @Test
    void testWaitForAllTask_AllSuccess() {
        Collection<Future<Boolean>> futures = new ArrayList<>();
        futures.add(CompletableFuture.completedFuture(true));
        futures.add(CompletableFuture.completedFuture(true));
        futures.add(CompletableFuture.completedFuture(true));

        // 不应抛出异常
        assertDoesNotThrow(() -> Utils.waitForAllTask(futures, result -> result));
    }

    /**
     * 测试 waitForAllTask - 全部失败
     * 预期结果：正常完成，不抛出异常
     */
    @Test
    void testWaitForAllTask_AllFailure() {
        Collection<Future<Boolean>> futures = new ArrayList<>();
        futures.add(CompletableFuture.completedFuture(false));
        futures.add(CompletableFuture.completedFuture(false));

        assertDoesNotThrow(() -> Utils.waitForAllTask(futures, result -> result));
    }

    /**
     * 测试 waitForAllTask - 成功和失败混合
     * 预期结果：正常完成，不抛出异常
     */
    @Test
    void testWaitForAllTask_Mixed() {
        Collection<Future<Boolean>> futures = new ArrayList<>();
        futures.add(CompletableFuture.completedFuture(true));
        futures.add(CompletableFuture.completedFuture(false));
        futures.add(CompletableFuture.completedFuture(true));

        assertDoesNotThrow(() -> Utils.waitForAllTask(futures, result -> result));
    }

    /**
     * 测试 waitForAllTask - 空集合
     * 预期结果：正常完成，不抛出异常
     */
    @Test
    void testWaitForAllTask_Empty() {
        Collection<Future<Boolean>> futures = Collections.emptyList();
        assertDoesNotThrow(() -> Utils.waitForAllTask(futures, result -> result));
    }

    /**
     * 测试 waitForAllTask - 单个 Future 异常
     * 预期结果：异常被捕获，不影响后续任务
     */
    @Test
    void testWaitForAllTask_SingleException() {
        Collection<Future<Boolean>> futures = new ArrayList<>();
        CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Test exception"));
        futures.add(failedFuture);
        futures.add(CompletableFuture.completedFuture(true));

        assertDoesNotThrow(() -> Utils.waitForAllTask(futures, result -> result));
    }
}
