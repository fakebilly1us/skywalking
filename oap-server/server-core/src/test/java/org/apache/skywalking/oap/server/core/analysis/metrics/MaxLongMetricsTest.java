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

package org.apache.skywalking.oap.server.core.analysis.metrics;

import org.apache.skywalking.oap.server.core.remote.grpc.proto.RemoteData;
import org.apache.skywalking.oap.server.core.storage.StorageID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 **/
public class MaxLongMetricsTest {

    @Test
    public void testEntranceCombine() {
        MaxLongMetricsImpl impl = new MaxLongMetricsImpl();
        impl.combine(10);
        impl.combine(5);
        impl.combine(20);
        impl.calculate();
        Assertions.assertEquals(20, impl.getValue());
    }

    @Test
    public void testSelfCombine() {
        MaxLongMetricsImpl impl = new MaxLongMetricsImpl();
        impl.combine(10);
        impl.combine(5);

        MaxLongMetricsImpl impl2 = new MaxLongMetricsImpl();
        impl2.combine(2);
        impl2.combine(6);

        impl.combine(impl2);
        Assertions.assertEquals(10, impl.getValue());
    }

    public class MaxLongMetricsImpl extends MaxLongMetrics {

        @Override
        protected StorageID id0() {
            return null;
        }

        @Override
        public Metrics toHour() {
            return null;
        }

        @Override
        public Metrics toDay() {
            return null;
        }

        @Override
        public int remoteHashCode() {
            return 0;
        }

        @Override
        public void deserialize(RemoteData remoteData) {

        }

        @Override
        public RemoteData.Builder serialize() {
            return null;
        }
    }
}
