import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import managers.task_managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager tm;

    @BeforeEach
    void preSet() {
        tm = Managers.getDefault();
    }

    @Test
    void removedSubtaskRemovedFromEpic() {
        Epic epic = new Epic("Эпик с сабтасками", "В нем будет сабтаска, которую далее удалим");
        tm.createEpic(epic);

        SubTask subTask = new SubTask("Сабтска", "Ее не будем удалять", epic.getId());
        SubTask subTask2 = new SubTask("Сабтска", "А вот эту удалим", epic.getId());
        tm.createSubTask(subTask);
        tm.createSubTask(subTask2);

        tm.removeSubTask(subTask2.getId());

        assertEquals(epic.getSubTasksId().size(), 1, "У эпика должна быть только 1 сабтаска");
    }

    @Test
    void clearSubtasksRemovedFromEpic() {
        Epic epic = new Epic("Эпик с сабтасками", "В нем будет сабтаска, которую далее удалим");
        tm.createEpic(epic);

        SubTask subTask = new SubTask("Сабтска", "Ее не будем удалять", epic.getId());
        SubTask subTask2 = new SubTask("Сабтска", "А вот эту удалим", epic.getId());
        tm.createSubTask(subTask);
        tm.createSubTask(subTask2);

        tm.subTaskClear();

        assertEquals(epic.getSubTasksId().isEmpty(), true, "У эпика не должно быть сабтасок");
    }
}