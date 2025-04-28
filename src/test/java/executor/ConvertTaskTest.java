package executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author 烛远
 * 测试转换任务功能
 * 主要测试转换任务的执行过程，包括：
 * 1. 任务创建
 * 2. 任务执行
 * 3. 状态更新
 */
public class ConvertTaskTest {

    private ConvertTask convertTask;
    private DefaultTableModel tableModel;
    private File testNcmFile;
    private File outputDir;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        outputDir = tempDir.toFile();
        
        // 创建表格模型
        String[] columnNames = {"音乐名", "文件路径", "文件大小", "状态"};
        Object[][] data = new Object[0][4];
        tableModel = new DefaultTableModel(data, columnNames);
        tableModel.addRow(new Object[]{"test.ncm", "test/path", "1024", "准备转换"});
        
        // 创建测试文件目录
        File testDir = new File("src/test/resources/test-files");
        if (!testDir.exists()) {
            assertTrue(testDir.mkdirs(), "无法创建测试目录");
        }
        
        // 创建测试文件
        testNcmFile = new File(testDir, "test.ncm");
        try {
            if (!testNcmFile.exists()) {
                assertTrue(testNcmFile.createNewFile(), "无法创建测试文件");
            }
        } catch (IOException e) {
            fail("创建测试文件失败: " + e.getMessage());
        }
    }

    /**
     * 测试任务创建
     * 预期结果：成功创建转换任务
     */
    @Test
    void testTaskCreation() {
        // 准备测试数据
        String ncmFilePath = testNcmFile.getAbsolutePath();
        String outFilePath = outputDir.getAbsolutePath();
        
        // 执行测试
        convertTask = new ConvertTask(ncmFilePath, outFilePath, tableModel, 0);
        
        // 验证结果
        assertNotNull(convertTask, "转换任务不应该为空");
        assertEquals("转换中..", tableModel.getValueAt(0, 3), "任务状态应该为'转换中..'");
    }

    /**
     * 测试任务执行失败 - 无效的 NCM 文件
     * 预期结果：转换失败，状态更新为失败
     */
    @Test
    void testTaskExecution_InvalidFile() throws Exception {
        // 准备测试数据
        String ncmFilePath = testNcmFile.getAbsolutePath();
        String outFilePath = outputDir.getAbsolutePath();
        convertTask = new ConvertTask(ncmFilePath, outFilePath, tableModel, 0);
        
        // 执行测试
        Boolean result = convertTask.call();
        
        // 验证结果
        assertFalse(result, "转换应该失败，因为测试文件不是有效的 NCM 文件");
        assertEquals("转换失败", tableModel.getValueAt(0, 3), "任务状态应该为'转换失败'");
    }

    /**
     * 测试任务执行失败 - 文件不存在
     * 预期结果：转换失败，状态更新为失败
     */
    @Test
    void testTaskExecution_FileNotFound() throws Exception {
        // 准备测试数据
        String ncmFilePath = "non_existent_file.ncm";
        String outFilePath = outputDir.getAbsolutePath();
        convertTask = new ConvertTask(ncmFilePath, outFilePath, tableModel, 0);
        
        // 执行测试
        Boolean result = convertTask.call();
        
        // 验证结果
        assertFalse(result, "转换应该失败，因为文件不存在");
        assertEquals("转换失败", tableModel.getValueAt(0, 3), "任务状态应该为'转换失败'");
    }

    /**
     * 测试任务执行失败 - 输出目录不存在
     * 预期结果：转换失败，状态更新为失败
     */
    @Test
    void testTaskExecution_InvalidOutputDir() throws Exception {
        // 准备测试数据
        String ncmFilePath = testNcmFile.getAbsolutePath();
        String outFilePath = "/non/existent/directory";
        convertTask = new ConvertTask(ncmFilePath, outFilePath, tableModel, 0);
        
        // 执行测试
        Boolean result = convertTask.call();
        
        // 验证结果
        assertFalse(result, "转换应该失败，因为输出目录不存在");
        assertEquals("转换失败", tableModel.getValueAt(0, 3), "任务状态应该为'转换失败'");
    }
}