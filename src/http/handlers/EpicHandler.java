package http.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import managers.task_managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager tm) {
        super(tm);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        switch (method) {
            case "GET" -> {
                switch (uriElements.length) {
                    case 2 -> getAllEpics(exchange);
                    case 3 -> getEpic(exchange, uriElements[2]);
                    case 4 -> {
                        if (!uriElements[3].equals("subtasks")) sendResponse(exchange,
                                "Метод " + method + exchange.getRequestURI().getPath() + " не реализован", 404);
                        getSubtasks(exchange, uriElements[2]);
                    }
                    default -> sendResponse(exchange, "Для " + exchange.getRequestURI().getPath() +
                            " Обработчик не задан", 404);
                }
            }
            case "POST" -> {
                switch (uriElements.length) {
                    case 2 -> createEpic(exchange, jo);
                    default -> sendResponse(exchange, "Для " + exchange.getRequestURI().getPath() +
                            " Обработчик не задан", 404);
                }
            }
            case "DELETE" -> {
                switch (uriElements.length) {
                    case 3 -> deleteEpic(exchange, uriElements[2]);
                    default -> sendResponse(exchange, "Для " + exchange.getRequestURI().getPath() +
                            " Обработчик не задан", 404);
                }
            }
            default -> sendResponse(exchange, "Метод " + method + " не реализован", 404);
        }
    }

    private void getAllEpics(HttpExchange exchange) {
        try {
            List<Epic> tasks = tm.getEpicList();
            sendResponse(exchange, gson.toJson(tasks), 200);

        } catch (IOException e) {
            System.out.println("Ошибка формирования ответа для " + method + exchange.getRequestURI().getPath()
                    + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getEpic(HttpExchange exchange, String textEpicId) throws IOException {
        try {
            int epicId = Integer.parseInt(textEpicId);

            Epic epic = tm.getEpic(epicId, true);

            if (epic == null) {
                sendResponse(exchange, "Задача с идентификатором " + textEpicId + " не найдена", 404);
                return;
            }

            sendResponse(exchange, gson.toJson(epic), 200);

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            sendResponse(exchange, "Идентификатор не является целым числом: " + textEpicId, 400);
        }
    }

    private void createEpic(HttpExchange exchange, JsonObject jo) throws IOException {
        Epic newEpic = gson.fromJson(jo, Epic.class);

        tm.createEpic(newEpic);
        sendResponse(exchange, 201);
    }

    private void deleteEpic(HttpExchange exchange, String textEpicId) throws IOException {
        try {
            int epicId = Integer.parseInt(textEpicId);
            Epic epic = tm.getEpic(epicId);

            if (epic == null) {
                sendResponse(exchange, "Задача с идентификатором " + textEpicId + " не найдена", 404);
                return;
            }

            tm.removeEpic(epicId);
            sendResponse(exchange, 201);

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            sendResponse(exchange, "Идентификатор не является целым числом: " + textEpicId, 400);
        }
    }

    private void getSubtasks(HttpExchange exchange, String textEpicId) throws IOException {
        try {
            int epicId = Integer.parseInt(textEpicId);
            Epic epic = tm.getEpic(epicId);

            if (epic == null) {
                sendResponse(exchange, "Задача с идентификатором " + textEpicId + " не найдена", 404);
                return;
            }

            sendResponse(exchange, gson.toJson(epic.getSubTasksId()), 200);

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            sendResponse(exchange, "Идентификатор не является целым числом: " + textEpicId, 400);
        }
    }
}
