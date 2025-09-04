package managers;

import managers.history_managers.HistoryManager;
import managers.history_managers.InMemoryHistoryManager;
import managers.task_managers.FileBackedTaskManager;
import managers.task_managers.InMemoryTaskManager;
import managers.task_managers.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedManager() {
        return new FileBackedTaskManager("Save.csv");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
