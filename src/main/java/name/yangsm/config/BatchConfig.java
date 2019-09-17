package name.yangsm.config;

import lombok.extern.slf4j.Slf4j;
import name.ealen.model.Access;
import name.yangsm.bean.FromBean;
import name.yangsm.bean.ToBean;
import name.yangsm.service.MyService;
import name.yangsm.util.OrikaMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

/**
 *@descption 批量处理
 *@author yangsm
 *@date  2019/9/17
 */
@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfig {
    @Autowired
    JobBuilderFactory jobBuilderFactory;
    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Resource
    private EntityManagerFactory emf;

    @Bean
    public Job testJob() {
        return jobBuilderFactory.get("testJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .start(testStep())
                .build();
    }

    @Bean
    public Step testStep() {
        return stepBuilderFactory.get("testStep")
                .<FromBean, ToBean>chunk(1000)
                //这里不用传单数
                .reader(getDataReader(""))
                .processor(getDataProcessor())
                .writer(getDataWriter())
                .faultTolerant()
                .skipLimit(1000)
                .skip(Exception.class)
                //设置一个step监听器
                .listener(skipListener())
                .retryLimit(3)
                .retry(RuntimeException.class)
                .build();
    }

    /**
     *@descption   自定义 查询sql 并且支持传递参数
     *@param    testType 测试参数
     *@author yangsm
     *@date  2019/9/17
     */
    @Bean(destroyMethod = "")
    @StepScope
    public ItemReader<? extends FromBean> getDataReader(@Value("#{jobParameters[testType]}") String testType) {
        //读取数据,这里可以用JPA,JDBC,JMS 等方式 读入数据
        JpaPagingItemReader<FromBean> reader = new JpaPagingItemReader<>();
        //这里选择JPA方式读数据 一个简单的 native SQL
        String sqlQuery = "SELECT * FROM from_tab where type = '"+ testType + "'";
        try {
            JpaNativeQueryProvider<FromBean> queryProvider = new JpaNativeQueryProvider<>();
            queryProvider.setSqlQuery(sqlQuery);
            queryProvider.setEntityClass(FromBean.class);
            queryProvider.afterPropertiesSet();
            reader.setEntityManagerFactory(emf);
            //每页查询个数   5
            reader.setPageSize(1000);
            reader.setQueryProvider(queryProvider);
            reader.afterPropertiesSet();
            //所有ItemReader和ItemWriter实现都会在ExecutionContext提交之前将其当前状态存储在其中,如果不希望这样做,可以设置setSaveState(false)
            reader.setSaveState(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reader;
    }

    @Bean
    public ItemProcessor<FromBean, ToBean> getDataProcessor() {
        return fromBean -> {
            ToBean toBean = new ToBean();
            OrikaMapper.map(fromBean, toBean);
            //模拟  假装处理数据,这里处理就是打印一下
            log.info("processor data : " + toBean.toString());

            log.info("新增一下");
            new MyService<ToBean>().doAdd(toBean);

            return toBean;
        };
    }

    @Bean
    public ItemWriter<ToBean> getDataWriter() {
        return list -> {
            for (ToBean toBean : list) {
                log.info("write data : " + toBean); //模拟 假装写数据 ,这里写真正写入数据的逻辑
            }
        };
    }

    @Bean
    public JobExecutionListener listener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("====================================listener.beforeJob=============================");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("====================================listener.afterJob=============================");
            }
        };
    }

    @Bean
    public SkipListener<FromBean,ToBean> skipListener(){
        return new SkipListener<FromBean,ToBean>() {
            @Override
            public void onSkipInRead(Throwable e) {
                log.error("===========跳过读取的时候触发===============skipListener.onSkipInRead=============================",e);
            }

            @Override
            public void onSkipInWrite(ToBean toBean, Throwable e) {
                log.error("===========跳过写入的时候触发===============skipListener.onSkipInWrite=============================",e);
            }

            @Override
            public void onSkipInProcess(FromBean fromBean, Throwable e) {
                log.error("===========处理出粗的时候触发===============skipListener.onSkipInWrite=============================",e);
            }
        };
    }
}