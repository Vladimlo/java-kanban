package managers.task_managers.history_managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    //Добавление задачи в историю
    public void add(Task task);

    //Получение истории
    public List<Task> getHistory();

    //Удаление задачи из истории
    public void remove(int id);
}
