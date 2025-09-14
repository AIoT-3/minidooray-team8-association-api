package com.nhnacademy.handler;

import com.nhnacademy.session.SessionManager;
import com.nhnacademy.util.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;

public class LoginApiHandler implements HttpHandler {
    private final SessionManager sessionManager;
    private static final String FIXED_ID = "admin";
    private static final String FIXED_PW = "1234";

    public LoginApiHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> params = HttpUtils.parseFormData(exchange.getRequestBody());
            String id = params.getOrDefault("id", "");
            String password = params.getOrDefault("password", "");

            if (FIXED_ID.equals(id) && FIXED_PW.equals(password)) {
                String sessionId = sessionManager.createSession(id);
                String cookie = String.format("session=%s; Path=/; HttpOnly; SameSite=Lax", sessionId);
                exchange.getResponseHeaders().set("Set-Cookie", cookie);
                HttpUtils.sendResponse(exchange, 200, "{\"status\":\"success\"}", "application/json");
            } else {
                HttpUtils.sendResponse(exchange, 401, "{\"status\":\"failed\"}", "application/json");
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}