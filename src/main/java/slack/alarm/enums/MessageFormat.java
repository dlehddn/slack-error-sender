package slack.alarm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessageFormat {
    EXTRACTION_ERROR_MESSAGE("메세지를 추출하는데 오류가 생겼습니다.\nmessagee : %s"),
    EXCEPTION_MESSAGE_FORMAT("_%s_ %s.%s:%d - %s"),
    SLACK_MESSAGE_FORMAT("*[%s]* %s\n\n*[요청한 멤버 id]* %s\n\n*[ERROR LOG]*\n%s\n\n*[REQUEST_INFORMATION]*\n%s %s\n%s\n\n*[REQUEST_BODY]*%s"),
    EMPTY_BODY_MESSAGE("{BODY IS EMPTY}"),
    SLACK_ALARM_FORMAT("[SlackAlarm] %s");

    private final String message;

}
