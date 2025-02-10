import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

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

    //Сохраняет состояние задач в файл
    private void save() {
        String fileContent = String.join(DELIMITER, "id", "type", "name", "status", "description", "epic")
                + "\n";

        fileContent += mapToString(taskList) + mapToString(epicList) + mapToString(subTaskList);

        try (FileWriter fw = new FileWriter(path.toString())) {
            fw.write(fileContent);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл для сохранения" + path);
        }
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
                    value.getDescription()
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

        if (id >= taskCount) taskCount = id + 1;

        switch (type) {
            case "Task":
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                fm.taskList.put(id, task);
                break;
            case "Epic":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                fm.epicList.put(id, epic);
                break;
            case "SubTask":
                Integer epicId = Integer.parseInt(fields[5]);
                SubTask subTask = new SubTask(name, description, epicId);
                subTask.setId(id);
                subTask.setStatus(status);
                fm.epicList.get(epicId).getSubTasksId().add(id);
                fm.subTaskList.put(id, subTask);
                break;
        }
    }
}