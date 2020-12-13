package com.k15t.cloud.upm_client.testapp;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;


@RestController
public class DescriptorController {

    private static final String DESCRIPTOR_JSON =
            Optional.of(new ClassPathResource("atlassian-connect.json"))
                    .map(cpr -> {
                        try {
                            return cpr.getInputStream();
                        } catch (IOException ioe) {
                            throw new RuntimeException(ioe);
                        }
                    })
                    .map(is -> {
                        try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
                            return scanner.useDelimiter("\\A").next();
                        }
                    }).orElse("{}");


    @GetMapping(value = "atlassian-connect.json", produces = "application/json")
    public String descriptor() throws IOException {
        String ngrokUrl = System.getProperty("ngrok.public_url");
        return DESCRIPTOR_JSON.replace("${{appBaseUrl}}", ngrokUrl);
    }

}
