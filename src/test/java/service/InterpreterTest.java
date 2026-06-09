package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试命令行参数解释器
 * 主要测试命令分发逻辑。
 * 注意：由于 HelpCommand 和 ConvertCommand 会调用 System.exit(0)，
 * 在 Gradle 测试环境下会导致测试执行器跳过后续测试类。
 * 因此这里仅测试不会触发 System.exit 的路径。
 */
public class InterpreterTest {

    private Interpreter interpreter;

    @BeforeEach
    void setUp() {
        interpreter = new Interpreter();
    }

    /**
     * 测试空参数 - 默认打开 VIEW
     * 预期结果：不抛出异常（ViewCommand 不会调 System.exit）
     */
    @Test
    void testHandleArgs_Empty() {
        assertDoesNotThrow(() -> interpreter.handleArgs(new String[]{}));
    }

    /**
     * 测试 view 命令
     * 预期结果：不抛出异常（ViewCommand 不会调 System.exit）
     */
    @Test
    void testHandleArgs_View() {
        assertDoesNotThrow(() -> interpreter.handleArgs(new String[]{"-v"}));
    }
}
