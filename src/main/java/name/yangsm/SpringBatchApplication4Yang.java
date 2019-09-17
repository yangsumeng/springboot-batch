package name.yangsm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yangsm
 */
@SpringBootApplication(scanBasePackages="name.yangsm")
@EnableScheduling
@EnableAsync
public class SpringBatchApplication4Yang {
    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication4Yang.class, args);
    }
}
