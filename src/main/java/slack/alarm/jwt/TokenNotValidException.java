package slack.alarm.jwt;


public class TokenNotValidException extends RuntimeException {
    public TokenNotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
