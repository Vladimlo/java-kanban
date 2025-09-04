import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SubTaskHandlerTest extends BaseHandlerTest {

    @Override
    HttpResponse<String> createTask() throws IOException, InterruptedException {
        createEpic();

        URI uri = URI.create(getUri());

        String requestBody = """
                {
                	"name":"name",
                	"description":"description",
                	"startTime":"12.03.2025 14:00",
                	"duration":"1:45",
                    "epicId":"1"
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

    @Override
    String getUri() {
        return "http://localhost:8080/subtasks";
    }

    @Override
    String getUriWithId() {
        return "/2";
    }
}
