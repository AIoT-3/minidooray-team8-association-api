package com.nhnacademy.handler;

import com.nhnacademy.repository.TodoRepository;
import com.nhnacademy.session.SessionManager;
import com.nhnacademy.util.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;

public class TodosApiHandler implements HttpHandler {
    private final SessionManager sessionManager;
    private final TodoRepository todoRepository;

    public TodosApiHandler(SessionManager sessionManager, TodoRepository todoRepository) {
        this.sessionManager = sessionManager;
        this.todoRepository = todoRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!sessionManager.isLoggedIn(exchange)) {
            HttpUtils.sendResponse(exchange, 401, "{\"error\":\"Unauthorized\"}", "application/json");
            return;
        }
        switch (exchange.getRequestMethod()) {
            case "GET":
                String jsonResponse = HttpUtils.manualListToJson(todoRepository.getTodos());
                HttpUtils.sendResponse(exchange, 200, jsonResponse, "application/json");
                break;
            case "POST":
                Map<String, String> params = HttpUtils.parseFormData(exchange.getRequestBody());
                todoRepository.addTodo(params.get("todo"));
                HttpUtils.sendResponse(exchange, 200, "{\"status\":\"success\"}", "application/json");
                break;
            case "DELETE":
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("index=")) {
                    try {
                        int index = Integer.parseInt(query.substring("index=".length()));
                        todoRepository.removeTodo(index);
                    } catch (NumberFormatException ignored) {}
                } else {
                    todoRepository.clearTodos();
                }
                HttpUtils.sendResponse(exchange, 200, "{\"status\":\"success\"}", "application/json");
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
                break;
        }
    }
}