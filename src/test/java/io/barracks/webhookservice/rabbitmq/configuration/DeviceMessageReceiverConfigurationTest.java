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

package io.barracks.webhookservice.rabbitmq.configuration;

import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMq;
import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMqConfig;
import io.barracks.webhookservice.Application;
import io.barracks.webhookservice.config.RabbitMQConfig;
import io.barracks.webhookservice.manager.WebhookManager;
import io.barracks.webhookservice.model.*;
import io.barracks.webhookservice.rabbitmq.WebhookMessageListener;
import io.barracks.webhookservice.utils.DeviceChangeEventUtils;
import io.barracks.webhookservice.utils.DeviceEventUtils;
import io.barracks.webhookservice.utils.HookUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.junit.BrokerRunning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RabbitMQConfig.class, Application.class})
public class DeviceMessageReceiverConfigurationTest {

    private static EmbeddedRabbitMq rabbitMq;

    @SpyBean
    private WebhookMessageListener webhookMessageListener;

    @Value("${io.barracks.amqp.exchangename}")
    private String exchangeName;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private WebhookManager webhookManager;

    @BeforeClass
    public static void startBroker() throws ExecutionException, InterruptedException {
        EmbeddedRabbitMqConfig config = new EmbeddedRabbitMqConfig.Builder()
                .erlangCheckTimeoutInMillis(150000)
                .rabbitMqServerInitializationTimeoutInMillis(100000)
                .defaultRabbitMqCtlTimeoutInMillis(100000)
                .downloadConnectionTimeoutInMillis(150000)
                .downloadReadTimeoutInMillis(150000)
                .build();
        rabbitMq = new EmbeddedRabbitMq(config);
        rabbitMq.start();

    }

    @AfterClass
    public static void clear() throws Exception {
        rabbitMq.stop();
    }

    @Before
    public void isBrokerRunning() {
        BrokerRunning.isRunning();
    }

    @Test
    public void receiveMessage_whenAllIsFine_shouldCallManager() throws Exception {
        //Given
        final Hook hook = HookUtils.getHook();
        final DeviceEvent deviceEvent = DeviceEventUtils.getDeviceEvent();
        final DeviceEventHook deviceEventHook = DeviceEventHook.builder().hook(hook).deviceEvent(deviceEvent).build();

        //When
        this.rabbitTemplate.convertSendAndReceive(exchangeName, "devices.v2.#", deviceEventHook);

        //Then
        verify(webhookMessageListener).receiveMessage(deviceEventHook);
        verify(webhookManager).postDeviceEventHook(deviceEventHook);
    }

    @Test
    public void receiveMessage_whenException_shouldLogError() throws Exception {
        //Given
        final Hook hook = HookUtils.getHook();
        final DeviceEvent deviceEvent = DeviceEventUtils.getDeviceEvent();
        final DeviceEventHook deviceEventHook = DeviceEventHook.builder().hook(hook).deviceEvent(deviceEvent).build();

        doThrow(Exception.class).when(webhookManager).postDeviceEventHook(any());
        //When
        this.rabbitTemplate.convertSendAndReceive(exchangeName, "devices.v2.#", deviceEventHook);

        //Then
        verify(webhookMessageListener).receiveMessage(deviceEventHook);
        verify(webhookManager).postDeviceEventHook(deviceEventHook);
    }

    @Test
    public void receiveDeviceChangedMessage_whenAllIsFine_shouldCallManager() throws Exception {
        //Given
        final Hook hook = HookUtils.getHook();
        final DeviceChangeEvent deviceChangeEvent = DeviceChangeEventUtils.getDeviceChangeEvent();
        final DeviceChangeEventHook deviceChangeEventHook = DeviceChangeEventHook.builder()
                .hook(hook)
                .deviceChangeEvent(deviceChangeEvent)
                .build();

        //When
        this.rabbitTemplate.convertSendAndReceive(exchangeName, "devices.change.v2.#", deviceChangeEventHook);

        //Then
        verify(webhookMessageListener).receiveChangeMessage(deviceChangeEventHook);
        verify(webhookManager).postDeviceChangeEventHook(deviceChangeEventHook);
    }

    @Test
    public void receiveDeviceChangedMessage_whenException_shouldLogError() throws Exception {
        //Given
        final Hook hook = HookUtils.getHook();
        final DeviceChangeEvent deviceChangeEvent = DeviceChangeEventUtils.getDeviceChangeEvent();
        final DeviceChangeEventHook deviceChangeEventHook = DeviceChangeEventHook.builder()
                .hook(hook)
                .deviceChangeEvent(deviceChangeEvent)
                .build();

        doThrow(Exception.class).when(webhookManager).postDeviceEventHook(any());
        //When
        this.rabbitTemplate.convertSendAndReceive(exchangeName, "devices.change.v2.#", deviceChangeEventHook);

        //Then
        verify(webhookMessageListener).receiveChangeMessage(deviceChangeEventHook);
        verify(webhookManager).postDeviceChangeEventHook(deviceChangeEventHook);
    }

}
