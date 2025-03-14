package managers.task_managers;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final String DELIMITER = ",";

    private final Path path;

    private static int taskCount = 1;

    public FileBackedTaskManager(String path) {
        this.path = Paths.get(path);
    }

    @Override
    public void taskClear() {
        super.taskClear();
        save();
    }

    @Override
    public void subTaskClear() {
        super.subTaskClear();
        save();
    }

    @Override
    public void epicClear() {
        super.epicClear();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    //Восстанавливает состояние из файла
    public static FileBackedTaskManager loadFromFile(String path) {
        FileBackedTaskManager fm = new FileBackedTaskManager(path);

        Path loadPath = Paths.get(path);

        if (Files.notExists(loadPath)) {
            try {
                Files.createFile(loadPath);
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания файла для загрузки: " + loadPath);
            }
        }

        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            bf.readLine();
            while (bf.ready()) {
                loadTaskFromString(bf.readLine(), fm);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка создания потока записи в файл загрузки");
        }

        return fm;
    }

    //Сохраняет состояние задач в файл
    private void save() {
        String fileContent = String.join(DELIMITER, "id",
                "type",
                "name",
                "status",
                "description",
                "startTime",
                "duration",
                "epic")
                + "\n";

        fileContent += mapToString(taskList) + mapToString(epicList) + mapToString(subTaskList);

        try (FileWriter fw = new FileWriter(path.toString())) {
            fw.write(fileContent);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл для сохранения" + path);
        }
    }

    //Принимает список задач, возвращает строку для файла загрузки
    private <T extends Task> String mapToString(Map<Integer, T> map) {

        StringBuilder result = new StringBuilder();

        for (Map.Entry<Integer, T> entry : map.entrySet()) {
            Integer key = entry.getKey();
            T value = entry.getValue();

            result.append(String.join(
                    DELIMITER,
                    key.toString(),
                    value.getClass().getSimpleName(),
                    value.getName(),
                    value.getStatus().name(),
                    value.getDescription(),
                    value.getStartTime() != null ? value.getStartTime().format(DATE_TIME_FORMATTER) : null,
                    value.getDuration() != null ? value.getDuration().toHours() +
                            ":" + value.getDuration().toMinutesPart() : "null"
            )).append(",");

            if (value instanceof SubTask) {
                result.append(((SubTask) value).getEpicId());
            }

            result.append("\n");
        }

        return result.toString();
    }

    private static void loadTaskFromString(String line, FileBackedTaskManager fm) {
        String[] fields = line.split(DELIMITER);

        Integer id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = !fields[5].equals("null") ? LocalDateTime.parse(fields[5], DATE_TIME_FORMATTER) : null;
        Duration duration;

        if (!fields[6].equals("null")) {
            String[] hoursAndMinutesOfDuration = fields[6].split(":");

            Duration durationHours = Duration.ofHours(Long.parseLong(hoursAndMinutesOfDuration[0]));
            Duration durationMinutes = Duration.ofMinutes(Long.parseLong(hoursAndMinutesOfDuration[1]));
            duration = durationHours.plus(durationMinutes);
        } else {
            duration = null;
        }

        if (id >= taskCount) taskCount = id + 1;

        switch (type) {
            case "Task":
                Task task = new Task(name, description, startTime, duration);
                task.setId(id);
                task.setStatus(status);
                fm.taskList.put(id, task);
                if (startTime != null && !fm.hasTimeConflict(task)) fm.sortedTasks.put(startTime, task);
                break;
            case "Epic":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                fm.epicList.put(id, epic);
                break;
            case "SubTask":
                int epicId = Integer.parseInt(fields[7]);
                SubTask subTask = new SubTask(name, description, epicId, startTime, duration);
                subTask.setId(id);
                subTask.setStatus(status);
                fm.epicList.get(epicId).getSubTasksId().add(id);
                fm.subTaskList.put(id, subTask);
                if (startTime != null && !fm.hasTimeConflict(subTask))
                    fm.sortedTasks.put(subTask.getStartTime(), subTask);
                fm.updateEpicDatesAndDuration(epicId);
                break;
        }
    }
}