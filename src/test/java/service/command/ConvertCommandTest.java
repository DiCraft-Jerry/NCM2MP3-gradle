package service.command;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试转换命令的目录处理逻辑。
 * 注意：此测试需要重构 ConvertCommand 移除 System.exit(0) 后才能启用。
 * 当前 Gradle 8.14 + Java 17 环境无 SecurityManager，System.exit 会直接杀死测试 JVM。
 */
@Disabled("ConvertCommand.handle() 调用 System.exit(0) 会杀死 Gradle 测试 Worker JVM")
public class ConvertCommandTest {

    /**
     * 测试转换命令 - 对象创建
     * 验证 ConvertCommand 可正常实例化
     */
    @Test
    void testCommandCreation() {
        ConvertCommand command = new ConvertCommand();
        assertNotNull(command, "ConvertCommand 应该被成功创建");
    }
}
