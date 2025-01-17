import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager tm;
    int historyMaxSize = 10;

    @BeforeEach
    void preSet() {
        tm = Managers.getDefault();
    }

    @Test
    void maxHistorySizeTest() {
        Task task = new Task("Просто таска", "В истории их будет нужное количество штук");
        tm.createTask(task);

        for (int i = 0; i < historyMaxSize + 1; i++) {
            tm.getTask(task.getId());
        }

        int resutl = tm.getHistory().size();
        assertEquals(historyMaxSize, resutl, String.format("Размер истории не должен превышать %s. " +
                "Размер истории " + resutl, historyMaxSize));
    }

    @Test
    void firstElementIsRemoved() {
        Task task = new Task("Просто таска", "Ее не будет в истории");
        tm.createTask(task);

        Epic epic = new Epic("Просто эпик", "Он будет на 1 месте в истории");
        tm.createEpic(epic);

        tm.getTask(task.getId());
        for (int i = 0; i < historyMaxSize; i++) {
            tm.getEpic(epic.getId());
        }

        assertEquals(epic, tm.getHistory().getFirst(), "Ожидался Epic");
    }

    @Test
    void lastElementIsAdded() {
        Task task = new Task("Просто таска", "Она не будет последним элементом в истории");
        tm.createTask(task);

        Epic epic = new Epic("Просто эпик", "Он будет последним элементом в истории");
        tm.createEpic(epic);

        for (int i = 0; i < historyMaxSize; i++) {
            tm.getTask(task.getId());
        }
        tm.getEpic(epic.getId());

        assertEquals(epic, tm.getHistory().getLast(), "Ожидался Epic");
    }
}