import managers.task_managers.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    @Test
    void subtaskHaveEpic() {
        Epic epic = new Epic("Эпик", "Этот эпик будет связан с подзадачей");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Сабтска",
                "У этой подзадачи будет эпик",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        taskManager.createSubTask(subTask);

        assertEquals(subTask.getEpicId(), epic.getId(), "Эпик подзадачи не соответствует переданному");
    }

    @Test
    void epicStatusNEWTest() {
        Epic epic = new Epic("Эпик", "В нем будет сабтаска, в статусе NEW");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Сабтска",
                "В статусе NEW",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        taskManager.createSubTask(subTask);

        assertEquals(epic.getStatus(), TaskStatus.NEW, "Эпик должен иметь статус NEW");
    }

    @Test
    void epicStatusDONETest() {
        Epic epic = new Epic("Эпик", "В нем будет сабтаска, в статусе NEW");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Сабтска",
                "Будет в статусе DONE",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        subTask.setStatus(TaskStatus.DONE);
        taskManager.createSubTask(subTask);


        assertEquals(epic.getStatus(), TaskStatus.DONE, "Эпик должен иметь статус DONE");
    }

    @Test
    void epicStatusINPROGRESSTest1() {
        //Данный кейс проверяет статус епика IN_PROGRESS когда тот содержит подзадачи в статусе NEW и DONE
        Epic epic = new Epic("Эпик", "В нем будут сабтаски, в статусе NEW и DONE");
        taskManager.createEpic(epic);

        SubTask doneSubtask = new SubTask("Сабтска",
                "Будет в статусе DONE",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        doneSubtask.setStatus(TaskStatus.DONE);
        taskManager.createSubTask(doneSubtask);

        SubTask newSubtask = new SubTask("Сабтска",
                "Будет в статусе NEW",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        taskManager.createSubTask(newSubtask);

        assertEquals(epic.getStatus(), TaskStatus.IN_PROCESS, "Эпик должен иметь статус IN_PROCESS");
    }

    @Test
    void epicStatusINPROGRESSTest2() {
        //Данный кейс проверяет статус епика IN_PROGRESS когда тот содержит подзадачи в статусе IN_PROGRESS
        Epic epic = new Epic("Эпик", "В нем будут сабтаски, в статусе IN_PROCESS");
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask("Сабтска",
                "Будет в статусе IN_PROCESS",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        subTask1.setStatus(TaskStatus.IN_PROCESS);
        taskManager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Сабтска",
                "Будет в статусе IN_PROCESS",
                epic.getId(),
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        subTask2.setStatus(TaskStatus.IN_PROCESS);
        taskManager.createSubTask(subTask2);

        assertEquals(epic.getStatus(), TaskStatus.IN_PROCESS, "Эпик должен иметь статус IN_PROCESS");
    }

    @Test
    void timeConflictNegativeTest1() {
        //Данный тест проверяет кейс, когда старт одной задачи входит в диапазон уже имеющейся
        Task oldTask = new Task("Задача",
                "Начинается во время уже имеющейся",
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        taskManager.createTask(oldTask);

        Task newtask = new Task("Задача",
                "Начинается во время уже имеющейся",
                LocalDateTime.of(2025, 3, 9, 12, 55),
                Duration.ofMinutes(45));
        taskManager.createTask(newtask);

        List<Task> extend = List.of(oldTask);
        List<Task> result = taskManager.getPrioritizedTasks();

        assertEquals(result, extend, "Список приоритезированных задач не корректен");
    }

    @Test
    void timeConflictNegativeTest2() {
        //Данный тест проверяет кейс, когда окончание одной задачи входит в диапазон уже имеющейся
        Task oldTask = new Task("Задача",
                "Начинается во время уже имеющейся",
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        taskManager.createTask(oldTask);

        Task newtask = new Task("Задача",
                "Начинается во время уже имеющейся",
                LocalDateTime.of(2025, 3, 9, 12, 45),
                Duration.ofMinutes(10));
        taskManager.createTask(newtask);

        List<Task> extend = List.of(oldTask);
        List<Task> result = taskManager.getPrioritizedTasks();

        assertEquals(result, extend, "Список приоритезированных задач не корректен");
    }

    @Test
    void timeConflictNegativeTest3() {
        //Данный тест проверяет кейс, когда задача целиком входит в диапазон уже имеющейся
        Task oldTask = new Task("Задача",
                "Начинается во время уже имеющейся",
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(45));
        taskManager.createTask(oldTask);

        Task newtask = new Task("Задача",
                "Начинается во время уже имеющейся",
                LocalDateTime.of(2025, 3, 9, 12, 55),
                Duration.ofMinutes(5));
        taskManager.createTask(newtask);

        List<Task> extend = List.of(oldTask);
        List<Task> result = taskManager.getPrioritizedTasks();

        assertEquals(result, extend, "Список приоритезированных задач не корректен");
    }

    @Test
    void timeConflictPositiveTest() {
        Task oldTask = new Task("Задача",
                "Начинается во время уже имеющейся",
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(5));
        taskManager.createTask(oldTask);

        Task newtask = new Task("Задача",
                "Начинается во время уже имеющейся",
                LocalDateTime.of(2025, 3, 9, 12, 55),
                Duration.ofMinutes(5));
        taskManager.createTask(newtask);

        List<Task> extend = List.of(oldTask, newtask);
        List<Task> result = taskManager.getPrioritizedTasks();

        assertEquals(result, extend, "Список приоритезированных задач не корректен");
    }
}
