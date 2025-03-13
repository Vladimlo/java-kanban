package managers.task_managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    //Получение списков задач
    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<SubTask> getSubTaskList();

    //Чистка списков задач
    void taskClear();

    void epicClear();

    void subTaskClear();

    //Получение задач по id
    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    //Создание задач
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask);

    //Обновление задач
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    //Удаление задач
    void removeTask(int id);

    void removeEpic(int id);

    void removeSubTask(int id);

    //Получение всех подзадач эпика
    List<SubTask> getEpicSubtasks(int id);

    //Получение истории просмотренных задач
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
