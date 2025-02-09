import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> taskList = new HashMap<Integer, Task>();
    private Map<Integer, Epic> epicList = new HashMap<Integer, Epic>();
    private Map<Integer, SubTask> subTaskList = new HashMap<Integer, SubTask>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    private static int taskCount = 1;

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(taskList.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epicList.values());
    }

    @Override
    public List<SubTask> getSubTaskList() {
        return new ArrayList<>(subTaskList.values());
    }

    @Override
    public void taskClear() {
        removeAllTasksFromHistory(taskList.keySet());
        taskList.clear();
    }

    @Override
    public void subTaskClear() {
        //Нужен что-бы не словить ConcurrentModificationException в цикле
        Set<Integer> subTasksKeys = new HashSet<>(subTaskList.keySet());

        removeAllTasksFromHistory(subTasksKeys);

        for (Integer subtaskId : subTasksKeys) {
            removeSubTask(subtaskId);
        }
    }

    @Override
    public void epicClear() {
        removeAllTasksFromHistory(epicList.keySet());
        epicList.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = taskList.get(id);

        historyManager.add(task);

        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic task = epicList.get(id);

        historyManager.add(task);

        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask task = subTaskList.get(id);

        historyManager.add(task);

        return task;
    }

    @Override
    public void createTask(Task task) {
        task.setId(taskCount++);
        taskList.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskCount++);
        epicList.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        subTask.setId(taskCount++);
        epicList.get(subTask.getEpicId()).getSubTasksId().add(subTask.getId());
        subTaskList.put(subTask.getId(), subTask);
        updateEpicStatus(epicList.get(subTask.getEpicId()).getId());
    }

    @Override
    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicList.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTaskList.put(subTask.getId(), subTask);
        updateEpicStatus(epicList.get(subTask.getEpicId()).getId());
    }

    @Override
    public void removeTask(int id) {
        taskList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        epicList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        Epic epic = epicList.get(subTaskList.get(id).getEpicId());
        epic.getSubTasksId().remove(Integer.valueOf(id));
        subTaskList.remove(id);
        updateEpicStatus(epic.getId());
        historyManager.remove(id);
    }

    @Override
    public List<SubTask> getEpicSubtasks(int id) {
        List<SubTask> epicSubtasks = new ArrayList<>();

        for (Integer i : epicList.get(id).getSubTasksId()) {
            epicSubtasks.add(subTaskList.get(i));
        }

        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
        List<SubTask> epicSubtasks = getEpicSubtasks(epicId);

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

    private void removeAllTasksFromHistory(Set<Integer> ids) {
        for (Integer id : ids) {
            historyManager.remove(id);
        }
    }
}
