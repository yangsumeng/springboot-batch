package name.ealen.task;

import lombok.extern.slf4j.Slf4j;
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

import java.util.Date;

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
    private Job dataHandleJob;

    @Scheduled(cron = "0 0/5 * * * ?")
    public synchronized void onSchedule() {
        log.info("============================开始====================");
        //可以色值参数
        JobParameters jobParameters = new JobParametersBuilder()
//                .addString(Constants.PARAM_CHANNELCODE, channelCode)
//                .addString(Constants.PARAM_JOBTIME, jobTime)
                .toJobParameters();
        try {
            jobLauncher.run(dataHandleJob, jobParameters);
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
