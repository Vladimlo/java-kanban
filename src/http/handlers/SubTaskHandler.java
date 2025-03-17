package http.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskTimeConflictException;
import managers.task_managers.TaskManager;
import tasks.SubTask;
import tasks.TaskStatus;

import java.io.IOException;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler {

    public SubTaskHandler(TaskManager tm) {
        super(tm);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        switch (method) {
            case "GET" -> {
                switch (uriElements.length) {
                    case 2 -> getAllSubTasks(exchange);
                    case 3 -> getSubTask(exchange, uriElements[2]);
                    default -> sendResponse(exchange, "Для " + exchange.getRequestURI().getPath() +
                            " Обработчик не задан", 404);
                }
            }
            case "POST" -> {
                switch (uriElements.length) {
                    case 2 -> {
                        if (jo.has("id")) {
                            updateSubTask(exchange, jo);
                        } else {
                            createSubTask(exchange, jo);
                        }
                    }
                    default -> sendResponse(exchange, "Для " + exchange.getRequestURI().getPath() +
                            " Обработчик не задан", 404);
                }
            }
            case "DELETE" -> {
                switch (uriElements.length) {
                    case 3 -> deleteSubTask(exchange, uriElements[2]);
                    default -> sendResponse(exchange, "Для " + exchange.getRequestURI().getPath() +
                            " Обработчик не задан", 404);
                }
            }
            default -> sendResponse(exchange, "Метод " + method + " не реализован", 404);
        }
    }

    private void getAllSubTasks(HttpExchange exchange) {
        try {
            List<SubTask> tasks = tm.getSubTaskList();

            sendResponse(exchange, gson.toJson(tasks), 200);
        } catch (IOException e) {
            System.out.println("Ошибка формирования ответа для " + method + exchange.getRequestURI().getPath()
                    + ": " + e.getMessage());
        }
    }

    private void getSubTask(HttpExchange exchange, String textTaskId) throws IOException {
        int taskId;

        try {
            taskId = Integer.parseInt(textTaskId);

            SubTask task = tm.getSubTask(taskId, true);

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

    private void createSubTask(HttpExchange exchange, JsonObject jo) throws IOException {
        SubTask newTask = gson.fromJson(jo, SubTask.class);

        if (!jo.has("epicId")) {
            sendResponse(exchange, "Подзадача должна иметь родительскую задачу. Поле epicId является обязательным",
                    400);
            return;
        }

        if (tm.getEpic(jo.get("epicId").getAsInt()) == null) {
            sendResponse(exchange, "Родительская задача не найдена", 404);
            return;
        }

        try {
            tm.createSubTask(newTask);
            sendResponse(exchange, 201);
        } catch (TaskTimeConflictException e) {
            sendResponse(exchange, "Задача пересекается с уже имеющейся", 406);
        }
    }

    public void updateSubTask(HttpExchange exchange, JsonObject jo) throws IOException {
        Integer taskId = null;

        try {
            taskId = jo.get("id").getAsInt();
        } catch (NumberFormatException e) {
            sendResponse(exchange, "Идентификатор не является целым числом: " + jo.get("id").toString(), 400);
        }

        if (!jo.has("epicId")) {
            sendResponse(exchange, "Подзадача должна иметь родительскую задачу. Поле epicId является обязательным",
                    400);
            return;
        }

        if (tm.getEpic(jo.get("epicId").getAsInt()) == null) {
            sendResponse(exchange, "Родительская задача не найдена", 404);
            return;
        }

        SubTask newTask = gson.fromJson(jo, SubTask.class);
        newTask.setStatus(TaskStatus.NEW);

        if (tm.getSubTask(taskId) == null) {
            sendResponse(exchange, "Задача с идентификатором " + taskId + " не найдена", 404);
            return;
        }

        try {
            tm.updateSubTask(newTask);
            sendResponse(exchange, 201);
        } catch (TaskTimeConflictException e) {
            sendResponse(exchange, "Задача пересекается с уже имеющейся", 406);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteSubTask(HttpExchange exchange, String textTaskId) throws IOException {
        int taskId;

        try {
            taskId = Integer.parseInt(textTaskId);
            SubTask task = tm.getSubTask(taskId);

            if (task == null) {
                sendResponse(exchange, "Задача с идентификатором " + textTaskId + " не найдена", 404);
                return;
            }

            tm.removeSubTask(task.getId());
            sendResponse(exchange, 201);

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            sendResponse(exchange, "Идентификатор не является целым числом: " + textTaskId, 400);
        }
    }
}