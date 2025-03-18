package http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import managers.task_managers.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler implements HttpHandler {

    protected TaskManager tm;
    protected String method;
    protected String[] uriElements;
    protected Gson gson;
    protected String requestBody;
    protected JsonElement je;
    protected JsonObject jo;

    public BaseHttpHandler(TaskManager tm) {
        this.tm = tm;

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void handle(HttpExchange exchange) throws IOException {
        method = exchange.getRequestMethod();
        uriElements = exchange.getRequestURI().getPath().split("/");
        requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        je = JsonParser.parseString(requestBody);

        if (je.isJsonObject()) {
            jo = JsonParser.parseString(requestBody).getAsJsonObject();
        }
    }

    protected void sendResponse(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendResponse(HttpExchange h, int code) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, 0);
        h.close();
    }
}