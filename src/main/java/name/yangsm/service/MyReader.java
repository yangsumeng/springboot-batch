package name.yangsm.service;

import lombok.extern.slf4j.Slf4j;
import name.yangsm.bean.FromBean;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

@Service
@Slf4j
public class MyReader {

    @Resource
    private EntityManagerFactory emf;

    public JpaPagingItemReader<FromBean> initJpaReader(String sqlQuery) {
        JpaPagingItemReader<FromBean> reader = new JpaPagingItemReader<>();
        log.info("Method:OrderItemReader job query sql : " + sqlQuery);
        try {
            JpaNativeQueryProvider<FromBean> queryProvider = new JpaNativeQueryProvider<>();
            queryProvider.setSqlQuery(sqlQuery);
            queryProvider.setEntityClass(FromBean.class);
            queryProvider.afterPropertiesSet();
            reader.setEntityManagerFactory(emf);
            reader.setPageSize(1000);
            reader.setQueryProvider(queryProvider);
            reader.afterPropertiesSet();
            //所有ItemReader和ItemWriter实现都会在ExecutionContext提交之前将其当前状态存储在其中,如果不希望这样做,可以设置setSaveState(false)
            reader.setSaveState(true);
        } catch (Exception e) {
            log.error("Method:initJpaReader init dataReader error, sqlQuery is {} ", sqlQuery, e);
        }

        return reader;
    }
}
