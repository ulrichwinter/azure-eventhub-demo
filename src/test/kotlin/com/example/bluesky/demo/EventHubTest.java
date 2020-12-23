package com.example.bluesky.demo;

import io.github.netmikey.logunit.api.LogCapturer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
public class EventHubTest {

    private String startupTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @Autowired
    private EventhubSource source;

    @Autowired
    private EventhubSink sink;

    @RegisterExtension
    LogCapturer logs = LogCapturer.create().captureForType(EventhubSink.class);

    @Test
    void testEventSent() {
        String messageSent = "Can you copy? " + startupTime;
        // sometimes messages will be delayed for several seconds, so this test may fail sporadically.
        source.postMessage(messageSent);
        source.postMessage(messageSent);
        source.postMessage(messageSent);
        source.postMessage(messageSent);
        source.postMessage(messageSent);

        await().atMost(10, SECONDS).untilAsserted(() ->
                logs.assertContains("Can you copy?")
        );
    }
}
