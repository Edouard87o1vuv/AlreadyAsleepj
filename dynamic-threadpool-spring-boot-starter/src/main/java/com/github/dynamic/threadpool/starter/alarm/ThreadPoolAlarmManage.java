package com.github.dynamic.threadpool.starter.alarm;

import com.github.dynamic.threadpool.common.config.ApplicationContextHolder;
import com.github.dynamic.threadpool.starter.toolkit.CalculateUtil;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;
import com.github.dynamic.threadpool.starter.toolkit.thread.ResizableCapacityLinkedBlockIngQueue;
import lombok.extern.slf4j.Slf4j;

/**
 * Alarm Manage.
 *
 * @author chen.ma
 * @date 2021/8/15 14:13
 */
@Slf4j
public class ThreadPoolAlarmManage {

    private static final SendMessageService SEND_MESSAGE_SERVICE;

    static {
        SEND_MESSAGE_SERVICE = ApplicationContextHolder.getBean("sendMessageService", SendMessageService.class);
    }

    /**
     * checkPoolCapacityAlarm.
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
            SEND_MESSAGE_SERVICE.sendMessage(threadPoolExecutor);
        }
    }

    /**
     * checkPoolLivenessAlarm.
     *
     * @param isCore
     * @param threadPoolExecutor
     */
    public static void checkPoolLivenessAlarm(boolean isCore, CustomThreadPoolExecutor threadPoolExecutor) {
        if (isCore) {
            return;
        }
        int activeCount = threadPoolExecutor.getActiveCount();
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        int divide = CalculateUtil.divide(activeCount, maximumPoolSize);
        if (divide > threadPoolExecutor.getThreadPoolAlarm().getLivenessAlarm()) {
            SEND_MESSAGE_SERVICE.sendMessage(threadPoolExecutor);
        }
    }

    /**
     * checkPoolRejectAlarm.
     *
     * @param threadPoolExecutor
     */
    public static void checkPoolRejectAlarm(CustomThreadPoolExecutor threadPoolExecutor) {
        SEND_MESSAGE_SERVICE.sendMessage(threadPoolExecutor);
    }

}
