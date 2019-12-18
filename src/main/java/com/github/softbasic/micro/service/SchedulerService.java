package com.github.softbasic.micro.service;


import com.github.softbasic.micro.model.SchedulerModel;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix="spring.task.scheduling",name = "enable", havingValue = "true")
public class SchedulerService {
    // 任务调度
    @Autowired
    private Scheduler scheduler;
    private final static String JOB_DEFAULT_GROUP="JOB_DEFAULT_GROUP";
    private final static String TRIGGER_DEFAULT_GROUP="TRIGGER_DEFAULT_GROUP";
    private final static String TRIGGER="Trigger";

    /**
     * 启动一个定时任务
     * @param schedulerModel
     * @throws Exception
     */
    public void startJob(SchedulerModel schedulerModel) throws Exception {

        Class<Job> jobClass = (Class<Job>) Class.forName(schedulerModel.getJobClassName());
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("schedulerModel", schedulerModel);
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(schedulerModel.getJobName(), JOB_DEFAULT_GROUP)
                .setJobData(jobDataMap)
                .build();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(schedulerModel.getJobName()+TRIGGER, TRIGGER_DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(schedulerModel.getCron()))
                .build();
        scheduler.scheduleJob(jobDetail, cronTrigger);

/*        LocalDateTime localDateTime = message.getLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        //当前时间<=任务时间
        if(now.isBefore(localDateTime) || now.isEqual(localDateTime)){
            //时间转换为cron表达式
            String cron = timeUtil.localDateTimeToCron(message.getLocalDateTime());
            System.out.println("cron = " + cron);

        }*/
    }



    /**
     * 获取Job信息
     *
     * @param schedulerModel
     * @return
     * @throws Exception
     */
    public String getJobInfo(SchedulerModel schedulerModel) throws Exception {
        TriggerKey triggerKey = new TriggerKey(schedulerModel.getJobName()+TRIGGER, TRIGGER_DEFAULT_GROUP);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        return String.format("time:%s,state:%s", cronTrigger.getCronExpression(),
                scheduler.getTriggerState(triggerKey).name());
    }

    /**
     * 修改某个任务的执行时间
     *
     * @param schedulerModel
     * @return
     * @throws Exception
     */
    public boolean modifyJob(SchedulerModel schedulerModel) throws Exception {
        TriggerKey triggerKey = new TriggerKey(schedulerModel.getJobName()+TRIGGER, TRIGGER_DEFAULT_GROUP);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        String oldTime = cronTrigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(schedulerModel.getCron())) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(schedulerModel.getCron());
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(schedulerModel.getJobName()+TRIGGER, TRIGGER_DEFAULT_GROUP)
                    .withSchedule(cronScheduleBuilder)
                    .build();
            return scheduler.rescheduleJob(triggerKey, trigger)!=null;
        }
        return false;
    }

    /**
     * 暂停某个任务
     *
     * @param schedulerModel
     * @throws Exception
     */
    public void pauseJob(SchedulerModel schedulerModel) throws Exception {
        JobKey jobKey = new JobKey(schedulerModel.getJobName(), JOB_DEFAULT_GROUP);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return;
        }
        scheduler.pauseJob(jobKey);
    }

    /**
     * 暂停所有任务
     *
     * @throws Exception
     */
    public void pauseAllJob() throws Exception {
        scheduler.pauseAll();
    }

    /**
     * 恢复某个任务
     *
     * @param schedulerModel
     * @throws Exception
     */
    public void resumeJob(SchedulerModel schedulerModel) throws Exception {
        JobKey jobKey = new JobKey(schedulerModel.getJobName(), JOB_DEFAULT_GROUP);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler.resumeJob(jobKey);
    }

    /**
     * 恢复所有任务
     *
     * @throws Exception
     */
    public void resumeAllJob() throws Exception {
        scheduler.resumeAll();
    }


    /**
     * 删除某个任务
     *
     * @param schedulerModel
     * @throws Exception
     */
    public void deleteJob(SchedulerModel schedulerModel) throws Exception {
        JobKey jobKey = new JobKey(schedulerModel.getJobName(), JOB_DEFAULT_GROUP);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return;
        }
        scheduler.deleteJob(jobKey);
    }

}
