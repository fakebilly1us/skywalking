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

package org.apache.skywalking.oap.meter.analyzer.dsl;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static com.google.common.collect.ImmutableMap.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class ArithmeticTest {
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {
                "plus-scalar-1",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(1600592418480.0)
                          .name("http_success_request")
                          .build(),
                    Sample.builder()
                          .labels(of("idc", "t2"))
                          .value(1600592418481.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                "1000 + http_success_request.tagEqual('idc','t1')",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(1600592419480.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                false,
                },
            {
                "plus-scalar",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(1600592418480.0)
                          .name("http_success_request")
                          .build(),
                    Sample.builder()
                          .labels(of("idc", "t2"))
                          .value(1600592418481.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                "http_success_request.tagEqual('idc','t1') + 1000",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(1600592419480.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                false,
                },
            {
                "minus-scalar",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(1600592418480.0)
                          .name("http_success_request")
                          .build(),
                    Sample.builder()
                          .labels(of("idc", "t2"))
                          .value(1600592418481.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                "http_success_request.tagEqual('idc','t1') - 1000",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(1600592417480.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                false,
                },
            {
                "multiply-scalar",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(1600592418480.0)
                          .name("http_success_request")
                          .build(),
                    Sample.builder()
                          .labels(of("idc", "t2"))
                          .value(1600592418481.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                "http_success_request.tagEqual('idc','t1') * 1000",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(1600592418480000.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                false,
                },
            {
                "divide-scalar",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(1600592418480.0)
                          .name("http_success_request")
                          .build(),
                    Sample.builder()
                          .labels(of("idc", "t2"))
                          .value(1600592418481.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                "http_success_request.tagEqual('idc','t1') / 10",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(160059241848.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                false,
                },
            {
                "divide-zero",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(1600592418480.0)
                          .name("http_success_request")
                          .build(),
                    Sample.builder()
                          .labels(of("idc", "t2"))
                          .value(1600592418481.0)
                          .name("http_success_request")
                          .build()
                ).build()),
                "http_success_request.tagEqual('idc','t1') / 0",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(Double.POSITIVE_INFINITY)
                          .name("http_success_request")
                          .build()
                ).build()),
                false,
                },
            {
                "empty-plus-empty",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamily.EMPTY
                ),
                "http_success_request + http_error_request",
                Result.fail("Parsed result is an EMPTY sample family"),
                false,
                },
            {
                "empty-plus-sampleFamily",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamilyBuilder.newBuilder(
                        Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                        Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                    ).build()
                ),
                "http_success_request + http_error_request",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                ).build()),
                false,
                },
            {
                "sampleFamily-plus-empty",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamilyBuilder.newBuilder(
                        Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                        Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                    ).build()
                ),
                "http_error_request + http_success_request ",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                ).build()),
                false,
                },
            {
                "sampleFamily-plus-sampleFamily",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(100).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(30).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t3")).value(40).name("http_success_request").build(),
                    Sample.builder().labels(of("region", "us")).value(80).name("http_success_request").build()
                ).build(), "http_error_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t5")).value(3).name("http_error_request").build(),
                    Sample.builder().labels(of("tz", "en-US")).value(3).name("http_error_request").build()
                ).build()),
                "http_success_request + http_error_request",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(150).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(33).name("http_success_request").build()
                ).build()),
                false,
                },
            {
                "empty-minus-empty",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamily.EMPTY
                ),
                "http_success_request - http_error_request",
                Result.fail("Parsed result is an EMPTY sample family"),
                false,
                },
            {
                "empty-minus-sampleFamily",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamilyBuilder.newBuilder(
                        Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                        Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                    ).build()
                ),
                "http_success_request - http_error_request",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(-50).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(-3).name("http_error_request").build()
                ).build()),
                false,
                },
            {
                "sampleFamily-minus-empty",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamilyBuilder.newBuilder(
                        Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                        Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                    ).build()
                ),
                "http_error_request - http_success_request ",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                ).build()),
                false,
                },
            {
                "sampleFamily-minus-sampleFamily",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(100).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(30).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t3")).value(40).name("http_success_request").build(),
                    Sample.builder().labels(of("region", "us")).value(80).name("http_success_request").build()
                ).build(), "http_error_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t5")).value(3).name("http_error_request").build(),
                    Sample.builder().labels(of("tz", "en-US")).value(3).name("http_error_request").build()
                ).build()),
                "http_success_request - http_error_request",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(50).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(27).name("http_success_request").build()
                ).build()),
                false,
                },
            {
                "sameSampleFamily-minus-sameSampleFamily",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1", "service", "service1"))
                          .value(100)
                          .name("http_success_request")
                          .build(),
                    Sample.builder()
                          .labels(of("idc", "t2", "service", "service1"))
                          .value(30)
                          .name("http_success_request")
                          .build(),
                    Sample.builder()
                          .labels(of("idc", "t3", "service", "service1"))
                          .value(40)
                          .name("http_success_request")
                          .build(),
                    Sample.builder()
                          .labels(of("region", "us", "service", "service1"))
                          .value(80)
                          .name("http_success_request")
                          .build()
                ).build()),
                "http_success_request.sum(['service']) - http_success_request.sum(['service'])",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("service", "service1")).value(0).name("http_success_request").build()
                ).build()),
                false,
                },
            {
                "empty-multiple-empty",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamily.EMPTY
                ),
                "http_success_request * http_error_request",
                Result.fail("Parsed result is an EMPTY sample family"),
                false,
                },
            {
                "empty-multiple-sampleFamily",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamilyBuilder.newBuilder(
                        Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                        Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                    ).build()
                ),
                "http_success_request * http_error_request",
                Result.fail("Parsed result is an EMPTY sample family"),
                false,
                },
            {
                "sampleFamily-multiple-empty",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamilyBuilder.newBuilder(
                        Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                        Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                    ).build()
                ),
                "http_error_request * http_success_request ",
                Result.fail("Parsed result is an EMPTY sample family"),
                false,
                },
            {
                "sampleFamily-multiple-sampleFamily",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(100).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(30).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t3")).value(40).name("http_success_request").build(),
                    Sample.builder().labels(of("region", "us")).value(80).name("http_success_request").build()
                ).build(), "http_error_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t5")).value(3).name("http_error_request").build(),
                    Sample.builder().labels(of("tz", "en-US")).value(3).name("http_error_request").build()
                ).build()),
                "http_success_request * http_error_request",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(5000).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(90).name("http_success_request").build()
                ).build()),
                false,
                },
            {
                "empty-divide-empty",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamily.EMPTY
                ),
                "http_success_request / http_error_request",
                Result.fail("Parsed result is an EMPTY sample family"),
                false,
                },
            {
                "empty-divide-sampleFamily",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamilyBuilder.newBuilder(
                        Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                        Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                    ).build()
                ),
                "http_success_request / http_error_request",
                Result.fail("Parsed result is an EMPTY sample family"),
                false,
                },
            {
                "sampleFamily-divide-empty",
                of("http_success_request", SampleFamily.EMPTY,
                   "http_error_request", SampleFamilyBuilder.newBuilder(
                        Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                        Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build()
                    ).build()
                ),
                "http_error_request / http_success_request ",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder()
                          .labels(of("idc", "t1"))
                          .value(Double.POSITIVE_INFINITY)
                          .name("http_error_request")
                          .build(),
                    Sample.builder()
                          .labels(of("idc", "t2"))
                          .value(Double.POSITIVE_INFINITY)
                          .name("http_error_request")
                          .build()
                ).build()),
                false,
                },
            {
                "sampleFamily-divide-sampleFamily",
                of("http_success_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(100).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(30).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t3")).value(40).name("http_success_request").build(),
                    Sample.builder().labels(of("region", "us")).value(80).name("http_success_request").build()
                ).build(), "http_error_request", SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(50).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(3).name("http_error_request").build(),
                    Sample.builder().labels(of("idc", "t5")).value(3).name("http_error_request").build(),
                    Sample.builder().labels(of("tz", "en-US")).value(3).name("http_error_request").build()
                ).build()),
                "http_success_request / http_error_request",
                Result.success(SampleFamilyBuilder.newBuilder(
                    Sample.builder().labels(of("idc", "t1")).value(2).name("http_success_request").build(),
                    Sample.builder().labels(of("idc", "t2")).value(10).name("http_success_request").build()
                ).build()),
                false,
                },
            });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    public void test(String name,
                     ImmutableMap<String, SampleFamily> input,
                     String expression,
                     Result want,
                     boolean isThrow) {
        Expression e = DSL.parse(expression);
        Result r = null;
        try {
            r = e.run(input);
        } catch (Throwable t) {
            if (isThrow) {
                return;
            }
            log.error("Test failed", t);
            fail("Should not throw anything");
        }
        if (isThrow) {
            fail("Should throw something");
        }
        assertThat(r).isEqualTo(want);
    }
}
