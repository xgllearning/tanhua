package com.tanhua.admin.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AnalysisTask {
    /**
     * 配置时间规则  秒分时日月周
     * 0 15 10 ? * *    每天的10：15：0
     * 0 15 10 * * ?    每天的10：15：0
     * 0 * 14 * * ?     14点每分钟的0秒都要执行，*只匹配整数
     * 0 0/5 14 * * ?   每天的14点每隔5分钟执行
     * 0 0/5 14,18 * * ?  每天的14点和18点每隔5分钟执行
     */
    @Scheduled( cron = "0/20 * * * * ? ")//秒分时日月周
    public void analysis() {
        //业务逻辑
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("当前时间："+time);

    }
}
