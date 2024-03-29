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

package org.apache.skywalking.oap.server.receiver.envoy.als.mx;

import com.google.protobuf.util.JsonFormat;
import io.envoyproxy.envoy.service.accesslog.v3.StreamAccessLogsMessage;
import org.apache.skywalking.oap.server.receiver.envoy.als.ServiceMetaInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

import static org.apache.skywalking.oap.server.receiver.envoy.als.k8s.K8SALSServiceMeshHTTPAnalysisTest.getResourceAsStream;
import static org.assertj.core.api.Assertions.assertThat;

public class FieldsHelperTest {
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {
                "serviceName: ${LABELS.\"service.istio.io/canonical-name\",LABELS.\"app.kubernetes.io/name\",LABELS.app}\nserviceInstanceName: ${NAME}",
                "productpage",
                "productpage-v1-65576bb7bf-4mzsp"
            },
            {
                "serviceName: ${LABELS.\"service.istio.io/canonical-name\"}\nserviceInstanceName: ${NAME}",
                "productpage",
                "productpage-v1-65576bb7bf-4mzsp"
            },
            {
                "serviceName: ${LABELS.\"service.istio.io/canonical-name\"}-${LABELS.version}\nserviceInstanceName: ${NAME}.${NAMESPACE}",
                "productpage-v1",
                "productpage-v1-65576bb7bf-4mzsp.default"
            },
            {
                "serviceName: ${LABELS.\"service.istio.io/canonical-name\"}-${CLUSTER_ID}\nserviceInstanceName: ${NAME}.${NAMESPACE}.${SERVICE_ACCOUNT}",
                "productpage-Kubernetes",
                "productpage-v1-65576bb7bf-4mzsp.default.bookinfo-productpage"
            },
            {
                "serviceName: fixed-${LABELS.\"service.istio.io/canonical-name\"}\nserviceInstanceName: yeah_${NAME}",
                "fixed-productpage",
                "yeah_productpage-v1-65576bb7bf-4mzsp"
            },
            {
                "serviceName: fixed-${LABELS.\"service.istio.io/not-exist\",LABELS.\"service.istio.io/canonical-name\"}\nserviceInstanceName: yeah_${NAME}",
                "fixed-productpage",
                "yeah_productpage-v1-65576bb7bf-4mzsp"
            },
            {
                "serviceName: fixed-${LABELS.\"service.istio.io/not-exist\",LABELS.\"service.istio.io/not-exist-2\"}\nserviceInstanceName: yeah_${NAME}",
                "fixed--",
                "yeah_productpage-v1-65576bb7bf-4mzsp"
            }
        });
    }

    @BeforeEach
    public void setUp() {
        Whitebox.setInternalState(FieldsHelper.SINGLETON, "initialized", false);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    public void testFormat(String mapping, String expectedServiceName, String expectedServiceInstanceName) throws Exception {
        try (final InputStreamReader isr = new InputStreamReader(getResourceAsStream("field-helper.msg"))) {
            final StreamAccessLogsMessage.Builder requestBuilder = StreamAccessLogsMessage.newBuilder();
            JsonFormat.parser().merge(isr, requestBuilder);
            final ServiceMetaInfo info = new ServiceMetaInfo();
            FieldsHelper.SINGLETON.init(new ByteArrayInputStream(mapping.getBytes()), ServiceMetaInfo.class);
            FieldsHelper.SINGLETON.inflate(
                requestBuilder.getIdentifier().getNode().getMetadata(),
                info
            );
            assertThat(info.getServiceName()).isEqualTo(expectedServiceName);
        }
    }
}
