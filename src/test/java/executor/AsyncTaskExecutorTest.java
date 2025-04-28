package executor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试异步任务执行器
 * 主要测试线程池的功能，包括：
 * 1. 任务提交
 * 2. 任务执行
 * 3. 异常处理
 */
public class AsyncTaskExecutorTest {

    /**
     * 测试任务提交
     * 预期结果：成功提交任务并返回 Future
     */
    @Test
    void testTaskSubmission() {
        // 准备测试数据
        Callable<Boolean> task = () -> true;

        // 执行测试
        CompletableFuture<Boolean> future = AsyncTaskExecutor.submit(task);

        // 验证结果
        assertNotNull(future, "Future 不应该为空");
    }

    /**
     * 测试任务执行
     * 预期结果：成功执行任务并返回结果
     */
    @Test
    void testTaskExecution() throws Exception {
        // 准备测试数据
        Callable<Boolean> task = () -> true;

        // 执行测试
        CompletableFuture<Boolean> future = AsyncTaskExecutor.submit(task);
        Boolean result = future.get();

        // 验证结果
        assertTrue(result, "任务应该返回 true");
    }

    /**
     * 测试异常处理
     * 预期结果：正确处理任务执行异常
     */
    @Test
    void testExceptionHandling() {
        // 准备测试数据
        Callable<Boolean> task = () -> {
            throw new RuntimeException("Test exception");
        };

        // 执行测试
        CompletableFuture<Boolean> future = AsyncTaskExecutor.submit(task);

        // 验证结果
        assertThrows(ExecutionException.class, () -> {
            future.get();
        }, "应该抛出 ExecutionException");
    }
}