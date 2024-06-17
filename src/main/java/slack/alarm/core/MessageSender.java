package slack.alarm.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSender {

    @Value("${slack.webhook.uri}")
    private String SLACK_LOGGER_WEBHOOK_URI;

    private final ObjectMapper objectMapper;

    public void send(String message) {
        WebClient.create(SLACK_LOGGER_WEBHOOK_URI)
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(toJson(message))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        res -> log.info("slack response = {}", res),
                        err -> log.error("error sending message to slack", err)
                );
    }

    private String toJson(String message) {
        try {                                                     
            Map<String, String> values = new HashMap<>();
            values.put("text", message);
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException ignored) {}
        return "{\"text\" : \"can not covert to json.\"}";
    }


}
