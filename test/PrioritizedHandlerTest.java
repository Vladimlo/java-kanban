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

public class PrioritizedHandlerTest {
    @BeforeEach
    void setUp() {
        HttpTaskServer.start(Managers.getDefault());
    }

    @AfterEach
    void cleanUp() {
        HttpTaskServer.stop();
    }

    @Test
    void getPrioritized200StatusCodeTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/prioritized");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ошибка получения приоритезированных задач");
    }

}
