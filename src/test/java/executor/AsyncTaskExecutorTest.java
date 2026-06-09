package executor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 烛远
 * 测试异步任务执行器
 * 主要测试线程池的功能，包括：
 * 1. 任务提交
 * 2. 任务执行
 * 3. 异常处理
 * 4. 并发提交
 * 5. 线程池复用
 */
public class AsyncTaskExecutorTest {

    /**
     * 测试任务提交
     * 预期结果：成功提交任务并返回 Future
     */
    @Test
    void testTaskSubmission() {
        Callable<Boolean> task = () -> true;
        CompletableFuture<Boolean> future = AsyncTaskExecutor.submit(task);
        assertNotNull(future, "Future 不应该为空");
    }

    /**
     * 测试任务执行
     * 预期结果：成功执行任务并返回结果
     */
    @Test
    void testTaskExecution() throws Exception {
        Callable<Boolean> task = () -> true;
        CompletableFuture<Boolean> future = AsyncTaskExecutor.submit(task);
        Boolean result = future.get();
        assertTrue(result, "任务应该返回 true");
    }

    /**
     * 测试异常处理
     * 预期结果：正确处理任务执行异常
     */
    @Test
    void testExceptionHandling() {
        Callable<Boolean> task = () -> {
            throw new RuntimeException("Test exception");
        };
        CompletableFuture<Boolean> future = AsyncTaskExecutor.submit(task);
        assertThrows(ExecutionException.class, () -> {
            future.get();
        }, "应该抛出 ExecutionException");
    }

    /**
     * 测试并发提交多个任务
     * 预期结果：所有任务都被执行并返回正确结果
     */
    @Test
    void testConcurrentSubmissions() throws Exception {
        int taskCount = 10;
        AtomicInteger counter = new AtomicInteger(0);
        List<CompletableFuture<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            futures.add(AsyncTaskExecutor.submit(() -> {
                counter.incrementAndGet();
                return taskId;
            }));
        }

        // 等待所有任务完成
        int sum = 0;
        for (CompletableFuture<Integer> future : futures) {
            sum += future.get();
        }

        assertEquals(taskCount, counter.get(), "所有任务都应该被执行");
        // sum = 0 + 1 + 2 + ... + (taskCount-1) = taskCount * (taskCount - 1) / 2
        assertEquals(taskCount * (taskCount - 1) / 2, sum, "所有任务都应该返回正确结果");
    }

    /**
     * 测试提交 null 任务
     * 预期结果：应该抛出异常
     */
    @Test
    void testSubmitNullTask() {
        assertThrows(NullPointerException.class, () -> {
            AsyncTaskExecutor.submit(null);
        }, "提交 null 任务应该抛出 NullPointerException");
    }
}
