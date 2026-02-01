package com.shashank.mcpServer.controller.mcp;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

/**
 * Simple Model Context Protocol (MCP) server.
 * Can later be extended to connect to Kafka / other services.
 */
@Component
public class SimpleMcpServer {

    private DisposableServer server;

    @PostConstruct
    public void start() {
        // Start TCP server on port 9000
        server = TcpServer.create()
                .host("0.0.0.0")
                .port(9000)
                .handle((inbound, outbound) -> {
                    // For now: just echo messages back
                    return outbound.sendString(
                            inbound.receive()
                                   .asString()
                                   .flatMap(msg -> {
                                       System.out.println("MCP Received: " + msg);
                                       return Mono.just("ACK: " + msg + "\n");
                                   })
                    );
                })
                .bindNow();

        System.out.println("MCP Server started on port 9000");
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.disposeNow();
            System.out.println("MCP Server stopped");
        }
    }
}
