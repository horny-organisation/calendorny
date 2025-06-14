package ru.calendorny.eventservice.quartz.service;

import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;
import ru.calendorny.eventservice.kafka.dto.request.EventNotificationRequest;
import ru.calendorny.eventservice.quartz.job.MonthlyJob;
import ru.calendorny.eventservice.quartz.job.OneTimeJob;
import ru.calendorny.eventservice.quartz.job.WeeklyJob;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobSchedulerService {

    private final Scheduler scheduler;

    public String scheduleOneTime(EventNotificationRequest request, LocalDateTime runAt) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(OneTimeJob.class, request);
        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .startAt(Timestamp.valueOf(runAt))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
        return jobDetail.getKey().getName();
    }

    public String scheduleWeekly(EventNotificationRequest request, DayOfWeek dayOfWeek, LocalTime time) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(WeeklyJob.class, request);
        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withSchedule(CronScheduleBuilder
                .weeklyOnDayAndHourAndMinute(dayOfWeek.getValue(), time.getHour(), time.getMinute())
                .withMisfireHandlingInstructionFireAndProceed())
            .startNow()
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
        return jobDetail.getKey().getName();
    }

    public String scheduleMonthly(EventNotificationRequest request, int dayOfMonth, LocalTime time) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(MonthlyJob.class, request);
        String cron = String.format("0 %d %d %d * ?", time.getMinute(), time.getHour(), dayOfMonth);

        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withSchedule(CronScheduleBuilder
                .cronSchedule(cron)
                .withMisfireHandlingInstructionFireAndProceed())
            .startNow()
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
        return jobDetail.getKey().getName();
    }


    private JobDetail buildJobDetail(Class<? extends Job> jobClass, EventNotificationRequest request) {
        JobDataMap map = new JobDataMap();
        map.put("eventId", request.eventId());
        map.put("userId", request.userId().toString());
        map.put("title", request.title());
        map.put("location", request.location());
        map.put("start", request.start().toString());
        map.put("end", request.end().toString());

        return JobBuilder.newJob(jobClass)
            .withIdentity(UUID.randomUUID().toString(), "event-jobs")
            .usingJobData(map)
            .storeDurably()
            .build();
    }
}

