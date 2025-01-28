import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager tm;

    @BeforeEach
    void preSet() {
        tm = Managers.getDefault();
    }

    @Test
    void removedTasksRemoveFromHistoy() {
        Epic epic = new Epic("Обычный эпик", "Его не будем удалять");
        Epic epic2 = new Epic("Обычный эпик", "Его будем удалять");
        tm.createEpic(epic);
        tm.createEpic(epic2);

        SubTask subTask = new SubTask("Обычная сабтаска", "Не будем ее удалять", epic.getId());
        SubTask subTask2 = new SubTask("Обычная сабтаска", "Будем ее удалять", epic.getId());
        tm.createSubTask(subTask);
        tm.createSubTask(subTask2);

        Task task = new Task("Обычная таска", "Не будем ее удалять");
        Task task2 = new Task("Обычная таска", "Не будем ее удалять");
        tm.createTask(task);
        tm.createTask(task2);

        tm.getEpic(epic.getId());
        tm.getEpic(epic2.getId());
        tm.getSubTask(subTask.getId());
        tm.getSubTask(subTask2.getId());
        tm.getTask(task.getId());
        tm.getTask(task2.getId());

        tm.removeEpic(epic2.getId());
        tm.removeEpic(subTask2.getId());
        tm.removeEpic(task2.getId());

        assertEquals(3, tm.getHistory().size(), "В истории должно находиться только 3 элемента");
    }

    @Test
    void clearTasksRemoveFromHistory() {
        Epic epic = new Epic("Обычный эпик", "Удалим");
        tm.createEpic(epic);

        SubTask subTask = new SubTask("Обычная сабтаска", "Удалим", epic.getId());
        tm.createSubTask(subTask);

        Task task = new Task("Обычная таска", "Удалим");
        tm.createTask(task);

        tm.getEpic(epic.getId());
        tm.getSubTask(subTask.getId());
        tm.getTask(task.getId());

        tm.subTaskClear();
        tm.epicClear();
        tm.taskClear();

        assertEquals(tm.getHistory().isEmpty(), true, "История должна быть пустой");
    }

    @Test
    void removingNonUniqueTasksFromHistory() {
        Task task = new Task("Обычная таска", "Запросим ее 2 раза");
        Task task2 = new Task("Обычная таска", "Тоже запросим ее 2 раза");
        tm.createTask(task);
        tm.createTask(task2);

        tm.getTask(task2.getId());
        tm.getTask(task2.getId());
        tm.getTask(task.getId());
        tm.getTask(task.getId());

        assertEquals(tm.getHistory().size(), 2, "В истории должно быть 2 записи");
    }

    @Test
    void nonUniqueTasksAddedToTheEnd() {
        Task task = new Task("Обычная таска", "Запросим ее 2 раза");
        Task task2 = new Task("Обычная таска", "Тоже запросим ее 2 раза");
        tm.createTask(task);
        tm.createTask(task2);

        tm.getTask(task.getId());
        tm.getTask(task2.getId());
        tm.getTask(task.getId());

        assertEquals(tm.getHistory().get(1), task, "Элемент должен добавляться в конец истории" +
                " даже если он не уникальный");
    }
}
