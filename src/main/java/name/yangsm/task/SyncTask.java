package name.yangsm.task;

import lombok.extern.slf4j.Slf4j;
import name.yangsm.bean.FromBean;
import name.yangsm.service.MyService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author YSTen_NC0705
 * @version 1.0
 */
@Component
@Slf4j
public class SyncTask {
    //执行器
    @Autowired
    private JobLauncher jobLauncher;
    //  DataBatchConfiguration 中注入的Job Bean
    @Autowired
    private Job testJob;

    @Scheduled(cron = "0 0/1 * * * ?")
    public synchronized void onSchedule() {
        log.info("============================开始====================");
        //可以色值参数
//        new MyService<FromBean>().doAdd(new FromBean("1","name1","1","false"));
//        new MyService<FromBean>().doAdd(new FromBean("2","name1","1","false"));
//        new MyService<FromBean>().doAdd(new FromBean("3","name1","1","false"));
//        new MyService<FromBean>().doAdd(new FromBean("4","name1","1","false"));
//        new MyService<FromBean>().doAdd(new FromBean("5","name1","1","false"));
//        new MyService<FromBean>().doAdd(new FromBean("6","name1","1","false"));

        try {
            JobParameters jobParameters = new JobParametersBuilder().addString("testType", "1").toJobParameters();
            jobLauncher.run(testJob, jobParameters);

            log.info("============================上下两个任务分割====================");

            jobParameters = new JobParametersBuilder().addString("testType", "2").toJobParameters();
            jobLauncher.run(testJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        }
        log.info("============================结束====================");
    }
}
