import java.io.IOException;
import java.net.http.HttpResponse;

public class EpicHandlerTest extends BaseHandlerTest{
    @Override
    HttpResponse<String> createTask() throws IOException, InterruptedException {
        return createEpic();
    }

    @Override
    String getUri() {
        return "http://localhost:8080/epics";
    }

    @Override
    String getUriWithId() {
        return "/1";
    }
}
