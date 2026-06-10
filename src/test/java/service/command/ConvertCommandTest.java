package service.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.ExitInterceptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试转换命令的目录处理逻辑
 */
public class ConvertCommandTest {

    private ExitInterceptor exitInterceptor;

    @BeforeEach
    void setUp() {
        exitInterceptor = new ExitInterceptor();
        exitInterceptor.install();
    }

    @AfterEach
    void tearDown() {
        exitInterceptor.uninstall();
    }

    /**
     * 测试转换命令 - 传入不存在的路径
     * 预期结果：创建 output 目录，handle 执行完毕后调用 System.exit(0)
     */
    @Test
    void testHandle_NonExistentPath() {
        ConvertCommand command = new ConvertCommand();
        List<String> params = new ArrayList<>();
        params.add("/non/existent/path");

        try {
            command.handle(params);
        } catch (SecurityException e) {
            // System.exit(0) 被 ExitInterceptor 拦截，属于预期行为
        }

        java.io.File outputDir = new java.io.File("output");
        boolean created = outputDir.exists();
        // 清理
        if (outputDir.isDirectory()) {
            java.io.File[] files = outputDir.listFiles();
            if (files != null) {
                for (java.io.File f : files) {
                    f.delete();
                }
            }
            outputDir.delete();
        }
        assertTrue(created, "output 目录应该被创建");
        assertTrue(exitInterceptor.wasExitCalled(), "System.exit() 应该被调用");
        assertEquals(0, exitInterceptor.getExitStatus(), "退出状态码应为 0");
    }

    /**
     * 测试转换命令 - output 目录已存在时复用
     * 预期结果：复用已存在的目录，handle 执行完毕后调用 System.exit(0)
     */
    @Test
    void testHandle_OutputDirAlreadyExists() {
        // 先创建 output 目录
        java.io.File outputDir = new java.io.File("output");
        outputDir.mkdirs();
        assertTrue(outputDir.exists(), "前置条件: output 目录应已存在");

        ConvertCommand command = new ConvertCommand();
        List<String> params = new ArrayList<>();
        params.add("src/test/resources/test-files");

        try {
            command.handle(params);
        } catch (SecurityException e) {
            // System.exit(0) 被 ExitInterceptor 拦截，属于预期行为
        }

        // 清理
        if (outputDir.isDirectory()) {
            java.io.File[] files = outputDir.listFiles();
            if (files != null) {
                for (java.io.File f : files) {
                    f.delete();
                }
            }
            outputDir.delete();
        }
        assertTrue(exitInterceptor.wasExitCalled(), "System.exit() 应该被调用");
        assertEquals(0, exitInterceptor.getExitStatus(), "退出状态码应为 0");
    }
}
