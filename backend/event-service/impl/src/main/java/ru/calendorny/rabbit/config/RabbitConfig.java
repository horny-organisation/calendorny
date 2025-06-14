package ru.calendorny.rabbit.config;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.calendorny.rabbit.properties.RabbitConfigProperties;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

    private final RabbitConfigProperties properties;
    private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue googleMeetQueue() {
        return QueueBuilder.durable(properties.googleMeetQueue())
            .withArgument(X_DEAD_LETTER_EXCHANGE, properties.deadLetterExchange())
            .withArgument(X_DEAD_LETTER_ROUTING_KEY, properties.googleMeetDlqRoutingKey())
            .build();
    }

    @Bean
    public Queue meetingLinksQueue() {
        return QueueBuilder.durable(properties.meetingLinksQueue()).build();
    }

    @Bean
    public Queue googleMeetDeadLetterQueue() {
        return QueueBuilder.durable(properties.googleMeetDlq()).build();
    }

    @Bean
    public DirectExchange meetingCreateExchange() {
        return ExchangeBuilder.directExchange(properties.meetingCreateExchange())
            .durable(true)
            .build();
    }

    @Bean
    public DirectExchange meetingLinksExchange() {
        return ExchangeBuilder.directExchange(properties.meetingLinksExchange())
            .durable(true)
            .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(properties.deadLetterExchange())
            .durable(true)
            .build();
    }

    @Bean
    public Binding googleMeetBinding(Queue googleMeetQueue, DirectExchange meetingCreateExchange) {
        return BindingBuilder.bind(googleMeetQueue).to(meetingCreateExchange).with(properties.googleMeetRoutingKey());
    }

    @Bean
    public Binding meetingLinkBinding(Queue meetingLinksQueue, DirectExchange meetingLinksExchange) {
        return BindingBuilder.bind(meetingLinksQueue).to(meetingLinksExchange).with(properties.meetingLinkRoutingKey());
    }

    @Bean
    public Binding googleMeetDLQBinding(Queue googleMeetDeadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(googleMeetDeadLetterQueue)
            .to(deadLetterExchange)
            .with(properties.googleMeetDlqRoutingKey());
    }


    @Bean
    public Queue zoomMeetQueue() {
        return QueueBuilder.durable(properties.zoomMeetQueue())
            .withArgument(X_DEAD_LETTER_EXCHANGE, properties.deadLetterExchange())
            .withArgument(X_DEAD_LETTER_ROUTING_KEY, properties.zoomMeetDlqRoutingKey())
            .build();
    }


    @Bean
    public Queue zoomMeetDeadLetterQueue() {
        return QueueBuilder.durable(properties.zoomMeetDlq()).build();
    }


    @Bean
    public Binding zoomMeetBinding(Queue zoomMeetQueue, DirectExchange meetingCreateExchange) {
        return BindingBuilder.bind(zoomMeetQueue).to(meetingCreateExchange).with(properties.zoomMeetRoutingKey());
    }

    @Bean
    public Binding zoomMeetDLQBinding(Queue zoomMeetDeadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(zoomMeetDeadLetterQueue)
            .to(deadLetterExchange)
            .with(properties.zoomMeetDlqRoutingKey());
    }
}
