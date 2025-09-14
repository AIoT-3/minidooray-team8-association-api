package com.nhnacademy;

import com.nhnacademy.handler.FileHandler;
import com.nhnacademy.handler.LoginApiHandler;
import com.nhnacademy.handler.LogoutApiHandler;
import com.nhnacademy.handler.TodosApiHandler;
import com.nhnacademy.repository.TodoRepository;
import com.nhnacademy.session.SessionManager;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class TodoServer {
    public static void main(String[] args) throws IOException {
        // 1. 핵심 의존 객체들 생성
        TodoRepository todoRepository = new TodoRepository();
        SessionManager sessionManager = new SessionManager();

        // 2. 서버 생성
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 3. 각 핸들러에 필요한 의존성 주입 및 컨텍스트 설정
        server.createContext("/login", new FileHandler("src/main/html/login.html", sessionManager));
        server.createContext("/todos", new FileHandler("src/main/html/todo_list.html", sessionManager));
        server.createContext("/api/login", new LoginApiHandler(sessionManager));
        server.createContext("/api/logout", new LogoutApiHandler(sessionManager));
        server.createContext("/api/todos", new TodosApiHandler(sessionManager, todoRepository));

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server started on port 8080. Access at http://localhost:8080/login");
    }
}