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
 * 2. 任务执行（成功和失败路径）
 * 3. 状态更新
 */
public class ConvertTaskTest {

    private DefaultTableModel tableModel;
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
    }

    /**
     * 测试任务创建
     * 预期结果：成功创建转换任务，状态设为"转换中.."
     */
    @Test
    void testTaskCreation() {
        tableModel.addRow(new Object[]{"test.ncm", "test/path", "1024", "准备转换"});
        ConvertTask task = new ConvertTask("test/path", outputDir.getAbsolutePath(), tableModel, 0);
        assertNotNull(task, "转换任务不应该为空");
        assertEquals("转换中..", tableModel.getValueAt(0, 3), "任务状态应该为'转换中..'");
    }

    /**
     * 测试任务执行成功 - 有效的 NCM 文件
     * 预期结果：转换成功，状态更新为"转换完毕"
     */
    @Test
    void testTaskExecution_ValidFile() throws Exception {
        String ncmFilePath = "src/test/resources/test-files/valid.ncm";
        tableModel.addRow(new Object[]{"valid.ncm", ncmFilePath, "1024", "准备转换"});
        ConvertTask task = new ConvertTask(ncmFilePath, outputDir.getAbsolutePath(), tableModel, 0);

        Boolean result = task.call();
        assertTrue(result, "转换应该成功");
        assertEquals("转换完毕", tableModel.getValueAt(0, 3), "任务状态应该为'转换完毕'");
    }

    /**
     * 测试任务执行失败 - 无效的 NCM 文件
     * 预期结果：转换失败，状态更新为"转换失败"
     */
    @Test
    void testTaskExecution_InvalidFile() throws Exception {
        // 创建无效文件
        File invalidFile = new File(outputDir, "invalid.ncm");
        invalidFile.createNewFile();

        tableModel.addRow(new Object[]{"invalid.ncm", invalidFile.getAbsolutePath(), "1024", "准备转换"});
        ConvertTask task = new ConvertTask(invalidFile.getAbsolutePath(), outputDir.getAbsolutePath(), tableModel, 0);

        Boolean result = task.call();
        assertFalse(result, "转换应该失败");
        assertEquals("转换失败", tableModel.getValueAt(0, 3), "任务状态应该为'转换失败'");
    }

    /**
     * 测试任务执行失败 - 文件不存在
     * 预期结果：转换失败，状态更新为"转换失败"
     */
    @Test
    void testTaskExecution_FileNotFound() throws Exception {
        tableModel.addRow(new Object[]{"missing.ncm", "non_existent_file.ncm", "1024", "准备转换"});
        ConvertTask task = new ConvertTask("non_existent_file.ncm", outputDir.getAbsolutePath(), tableModel, 0);

        Boolean result = task.call();
        assertFalse(result, "转换应该失败，因为文件不存在");
        assertEquals("转换失败", tableModel.getValueAt(0, 3), "任务状态应该为'转换失败'");
    }

    /**
     * 测试任务执行失败 - 输出目录不存在
     * 预期结果：转换失败，状态更新为"转换失败"
     */
    @Test
    void testTaskExecution_InvalidOutputDir() throws Exception {
        File testFile = new File(outputDir, "test.ncm");
        testFile.createNewFile();

        tableModel.addRow(new Object[]{"test.ncm", testFile.getAbsolutePath(), "1024", "准备转换"});
        ConvertTask task = new ConvertTask(testFile.getAbsolutePath(), "/non/existent/directory", tableModel, 0);

        Boolean result = task.call();
        assertFalse(result, "转换应该失败，因为输出目录不存在");
        assertEquals("转换失败", tableModel.getValueAt(0, 3), "任务状态应该为'转换失败'");
    }
}
