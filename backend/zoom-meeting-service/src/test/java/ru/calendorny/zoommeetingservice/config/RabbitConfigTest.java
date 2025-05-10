package ru.calendorny.zoommeetingservice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import ru.calendorny.zoommeetingservice.properties.RabbitProperties;

@ExtendWith(MockitoExtension.class)
public class RabbitConfigTest {

    private static final String ZOOM_MEET_QUEUE = "zoom.meet.queue";
    private static final String MEETING_LINKS_QUEUE = "meeting.links.queue";
    private static final String MEETING_CREATE_EXCHANGE = "meeting.create.exchange";
    private static final String MEETING_LINKS_EXCHANGE = "meeting.links.exchange";
    private static final String ZOOM_MEET_ROUTING_KEY = "zoom.meet.routingKey";
    private static final String MEETING_LINK_ROUTING_KEY = "meeting.link.routingKey";

    @Mock
    private RabbitProperties properties;

    @InjectMocks
    private RabbitConfig rabbitConfig;

    @Mock
    private ConnectionFactory connectionFactory;

    @Test
    void testJsonMessageConverter() {
        MessageConverter converter = rabbitConfig.jsonMessageConverter();
        assertThat(converter).isInstanceOf(Jackson2JsonMessageConverter.class);
    }

    @Test
    void testAmqpTemplate() {
        AmqpTemplate template = rabbitConfig.amqpTemplate(connectionFactory);
        assertThat(template).isInstanceOf(RabbitTemplate.class);
        assertThat(((RabbitTemplate) template).getMessageConverter()).isInstanceOf(Jackson2JsonMessageConverter.class);
    }

    @Test
    void testZoomMeetQueue() {
        when(properties.zoomMeetQueue()).thenReturn(ZOOM_MEET_QUEUE);
        Queue queue = rabbitConfig.zoomMeetQueue();
        assertThat(queue.getName()).isEqualTo(ZOOM_MEET_QUEUE);
        assertThat(queue.isDurable()).isTrue();
    }

    @Test
    void testMeetingLinksQueue() {
        when(properties.meetingLinksQueue()).thenReturn(MEETING_LINKS_QUEUE);
        Queue queue = rabbitConfig.meetingLinksQueue();
        assertThat(queue.getName()).isEqualTo(MEETING_LINKS_QUEUE);
        assertThat(queue.isDurable()).isTrue();
    }

    @Test
    void testMeetingCreateExchange() {
        when(properties.meetingCreateExchange()).thenReturn(MEETING_CREATE_EXCHANGE);
        DirectExchange exchange = rabbitConfig.meetingCreateExchange();
        assertThat(exchange.getName()).isEqualTo(MEETING_CREATE_EXCHANGE);
        assertThat(exchange.isDurable()).isTrue();
    }

    @Test
    void testMeetingLinksExchange() {
        when(properties.meetingLinksExchange()).thenReturn(MEETING_LINKS_EXCHANGE);
        DirectExchange exchange = rabbitConfig.meetingLinksExchange();
        assertThat(exchange.getName()).isEqualTo(MEETING_LINKS_EXCHANGE);
        assertThat(exchange.isDurable()).isTrue();
    }

    @Test
    void testZoomMeetBinding() {
        when(properties.zoomMeetRoutingKey()).thenReturn(ZOOM_MEET_ROUTING_KEY);

        Queue zoomQueue = new Queue(ZOOM_MEET_QUEUE);
        DirectExchange createExchange = new DirectExchange(MEETING_CREATE_EXCHANGE);

        Binding meetBinding = rabbitConfig.zoomMeetBinding(zoomQueue, createExchange);

        assertThat(meetBinding.getDestination()).isEqualTo(ZOOM_MEET_QUEUE);
        assertThat(meetBinding.getExchange()).isEqualTo(MEETING_CREATE_EXCHANGE);
        assertThat(meetBinding.getRoutingKey()).isEqualTo(ZOOM_MEET_ROUTING_KEY);
    }

    @Test
    void testMeetingLinkBinding() {
        when(properties.meetingLinkRoutingKey()).thenReturn(MEETING_LINK_ROUTING_KEY);

        Queue linksQueue = new Queue(MEETING_LINKS_QUEUE);
        DirectExchange linksExchange = new DirectExchange(MEETING_LINKS_EXCHANGE);

        Binding linkBinding = rabbitConfig.meetingLinkBinding(linksQueue, linksExchange);

        assertThat(linkBinding.getDestination()).isEqualTo(MEETING_LINKS_QUEUE);
        assertThat(linkBinding.getExchange()).isEqualTo(MEETING_LINKS_EXCHANGE);
        assertThat(linkBinding.getRoutingKey()).isEqualTo(MEETING_LINK_ROUTING_KEY);
    }
}
