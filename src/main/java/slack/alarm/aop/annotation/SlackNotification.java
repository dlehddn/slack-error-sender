package slack.alarm.aop.annotation;


import slack.alarm.enums.SlackNotificationLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlackNotification {
    SlackNotificationLevel level() default SlackNotificationLevel.WARN;
}
