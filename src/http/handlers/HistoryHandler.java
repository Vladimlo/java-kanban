package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.task_managers.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager tm) {
        super(tm);
    }

    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        if (!method.equals("GET") || uriElements.length != 2 || !uriElements[1].equals("history")) {
            sendResponse(exchange, "Метод " + method + " " + exchange.getRequestURI().getPath() + " не реализован",
                    404);
            return;
        }

        sendResponse(exchange, gson.toJson(tm.getHistory()), 200);
    }
}
