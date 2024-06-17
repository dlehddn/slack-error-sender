package slack.alarm.aop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import slack.alarm.aop.annotation.SlackNotification;
import slack.alarm.core.MessageGenerator;
import slack.alarm.core.MessageSender;
import slack.alarm.core.RequestStorage;
import slack.alarm.enums.SlackNotificationLevel;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SlackAspect {

    private final RequestStorage requestStorage;
    private final MessageGenerator messageGenerator;
    private final MessageSender messageSender;

    @Before("@annotation(slack.alarm.aop.annotation.SlackNotification)")
    public void sendExceptionMessage(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (!validateIsException(args)) {
            return;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        SlackNotificationLevel level = signature.getMethod()
                .getAnnotation(SlackNotification.class)
                .level();

        String message = messageGenerator.generate(requestStorage.get(), (Exception) args[0], level);
        messageSender.send(message);
    }

    private boolean validateIsException(Object[] args) {
        if (!(args[0] instanceof Exception)) {
            log.warn("[SlackAlarm] argument is not Exception");
            return false;
        }
        return true;
    }
}
