import exceptions.TaskTimeConflictException;
import managers.Managers;
import managers.task_managers.InMemoryTaskManager;
import managers.task_managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    TaskManager tm;

    InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }

    @BeforeEach
    void preSet() {
        tm = Managers.getDefault();
    }

    @Test
    void removedSubtaskRemovedFromEpic() throws TaskTimeConflictException {
        Epic epic = new Epic("Эпик с сабтасками", "В нем будет сабтаска, которую далее удалим");
        tm.createEpic(epic);

        SubTask subTask = new SubTask("Сабтска",
                "Ее не будем удалять",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        SubTask subTask2 = new SubTask("Сабтска",
                "А вот эту удалим",
                epic.getId(),
                LocalDateTime.of(2025, 3, 10, 12, 50),
                Duration.ofMinutes(45));
        tm.createSubTask(subTask);
        tm.createSubTask(subTask2);

        tm.removeSubTask(subTask2.getId());

        assertEquals(epic.getSubTasksId().size(), 1, "У эпика должна быть только 1 сабтаска");
    }

    @Test
    void clearSubtasksRemovedFromEpic() throws TaskTimeConflictException {
        Epic epic = new Epic("Эпик с сабтасками", "В нем будет сабтаска, которую далее удалим");
        tm.createEpic(epic);

        SubTask subTask = new SubTask("Сабтска",
                "Ее не будем удалять",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        SubTask subTask2 = new SubTask("Сабтска",
                "А вот эту удалим",
                epic.getId(),
                LocalDateTime.of(2025, 3, 10, 12, 50),
                Duration.ofMinutes(45));
        tm.createSubTask(subTask);
        tm.createSubTask(subTask2);

        tm.subTaskClear();

        assertEquals(epic.getSubTasksId().isEmpty(), true, "У эпика не должно быть сабтасок");
    }
}