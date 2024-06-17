package slack.alarm.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import slack.alarm.enums.MessageFormat;
import slack.alarm.enums.SlackNotificationLevel;
import slack.alarm.jwt.JwtTokenProvider;
import slack.alarm.jwt.TokenNotValidException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageGenerator {

    private final Environment environment;
    private final JwtTokenProvider jwtTokenProvider;

    public String generate(ContentCachingRequestWrapper request,
                           Exception exception,
                           SlackNotificationLevel level) {
        try {
            String token = jwtTokenProvider.getToken(request);
            String profile = getProfile();
            String currentTime = getCurrentTime();
            String method = request.getMethod();
            String userId = getUserId(token);
            String requestURI = request.getRequestURI();
            String headers = extractHeaders(request);
            String body = getBody(request);
            String exceptionMessage = extractExceptionMessage(exception, level);

            return toMessage(profile, currentTime, userId,
                    exceptionMessage, method, requestURI, headers, body);
        } catch (Exception e) {
            return String.format(MessageFormat.EXTRACTION_ERROR_MESSAGE.getMessage(), e.getMessage());
        }
    }


    private String getProfile() {
        return String.join(",", environment.getActiveProfiles()).toUpperCase();
    }

    private String getCurrentTime() {
        return LocalDateTime.now().toString();
    }

    private String getUserId(String token) {
        try {
            return jwtTokenProvider.extractSubject(token);
        } catch (TokenNotValidException e) {
            return "Guest";
        }
    }

    private String extractHeaders(ContentCachingRequestWrapper request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> values = new HashMap<>();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            values.put(headerName, request.getHeader(headerName));
        }

        return values.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(joining("\n"));
    }

    private String getBody(ContentCachingRequestWrapper request) {
        String body = new String(request.getContentAsByteArray());
        if (body.isEmpty()) {
            body = MessageFormat.EMPTY_BODY_MESSAGE.getMessage();
        }
        return body;
    }

    private String extractExceptionMessage(Exception e, SlackNotificationLevel level) {
        StackTraceElement stackTrace = e.getStackTrace()[0];
        String className = stackTrace.getClassName();
        int lineNumber = stackTrace.getLineNumber();
        String methodName = stackTrace.getMethodName();
        String message = e.getMessage();

        if (message == null) {
            return Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(joining("\n"));
        }

        return String
            .format(MessageFormat.EXCEPTION_MESSAGE_FORMAT.getMessage(), level.name(),
                    className, methodName, lineNumber, message);
    }


    private String toMessage(String profile, String currentTime, String userId, String errorMessage,
                             String method, String requestURI, String headers, String body) {
        return String.format(
            MessageFormat.SLACK_MESSAGE_FORMAT.getMessage(), profile, currentTime, userId,
            errorMessage, method, requestURI, headers, body
        );
    }
}
