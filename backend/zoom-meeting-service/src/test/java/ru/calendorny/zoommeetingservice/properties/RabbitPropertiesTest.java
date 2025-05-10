package ru.calendorny.zoommeetingservice.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class RabbitPropertiesTest {

    private static final String ZOOM_MEET_QUEUE = "zoom.meet.queue";
    private static final String MEETING_LINKS_QUEUE = "meeting.links.queue";
    private static final String MEETING_CREATE_EXCHANGE = "meeting.create.exchange";
    private static final String MEETING_LINKS_EXCHANGE = "meeting.links.exchange";
    private static final String ZOOM_MEET_ROUTING_KEY = "zoom.meet.routingKey";
    private static final String MEETING_LINK_ROUTING_KEY = "meeting.link.routingKey";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "app.rabbit.zoomMeetQueue=%s".formatted(ZOOM_MEET_QUEUE),
                    "app.rabbit.meetingLinksQueue=%s".formatted(MEETING_LINKS_QUEUE),
                    "app.rabbit.meetingCreateExchange=%s".formatted(MEETING_CREATE_EXCHANGE),
                    "app.rabbit.meetingLinksExchange=%s".formatted(MEETING_LINKS_EXCHANGE),
                    "app.rabbit.zoomMeetRoutingKey=%s".formatted(ZOOM_MEET_ROUTING_KEY),
                    "app.rabbit.meetingLinkRoutingKey=%s".formatted(MEETING_LINK_ROUTING_KEY));

    @Test
    void testRabbitPropertiesBinding() {
        contextRunner.run(context -> {
            RabbitProperties props = context.getBean(RabbitProperties.class);
            assertThat(props.zoomMeetQueue()).isEqualTo(ZOOM_MEET_QUEUE);
            assertThat(props.meetingLinksQueue()).isEqualTo(MEETING_LINKS_QUEUE);
            assertThat(props.meetingCreateExchange()).isEqualTo(MEETING_CREATE_EXCHANGE);
            assertThat(props.meetingLinksExchange()).isEqualTo(MEETING_LINKS_EXCHANGE);
            assertThat(props.zoomMeetRoutingKey()).isEqualTo(ZOOM_MEET_ROUTING_KEY);
            assertThat(props.meetingLinkRoutingKey()).isEqualTo(MEETING_LINK_ROUTING_KEY);
        });
    }

    @EnableConfigurationProperties(RabbitProperties.class)
    static class TestConfig {}
}
