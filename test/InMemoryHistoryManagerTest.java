import exceptions.TaskTimeConflictException;
import managers.Managers;
import managers.task_managers.InMemoryTaskManager;
import managers.task_managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    TaskManager tm;

    InMemoryHistoryManagerTest() {
        super(new InMemoryTaskManager());
    }

    @BeforeEach
    void preSet() {
        tm = Managers.getDefault();
    }

    @Test
    void removedTasksRemoveFromHistoy() throws TaskTimeConflictException {
        Epic epic = new Epic("Обычный эпик", "Его не будем удалять");
        Epic epic2 = new Epic("Обычный эпик", "Его будем удалять");
        tm.createEpic(epic);
        tm.createEpic(epic2);

        SubTask subTask = new SubTask("Обычная сабтаска",
                "Не будем ее удалять",
                epic.getId(),
                LocalDateTime.of(2025, 3, 8, 12, 50),
                Duration.ofMinutes(45));
        SubTask subTask2 = new SubTask("Обычная сабтаска",
                "Не будем ее удалять",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        tm.createSubTask(subTask);
        tm.createSubTask(subTask2);

        Task task = new Task("Обычная таска",
                "Не будем ее удалять",
                LocalDateTime.of(2025, 3, 10, 12, 50),
                Duration.ofMinutes(45));
        Task task2 = new Task("Обычная таска",
                "Не будем ее удалять",
                LocalDateTime.of(2025, 3, 11, 12, 50),
                Duration.ofMinutes(45));
        tm.createTask(task);
        tm.createTask(task2);

        tm.getEpic(epic.getId(), true);
        tm.getEpic(epic2.getId(), true);
        tm.getSubTask(subTask.getId(), true);
        tm.getSubTask(subTask2.getId(), true);
        tm.getTask(task.getId(), true);
        tm.getTask(task2.getId(), true);

        tm.removeEpic(epic2.getId());
        tm.removeEpic(subTask2.getId());
        tm.removeEpic(task2.getId());

        assertEquals(3, tm.getHistory().size(), "В истории должно находиться только 3 элемента");
    }

    @Test
    void clearTasksRemoveFromHistory() throws TaskTimeConflictException {
        Epic epic = new Epic("Обычный эпик", "Удалим");
        tm.createEpic(epic);

        SubTask subTask = new SubTask("Обычная сабтаска",
                "Удалим",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        tm.createSubTask(subTask);

        Task task = new Task("Обычная таска",
                "Удалим",
                LocalDateTime.of(2025, 3, 10, 12, 50),
                Duration.ofMinutes(45));
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
    void removingNonUniqueTasksFromHistory() throws TaskTimeConflictException {
        Task task = new Task("Обычная таска",
                "Запросим ее 2 раза",
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        Task task2 = new Task("Обычная таска",
                "Запросим ее 2 раза",
                LocalDateTime.of(2025, 3, 10, 12, 50),
                Duration.ofMinutes(45));
        tm.createTask(task);
        tm.createTask(task2);

        tm.getTask(task2.getId(), true);
        tm.getTask(task2.getId(), true);
        tm.getTask(task.getId(), true);
        tm.getTask(task.getId(), true);

        assertEquals(tm.getHistory().size(), 2, "В истории должно быть 2 записи");
    }

    @Test
    void nonUniqueTasksAddedToTheEnd() throws TaskTimeConflictException {
        Task task = new Task("Обычная таска",
                "Запросим ее 2 раза",
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        Task task2 = new Task("Обычная таска",
                "Тоже запросим ее 2 раза",
                LocalDateTime.of(2025, 3, 10, 12, 50),
                Duration.ofMinutes(45));
        tm.createTask(task);
        tm.createTask(task2);

        tm.getTask(task.getId(), true);
        tm.getTask(task2.getId(), true);
        tm.getTask(task.getId(), true);

        assertEquals(tm.getHistory().get(1), task, "Элемент должен добавляться в конец истории" +
                " даже если он не уникальный");
    }
}
