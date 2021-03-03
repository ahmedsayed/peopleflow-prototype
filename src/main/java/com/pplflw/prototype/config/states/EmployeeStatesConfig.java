package com.pplflw.prototype.config.states;

import com.pplflw.prototype.domains.enums.EmployeeStatus;
import com.pplflw.prototype.domains.enums.EmployeeStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

@Configuration
@EnableStateMachineFactory
public class EmployeeStatesConfig extends EnumStateMachineConfigurerAdapter<EmployeeStatus, EmployeeStatusEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public void configure(StateMachineConfigurationConfigurer<EmployeeStatus, EmployeeStatusEvent> config)
            throws Exception {
        config.withConfiguration()
                .autoStartup(false)
                .listener(new StateMachineListenerAdapter<>() {
                    @Override
                    public void eventNotAccepted(Message<EmployeeStatusEvent> event) {
                        log.error("event not accepted: {}", event);
                    }
                });
    }
    
    @Override
    public void configure(StateMachineStateConfigurer<EmployeeStatus, EmployeeStatusEvent> states)
            throws Exception {
        states.withStates()
                .initial(EmployeeStatus.ADDED)
                .state(EmployeeStatus.IN_CHECK)
                .state(EmployeeStatus.APPROVED)
                .end(EmployeeStatus.ACTIVE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EmployeeStatus, EmployeeStatusEvent> transitions)
            throws Exception {
        transitions
                .withExternal().source(EmployeeStatus.ADDED).target(EmployeeStatus.IN_CHECK).event(EmployeeStatusEvent.START_CHECK)
                .and()
                .withExternal().source(EmployeeStatus.IN_CHECK).target(EmployeeStatus.APPROVED).event(EmployeeStatusEvent.APPROVE)
                .and()
                .withExternal().source(EmployeeStatus.APPROVED).target(EmployeeStatus.ACTIVE).event(EmployeeStatusEvent.ACTIVATE);
    }
}
