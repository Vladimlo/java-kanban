import http.HttpTaskServer;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskHandlerTest {

    @BeforeEach
    void setUp() {
        HttpTaskServer.start(Managers.getDefault());
    }

    @AfterEach
    void cleanUp() {
        HttpTaskServer.stop();
    }

    @Test
    void createTask201StatusCodeTest() throws IOException, InterruptedException {
        HttpResponse<String> response = createTask();

        assertEquals(201, response.statusCode(), "Ошибка при создании задачи");
    }

    @Test
    void getTasks200StatusCode() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ошибка получения задач");
    }

    @Test
    void getTask200StatusCode() throws IOException, InterruptedException {
        createTask();
        URI uri = URI.create("http://localhost:8080/tasks/1");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ошибка получения конкретной задачи");
    }

    @Test
    void deleteTask201StatusCodeTest() throws IOException, InterruptedException {
        createTask();
        URI uri = URI.create("http://localhost:8080/tasks/1");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(201, response.statusCode(), "Ошибка при удалении задачи");
    }

    private HttpResponse<String> createTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");

        String requestBody = """
                {
                	"name":"name",
                	"description":"description",
                	"startTime":"19.03.2025 14:00",
                	"duration":"1:45"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        return client.send(request, handler);
    }
}
