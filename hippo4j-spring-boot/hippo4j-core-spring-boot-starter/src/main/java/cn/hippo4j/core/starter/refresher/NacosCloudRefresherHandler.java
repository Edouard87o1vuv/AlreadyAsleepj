/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Nacos cloud refresher handler.
 *
 * @author chen.ma
 * @date 2022/2/26 11:21
 */
@Slf4j
public class NacosCloudRefresherHandler extends AbstractCoreThreadPoolDynamicRefresh {

    private final NacosConfigManager nacosConfigManager;

    public NacosCloudRefresherHandler(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler,
                                      BootstrapCoreProperties bootstrapCoreProperties) {
        super(threadPoolNotifyAlarmHandler, bootstrapCoreProperties);
        nacosConfigManager = ApplicationContextHolder.getBean(NacosConfigManager.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> nacosConfig = bootstrapCoreProperties.getNacos();

        nacosConfigManager.getConfigService().addListener(nacosConfig.get("data-id"),
                nacosConfig.get("group"), new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return dynamicRefreshExecutorService;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        dynamicRefresh(configInfo);
                    }
                });
    }

}
