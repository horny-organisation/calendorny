package ru.calendorny.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class NotificationJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //TODO: логика запихивания в кафку
    }
}
