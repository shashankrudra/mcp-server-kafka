package com.shashank.mcpServer.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Handles communication with a local LLaMA Python process.
 */
public class LlamaChatHandler {

    private static Process llamaProcess;
    private static OutputStreamWriter writer;
    private static BufferedReader reader;

    static {
        try {

            String scriptPath = new File(
                    Thread.currentThread().getContextClassLoader()
                            .getResource("python/run_llama.py").toURI())
                    .getAbsolutePath();

            // Start your Python script that runs LLaMA
            llamaProcess = new ProcessBuilder("python3", scriptPath)
                    .redirectErrorStream(true)
                    .start();

            writer = new OutputStreamWriter(llamaProcess.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(llamaProcess.getInputStream()));

            System.out.println("Python LLaMA process started successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to start LLaMA Python process");
        }
    }

    /**
     * Sends a prompt to Python LLaMA and returns the generated response.
     */
    public static synchronized String generateResponse(String prompt) {
        try {
            // Send prompt to Python
            writer.write(prompt + "\n");
            writer.flush();

            // Read single-line response
            String response;
            do {
                response = reader.readLine();
            } while (response != null && response.trim().isEmpty());

            if (response == null) {
                return "LLM process terminated unexpectedly";
            }

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating response: " + e.getMessage();
        }
    }
}
