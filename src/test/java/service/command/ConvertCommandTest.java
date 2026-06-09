package service.command;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试转换命令的目录处理逻辑
 */
public class ConvertCommandTest {

    /**
     * 测试转换命令 - 传入不存在的路径
     * 预期结果：创建 output 目录
     */
    @Test
    void testHandle_NonExistentPath() {
        ConvertCommand command = new ConvertCommand();
        List<String> params = new ArrayList<>();
        params.add("/non/existent/path");

        command.handle(params);

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
    }

    /**
     * 测试转换命令 - output 目录已存在时复用
     * 预期结果：复用已存在的目录
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

        command.handle(params);

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
    }
}
