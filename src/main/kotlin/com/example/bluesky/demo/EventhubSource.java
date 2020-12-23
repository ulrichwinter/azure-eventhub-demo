package com.example.bluesky.demo;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

@EnableBinding(Source.class)
@RestController
public class EventhubSource {
    private static final Logger LOGGER = getLogger(EventhubSource.class);

    private AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private Source source;

    @PostMapping(path = "messages")
    public String postMessage(@RequestBody String message) {
        message = message + " " + counter.incrementAndGet();
        LOGGER.info("sending message: {}", message);
        this.source.output().send(new GenericMessage<>(message));
        return message;
    }
}