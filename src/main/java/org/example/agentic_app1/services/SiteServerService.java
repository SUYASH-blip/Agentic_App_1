package org.example.agentic_app1.services;




import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import com.sun.net.httpserver.SimpleFileServer.OutputLevel;
import jakarta.annotation.PreDestroy; // Spring Boot 2.x: javax.annotation.PreDestroy
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serves a generated website folder (index.html + css + js) on a free local port.
 * Needs Java 18+ (uses the built-in SimpleFileServer, no extra dependency).
 */
@Service
public class SiteServerService {

    // The folder where your website-generation tool writes projects. Change this.
    private final Path baseDir = Paths.get("generated-sites").toAbsolutePath().normalize();

    private final Map<String, HttpServer> running = new ConcurrentHashMap<>();

    public synchronized String start(String name) throws IOException {
        HttpServer existing = running.get(name);
        if (existing != null) return urlOf(existing);

        Path dir = resolve(name);
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Site folder not found: " + name);
        }

        // Port 0 = pick any free port. Bound to loopback so only this machine can reach it.
        HttpServer server = SimpleFileServer.createFileServer(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), dir, OutputLevel.NONE);
        server.start();
        running.put(name, server);
        return urlOf(server);
    }

    public synchronized void stop(String name) {
        HttpServer server = running.remove(name);
        if (server != null) server.stop(0);
    }

    private Path resolve(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Invalid site name");
        Path p = baseDir.resolve(name).normalize();
        if (!p.startsWith(baseDir)) throw new IllegalArgumentException("Invalid site name"); // blocks ../ tricks
        return p;
    }

    private String urlOf(HttpServer server) {
        return "http://localhost:" + server.getAddress().getPort();
    }

    @PreDestroy
    void stopAll() {
        running.values().forEach(s -> s.stop(0));
        running.clear();
    }
}