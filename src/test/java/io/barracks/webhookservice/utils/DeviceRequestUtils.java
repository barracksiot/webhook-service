/*
 * MIT License
 *
 * Copyright (c) 2017 Barracks Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.barracks.webhookservice.utils;


import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.barracks.webhookservice.model.DeviceRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DeviceRequestUtils {

    public static DeviceRequest getDeviceRequest() {

        final ObjectNode customClientData = JsonNodeFactory.instance.objectNode()
                .put(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .put(UUID.randomUUID().toString(), false)
                .put(UUID.randomUUID().toString(), 42.42);

        final DeviceRequest request = DeviceRequest.builder()
                .userId(UUID.randomUUID().toString())
                .unitId(UUID.randomUUID().toString())
                .addPackage(PackageUtils.getPackage())
                .addPackage(PackageUtils.getPackage())
                .customClientData(customClientData)
                .userAgent(UUID.randomUUID().toString())
                .ipAddress("1324.345.12.23")
                .build();
        assertThat(request).hasNoNullFieldsOrProperties();
        return request;
    }

}
