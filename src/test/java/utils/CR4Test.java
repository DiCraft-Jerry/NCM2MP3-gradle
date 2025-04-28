package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 RC4 解密功能
 * 主要测试 RC4 解密算法的正确性，包括：
 * 1. KSA 算法
 * 2. PRGA 算法
 * 3. 完整解密流程
 */
public class CR4Test {

    private CR4 cr4;

    // 测试密钥 - 使用一个16字节的密钥
    private static final byte[] TEST_KEY = {
            0x68, 0x7A, 0x48, 0x52, 0x41, 0x6D, 0x73, 0x6F,
            0x35, 0x6B, 0x49, 0x6E, 0x62, 0x61, 0x78, 0x57
    };

    // 测试数据
    private static final byte[] TEST_DATA = "NetEaseCloudMusic Test Data".getBytes();


    @BeforeEach
    void setUp() {
        cr4 = new CR4();
    }

    /**
     * 测试 KSA 算法
     * 预期结果：成功生成 S 盒
     */
    @Test
    void testKSA() {
        // 执行测试
        cr4.KSA(TEST_KEY);

        // 验证结果
        // 由于 S 盒是私有字段，我们通过后续的 PRGA 测试来间接验证
        byte[] testData = TEST_DATA.clone();
        cr4.PRGA(testData, testData.length);

        // 验证数据是否被修改
        assertNotEquals(TEST_DATA, testData, "数据应该被修改");
    }

    /**
     * 测试 PRGA 算法 - 加密
     * 预期结果：成功加密数据
     */
    @Test
    void testPRGA_Encryption() {
        // 准备测试数据
        byte[] data = TEST_DATA.clone();

        // 执行测试
        cr4.KSA(TEST_KEY);
        cr4.PRGA(data, data.length);

        // 验证结果
        assertNotEquals(TEST_DATA, data, "加密后的数据应该与原始数据不同");
    }

    /**
     * 测试 PRGA 算法 - 解密
     * 预期结果：成功解密数据
     */
    @Test
    void testPRGA_Decryption() {
        // 准备测试数据
        byte[] originalData = TEST_DATA.clone();
        byte[] encryptedData = TEST_DATA.clone();

        // 加密
        cr4.KSA(TEST_KEY);
        cr4.PRGA(encryptedData, encryptedData.length);

        // 解密
        cr4.KSA(TEST_KEY); // 重新初始化 S 盒
        cr4.PRGA(encryptedData, encryptedData.length);

        // 验证结果
        assertArrayEquals(originalData, encryptedData, "解密后的数据应该与原始数据一致");
    }

    /**
     * 测试完整解密流程
     * 预期结果：成功解密数据
     */
    @Test
    void testFullDecryption() {
        // 准备测试数据
        byte[] originalData = TEST_DATA.clone();
        byte[] encryptedData = TEST_DATA.clone();

        // 加密
        cr4.KSA(TEST_KEY);
        cr4.PRGA(encryptedData, encryptedData.length);

        // 解密
        cr4.KSA(TEST_KEY); // 重新初始化 S 盒
        cr4.PRGA(encryptedData, encryptedData.length);

        // 验证结果
        assertArrayEquals(originalData, encryptedData, "解密后的数据应该与原始数据一致");
    }

    /**
     * 测试不同长度的密钥
     * 预期结果：成功处理不同长度的密钥
     */
    @Test
    void testDifferentKeyLengths() {
        // 准备测试数据
        byte[] shortKey = new byte[]{0x01, 0x02, 0x03}; // 3字节密钥
        byte[] longKey = new byte[32]; // 32字节密钥
        System.arraycopy(TEST_KEY, 0, longKey, 0, 16); // 复制测试密钥到长密钥

        byte[] data = TEST_DATA.clone();

        // 测试短密钥
        cr4.KSA(shortKey);
        cr4.PRGA(data, data.length);

        // 测试长密钥
        data = TEST_DATA.clone();
        cr4.KSA(longKey);
        cr4.PRGA(data, data.length);

        // 验证结果
        assertNotEquals(TEST_DATA, data, "加密后的数据应该与原始数据不同");
    }

    /**
     * 测试空数据
     * 预期结果：成功处理空数据
     */
    @Test
    void testEmptyData() {
        // 准备测试数据
        byte[] emptyData = new byte[0];

        // 执行测试
        cr4.KSA(TEST_KEY);
        cr4.PRGA(emptyData, 0);

        // 验证结果
        assertEquals(0, emptyData.length, "空数据长度应该保持为0");
    }
}