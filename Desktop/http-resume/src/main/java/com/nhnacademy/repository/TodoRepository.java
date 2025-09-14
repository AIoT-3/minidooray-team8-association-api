package com.nhnacademy.repository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TodoRepository {
    private final List<String> todoList = Collections.synchronizedList(new LinkedList<>());

    public List<String> getTodos() {
        return todoList;
    }

    public void addTodo(String todo) {
        if (todo != null && !todo.trim().isEmpty()) {
            todoList.add(todo.trim());
        }
    }

    public void removeTodo(int index) {
        synchronized (todoList) {
            if (index >= 0 && index < todoList.size()) {
                todoList.remove(index);
            }
        }
    }

    public void clearTodos() {
        todoList.clear();
    }
}