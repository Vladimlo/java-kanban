import java.util.List;

public interface TaskManager {

    public List<Task> getTaskList();
    public List<Epic> getEpicList();
    public List<SubTask> getSubTaskList();

    public void taskClear();
    public void epicClear();
    public void subTaskClear();

    public Task getTask(int id);
    public Epic getEpic(int id);
    public SubTask getSubTask(int id);

    public void createTask(Task task);
    public void createEpic(Epic epic);
    public void createSubTask(SubTask subTask);

    public void updateTask(Task task);
    public void updateEpic(Epic epic);
    public void updateSubTask(SubTask subTask);

    public void removeTask(int id);
    public void removeEpic(int id);
    public void removeSubTask(int id);

    public List<SubTask> getEpicSubtasks(int id);

    public List<? extends Task> getHistory();
}
