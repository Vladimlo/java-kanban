import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import managers.task_managers.FileBackedTaskManager;
import managers.task_managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {

    TaskManager managerForSave = Managers.getFileBackedManager();

    Path saveFile = Paths.get("Save.csv");

    FileWriter fw;

    @BeforeEach
    void setUp() throws IOException {
        fw = new FileWriter(saveFile.toString(), true);
    }

    @AfterEach
    void cleanUp() throws IOException {
        managerForSave.subTaskClear();
        managerForSave.taskClear();
        managerForSave.epicClear();

        fw.close();
        Files.delete(saveFile);
    }

    @Test
    void saveTaskOnCreate() throws IOException {
        String taskName = "saveTaskOnCreate";
        String taskDesk = "saveTaskOnCreateDesc";

        Task task = new Task(taskName, taskDesk);
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

        SubTask sb = new SubTask(taskName, taskDesk, epic.getId());
        managerForSave.createSubTask(sb);

        String[] taskFields = Files.readAllLines(saveFile).get(2).split(",");

        boolean taskIsValid = (
                taskFields[2].equals(taskName) &&
                        taskFields[4].equals(taskDesk) &&
                        taskFields[5].equals(Integer.toString(epic.getId()))
        );

        assertTrue(taskIsValid, "Сабтаска в файле не соответствует созданной");
    }

    @Test
    void deleteSubtaskFromFileOnDelete() throws IOException {
        String taskName = "saveTaskOnCreate";
        String taskDesk = "saveTaskOnCreateDesc";

        Task task = new Task(taskName, taskDesk);
        managerForSave.createTask(task);
        managerForSave.removeTask(task.getId());

        List<String> fileLines = Files.readAllLines(saveFile);

        assertEquals(fileLines.size(), 1, "В файле не должно быть задач");
    }

    @Test
    void loadTaskFromFile() throws IOException {
        String taskName = "loadTaskFromFileName";
        String taskDesk = "loadTaskFromFileDesc";

        Task savedTask = new Task(taskName, taskDesk);

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

        SubTask savedSubTask = new SubTask(taskName, taskDesk, epic.getId());
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
}