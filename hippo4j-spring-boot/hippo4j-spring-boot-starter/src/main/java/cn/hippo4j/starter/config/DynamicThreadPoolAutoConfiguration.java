package cn.hippo4j.starter.config;

import cn.hippo4j.common.api.ThreadDetailState;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.refresh.ThreadPoolDynamicRefresh;
import cn.hippo4j.core.toolkit.IdentifyUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import cn.hippo4j.starter.controller.PoolRunStateController;
import cn.hippo4j.starter.controller.WebThreadPoolController;
import cn.hippo4j.starter.core.ConfigService;
import cn.hippo4j.starter.core.DynamicThreadPoolPostProcessor;
import cn.hippo4j.starter.core.ThreadPoolConfigService;
import cn.hippo4j.starter.core.ThreadPoolOperation;
import cn.hippo4j.starter.enable.MarkerConfiguration;
import cn.hippo4j.starter.event.ApplicationContentPostProcessor;
import cn.hippo4j.starter.handler.BaseThreadDetailStateHandler;
import cn.hippo4j.starter.handler.DynamicThreadPoolBannerHandler;
import cn.hippo4j.starter.handler.ThreadPoolRunStateHandler;
import cn.hippo4j.starter.handler.web.TomcatWebThreadPoolHandler;
import cn.hippo4j.starter.handler.web.WebThreadPoolHandlerChoose;
import cn.hippo4j.starter.handler.web.WebThreadPoolRunStateHandler;
import cn.hippo4j.starter.monitor.ReportingEventExecutor;
import cn.hippo4j.starter.monitor.collect.RunTimeInfoCollector;
import cn.hippo4j.starter.monitor.send.HttpConnectSender;
import cn.hippo4j.starter.monitor.send.MessageSender;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hippo4j.starter.remote.HttpScheduledHealthCheck;
import cn.hippo4j.starter.remote.ServerHealthCheck;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * DynamicTp auto configuration.
 *
 * @author chen.ma
 * @date 2021/6/22 09:20
 */
@Configuration
@AllArgsConstructor
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@EnableConfigurationProperties(BootstrapProperties.class)
@ConditionalOnProperty(prefix = BootstrapProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
@ImportAutoConfiguration({HttpClientConfiguration.class, DiscoveryConfiguration.class, MessageNotifyConfiguration.class, UtilAutoConfiguration.class})
public class DynamicThreadPoolAutoConfiguration {

    private final BootstrapProperties properties;

    private final ConfigurableEnvironment environment;

    @Bean
    public DynamicThreadPoolBannerHandler threadPoolBannerHandler() {
        return new DynamicThreadPoolBannerHandler(properties);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationContextHolder hippo4JApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    @SuppressWarnings("all")
    public ConfigService configService(HttpAgent httpAgent, InetUtils hippo4JInetUtils, ServerHealthCheck serverHealthCheck) {
        String identify = IdentifyUtil.generate(environment, hippo4JInetUtils);
        return new ThreadPoolConfigService(httpAgent, identify, serverHealthCheck);
    }

    @Bean
    public ThreadPoolOperation threadPoolOperation(ConfigService configService) {
        return new ThreadPoolOperation(properties, configService);
    }

    @Bean
    @SuppressWarnings("all")
    public DynamicThreadPoolPostProcessor threadPoolBeanPostProcessor(HttpAgent httpAgent,
                                                                      ThreadPoolOperation threadPoolOperation,
                                                                      ApplicationContextHolder hippo4JApplicationContextHolder,
                                                                      ThreadPoolDynamicRefresh threadPoolDynamicRefresh) {
        return new DynamicThreadPoolPostProcessor(properties, httpAgent, threadPoolOperation, threadPoolDynamicRefresh);
    }

    @Bean
    @SuppressWarnings("all")
    public ThreadPoolRunStateHandler threadPoolRunStateHandler(InetUtils hippo4JInetUtils) {
        return new ThreadPoolRunStateHandler(hippo4JInetUtils, environment);
    }

    @Bean
    @ConditionalOnMissingBean(value = ThreadDetailState.class)
    public ThreadDetailState baseThreadDetailStateHandler() {
        return new BaseThreadDetailStateHandler();
    }

    @Bean
    public PoolRunStateController poolRunStateController(ThreadPoolRunStateHandler threadPoolRunStateHandler,
                                                         ThreadDetailState threadDetailState) {
        return new PoolRunStateController(threadPoolRunStateHandler, threadDetailState);
    }

    @Bean
    @SuppressWarnings("all")
    public HttpConnectSender httpMvcSender(HttpAgent httpAgent) {
        return new HttpConnectSender(httpAgent);
    }

    @Bean
    public ReportingEventExecutor reportingEventExecutor(BootstrapProperties properties, MessageSender messageSender,
                                                         ServerHealthCheck serverHealthCheck) {
        return new ReportingEventExecutor(properties, messageSender, serverHealthCheck);
    }

    @Bean
    @SuppressWarnings("all")
    public ServerHealthCheck httpScheduledHealthCheck(HttpAgent httpAgent) {
        return new HttpScheduledHealthCheck(httpAgent);
    }

    @Bean
    public RunTimeInfoCollector runTimeInfoCollector() {
        return new RunTimeInfoCollector(properties);
    }

    @Bean
    public ApplicationContentPostProcessor applicationContentPostProcessor() {
        return new ApplicationContentPostProcessor();
    }

    @Bean
    public WebThreadPoolRunStateHandler webThreadPoolRunStateHandler() {
        return new WebThreadPoolRunStateHandler();
    }

    @Bean
    @ConditionalOnBean(name = "tomcatServletWebServerFactory")
    public TomcatWebThreadPoolHandler tomcatWebThreadPoolHandler(@Autowired(required = false) ServletWebServerApplicationContext applicationContext) {
        return new TomcatWebThreadPoolHandler(applicationContext);
    }

    @Bean
    public WebThreadPoolHandlerChoose webThreadPoolServiceChoose() {
        return new WebThreadPoolHandlerChoose();
    }

    @Bean
    public WebThreadPoolController webThreadPoolController(WebThreadPoolHandlerChoose webThreadPoolServiceChoose,
                                                           WebThreadPoolRunStateHandler webThreadPoolRunStateHandler) {
        return new WebThreadPoolController(webThreadPoolServiceChoose, webThreadPoolRunStateHandler);
    }

}


