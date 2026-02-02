package com.shashank.mcpServer.controller.mcp;

import com.shashank.mcpServer.handler.LlamaChatHandler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

@Component
public class ChatMcpServer {

    private DisposableServer server;

    @PostConstruct
    public void start() {
        server = TcpServer.create()
                .host("0.0.0.0")
                .port(9001)
                .handle((inbound, outbound) -> {
                    return outbound.sendString(
                            inbound.receive()
                                    .asString()
                                    .flatMap(msg -> {
                                        if (!msg.trim().isEmpty()) {
                                            System.out.println("Client says: " + msg);
                                        }

                                        // Call your local LLaMA model
                                        String response = LlamaChatHandler.generateResponse(msg);

                                        return Mono.just(response + "\n");
                                    }));
                })
                .bindNow();

        System.out.println("Chat MCP Server started on port 9001");
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.disposeNow();
            System.out.println("Chat MCP Server stopped");
        }
    }
}
