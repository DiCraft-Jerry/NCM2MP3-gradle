package service.command;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试帮助命令的输出
 */
public class HelpCommandTest {

    /**
     * 测试帮助命令输出
     * 预期结果：打印包含命令列表的帮助信息
     */
    @Test
    void testHandle_PrintsHelp() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            HelpCommand command = new HelpCommand();
            command.handle(new ArrayList<>());
        } finally {
            System.setOut(originalOut);
        }

        String output = outContent.toString();
        assertTrue(output.contains("Usage"), "应该包含使用说明");
        assertTrue(output.contains("Command List"), "应该包含命令列表");
        assertTrue(output.contains("-v"), "应该包含 view 命令");
        assertTrue(output.contains("-c"), "应该包含 convert 命令");
        assertTrue(output.contains("-h"), "应该包含 help 命令");
    }
}
