package ru.calendorny.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;
import ru.calendorny.dto.response.EventShortResponse;
import ru.calendorny.exception.ServiceException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobSchedulerService {

    private final Scheduler scheduler;

    public String scheduleOneTimeJob(LocalDateTime dateTime, UUID userId, EventShortResponse eventShortResponse) throws SchedulerException {
        String jobId = UUID.randomUUID().toString();
        JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
            .withIdentity(jobId, "notification-jobs")
            .usingJobData("event", eventShortResponse.title())
            .usingJobData("location", eventShortResponse.location())
            .usingJobData("start", String.valueOf(eventShortResponse.startTime()))
            .usingJobData("end", String.valueOf(eventShortResponse.endTime()))
            .usingJobData("userId", userId.toString())
            .storeDurably()
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(UUID.randomUUID().toString(), "notification-triggers")
            .startAt(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withMisfireHandlingInstructionFireNow())
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
        return jobId;
    }

    public void deleteJob(String jobId) {
        try {
            scheduler.deleteJob(JobKey.jobKey(jobId, "notification-jobs"));
        } catch (SchedulerException e) {
            throw new ServiceException("Can not delete notification job with id: %s".formatted(jobId));
        }
    }


}
