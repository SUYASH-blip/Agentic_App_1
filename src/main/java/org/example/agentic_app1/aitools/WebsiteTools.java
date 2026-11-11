package org.example.agentic_app1.aitools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Component
public class WebsiteTools {

    private final Path workspace =
            Path.of("generated-sites").toAbsolutePath().normalize();

    // Name of the website folder that was written to most recently, e.g. "brewlab".
    // Used by WebsiteBuilderService to append the [[SITE:name]] marker.
    private volatile String lastSite;

    public WebsiteTools() {
        try {
            Files.createDirectories(workspace);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create website workspace", e);
        }
    }

    // These two are NOT @Tool methods, so the AI model cannot see or call them.
    public String getLastSite() {
        return lastSite;
    }

    public void clearLastSite() {
        lastSite = null;
    }

    @Tool(description = "Creates a new directory inside the website workspace.")
    public String createDirectory(
            @ToolParam(description = "Required. Relative directory path, for example brewlab")
            String path) {

        try {
            Files.createDirectories(safePath(path));
            return "Directory created successfully: " + path;
        } catch (IOException | IllegalArgumentException e) {
            return "Failed to create directory: " + e.getMessage();
        }
    }

    @Tool(description = """
            Creates or overwrites a text file inside the website workspace.
            Use this to create HTML, CSS and JavaScript files.
            """)
    public String writeFile(
            @ToolParam(description = "Required. Relative file path, for example brewlab/index.html")
            String path,
            @ToolParam(description = "Required. Complete content to write into the file")
            String content) {

        try {
            Path file = safePath(path);
            Files.createDirectories(file.getParent());
            Files.writeString(file, content == null ? "" : content, StandardCharsets.UTF_8);

            // Remember the top-level project folder (brewlab/index.html -> "brewlab")
            Path relative = workspace.relativize(file);
            if (relative.getNameCount() > 1) {
                String top = relative.getName(0).toString();
                if (top.matches("[\\w.\\-]+")) {
                    lastSite = top;
                }
            }

            return "File written successfully: " + path;
        } catch (IOException | IllegalArgumentException e) {
            return "Failed to write file: " + e.getMessage();
        }
    }

    @Tool(description = "Reads the contents of an existing file from the website workspace.")
    public String readFile(
            @ToolParam(description = "Required. Relative file path") String path) {

        try {
            String content = Files.readString(safePath(path), StandardCharsets.UTF_8);
            return content.isEmpty() ? "File is empty." : content;   // never return ""
        } catch (IOException | IllegalArgumentException e) {
            return "Failed to read file: " + e.getMessage();
        }
    }

    @Tool(description = "Lists all files and directories inside a website project.")
    public String listFiles(
            @ToolParam(description = "Required. Relative directory path, for example brewlab. "
                    + "Use '.' for the root folder")
            String path) {

        try {
            Path directory = safePath(path);

            if (!Files.exists(directory)) {
                return "Directory does not exist: " + path;
            }

            try (var files = Files.walk(directory)) {
                String result = files
                        .filter(file -> !file.equals(directory))
                        .map(workspace::relativize)
                        .map(Path::toString)
                        .sorted()
                        .collect(Collectors.joining("\n"));

                return result.isEmpty() ? "Directory is empty." : result;   // never return ""
            }
        } catch (IOException | IllegalArgumentException e) {
            return "Failed to list files: " + e.getMessage();
        }
    }

    private Path safePath(String path) {
        String cleaned = (path == null || path.isBlank()) ? "." : path;
        Path resolved = workspace.resolve(cleaned).normalize();

        if (!resolved.startsWith(workspace)) {
            throw new IllegalArgumentException(
                    "Access outside generated-sites is not allowed");
        }
        return resolved;
    }
}