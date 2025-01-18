import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int MAX_SIZE = 10;

    private List<Task> history= new LinkedList<>();

    @Override
    public void add(Task task) {
        if (history.size() == MAX_SIZE) {
            history.removeFirst();
        }

        history.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
