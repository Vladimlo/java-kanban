package managers.task_managers;

import exceptions.TaskTimeConflictException;
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

    Task getTask(int id, boolean addToHistiry);

    Epic getEpic(int id);

    Epic getEpic(int id, boolean addToHistiry);

    SubTask getSubTask(int id);

    SubTask getSubTask(int id, boolean addToHistiry);

    //Создание задач
    void createTask(Task task) throws TaskTimeConflictException;

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask) throws TaskTimeConflictException;

    //Обновление задач
    void updateTask(Task task) throws TaskTimeConflictException;

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask) throws TaskTimeConflictException;

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
