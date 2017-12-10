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

package io.barracks.webhookservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.barracks.webhookservice.client.exception.WebhookClientException;
import io.barracks.webhookservice.model.*;
import io.barracks.webhookservice.utils.DeviceChangeEventUtils;
import io.barracks.webhookservice.utils.DeviceEventUtils;
import io.barracks.webhookservice.utils.WebhookUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(WebhookClient.class)
public class WebhookClientTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private WebhookClient webhookClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${io.barracks.webhookservice.base_url}")
    private String baseUrl;

    @Test
    public void postDeviceEvent_whenServiceSucceeds_shouldCallResource() throws Exception {
        // Given
        final DeviceEvent deviceEvent = DeviceEventUtils.getDeviceEvent();
        final Hook hook = WebhookUtils.getWebhook()
                .toBuilder()
                .url("http://not.barracks.io/")
                .build();

        final DeviceEventHook deviceEventHook = DeviceEventHook.builder()
                .deviceEvent(deviceEvent)
                .hook(hook)
                .build();

        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo(hook.getUrl()))
                .andRespond(withSuccess());

        // When
        webhookClient.postDeviceEvent(deviceEventHook);

        // Then
        mockServer.verify();
    }

    @Test
    public void postDeviceEvent_whenRequestFailed_shouldThrowException() throws Exception {
        // Given
        final DeviceEvent deviceEvent = DeviceEventUtils.getDeviceEvent();
        final Hook hook = WebhookUtils.getWebhook()
                .toBuilder()
                .url("http://not.barracks.io/")
                .build();

        final DeviceEventHook deviceEventHook = DeviceEventHook.builder()
                .deviceEvent(deviceEvent)
                .hook(hook)
                .build();

        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo(hook.getUrl()))
                .andRespond(withServerError());

        // When / Then
        assertThatExceptionOfType(WebhookClientException.class)
                .isThrownBy(() -> webhookClient.postDeviceEvent(deviceEventHook));
        mockServer.verify();
    }

    @Test
    public void postDeviceChangeEvent_whenServiceSucceeds_shouldCallResource() throws Exception {
        // Given
        final DeviceChangeEvent deviceChangeEvent = DeviceChangeEventUtils.getDeviceChangeEvent();
        final Hook hook = WebhookUtils.getWebhook()
                .toBuilder()
                .url("http://not.barracks.io/")
                .build();

        final DeviceChangeEventHook deviceChangeEventHook = DeviceChangeEventHook.builder()
                .deviceChangeEvent(deviceChangeEvent)
                .hook(hook)
                .build();

        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo(hook.getUrl()))
                .andRespond(withSuccess());

        // When
        webhookClient.postDeviceChangeEvent(deviceChangeEventHook);

        // Then
        mockServer.verify();
    }

    @Test
    public void postDeviceChangeEvent_whenRequestFailed_shouldThrowException() throws Exception {
        // Given
        final DeviceChangeEvent deviceChangeEvent = DeviceChangeEventUtils.getDeviceChangeEvent();
        final Hook hook = WebhookUtils.getWebhook()
                .toBuilder()
                .url("http://not.barracks.io/")
                .build();

        final DeviceChangeEventHook deviceChangeEventHook = DeviceChangeEventHook.builder()
                .deviceChangeEvent(deviceChangeEvent)
                .hook(hook)
                .build();

        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo(hook.getUrl()))
                .andRespond(withServerError());

        // When / Then
        assertThatExceptionOfType(WebhookClientException.class)
                .isThrownBy(() -> webhookClient.postDeviceChangeEvent(deviceChangeEventHook));
        mockServer.verify();
    }

}
