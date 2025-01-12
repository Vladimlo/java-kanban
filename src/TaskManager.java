import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> taskList = new HashMap<Integer, Task>();
    private HashMap<Integer, Epic> epicList = new HashMap<Integer, Epic>();
    private HashMap<Integer, SubTask> subTaskList = new HashMap<Integer, SubTask>();

    private static int taskCount = 1;

    static int incTaskCount() {
        return taskCount++;
    }

    public HashMap<Integer, Task> getTaskList() {
        return taskList;
    }

    public HashMap<Integer, Epic> getEpicList() {
        return epicList;
    }

    public HashMap<Integer, SubTask> getSubTaskList() {
        return subTaskList;
    }

    public void taskClear() {
        taskList.clear();
    }

    public void subTaskClear() {
        subTaskList.clear();
    }

    public void epicClear() {
        epicList.clear();
    }

    public Task getTask(int id) {
        return taskList.get(id);
    }

    public Epic getEpic(int id) {
        return epicList.get(id);
    }

    public SubTask getTSubTask(int id) {
        return subTaskList.get(id);
    }

    public void createTask(Task task) {
        if (taskList.containsValue(task) || task == null) {
            return;
        }

        taskList.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        if (epicList.containsValue(epic) || epic == null) {
            return;
        }

        epicList.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
        if (subTaskList.containsValue(subTask) || subTask == null) {
            return;
        }

        subTaskList.put(subTask.getId(), subTask);
    }

    public void updateTask(int id, Task task) {
        taskList.remove(id);
        taskList.put(task.getId(), task);
    }

    public void updateEpic(int id, Epic epic) {
        epicList.remove(id);
        epicList.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    public void updateSubTask(int id, SubTask subTask) {
        subTaskList.remove(id);
        subTaskList.put(subTask.getId(), subTask);
        updateEpicStatus(epicList.get(subTask.getEpicId()).getId());
    }

    public void removeTask(int id) {
        taskList.remove(id);
    }

    public void removeEpic(int id) {
        epicList.remove(id);
    }

    public void removeSubTask(int id) {
        subTaskList.remove(id);
        updateEpicStatus(subTaskList.get(id).getEpicId());
    }

    public ArrayList<SubTask> getEpicSubtasks(int id) {
        ArrayList<SubTask> epicSubtasks = new ArrayList<>();

        for (Integer i : subTaskList.keySet()) {
            if (subTaskList.get(i).getEpicId() == id) epicSubtasks.add(subTaskList.get(i));
        }

        return epicSubtasks;
    }

    private void updateEpicStatus(int epicId) {
        ArrayList<SubTask> epicSubtasks = getEpicSubtasks(epicId);

        int doneCount = 0;
        int inProgressCount = 0;
        int newCount = 0;

        for (SubTask epicSubtask : epicSubtasks) {
            switch (epicSubtask.getStatus()) {
                case DONE -> doneCount++;
                case IN_PROCESS -> inProgressCount++;
                case NEW -> newCount++;
            }
        }


        if (epicSubtasks.isEmpty() || (doneCount == 0 && inProgressCount == 0)) {
            epicList.get(epicId).setStatus(TaskStatus.NEW);
            return;
        }

        if (inProgressCount == 0 && newCount == 0) {
            epicList.get(epicId).setStatus(TaskStatus.DONE);
            return;
        }

        epicList.get(epicId).setStatus(TaskStatus.IN_PROCESS);
    }
}
