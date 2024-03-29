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
 *
 */

package org.apache.skywalking.oap.server.core.config;

import lombok.Getter;
import org.apache.skywalking.oap.server.core.CoreModuleConfig;
import org.apache.skywalking.oap.server.library.module.ModuleProvider;
import org.apache.skywalking.oap.server.library.module.Service;

@Getter
public class ConfigService implements Service {
    private final String gRPCHost;
    private final int gRPCPort;
    private final SearchableTracesTagsWatcher searchableTracesTags;
    private final String searchableLogsTags;
    private final String searchableAlarmTags;
    private final int metricsDataTTL;
    private final int recordDataTTL;
    private final int persistentPeriod;

    public ConfigService(CoreModuleConfig moduleConfig, ModuleProvider provider) {
        this.gRPCHost = moduleConfig.getGRPCHost();
        this.gRPCPort = moduleConfig.getGRPCPort();

        this.searchableTracesTags =
                new SearchableTracesTagsWatcher(moduleConfig.getSearchableTracesTags(), provider);
        moduleConfig.setSearchableTracesTagsWatcher(this.searchableTracesTags);

        this.searchableLogsTags = moduleConfig.getSearchableLogsTags();
        this.searchableAlarmTags = moduleConfig.getSearchableAlarmTags();
        this.metricsDataTTL = moduleConfig.getMetricsDataTTL();
        this.recordDataTTL = moduleConfig.getRecordDataTTL();
        this.persistentPeriod = moduleConfig.getPersistentPeriod();
    }
}
