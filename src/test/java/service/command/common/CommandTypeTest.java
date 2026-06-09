package service.command.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试命令类型枚举
 * 主要测试命令匹配逻辑，包括：
 * 1. 精确匹配命令名称
 * 2. 精确匹配命令别名
 * 3. 未知命令的默认行为
 */
public class CommandTypeTest {

    /**
     * 测试通过短名称匹配 HELP
     * 预期结果：返回 HELP
     */
    @Test
    void testValueFor_HelpShortName() {
        assertEquals(CommandType.HELP, CommandType.valueFor("-h"));
    }

    /**
     * 测试通过别名匹配 HELP
     * 预期结果：返回 HELP
     */
    @Test
    void testValueFor_HelpAlias() {
        assertEquals(CommandType.HELP, CommandType.valueFor("--help"));
    }

    /**
     * 测试通过短名称匹配 CONVERT
     * 预期结果：返回 CONVERT
     */
    @Test
    void testValueFor_ConvertShortName() {
        assertEquals(CommandType.CONVERT, CommandType.valueFor("-c"));
    }

    /**
     * 测试通过别名匹配 CONVERT
     * 预期结果：返回 CONVERT
     */
    @Test
    void testValueFor_ConvertAlias() {
        assertEquals(CommandType.CONVERT, CommandType.valueFor("--convert"));
    }

    /**
     * 测试通过短名称匹配 VIEW
     * 预期结果：返回 VIEW
     */
    @Test
    void testValueFor_ViewShortName() {
        assertEquals(CommandType.VIEW, CommandType.valueFor("-v"));
    }

    /**
     * 测试通过别名匹配 VIEW
     * 预期结果：返回 VIEW
     */
    @Test
    void testValueFor_ViewAlias() {
        assertEquals(CommandType.VIEW, CommandType.valueFor("--view"));
    }

    /**
     * 测试空字符串的默认行为
     * 预期结果：返回 VIEW（默认命令）
     */
    @Test
    void testValueFor_EmptyString() {
        assertEquals(CommandType.VIEW, CommandType.valueFor(""));
    }

    /**
     * 测试未知命令的默认行为
     * 预期结果：返回 VIEW（默认命令）
     */
    @Test
    void testValueFor_UnknownCommand() {
        assertEquals(CommandType.VIEW, CommandType.valueFor("--unknown"));
    }

    /**
     * 测试 null 的默认行为
     * 预期结果：返回 VIEW（默认命令）
     */
    @Test
    void testValueFor_Null() {
        assertEquals(CommandType.VIEW, CommandType.valueFor(null));
    }
}
