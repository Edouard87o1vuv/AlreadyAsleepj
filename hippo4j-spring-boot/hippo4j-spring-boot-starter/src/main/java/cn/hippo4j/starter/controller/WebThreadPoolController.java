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

package cn.hippo4j.starter.controller;

import cn.hippo4j.common.model.PoolBaseInfo;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.core.executor.web.WebThreadPoolHandlerChoose;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Web thread pool controller.
 *
 * <p> At present, only Tomcat is well supported, and other web containers need to be improved.
 *
 * @author chen.ma
 * @date 2022/1/19 20:54
 */
@CrossOrigin
@RestController
@AllArgsConstructor
public class WebThreadPoolController {

    private final WebThreadPoolHandlerChoose webThreadPoolServiceChoose;

    @GetMapping("/web/base/info")
    public Result<PoolBaseInfo> getPoolBaseState() {
        PoolBaseInfo poolBaseInfo = webThreadPoolServiceChoose.choose().simpleInfo();
        return Results.success(poolBaseInfo);
    }

    @GetMapping("/web/run/state")
    public Result<PoolRunStateInfo> getPoolRunState() {
        PoolRunStateInfo poolRunState = webThreadPoolServiceChoose.choose().getWebRunStateInfo();
        return Results.success(poolRunState);
    }

    @PostMapping("/web/update/pool")
    public Result<Void> updateWebThreadPool(@RequestBody PoolParameterInfo poolParameterInfo) {
        webThreadPoolServiceChoose.choose().updateWebThreadPool(poolParameterInfo);
        return Results.success();
    }

}
