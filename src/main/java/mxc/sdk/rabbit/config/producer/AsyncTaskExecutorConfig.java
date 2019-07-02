package mxc.sdk.rabbit.config.producer;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


public class AsyncTaskExecutorConfig {

    //线程池中线程名称前缀
    private static final String THREAD_POOL_NAME_PREFIX = "rabbitmq-taskExecutor-";
    //核心线程数，默认为1
    private static final Integer CORE_POOL_SIZE = 1;
    //最大线程数，默认为Integer.MAX_VALUE
    private static final Integer MAX_POOL_SIZE = 10;
    //队列最大长度，一般需要设置值>=notifyScheduledMainExecutor.maxNum；默认为Integer.MAX_VALUE
    private static final Integer QUEUE_CAPACITY = 1000;
    //线程池维护线程所允许的空闲时间，默认为60s
    private static final Integer KEEP_ALIVE_SECONDS = 300;
    //是否允许Core Thread超时后可以关闭
    private static final Boolean ALLOW_CORE_THREAD_TIME_OUT = Boolean.FALSE;
    //线程关闭策略,默认false,当值为true时,只有当子线程里面的任务完成时才会调用shutdown()来关闭现场
    private static final Boolean WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = Boolean.FALSE;

    @Bean(name = "taskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix(THREAD_POOL_NAME_PREFIX);
        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        threadPoolTaskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        threadPoolTaskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(ALLOW_CORE_THREAD_TIME_OUT);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN);
        /**
         * 线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy默认为后者
         * AbortPolicy:直接抛出java.util.concurrent.RejectedExecutionException异常
         * CallerRunsPolicy:主线程直接执行该任务，执行完之后尝试添加下一个任务到线程池中，可以有效降低向线程池内添加任务的速度
         */
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
