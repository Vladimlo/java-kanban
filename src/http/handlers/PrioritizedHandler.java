package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.task_managers.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager tm) {
        super(tm);
    }

    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        if (!method.equals("GET") || uriElements.length != 2 || !uriElements[1].equals("prioritized")) {
            sendResponse(exchange, "Метод " + method + " " + exchange.getRequestURI().getPath() + " не реализован",
                    404);
            return;
        }

        sendResponse(exchange, gson.toJson(tm.getPrioritizedTasks()), 200);
    }
}
