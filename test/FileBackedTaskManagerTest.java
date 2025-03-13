import managers.Managers;
import managers.task_managers.FileBackedTaskManager;
import managers.task_managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    static Path saveFile = Paths.get("Save.csv");

    TaskManager managerForSave;

    FileWriter fw;

    FileBackedTaskManagerTest() {
        super(FileBackedTaskManager.loadFromFile(saveFile.toString()));
    }

    @BeforeEach
    void setUp() throws IOException {
        managerForSave = Managers.getFileBackedManager();
        fw = new FileWriter(saveFile.toString(), true);
    }

    @AfterEach
    void cleanUp() throws IOException {
        fw.close();
        Files.delete(saveFile);
    }

    @Test
    void saveTaskOnCreate() throws IOException {
        String taskName = "saveTaskOnCreate";
        String taskDesk = "saveTaskOnCreateDesc";

        Task task = new Task(taskName,
                taskDesk,
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(40));
        managerForSave.createTask(task);

        String[] taskFields = Files.readAllLines(saveFile).get(1).split(",");

        boolean taskIsValid = (taskFields[2].equals(taskName) && taskFields[4].equals(taskDesk));

        assertTrue(taskIsValid, "Таска в файле не соответствует созданной");
    }

    @Test
    void saveSubTaskOnCreate() throws IOException {
        String taskName = "saveSubTaskOnCreate";
        String taskDesk = "saveSubTaskOnCreateDesc";

        Epic epic = new Epic("Не имеет значения", "Не имеет значения");
        managerForSave.createEpic(epic);

        SubTask sb = new SubTask(taskName,
                taskDesk,
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        managerForSave.createSubTask(sb);

        String[] taskFields = Files.readAllLines(saveFile).get(2).split(",");

        boolean taskIsValid = (
                taskFields[2].equals(taskName) &&
                        taskFields[4].equals(taskDesk) &&
                        taskFields[7].equals(Integer.toString(epic.getId()))
        );

        assertTrue(taskIsValid, "Сабтаска в файле не соответствует созданной");
    }

    @Test
    void deleteSubtaskFromFileOnDelete() throws IOException {
        String taskName = "saveTaskOnCreate";
        String taskDesk = "saveTaskOnCreateDesc";

        Task task = new Task(taskName,
                taskDesk,
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(40));
        managerForSave.createTask(task);
        managerForSave.removeTask(task.getId());

        List<String> fileLines = Files.readAllLines(saveFile);

        assertEquals(fileLines.size(), 1, "В файле не должно быть задач");
    }

    @Test
    void loadTaskFromFile() throws IOException {
        String taskName = "loadTaskFromFileName";
        String taskDesk = "loadTaskFromFileDesc";

        Task savedTask = new Task(taskName,
                taskDesk,
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(40));

        managerForSave.createTask(savedTask);
        TaskManager managerForLoad = FileBackedTaskManager.loadFromFile(saveFile.toString());

        String[] taskFields = Files.readAllLines(saveFile).get(1).split(",");

        Task loadedTask = managerForLoad.getTask(savedTask.getId());

        boolean taskIsValid = (
                savedTask.getId() == loadedTask.getId() &&
                        savedTask.getName().equals(loadedTask.getName()) &&
                        savedTask.getDescription().equals(loadedTask.getDescription())
        );

        assertTrue(taskIsValid, "Созданная таска не соответствует загруженной");
    }

    @Test
    void loadSubTaskFromFile() throws IOException {
        String taskName = "loadSubTaskFromFile";
        String taskDesk = "loadSubTaskFromFileDesc";

        Epic epic = new Epic("Не имеет значения", "Не имеет значения");
        managerForSave.createEpic(epic);

        SubTask savedSubTask = new SubTask(taskName,
                taskDesk,
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        managerForSave.createSubTask(savedSubTask);

        TaskManager managerForLoad = FileBackedTaskManager.loadFromFile(saveFile.toString());

        SubTask loadedSubTask = managerForLoad.getSubTask(savedSubTask.getId());

        boolean taskIsValid = (
                savedSubTask.getId() == loadedSubTask.getId()
                        && savedSubTask.getName().equals(loadedSubTask.getName())
                        && savedSubTask.getDescription().equals(loadedSubTask.getDescription())
                        && savedSubTask.getEpicId() == loadedSubTask.getEpicId()
        );

        assertTrue(taskIsValid, "Сохраненная сабтаска не соответствует загруженной");
    }

    @Test
    void testIOException() {
        assertThrows(IOException.class, () -> {
            String path = "test";
            Files.createFile(Path.of(path));
            FileBackedTaskManager.loadFromFile(path);
        }, "Создание файла с не уникальным именем должно приводить к исключению");
    }
}