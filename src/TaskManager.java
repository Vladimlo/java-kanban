import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> taskList = new HashMap<Integer, Task>();
    private HashMap<Integer, Epic> epicList = new HashMap<Integer, Epic>();
    private HashMap<Integer, SubTask> subTaskList = new HashMap<Integer, SubTask>();

    private static int taskCount = 1;

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskList.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicList.values());
    }

    public ArrayList<SubTask> getSubTaskList() {
        return new ArrayList<>(subTaskList.values());
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
        task.setId(taskCount++);
        taskList.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(taskCount++);
        epicList.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
        subTask.setId(taskCount++);
        epicList.get(subTask.getEpicId()).getSubTasksId().add(subTask.getId());
        subTaskList.put(subTask.getId(), subTask);
        updateEpicStatus(epicList.get(subTask.getEpicId()).getId());
    }

    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epicList.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    public void updateSubTask(SubTask subTask) {
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
        Epic epic = epicList.get(subTaskList.get(id).getEpicId());
        epic.getSubTasksId().remove(Integer.valueOf(id));
        subTaskList.remove(id);
        updateEpicStatus(epic.getId());
    }

    public ArrayList<SubTask> getEpicSubtasks(int id) {
        ArrayList<SubTask> epicSubtasks = new ArrayList<>();

        for (Integer i : epicList.get(id).getSubTasksId()) {
            epicSubtasks.add(subTaskList.get(i));
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
