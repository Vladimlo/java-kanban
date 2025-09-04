import exceptions.TaskTimeConflictException;
import managers.task_managers.FileBackedTaskManager;
import tasks.Epic;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws TaskTimeConflictException {

        FileBackedTaskManager fm = new FileBackedTaskManager("Save.csv");

        System.out.println("-".repeat(20) + "ПРОВЕРКА КОНФЛИКТА ЗАДАЧ" + "-".repeat(20));

        Task taskCheckConflict1 = new Task("Таска без конфликта",
                "Которая заканчивается когда начинается следующая",
                LocalDateTime.of(2025, 3, 9, 12, 0),
                Duration.ofMinutes(45));
        Task taskCheckConflict2 = new Task("Таска без конфликта",
                "Которая начинается когда заканчивается предыдущая",
                LocalDateTime.of(2025, 3, 9, 12, 45),
                Duration.ofMinutes(45));
        Task taskCheckConflict3 = new Task("Таска с конфликтом",
                "Которая полностью входит в другую",
                LocalDateTime.of(2025, 3, 9, 12, 10),
                Duration.ofMinutes(10));
        Task taskCheckConflict4 = new Task("Таска с конфликтом",
                "Которая полностью входит в другую но граничит датой начала",
                LocalDateTime.of(2025, 3, 9, 12, 45),
                Duration.ofMinutes(10));
        Task taskCheckConflict5 = new Task("Таска с конфликтом",
                "Которая полностью входит в другую но граничит датой окончания",
                LocalDateTime.of(2025, 3, 9, 12, 50),
                Duration.ofMinutes(40));

        Epic epic = new Epic("Test", "Test");
        fm.createEpic(epic);

        fm.createTask(taskCheckConflict1);
        fm.createTask(taskCheckConflict2);
        fm.createTask(taskCheckConflict3);
        fm.createTask(taskCheckConflict4);
        fm.createTask(taskCheckConflict5);

        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile("Save.csv");

        System.out.println(fm.getPrioritizedTasks());
        System.out.println(load.getPrioritizedTasks());


    }
}
