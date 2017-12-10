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
import io.barracks.webhookservice.model.DeviceChangeEventHook;
import io.barracks.webhookservice.model.DeviceEventHook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class WebhookMessageListener {

    private final WebhookManager webhookManager;

    private final ObjectMapper objectMapper;

    private final CounterService counter;

    @Autowired
    WebhookMessageListener(WebhookManager webhookManager, ObjectMapper objectMapper, CounterService counter) {
        this.webhookManager = webhookManager;
        this.objectMapper = objectMapper;
        this.counter = counter;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${io.barracks.webhook.queuename}", durable = "true", autoDelete = "false"),
                    exchange = @Exchange(value = "${io.barracks.amqp.exchangename}", type = "topic", durable = "true"),
                    key = "${io.barracks.webhook.routingkey}"
            )
    )
    public void receiveMessage(@Payload DeviceEventHook deviceEventHook) {
        try {
            webhookManager.postDeviceEventHook(deviceEventHook);
            incrementRabbitMQMetric("success");
        } catch (Exception e) {
            log.error("Error while sending data to the web hook.", e);
            incrementRabbitMQMetric("error");
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${io.barracks.webhookchange.queuename}", durable = "true", autoDelete = "false"),
                    exchange = @Exchange(value = "${io.barracks.amqp.exchangename}", type = "topic", durable = "true"),
                    key = "${io.barracks.webhookchange.routingkey}"
            )
    )
    public void receiveChangeMessage(@Payload DeviceChangeEventHook deviceChangeEventHook) {
        try {
            webhookManager.postDeviceChangeEventHook(deviceChangeEventHook);
            incrementRabbitMQMetric("success");
        } catch (Exception e) {
            log.error("Error while sending data to the web hook.", e);
            incrementRabbitMQMetric("error");
        }
    }

    private void incrementRabbitMQMetric(String status) {
        counter.increment("message.process.device.event." + status);
    }

}
