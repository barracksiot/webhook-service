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

package io.barracks.webhookservice.manager;

import io.barracks.webhookservice.client.WebhookClient;
import io.barracks.webhookservice.model.DeviceChangeEventHook;
import io.barracks.webhookservice.model.DeviceEvent;
import io.barracks.webhookservice.model.DeviceEventHook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static io.barracks.webhookservice.utils.DeviceChangeEventUtils.getDeviceChangeEvent;
import static io.barracks.webhookservice.utils.DeviceEventUtils.getDeviceEvent;
import static io.barracks.webhookservice.utils.WebhookUtils.getWebhook;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WebhookManagerTest {

    @InjectMocks
    @Spy
    private WebhookManager webhookManager;

    @Mock
    private WebhookClient webhookClient;

    @Test
    public void postDeviceEventHook_shouldCallClient() {
        //Given
        final DeviceEvent deviceEvent = getDeviceEvent();
        final DeviceEventHook deviceEventHook = DeviceEventHook.builder()
                .deviceEvent(deviceEvent)
                .hook(getWebhook())
                .build();

        doNothing().when(webhookClient).postDeviceEvent(deviceEventHook);

        //When
        webhookManager.postDeviceEventHook(deviceEventHook);

        //Then
        verify(webhookClient).postDeviceEvent(deviceEventHook);
    }

    @Test
    public void postDeviceChangeEventHook_shouldCallClient() {
        //Given
        final DeviceChangeEventHook deviceChangeEventHook = DeviceChangeEventHook.builder()
                .deviceChangeEvent(getDeviceChangeEvent())
                .hook(getWebhook())
                .build();

        doNothing().when(webhookClient).postDeviceChangeEvent(deviceChangeEventHook);

        //When
        webhookManager.postDeviceChangeEventHook(deviceChangeEventHook);

        //Then
        verify(webhookClient).postDeviceChangeEvent(deviceChangeEventHook);
    }

}
