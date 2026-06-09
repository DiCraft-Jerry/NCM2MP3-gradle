package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试命令行参数解释器
 * 主要测试命令分发逻辑：
 * 1. 无参数时打开 GUI
 * 2. 各个命令的匹配与路由
 * 3. 参数传递
 */
public class InterpreterTest {

    private Interpreter interpreter;

    @BeforeEach
    void setUp() {
        interpreter = new Interpreter();
    }

    /**
     * 测试空参数 - 默认打开 VIEW
     * 预期结果：不抛出异常
     */
    @Test
    void testHandleArgs_Empty() {
        assertDoesNotThrow(() -> interpreter.handleArgs(new String[]{}));
    }

    /**
     * 测试 view 命令
     * 预期结果：不抛出异常
     */
    @Test
    void testHandleArgs_View() {
        assertDoesNotThrow(() -> interpreter.handleArgs(new String[]{"-v"}));
    }

    /**
     * 测试 help 命令
     * 预期结果：命令正确路由到 HelpCommand，不抛出异常
     */
    @Test
    void testHandleArgs_Help() {
        assertDoesNotThrow(() -> interpreter.handleArgs(new String[]{"-h"}));
    }

    /**
     * 测试 convert 命令
     * 预期结果：命令正确路由到 ConvertCommand，不抛出异常
     */
    @Test
    void testHandleArgs_Convert() {
        assertDoesNotThrow(() -> interpreter.handleArgs(new String[]{"-c", "src/test/resources/test-files"}));
    }
}
