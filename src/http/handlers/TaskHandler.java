package http.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskTimeConflictException;
import managers.task_managers.TaskManager;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager tm) {
        super(tm);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        switch (method) {
            case "GET" -> {
                switch (uriElements.length) {
                    case 2 -> getAllTasks(exchange);
                    case 3 -> getTask(exchange, uriElements[2]);
                    default -> sendResponse(exchange, "Для " + exchange.getRequestURI().getPath() +
                            " Обработчик не задан", 404);
                }
            }
            case "POST" -> {
                switch (uriElements.length) {
                    case 2 -> {
                        if (jo.has("id")) {
                            updateTask(exchange, jo);
                        } else {
                            createTask(exchange, jo);
                        }
                    }
                    default -> sendResponse(exchange, "Для " + exchange.getRequestURI().getPath() +
                            " Обработчик не задан", 404);
                }
            }
            case "DELETE" -> {
                switch (uriElements.length) {
                    case 3 -> deleteTask(exchange, uriElements[2]);
                    default -> sendResponse(exchange, "Для " + exchange.getRequestURI().getPath() +
                            " Обработчик не задан", 404);
                }
            }
            default -> sendResponse(exchange, "Метод " + method + " не реализован", 404);
        }
    }

    private void getAllTasks(HttpExchange exchange) {
        try {
            List<Task> tasks = tm.getTaskList();

            sendResponse(exchange, gson.toJson(tasks), 200);
        } catch (IOException e) {
            System.out.println("Ошибка формирования ответа для " + method + exchange.getRequestURI().getPath()
                    + ": " + e.getMessage());
        }
    }

    private void getTask(HttpExchange exchange, String textTaskId) throws IOException {

        try {
            int taskId = Integer.parseInt(textTaskId);
            Task task = tm.getTask(taskId, true);

            if (task == null) {
                sendResponse(exchange, "Задача с идентификатором " + textTaskId + " не найдена", 404);
                return;
            }

            sendResponse(exchange, gson.toJson(task), 200);

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            sendResponse(exchange, "Идентификатор не является целым числом: " + textTaskId, 400);
        }
    }

    private void createTask(HttpExchange exchange, JsonObject jo) throws IOException {
        Task newTask = gson.fromJson(jo, Task.class);

        try {
            tm.createTask(newTask);
            sendResponse(exchange, 201);
        } catch (TaskTimeConflictException e) {
            sendResponse(exchange, "Задача пересекается с уже имеющейся", 406);
        }
    }

    public void updateTask(HttpExchange exchange, JsonObject jo) throws IOException {
        Integer taskId = null;

        try {
            taskId = jo.get("id").getAsInt();
        } catch (NumberFormatException e) {
            sendResponse(exchange, "Идентификатор не является целым числом: " + jo.get("id").toString(), 400);
        }

        Task newTask = gson.fromJson(jo, Task.class);
        newTask.setStatus(TaskStatus.NEW);

        if (tm.getTask(taskId) == null) {
            sendResponse(exchange, "Задача с идентификатором " + taskId + " не найдена", 404);
            return;
        }

        try {
            tm.updateTask(newTask);
            sendResponse(exchange, 201);
        } catch (TaskTimeConflictException e) {
            sendResponse(exchange, "Задача пересекается с уже имеющейся", 406);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteTask(HttpExchange exchange, String textTaskId) throws IOException {
        try {
            int taskId = Integer.parseInt(textTaskId);
            Task task = tm.getTask(taskId);

            if (task == null) {
                sendResponse(exchange, "Задача с идентификатором " + textTaskId + " не найдена", 404);
                return;
            }

            tm.removeTask(taskId);
            sendResponse(exchange, 201);

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            sendResponse(exchange, "Идентификатор не является целым числом: " + textTaskId, 400);
        }
    }
}
