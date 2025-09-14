package com.nhnacademy.handler;

import com.nhnacademy.session.SessionManager;
import com.nhnacademy.util.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler implements HttpHandler {
    private final String filePath;
    private final SessionManager sessionManager;

    public FileHandler(String filePath, SessionManager sessionManager) {
        this.filePath = filePath;
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/todos".equals(path) && !sessionManager.isLoggedIn(exchange)) {
            HttpUtils.sendRedirect(exchange, "/login");
            return;
        }
        if ("/login".equals(path) && sessionManager.isLoggedIn(exchange)) {
            HttpUtils.sendRedirect(exchange, "/todos");
            return;
        }
        try {
            String content = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
            HttpUtils.sendResponse(exchange, 200, content, "text/html");
        } catch (IOException e) {
            HttpUtils.sendResponse(exchange, 404, "File not found", "text/plain");
        }
    }
}