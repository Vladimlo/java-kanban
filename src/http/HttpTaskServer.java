package http;

import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import managers.Managers;
import managers.task_managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    public static HttpServer server;

    public static void main(String[] args) {
        start(Managers.getFileBackedManager());
    }

    public static void start(TaskManager manager) {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TaskHandler(manager));
            server.createContext("/subtasks", new SubTaskHandler(manager));
            server.createContext("/epics", new EpicHandler(manager));
            server.createContext("/prioritized", new PrioritizedHandler(manager));
            server.createContext("/history", new HistoryHandler(manager));

        } catch (IOException e) {
            System.out.println("Ошибка создания сервера:" + e.getMessage());
            System.exit(1);
        }
        server.start();
    }

    public static void stop() {
        server.stop(0);
    }
}
