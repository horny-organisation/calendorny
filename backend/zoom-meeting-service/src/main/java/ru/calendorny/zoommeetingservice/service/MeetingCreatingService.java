package ru.calendorny.zoommeetingservice.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.calendorny.zoommeetingservice.dto.meeting.ZoomMeetingRequest;
import ru.calendorny.zoommeetingservice.dto.meeting.ZoomMeetingResponse;
import ru.calendorny.zoommeetingservice.dto.meeting.ZoomMeetingSettings;
import ru.calendorny.zoommeetingservice.properties.ZoomProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingCreatingService {

    private static final String TOPIC_PREFIX = "Meeting on";
    private static final int MEETING_TYPE_SCHEDULED = 2;

    private final ObjectFactory<WebClient> zoomWebClientFactory;
    private final ZoomProperties properties;

    public String createMeeting(LocalDateTime startTime) {
        String formattedStartTime = getFormattedStartTime(startTime);

        ZoomMeetingRequest request = createMeetingRequest(formattedStartTime, startTime);

        ZoomMeetingResponse response = callZoomApi(request);

        return extractJoinUrl(response);
    }

    private String getFormattedStartTime(LocalDateTime startTime) {
        ZonedDateTime zonedStartTime = startTime.atZone(ZoneOffset.UTC);
        ZonedDateTime roundedStartTime = zonedStartTime.truncatedTo(ChronoUnit.SECONDS);
        return roundedStartTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private ZoomMeetingRequest createMeetingRequest(String formattedStartTime, LocalDateTime startTime) {
        String topicTitle = "%s %s"
                .formatted(TOPIC_PREFIX, startTime.format(DateTimeFormatter.ofPattern(properties.dateTimePattern())));
        ZoomMeetingSettings settings = new ZoomMeetingSettings(false);
        return new ZoomMeetingRequest(
                topicTitle, MEETING_TYPE_SCHEDULED, formattedStartTime, properties.timezone(), settings);
    }

    private ZoomMeetingResponse callZoomApi(ZoomMeetingRequest request) {
        WebClient client = zoomWebClientFactory.getObject();
        try {
            return client.post()
                    .uri(properties.meetingCreateUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ZoomMeetingResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Exception while calling Zoom API", e);
            return null;
        }
    }

    private String extractJoinUrl(ZoomMeetingResponse response) {
        if (response != null && response.joinUrl() != null) {
            return response.joinUrl();
        } else {
            log.error("Zoom API returned unexpected response");
            return "Error: Unexpected response";
        }
    }
}
