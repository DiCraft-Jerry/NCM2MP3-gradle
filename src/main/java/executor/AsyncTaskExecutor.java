package executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author charlottexiao
 */
@Slf4j
public class AsyncTaskExecutor {

    /**
     * 获取系统处理器核心数
     */
    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();

    /**
     * 根据系统资源动态设置线程池参数
     * 保留一个核心给系统
     */
    private static final int CORE_POOL_SIZE = Math.max(2, CPU_CORES - 1);
    /**
     * 最大线程数
     * 最大线程数为CPU核心数的2倍
     */
    private static final int MAX_POOL_SIZE = CPU_CORES * 2;

    /**
     * 空余线程存活最长时间
     */
    private static final long KEEP_ALIVE_TIME = 1L;  // 空闲线程存活时间

    /**
     * 使用无界队列存储线程
     */
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();

    /**
     * 异步线程执行器
     */
    private static volatile ThreadPoolExecutor executor = null;

    /**
     * 创建异步线程执行器
     *
     * @return 异步线程执行器
     */
    private static ThreadPoolExecutor createExecutor() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                WORK_QUEUE,
                new ThreadFactory() {
                    private final AtomicInteger counter = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable, "NCM-Converter-" + counter.getAndIncrement());
                        thread.setPriority(Thread.NORM_PRIORITY);
                        return thread;
                    }
                },
                // 当队列满时，由调用线程执行任务
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 执行异步任务
     *
     * @param task 异步任务
     * @return Future对象，用于获取任务执行结果
     */
    public static <T> CompletableFuture<T> submit(Callable<T> task) {
        if (executor == null) {
            synchronized (AsyncTaskExecutor.class) {
                if (executor == null) {
                    executor = createExecutor();
                }
            }
        }

        try {
            Future<T> future = executor.submit(task);
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new CompletionException(e);
                } catch (ExecutionException e) {
                    throw new CompletionException(e.getCause());
                }
            });
        } catch (Exception e) {
            log.error("Failed to submit task of type: {}", task.getClass().getSimpleName(), e);
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
}
