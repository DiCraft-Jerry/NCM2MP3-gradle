package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 AES 解密功能
 * 主要测试 AES 解密算法的正确性，包括：
 * 1. 正常解密
 * 2. 异常情况处理
 */
public class AESTest {

    private static final byte[] TEST_KEY = {
            0x68, 0x7A, 0x48, 0x52, 0x41, 0x6D, 0x73, 0x6F,
            0x35, 0x6B, 0x49, 0x6E, 0x62, 0x61, 0x78, 0x57
    }; // 使用 CORE_KEY 作为测试密钥

    private byte[] encryptedData;

    @BeforeEach
    void setUp() throws Exception {
        // 准备加密数据
        String testData = "NetEaseCloudMusic Test Data";
        Cipher cipher = Cipher.getInstance(AES.TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(TEST_KEY, AES.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        encryptedData = cipher.doFinal(testData.getBytes());
    }

    /**
     * 测试正常解密
     * 预期结果：成功解密数据
     */
    @Test
    void testDecrypt_WithValidData() throws Exception {

        // 执行解密
        byte[] decryptedData = AES.decrypt(encryptedData, TEST_KEY, AES.TRANSFORMATION, AES.ALGORITHM);

        // 验证结果
        assertNotNull(decryptedData, "解密后的数据不应该为空");
        assertTrue(decryptedData.length > 0, "解密后的数据长度应该大于 0");
    }

    /**
     * 测试使用 MATA_KEY 解密
     * 预期结果：成功解密数据
     */
    @Test
    void testDecrypt_WithMataKey() throws Exception {
        // 准备测试数据
        String testData = "Music Metadata Test";
        Cipher cipher = Cipher.getInstance(AES.TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(AES.MATA_KEY, AES.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] mataEncryptedData = cipher.doFinal(testData.getBytes());

        // 执行解密
        byte[] decryptedData = AES.decrypt(mataEncryptedData, AES.MATA_KEY, AES.TRANSFORMATION, AES.ALGORITHM);

        // 验证结果
        assertNotNull(decryptedData, "解密后的数据不应该为空");
        assertEquals("Music Metadata Test", new String(decryptedData),
                "解密后的数据应该与原始数据一致");
    }

    /**
     * 测试无效密钥解密
     * 预期结果：抛出异常
     */
    @Test
    void testDecrypt_WithInvalidKey() {
        // 准备测试数据
        byte[] invalidKey = new byte[16]; // 无效密钥

        // 执行测试并验证异常
        assertThrows(Exception.class, () -> {
            AES.decrypt(encryptedData, invalidKey, AES.TRANSFORMATION, AES.ALGORITHM);
        }, "使用无效密钥应该抛出异常");
    }

    /**
     * 测试非法数据解密
     * 预期结果：抛出异常
     * 原因：AES 加密数据块必须是 16 字节的倍数（使用 PKCS5Padding 填充）
     */
    @Test
    void testDecrypt_WithInvalidData() {
        // 准备测试数据 - 使用长度不是 16 字节倍数的数据
        byte[] invalidData = new byte[]{0x01, 0x02, 0x03}; // 3字节数据，不符合 AES 块大小要求

        // 执行测试并验证异常
        assertThrows(Exception.class, () -> {
            AES.decrypt(invalidData, TEST_KEY, AES.TRANSFORMATION, AES.ALGORITHM);
        }, "使用非法长度的数据应该抛出异常");
    }

    /**
     * 测试空数据解密
     * 预期结果：返回空数组
     */
    @Test
    void testDecrypt_WithEmptyData() throws Exception {
        // 准备测试数据
        byte[] emptyData = new byte[0];

        // 执行测试
        byte[] result = AES.decrypt(emptyData, TEST_KEY, AES.TRANSFORMATION, AES.ALGORITHM);

        // 验证结果
        assertNotNull(result, "结果不应该为 null");
        assertEquals(0, result.length, "空数据解密应该返回空数组");
    }
}