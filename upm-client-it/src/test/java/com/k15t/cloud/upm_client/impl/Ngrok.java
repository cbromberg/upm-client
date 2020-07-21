package com.k15t.cloud.upm_client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import java.io.IOException;
import java.nio.file.FileSystems;


public class Ngrok {

    private static final Logger logger = LoggerFactory.getLogger(Ngrok.class);


    private static final String executablePath = "./node_modules/.bin/ngrok";

    private static final Ngrok instance = new Ngrok(8080);


    public static Ngrok getInstance() {
        return instance;
    }


    private final int portNumber;
    private Process process;


    public Ngrok(int portNumber) {
        this.portNumber = portNumber;
    }


    public synchronized void start() {
        if (process == null) {
            String cwd = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
            String cmd = String.format("%s http --bind-tls=true %s", executablePath, portNumber);
            try {
                process = Runtime.getRuntime().exec(cmd);
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if (process != null) {
                            process.destroyForcibly();
                        }
                    }
                });
            } catch (IOException ioException) {
                logger.error("Starting ngrok via '{}' from {} failed: ", cmd, cwd, ioException);
                throw new RuntimeException(ioException);
            }
        }
    }


    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }


    public String getPublicUrl() {
        WebTarget client = ClientBuilder.newClient().target("http://localhost:4040/api/tunnels/command_line");
        JsonNode response = client.request("application/json").get(JsonNode.class);
        return response.get("public_url").asText();
    }
}
