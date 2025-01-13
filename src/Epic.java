import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void setSubTasksId(ArrayList<Integer> subTasksId) {
        this.subTasksId = subTasksId;
    }
}
