package managers.task_managers;

import exceptions.TaskTimeConflictException;
import managers.Managers;
import managers.history_managers.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> taskList = new HashMap<>();
    protected Map<Integer, Epic> epicList = new HashMap<>();
    protected Map<Integer, SubTask> subTaskList = new HashMap<>();

    protected Map<LocalDateTime, Task> sortedTasks = new TreeMap<>(new Comparator<LocalDateTime>() {
        @Override
        public int compare(LocalDateTime o1, LocalDateTime o2) {
            if (o1.equals(o2)) return 0;

            return o1.isBefore(o2) ? -1 : 1;
        }
    });

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int taskCount = 1;

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

        taskList.values().stream()
                .filter(task -> {
                    return task.getStartTime() != null;
                }).forEach(task -> sortedTasks.remove(task.getStartTime()));

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

        return task;
    }

    @Override
    public Task getTask(int id, boolean addToHistory) {
        Task task = taskList.get(id);

        if (task != null && addToHistory) historyManager.add(task);

        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic task = epicList.get(id);

        return task;
    }

    @Override
    public Epic getEpic(int id, boolean addToHistory) {
        Epic task = epicList.get(id);

        if (task != null && addToHistory) historyManager.add(task);

        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask task = subTaskList.get(id);

        return task;
    }

    @Override
    public SubTask getSubTask(int id, boolean addToHistory) {
        SubTask task = subTaskList.get(id);

        if (task != null && addToHistory) historyManager.add(task);

        return task;
    }

    @Override
    public void createTask(Task task) throws TaskTimeConflictException {
        task.setId(taskCount++);

        if (task.getStartTime() != null && !hasTimeConflict(task)) {
            if (task.getStatus() == null) task.setStatus(TaskStatus.NEW);
            sortedTasks.put(task.getStartTime(), task);
            taskList.put(task.getId(), task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskCount++);
        epic.setSubTasksId(new ArrayList<>());
        epic.setStatus(TaskStatus.NEW);
        epicList.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) throws TaskTimeConflictException {
        subTask.setId(taskCount++);

        Epic epic = epicList.get(subTask.getEpicId());

        if (subTask.getStartTime() != null && !hasTimeConflict(subTask)) {
            if (subTask.getStatus() == null) subTask.setStatus(TaskStatus.NEW);
            subTaskList.put(subTask.getId(), subTask);
            sortedTasks.put(subTask.getStartTime(), subTask);
            epic.getSubTasksId().add(subTask.getId());
            updateEpicDatesAndDuration(epic.getId());
            updateEpicStatus(epicList.get(subTask.getEpicId()).getId());
        }
    }

    @Override
    public void updateTask(Task task) throws TaskTimeConflictException {
        Task updatedTask = taskList.get(task.getId());
        sortedTasks.remove(updatedTask.getStartTime());

        if (task.getStartTime() != null && !hasTimeConflict(task)) {
            if (task.getStatus() == null) task.setStatus(TaskStatus.NEW);
            taskList.put(task.getId(), task);
            sortedTasks.put(task.getStartTime(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epicList.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void updateSubTask(SubTask subTask) throws TaskTimeConflictException {
        SubTask updatedSubTask = subTaskList.get(subTask.getId());
        sortedTasks.remove(updatedSubTask.getStartTime());

        //На случай обновления эпика прихраниваем старый
        Epic oldEpic = epicList.get(updatedSubTask.getEpicId());

        //Новый эпик
        Epic newEpic = epicList.get(subTask.getEpicId());

        if (subTask.getStartTime() != null && !hasTimeConflict(subTask)) {
            if (subTask.getStatus() == null) subTask.setStatus(TaskStatus.NEW);
            sortedTasks.put(subTask.getStartTime(), subTask);
            subTaskList.put(subTask.getId(), subTask);
            //Старый эпик может быть удален
            if (oldEpic != null ) oldEpic.getSubTasksId().remove(Integer.valueOf(updatedSubTask.getId()));
            newEpic.getSubTasksId().add(subTask.getId());
            updateEpicDatesAndDuration(newEpic.getId());
            updateEpicStatus(newEpic.getId());
            //Старый эпик может быть удален или равен новому, тогда обновление дат и статуса не имеет смысла
            if (oldEpic != null && !oldEpic.equals(newEpic)) {
                updateEpicDatesAndDuration(oldEpic.getId());
                updateEpicStatus(oldEpic.getId());
            }
        }
    }

    @Override
    public void removeTask(int id) {
        Task removedTask = taskList.get(id);
        sortedTasks.remove(removedTask.getStartTime());

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
        SubTask removedSubtask = subTaskList.get(id);

        //Удаляем сабтаску из эпика
        epic.getSubTasksId().remove(Integer.valueOf(id));

        //Удаляем сабтаску из приоритезированного списка
        sortedTasks.remove(removedSubtask.getStartTime());

        subTaskList.remove(id);
        updateEpicStatus(epic.getId());
        updateEpicDatesAndDuration(epic.getId());
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

        if(epicSubtasks.isEmpty()) {
            epicList.get(epicId).setStatus(TaskStatus.NEW);
            return;
        }

        for (SubTask epicSubtask : epicSubtasks) {
            switch (epicSubtask.getStatus()) {
                case DONE -> doneCount++;
                case IN_PROCESS -> inProgressCount++;
                case NEW -> newCount++;
            }
        }

        if (doneCount == 0 && inProgressCount == 0) {
            epicList.get(epicId).setStatus(TaskStatus.NEW);
            return;
        }

        if (inProgressCount == 0 && newCount == 0) {
            epicList.get(epicId).setStatus(TaskStatus.DONE);
            return;
        }

        epicList.get(epicId).setStatus(TaskStatus.IN_PROCESS);
    }

    public List<Task> getPrioritizedTasks() {
        return sortedTasks.values().stream().toList();
    }

    protected void updateEpicDatesAndDuration(int epicId) {
        Epic updatedEpic = epicList.get(epicId);

        List<SubTask> epicSubtasks = getEpicSubtasks(updatedEpic.getId());

        if (epicSubtasks.isEmpty()) {
            updatedEpic.setStartTime(null);
            updatedEpic.setEndTime(null);
            updatedEpic.setDuration(null);
            return;
        }

        if (epicSubtasks.size() == 1) {
            updatedEpic.setStartTime(epicSubtasks.getFirst().getStartTime());
            updatedEpic.setEndTime(epicSubtasks.getFirst().getEndTime());
            updatedEpic.setDuration(epicSubtasks.getFirst().getDuration());
            return;
        }

        List<LocalDateTime> startTimes = epicSubtasks
                .stream()
                .map(Task::getStartTime)
                .toList();

        List<LocalDateTime> endTimes = epicSubtasks
                .stream()
                .map(Task::getEndTime)
                .toList();

        List<Duration> durations = epicSubtasks
                .stream()
                .map(Task::getDuration)
                .toList();

        Optional<LocalDateTime> newStartTime = startTimes
                .stream()
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> newEndTime = endTimes
                .stream()
                .max(LocalDateTime::compareTo);

        Duration epicDuration = durations
                .stream()
                .reduce(Duration.ZERO, Duration::plus);

        updatedEpic.setStartTime(newStartTime.orElse(null));
        updatedEpic.setEndTime(newEndTime.orElse(null));
        updatedEpic.setDuration(epicDuration);
    }

    protected boolean hasTimeConflict(Task task) throws TaskTimeConflictException {
        boolean isConflict = sortedTasks.values().stream().filter(sortedTask -> {
                    //отбираем только те задачи, у которых конец не раньше начала добавляемой
                    return sortedTask.getEndTime().isAfter(task.getStartTime())
                            || sortedTask.getEndTime().equals(task.getEndTime());
                })
                .anyMatch(sortedTask -> {
                    //начало таски находится внутри уже имеющейся
                    if ((task.getStartTime().isAfter(sortedTask.getStartTime())
                            || task.getStartTime().equals(sortedTask.getStartTime()))
                            && task.getStartTime().isBefore(sortedTask.getEndTime())) return true;
                    //начало уже имеющейся таски находится внутри создаваемой
                    if ((sortedTask.getStartTime().isAfter(task.getStartTime())
                            || sortedTask.getStartTime().equals(task.getStartTime()))
                            && sortedTask.getStartTime().isBefore(task.getEndTime())) return true;
                    //конец таски находится внутри уже созданной
                    if ((task.getEndTime().isAfter(sortedTask.getStartTime()))
                            && (task.getEndTime().isBefore(sortedTask.getEndTime())
                            || task.getEndTime().equals(sortedTask.getEndTime()))) return true;
                    //конец уже имеющейся таски внутри создаваемой
                    if ((sortedTask.getEndTime().isAfter(task.getStartTime()))
                            && (sortedTask.getEndTime().isBefore(task.getEndTime())
                            || sortedTask.getEndTime().equals(task.getEndTime()))) return true;
                    return false;
                });

        if (isConflict) {
            throw new TaskTimeConflictException("Задача пересекается с уже имеющейся");
        }

        return false;
    }

    private void removeAllTasksFromHistory(Set<Integer> ids) {
        for (Integer id : ids) {
            historyManager.remove(id);
        }
    }
}
