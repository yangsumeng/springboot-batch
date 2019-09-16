package name.ealen.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @version 1.0
 */
@Component
public class SyncTask {
    @Scheduled(cron = "0 0/5 * * * ?")
    public synchronized void onSchedule() {
        System.out.println(new Date());
    }
}
