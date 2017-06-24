package com.jt.order.job;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.jt.order.mapper.OrderMapper;

public class PaymentOrderJob extends QuartzJobBean {

	@Override // 回调方法
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		ApplicationContext applicationContext = (ApplicationContext) context.getJobDetail().getJobDataMap()
				.get("applicationContext");
		//利用joda提供工具類對時間處理，每個1天來觸發這個任務
		applicationContext.getBean(OrderMapper.class).paymentOrderScan(new DateTime().minusDays(1).toDate());
	}

}
