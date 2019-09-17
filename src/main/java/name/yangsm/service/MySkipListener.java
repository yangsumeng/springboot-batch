package name.yangsm.service;

import lombok.extern.slf4j.Slf4j;
import name.yangsm.bean.FromBean;
import name.yangsm.bean.ToBean;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Service;

/**
 * @author wangqiangcc
 * @since 2019/4/19
 **/
@Slf4j
@Service
public class MySkipListener implements SkipListener<FromBean, ToBean> {

    @Override
    public void onSkipInRead(Throwable arg0) {
        log.error("Method:onSkipInRead OrderMetaEntity was skipped in process", arg0);
    }

    @Override
    public void onSkipInProcess(FromBean from, Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onSkipInWrite(ToBean toBean, Throwable throwable) {
    }

}