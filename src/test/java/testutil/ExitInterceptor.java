package testutil;

import java.security.Permission;

/**
 * 用于测试中拦截 System.exit() 调用的 SecurityManager。
 *
 * <p><b>为什么需要这个类：</b>
 * ConvertCommand 和 HelpCommand 在 handle() 末尾调用 System.exit(0) 确保进程彻底退出。
 * 在 Java 17+ 的 Gradle 测试环境下，System.exit(0) 会杀死测试 Worker JVM，
 * 导致后续测试全部丢失。通过拦截 System.exit() 为 SecurityException，既能验证
 * 退出逻辑被正确触发，又不影响测试进程继续执行。
 *
 * <p><b>Java 17+ 兼容性：</b>
 * 需要在 build.gradle 的 test jvmArgs 中添加 {@code -Djava.security.manager=allow}，
 * 否则 System.setSecurityManager() 会抛出 UnsupportedOperationException。
 * SecurityManager 在 Java 17 标记为 @Deprecated(forRemoval)，未来版本需迁移到
 * system-stubs 等替代方案。
 *
 * <p><b>权限处理：</b>
 * 覆盖 checkPermission() 为空实现（放行全部权限），仅拦截 checkExit()。
 * 否则 SecurityManager 会默认拒绝反射权限（suppressAccessChecks），导致
 * FastJSON 和 Gradle TestWorker 因 AccessControlException 失败。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * private ExitInterceptor exitInterceptor;
 *
 * @BeforeEach
 * void setUp() {
 *     exitInterceptor = new ExitInterceptor();
 *     exitInterceptor.install();
 * }
 *
 * @AfterEach
 * void tearDown() {
 *     exitInterceptor.uninstall();
 * }
 *
 * @Test
 * void testCommand() {
 *     try {
 *         command.handle(params);
 *     } catch (SecurityException e) {
 *         // System.exit() 被拦截，预期行为
 *     }
 *     assertTrue(exitInterceptor.wasExitCalled());
 *     assertEquals(0, exitInterceptor.getExitStatus());
 * }
 * }</pre>
 *
 * @author 烛远
 */
public class ExitInterceptor extends SecurityManager {

    /** System.exit() 被调用时传入的状态码，-1 表示未被调用 */
    private int exitStatus = -1;

    /** 安装前的原始 SecurityManager，卸载时恢复 */
    private final SecurityManager previous;

    /**
     * 创建拦截器并记录当前系统中已有的 SecurityManager（通常为 null）。
     */
    public ExitInterceptor() {
        this.previous = System.getSecurityManager();
    }

    /**
     * 安装此拦截器。之后所有 System.exit() 调用将被拦截为 SecurityException，
     * 其他所有权限（反射、文件、网络等）全部放行。
     */
    public void install() {
        System.setSecurityManager(this);
    }

    /**
     * 卸载此拦截器，恢复安装前的 SecurityManager。
     * 每个 @AfterEach 中必须调用此方法，否则会影响其他测试。
     */
    public void uninstall() {
        System.setSecurityManager(previous);
    }

    /**
     * 拦截 System.exit(status) 调用。
     * 不执行真正的进程退出，而是记录状态码并抛出 SecurityException 让调用栈处理。
     */
    @Override
    public void checkExit(int status) {
        this.exitStatus = status;
        throw new SecurityException("System.exit(" + status + ") intercepted by ExitInterceptor");
    }

    /**
     * 放行所有权限检查，确保 FastJSON 反射、Gradle TestWorker 等不受影响。
     * 如果不覆盖此方法，SecurityManager 默认策略会拒绝 reflectPermission(suppressAccessChecks)，
     * 导致 FastJSON 序列化/反序列化和 Gradle 测试框架抛出 AccessControlException。
     */
    @Override
    public void checkPermission(Permission perm) {
        // 放行全部权限，仅拦截 checkExit
    }

    /**
     * 放行带上下文的所有权限检查。
     */
    @Override
    public void checkPermission(Permission perm, Object context) {
        // 放行全部权限，仅拦截 checkExit
    }

    /**
     * @return System.exit() 是否被调用过
     */
    public boolean wasExitCalled() {
        return exitStatus >= 0;
    }

    /**
     * @return System.exit() 调用时传入的状态码，未被调用则返回 -1
     */
    public int getExitStatus() {
        return exitStatus;
    }
}
