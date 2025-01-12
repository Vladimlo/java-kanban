import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Integer> subTasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }
}
