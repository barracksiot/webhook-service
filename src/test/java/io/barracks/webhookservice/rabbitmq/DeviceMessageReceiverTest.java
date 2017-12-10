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

package io.barracks.webhookservice.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.barracks.webhookservice.manager.WebhookManager;
import io.barracks.webhookservice.model.*;
import io.barracks.webhookservice.utils.DeviceChangeEventUtils;
import io.barracks.webhookservice.utils.DeviceEventUtils;
import io.barracks.webhookservice.utils.HookUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.metrics.CounterService;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeviceMessageReceiverTest {

    private WebhookMessageListener webhookMessageListener;

    @Mock
    private WebhookManager webhookManager;

    @Before
    public void setUp() {
        webhookMessageListener = new WebhookMessageListener(webhookManager, new ObjectMapper(), mock(CounterService.class));
    }

    @Test
    public void receiveMessage_whenAllIsFine_shouldCallManager() throws Exception {
        //Given
        final Hook hook = HookUtils.getHook();
        final DeviceEvent deviceEvent = DeviceEventUtils.getDeviceEvent();
        final DeviceEventHook deviceEventHook = DeviceEventHook.builder().hook(hook).deviceEvent(deviceEvent).build();

        //When
        webhookMessageListener.receiveMessage(deviceEventHook);

        //Then
        verify(webhookManager).postDeviceEventHook(deviceEventHook);
    }

    @Test
    public void receiveMessage_whenException_shouldLogError() throws Exception {
        //Given
        final Hook hook = HookUtils.getHook();
        final DeviceEvent deviceEvent = DeviceEventUtils.getDeviceEvent();
        final DeviceEventHook deviceEventHook = DeviceEventHook.builder().hook(hook).deviceEvent(deviceEvent).build();
        doThrow(Exception.class).when(webhookManager).postDeviceEventHook(deviceEventHook);

        //When
        webhookMessageListener.receiveMessage(deviceEventHook);

        //Then
        verify(webhookManager).postDeviceEventHook(deviceEventHook);
    }

    @Test
    public void receiveDeviceChangedEventMessage_whenAllIsFine_shouldCallManager() throws Exception {
        //Given
        final Hook hook = HookUtils.getHook();
        final DeviceChangeEvent deviceChangeEvent = DeviceChangeEventUtils.getDeviceChangeEvent();
        final DeviceChangeEventHook deviceChangeEventHook = DeviceChangeEventHook.builder()
                .hook(hook)
                .deviceChangeEvent(deviceChangeEvent)
                .build();

        //When
        webhookMessageListener.receiveChangeMessage(deviceChangeEventHook);

        //Then
        verify(webhookManager).postDeviceChangeEventHook(deviceChangeEventHook);
    }

    @Test
    public void receiveDeviceChangedEventMessage_whenException_shouldLogError() throws Exception {
        //Given
        final Hook hook = HookUtils.getHook();
        final DeviceChangeEvent deviceChangeEvent = DeviceChangeEventUtils.getDeviceChangeEvent();
        final DeviceChangeEventHook deviceChangeEventHook = DeviceChangeEventHook.builder()
                .hook(hook)
                .deviceChangeEvent(deviceChangeEvent)
                .build();
        doThrow(Exception.class).when(webhookManager).postDeviceChangeEventHook(deviceChangeEventHook);

        //When
        webhookMessageListener.receiveChangeMessage(deviceChangeEventHook);

        //Then
        verify(webhookManager).postDeviceChangeEventHook(deviceChangeEventHook);
    }

}
