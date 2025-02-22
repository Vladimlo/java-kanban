package task_managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    //Получение списков задач
    public List<Task> getTaskList();

    public List<Epic> getEpicList();

    public List<SubTask> getSubTaskList();

    //Чистка списков задач
    public void taskClear();

    public void epicClear();

    public void subTaskClear();

    //Получение задач по id
    public Task getTask(int id);

    public Epic getEpic(int id);

    public SubTask getSubTask(int id);

    //Создание задач
    public void createTask(Task task);

    public void createEpic(Epic epic);

    public void createSubTask(SubTask subTask);

    //Обновление задач
    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubTask(SubTask subTask);

    //Удаление задач
    public void removeTask(int id);

    public void removeEpic(int id);

    public void removeSubTask(int id);

    //Получение всех подзадач эпика
    public List<SubTask> getEpicSubtasks(int id);

    //Получение истории просмотренных задач
    public List<Task> getHistory();
}
