package executor;

import com.alibaba.fastjson.JSON;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author charlottexiao
 */
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
    private static final BlockingDeque<Runnable> WORK_QUEUE = new LinkedBlockingDeque<>();

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
     * @return
     */
    public static <T> Future<T> submit(Callable<T> task) {
        try {
            if (executor == null) {
                synchronized (AsyncTaskExecutor.class) {
                    ThreadPoolExecutor temp = executor;
                    if (temp == null) {
                        temp = createExecutor();
                        executor = temp;
                    }
                }
            }
            Future<T> submit = executor.submit(task);
            return submit;
        } catch (Exception e) {
            System.out.format("异步任务启动失败: task %s ,异常： %s", JSON.toJSONString(task), JSON.toJSONString(e));
            return CompletableFuture.supplyAsync(() -> {throw e;});
        }
    }
}
