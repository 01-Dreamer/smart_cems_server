package zxylearn.smart_cems_server.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ALERT_EXCHANGE = "energy.alert.exchange";
    public static final String ALERT_QUEUE = "energy.alert.queue";
    public static final String ALERT_ROUTING_KEY = "energy.alert.key";

    @Bean
    public Exchange alertExchange() {
        return ExchangeBuilder.topicExchange(ALERT_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue alertQueue() {
        return QueueBuilder.durable(ALERT_QUEUE).build();
    }

    @Bean
    public Binding alertBinding(Queue alertQueue, Exchange alertExchange) {
        return BindingBuilder.bind(alertQueue)
                .to(alertExchange)
                .with(ALERT_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
