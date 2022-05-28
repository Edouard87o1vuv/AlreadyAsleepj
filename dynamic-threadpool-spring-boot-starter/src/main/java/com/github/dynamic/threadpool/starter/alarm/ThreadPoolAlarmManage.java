package com.github.dynamic.threadpool.starter.alarm;

import com.github.dynamic.threadpool.common.config.ApplicationContextHolder;
import com.github.dynamic.threadpool.common.model.PoolParameterInfo;
import com.github.dynamic.threadpool.starter.config.MessageAlarmConfig;
import com.github.dynamic.threadpool.starter.toolkit.CalculateUtil;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;
import com.github.dynamic.threadpool.starter.toolkit.thread.ResizableCapacityLinkedBlockIngQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Alarm manage.
 *
 * @author chen.ma
 * @date 2021/8/15 14:13
 */
@Slf4j
public class ThreadPoolAlarmManage {

    private static final SendMessageService SEND_MESSAGE_SERVICE;

    static {
        SEND_MESSAGE_SERVICE = Optional.ofNullable(ApplicationContextHolder.getInstance())
                .map(each -> each.getBean(MessageAlarmConfig.SEND_MESSAGE_BEAN_NAME, SendMessageService.class))
                .orElse(null);
    }

    /**
     * Check thread pool capacity alarm.
     *
     * @param threadPoolExecutor
     */
    public static void checkPoolCapacityAlarm(CustomThreadPoolExecutor threadPoolExecutor) {
        ThreadPoolAlarm threadPoolAlarm = threadPoolExecutor.getThreadPoolAlarm();
        ResizableCapacityLinkedBlockIngQueue blockIngQueue =
                (ResizableCapacityLinkedBlockIngQueue) threadPoolExecutor.getQueue();

        int queueSize = blockIngQueue.size();
        int capacity = queueSize + blockIngQueue.remainingCapacity();
        int divide = CalculateUtil.divide(queueSize, capacity);
        if (divide > threadPoolAlarm.getCapacityAlarm()) {
            SEND_MESSAGE_SERVICE.sendAlarmMessage(threadPoolExecutor);
        }
    }

    /**
     * Check thread pool activity alarm.
     *
     * @param isCore
     * @param threadPoolExecutor
     */
    public static void checkPoolLivenessAlarm(boolean isCore, CustomThreadPoolExecutor threadPoolExecutor) {
        if (isCore || SEND_MESSAGE_SERVICE == null) {
            return;
        }
        int activeCount = threadPoolExecutor.getActiveCount();
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        int divide = CalculateUtil.divide(activeCount, maximumPoolSize);
        if (divide > threadPoolExecutor.getThreadPoolAlarm().getLivenessAlarm()) {
            SEND_MESSAGE_SERVICE.sendAlarmMessage(threadPoolExecutor);
        }
    }

    /**
     * Check thread pool reject policy alarm.
     *
     * @param threadPoolExecutor
     */
    public static void checkPoolRejectAlarm(CustomThreadPoolExecutor threadPoolExecutor) {
        if (SEND_MESSAGE_SERVICE != null) {
            SEND_MESSAGE_SERVICE.sendAlarmMessage(threadPoolExecutor);
        }
    }

    /**
     * Send thread pool configuration change message.
     *
     * @param parameter
     */
    public static void sendPoolConfigChange(PoolParameterInfo parameter) {
        if (SEND_MESSAGE_SERVICE != null) {
            SEND_MESSAGE_SERVICE.sendChangeMessage(parameter);
        }
    }

}
