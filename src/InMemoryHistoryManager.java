import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private List<Task> history= new LinkedList<>();
    private final int MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (history.size() < MAX_SIZE) {
            history.addLast(task);
            return;
        }

        history.removeFirst();
        history.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
