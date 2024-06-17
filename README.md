# Slack-Error-Sender

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/github/actions/workflow/status/dlehddn/slack-error-sender/main.yml?branch=main)](https://github.com/dlehddn/slack-error-sender/actions)
[![Version](https://img.shields.io/github/v/release/dlehddn/slack-error-sender)](https://github.com/dlehddn/slack-error-sender/releases)

Instantly receive error notifications on Slack!

## Getting Started
This library utilizes Java 17 and Spring 3.X.X to provide annotation-based Spring AOP for error handling and Slack notifications. It includes functionality to capture request details in error messages.

### Installing
To integrate Slack error notifications into your project, follow these steps:

#### Step 1: Add Dependency
Add the following dependency to your build.gradle file:

```groovy
repositories {
	mavenCentral()
	maven { url 'https://jitpack.io' }
}
```
```groovy
dependencies {
    implementation 'com.yourpackage:slack-error-sender:v0.0.2'
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    implementation 'io.jsonwebtoken:jjwt-impl:0.11.5'
    implementation 'io.jsonwebtoken:jjwt-jackson:0.11.5'
    implementation 'org.springframework.boot:spring-boot-starter-aop'
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
}
```

#### Step 2: Configure Application Properties
Create or update your application.properties file with the following properties:
```
slack.webhook.uri=https://example.com/slack/webhook
jwt.public-key=-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2GzN/BFFyC7tA+6ZgWdQ
0RfLAVfbJ2OgtWgWv4IYUVoPp4sAGS+I7Zx8uhJGpNpSlVgIm5Nfi9Qizj25Be0O
yVjZdW3YDw8hdx+jovJll9KbD0ay5B4LTYhJfdKsj3vW4zGL3aJ3M+5vo2rcdFmV
uykS7e9AelGdK+Gzy1K2u4Tn21DZfVQZKmZEsHlZV3fOv6w+byklPViRm5U9DQON
Xib6RmeDde3Q9CHbT9IhkMk1FFDLb5ps12WvRc7RQZDso2cO0HDvTbH0jH5tRQhQ
1P0eZeeSA0B6S7qO2dy5tSMnLLt1LC9/Xn4kVxX1BfiO8Bk5Hxzbm1d4zQV2f2Ad
0QIDAQAB
-----END PUBLIC KEY-----
```
Replace https://example.com/slack/webhook with your actual Slack webhook URI and update the jwt.public-key with your RSA public key.

### Usage
To send error notifications to Slack, annotate your methods with @SlackNotification(). Here's an example:
```java
@SlackNotification(level = SlackNotificationLevel.WARN)
@ExceptionHandler(BusinessException.class)
public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
    log.warn(e.getErrorCode().getMessage());
    return makeResponseEntity(e.getErrorCode());
}

@SlackNotification(level = SlackNotificationLevel.ERROR)
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleCommonException (Exception e) {
    if (e.getMessage() != null) {
        log.error(e.getMessage());
    } else {
        log.error(e.getClass().toString());
        log.error(Arrays.stream(e.getStackTrace()) 
                .map(st -> st.toString()) 
                .collect(Collectors.joining("\n")));
    }
    return makeResponseEntity(CommonErrorCode.Common_ERROR);
}
```


Now you're ready to receive error information on Slack!
<img width="1122" alt="image" src="https://github.com/dlehddn/slack-error-sender/assets/127951186/b6d98e7e-fd74-425f-bc15-4a25622dddad">

