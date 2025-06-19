package ru.calendorny.eventservice.quartz.service;

import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;
import ru.calendorny.eventservice.kafka.dto.request.EventReminderRequest;
import ru.calendorny.eventservice.quartz.job.ReminderJob;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobSchedulerService {

    private final Scheduler scheduler;
    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");

    public UUID scheduleOneTime(EventReminderRequest request, LocalDateTime runAt) throws SchedulerException {
        ZonedDateTime zonedDateTime = runAt.atZone(MOSCOW_ZONE);
        Instant runAtUtc = zonedDateTime.toInstant();
        JobDetail jobDetail = buildJobDetail(request);
        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .startAt(Date.from(runAtUtc))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withMisfireHandlingInstructionFireNow())
            .build();
        scheduler.scheduleJob(jobDetail, trigger);
        return UUID.fromString(jobDetail.getKey().getName());
    }

    public UUID scheduleWeekly(EventReminderRequest request, DayOfWeek dayOfWeek, LocalTime time) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(request);
        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withSchedule(CronScheduleBuilder
                .weeklyOnDayAndHourAndMinute(dayOfWeek.getValue(), time.getHour(), time.getMinute())
                .withMisfireHandlingInstructionFireAndProceed()
                .inTimeZone(TimeZone.getTimeZone(MOSCOW_ZONE))
            )
            .startNow()
            .build();
        scheduler.scheduleJob(jobDetail, trigger);
        return UUID.fromString(jobDetail.getKey().getName());
    }

    public UUID scheduleMonthly(EventReminderRequest request, int dayOfMonth, LocalTime time) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(request);
        String cron = String.format("0 %d %d %d * ?", time.getMinute(), time.getHour(), dayOfMonth);
        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withSchedule(CronScheduleBuilder
                .cronSchedule(cron)
                .withMisfireHandlingInstructionFireAndProceed()
                .inTimeZone(TimeZone.getTimeZone(MOSCOW_ZONE))
            )
            .startNow()
            .build();
        scheduler.scheduleJob(jobDetail, trigger);
        return UUID.fromString(jobDetail.getKey().getName());
    }

    public void deleteJob(String jobId) throws SchedulerException {
        JobKey jobKey = new JobKey(jobId, "event-jobs");
        scheduler.deleteJob(jobKey);
    }

    private JobDetail buildJobDetail(EventReminderRequest request) {
        JobDataMap map = new JobDataMap();
        map.put("eventId", request.eventId());
        map.put("userId", request.userId().toString());
        map.put("title", request.title());
        map.put("location", request.location());
        return JobBuilder.newJob(ReminderJob.class)
            .withIdentity(UUID.randomUUID().toString(), "event-jobs")
            .usingJobData(map)
            .build();
    }
}
