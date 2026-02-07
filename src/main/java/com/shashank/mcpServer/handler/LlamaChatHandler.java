package com.shashank.mcpServer.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Handles communication with a local LLaMA Python process.
 */ 
public class LlamaChatHandler {

    private static final Object LOCK = new Object();

    private static Process process;
    private static BufferedWriter writer;
    private static BufferedReader reader;

    static {
        startPython();
    }

    private static void startPython() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "/Users/shashank/Documents/ideaSpace/mcp-server-kafka/.venv/bin/python",
                "src/python/run_llama.py"
            );

            pb.redirectErrorStream(true);
            process = pb.start();

            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            System.out.println("Python LLaMA process started successfully.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to start Python LLaMA process", e);
        }
    }

    public static String generateResponse(String prompt) {
        synchronized (LOCK) {
            try {
                if (!process.isAlive()) {
                    return "LLM process is not running";
                }

                writer.write(prompt);
                writer.newLine();
                writer.flush();

                String response = reader.readLine();
                if (response == null) {
                    return "LLM returned no output";
                }

                return response;

            } catch (IOException e) {
                return "LLM communication error: " + e.getMessage();
            }
        }
    }
}
