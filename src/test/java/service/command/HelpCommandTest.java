package service.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.ExitInterceptor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试帮助命令的输出
 */
public class HelpCommandTest {

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
     * 测试帮助命令输出
     * 预期结果：打印包含命令列表的帮助信息，然后调用 System.exit(0)
     */
    @Test
    void testHandle_PrintsHelp() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            HelpCommand command = new HelpCommand();
            try {
                command.handle(new java.util.ArrayList<>());
            } catch (SecurityException e) {
                // System.exit(0) 被 ExitInterceptor 拦截，属于预期行为
            }
        } finally {
            System.setOut(originalOut);
        }

        String output = outContent.toString();
        assertTrue(output.contains("Usage"), "应该包含使用说明");
        assertTrue(output.contains("Command List"), "应该包含命令列表");
        assertTrue(output.contains("-v"), "应该包含 view 命令");
        assertTrue(output.contains("-c"), "应该包含 convert 命令");
        assertTrue(output.contains("-h"), "应该包含 help 命令");
        assertTrue(exitInterceptor.wasExitCalled(), "System.exit() 应该被调用");
        assertEquals(0, exitInterceptor.getExitStatus(), "退出状态码应为 0");
    }
}
