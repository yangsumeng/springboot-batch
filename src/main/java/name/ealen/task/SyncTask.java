package name.ealen.task;

import lombok.extern.slf4j.Slf4j;
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
    @Scheduled(cron = "0 0/5 * * * ?")
    public synchronized void onSchedule() {
        log.info("============================开始====================");

        log.info("============================结束====================");
    }
}
