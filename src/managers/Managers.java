package managers.task_managers;

import managers.task_managers.history_managers.HistoryManager;
import managers.task_managers.history_managers.InMemoryHistoryManager;

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
