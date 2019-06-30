package com.bug1024.metralog.home.config;

import com.bug1024.metralog.consumer.MetralogConsumer;
import com.bug1024.metralog.input.cat.CatInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * InputConfig
 * @author bug1024
 * @date 2019-06-29
 */
@Component
@Slf4j
public class InputConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${input.cat.server-port}")
    private int catPort;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            try {
                CatInput input = CatInput.getInstance();
                input.setMetralogConsumer(new MetralogConsumer());
                input.setPort(catPort);
                input.init();
                log.warn("CatInput init success port:{}", catPort);
            } catch (Exception e) {
                log.error("CatInput init failed port:{}", catPort, e);
            }
        }
    }
}
