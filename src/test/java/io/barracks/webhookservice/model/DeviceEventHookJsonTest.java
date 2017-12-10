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

package io.barracks.webhookservice.model;


import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.barracks.webhookservice.utils.DeviceEventUtils;
import io.barracks.webhookservice.utils.HookUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JsonTest
public class DeviceEventHookJsonTest {

    @Autowired
    private JacksonTester<DeviceEventHook> json;

    @Value("classpath:io/barracks/webhookservice/model/deviceEventHook.json")
    private Resource hookResource;

    @Test
    public void serialize_whenWebhook_shouldFillAllFieldsExceptIdAndExchangeAndEventType() throws Exception {
        // Given
        final DeviceEventHook source = DeviceEventHook.builder()
                .deviceEvent(DeviceEventUtils.getDeviceEvent())
                .hook(HookUtils.getHook())
                .build();

        // When
        final JsonContent<DeviceEventHook> result = json.write(source);

        // Then
        assertThat(result).extractingJsonPathStringValue("deviceEvent.request.userId").isEqualTo(source.getDeviceEvent().getRequest().getUserId());
        assertThat(result).extractingJsonPathStringValue("deviceEvent.request.unitId").isEqualTo(source.getDeviceEvent().getRequest().getUnitId());
        assertThat(result).extractingJsonPathStringValue("deviceEvent.request.userAgent").isEqualTo(source.getDeviceEvent().getRequest().getUserAgent());
        assertThat(result).extractingJsonPathStringValue("deviceEvent.request.ipAddress").isEqualTo(source.getDeviceEvent().getRequest().getIpAddress());
        assertThat(result).extractingJsonPathStringValue("deviceEvent.response.available[0].reference").isEqualTo(source.getDeviceEvent().getResponse().getAvailable().get(0).getReference());
        assertThat(result).extractingJsonPathStringValue("deviceEvent.response.available[0].version").isEqualTo(source.getDeviceEvent().getResponse().getAvailable().get(0).getVersion());
        assertThat(result).extractingJsonPathStringValue("deviceEvent.response.available[0].url").isEqualTo(source.getDeviceEvent().getResponse().getAvailable().get(0).getUrl());
        assertThat(result).extractingJsonPathStringValue("deviceEvent.response.available[0].md5").isEqualTo(source.getDeviceEvent().getResponse().getAvailable().get(0).getMd5());
        assertThat(result).extractingJsonPathNumberValue("deviceEvent.response.available[0].size").isEqualTo(source.getDeviceEvent().getResponse().getAvailable().get(0).getSize());
        assertThat(result).extractingJsonPathStringValue("deviceEvent.response.unchanged[0].reference").isEqualTo(source.getDeviceEvent().getResponse().getUnchanged().get(0).getReference());
        assertThat(result).extractingJsonPathStringValue("deviceEvent.response.unavailable[0].reference").isEqualTo(source.getDeviceEvent().getResponse().getUnavailable().get(0).getReference());
        assertThat(result).extractingJsonPathStringValue("deviceEvent.response.changed[0].reference").isEqualTo(source.getDeviceEvent().getResponse().getChanged().get(0).getReference());
        assertThat(result).extractingJsonPathStringValue("hook.name").isEqualTo(source.getHook().getName());
        assertThat(result).extractingJsonPathStringValue("hook.url").isEqualTo(source.getHook().getUrl());
    }

    @Test
    public void deserialize_shouldWebhook_shouldFillAllFieldsExceptExchangeAndEventType() throws Exception {
        // Given
        final DeviceRequest request = DeviceRequest.builder()
                .unitId("ID transmitted by the device")
                .userId("Unique ID for the user")
                .userAgent("Version of the SDK installed on the device that sent the information")
                .ipAddress("IP address of the device")
                .customClientData(JsonNodeFactory.instance.objectNode().put("what", "this"))
                .build();

        final Version version = Version.builder()
                .reference("io.barracks.package")
                .filename("barracks-package-2-5-1.tar.gz")
                .md5("4c2383f5c88e9110642953b5dd7c88a1")
                .size(76544567L)
                .version("2-5-1")
                .build();

        final ResolvedVersions response = ResolvedVersions.builder()
                .addAvailable(version)
                .addAvailable(version)
                .addUnavailable(version)
                .addChanged(version)
                .addChanged(version)
                .addUnchanged(version)
                .build();

        final Hook hook = Hook.builder()
                .name("hookName")
                .url("http")
                .build();

        final DeviceEvent deviceEvent = DeviceEventUtils.getDeviceEvent()
                .toBuilder().request(request).response(response).build();

        final DeviceEventHook expected = DeviceEventHook.builder().deviceEvent(deviceEvent).hook(hook).build();

        // When
        final ObjectContent<DeviceEventHook> result = json.read(hookResource);

        // Then
        assertThat(result).isEqualTo(expected);
    }

}
