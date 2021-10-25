package io.dynamic.threadpool.starter.enable;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation to activate Dynamic ThreadPool related configuration.
 *
 * @author chen.ma
 * @date 2021/7/8 23:28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DynamicThreadPoolMarkerConfiguration.class)
public @interface EnableDynamicThreadPool {

}
