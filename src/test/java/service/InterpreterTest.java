package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.ExitInterceptor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试命令行参数解释器
 * 主要测试命令分发逻辑：
 * 1. 无参数时打开 GUI
 * 2. 各个命令的匹配与路由
 * 3. 参数传递
 *
 * <p><b>关于 ExitInterceptor：</b>
 * HelpCommand 和 ConvertCommand 在 handle() 末尾调用 System.exit(0) 确保进程退出。
 * 在 Java 17+ 的 Gradle 测试环境下，这会杀死测试 Worker JVM。
 * 使用 ExitInterceptor(SecurityManager) 拦截 System.exit() 调用，将其转为
 * SecurityException，从而验证退出逻辑同时保护测试进程。
 *
 * <p><b>注意：</b>
 * View 测试（testHandleArgs_Empty、testHandleArgs_View）不安装 ExitInterceptor，
 * 因为 FlatLaf/JFrame 在 macOS + SecurityManager 下会间接触发 System.exit()，
 * 导致误判。ViewCommand 本身不调用 System.exit()，无需拦截。
 */
public class InterpreterTest {

    private Interpreter interpreter;

    @BeforeEach
    void setUp() {
        interpreter = new Interpreter();
    }

    /**
     * 测试空参数 - 默认打开 VIEW
     * 预期结果：ViewCommand 创建 GUI 窗口，不调用 System.exit(0)
     */
    @Test
    void testHandleArgs_Empty() {
        assertDoesNotThrow(() -> interpreter.handleArgs(new String[]{}));
    }

    /**
     * 测试 view 命令
     * 预期结果：ViewCommand 创建 GUI 窗口，不调用 System.exit(0)
     */
    @Test
    void testHandleArgs_View() {
        assertDoesNotThrow(() -> interpreter.handleArgs(new String[]{"-v"}));
    }

    /**
     * 测试 help 命令路由
     * 预期结果：命令路由到 HelpCommand，打印帮助信息后调用 System.exit(0)，
     * 被 ExitInterceptor 拦截为 SecurityException。
     */
    @Test
    void testHandleArgs_Help() {
        ExitInterceptor exitInterceptor = new ExitInterceptor();
        exitInterceptor.install();
        try {
            assertThrows(SecurityException.class, () ->
                    interpreter.handleArgs(new String[]{"-h"}),
                    "HelpCommand 应该调用 System.exit(0)，触发 SecurityException");
            assertTrue(exitInterceptor.wasExitCalled(), "System.exit() 应该被调用");
            assertEquals(0, exitInterceptor.getExitStatus(), "退出状态码应为 0");
        } finally {
            exitInterceptor.uninstall();
        }
    }

    /**
     * 测试 convert 命令路由
     * 预期结果：命令路由到 ConvertCommand，转换完成后调用 System.exit(0)，
     * 被 ExitInterceptor 拦截为 SecurityException。
     */
    @Test
    void testHandleArgs_Convert() {
        ExitInterceptor exitInterceptor = new ExitInterceptor();
        exitInterceptor.install();
        try {
            assertThrows(SecurityException.class, () ->
                    interpreter.handleArgs(new String[]{"-c", "src/test/resources/test-files"}),
                    "ConvertCommand 应该调用 System.exit(0)，触发 SecurityException");
            assertTrue(exitInterceptor.wasExitCalled(), "System.exit() 应该被调用");
            assertEquals(0, exitInterceptor.getExitStatus(), "退出状态码应为 0");
        } finally {
            exitInterceptor.uninstall();
        }
    }
}
