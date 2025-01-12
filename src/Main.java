public class Main {

    public static void main(String[] args) {
        TaskManager tm = new TaskManager();

        Task task1 = new Task("Таска 1", "Просто таска");
        Task task2 = new Task("Таска 2", "Просто еще одна таска");
        tm.createTask(task1);
        tm.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Эпик с 1 подзадачей");
        Epic epic2 = new Epic("Эпик 2", "Эпик с 2 подзадачами");
        tm.createEpic(epic1);
        tm.createEpic(epic2);

        SubTask subTask11 = new SubTask("Сабтаска 11", "Сабтаска для epic1", epic1);
        SubTask subTask21 = new SubTask("Сабтаска 21", "Сабтаска для epic2", epic2);
        SubTask subTask22 = new SubTask("Сабтаска 22", "Сабтаска для epic2", epic2);
        tm.createSubTask(subTask11);
        tm.createSubTask(subTask21);
        tm.createSubTask(subTask22);

        System.out.println(tm.getTaskList());
        System.out.println(tm.getEpicList());
        System.out.println(tm.getSubTaskList());

        task1.setStatus(TaskStatus.DONE);
        task2.setStatus(TaskStatus.IN_PROCESS);
        tm.updateTask(task1.getId(), task1);
        tm.updateTask(task2.getId(), task2);

        subTask11.setStatus(TaskStatus.DONE);
        subTask21.setStatus(TaskStatus.NEW);
        subTask22.setStatus(TaskStatus.DONE);
        tm.updateSubTask(subTask11.getId(), subTask11);
        tm.updateSubTask(subTask21.getId(), subTask21);
        tm.updateSubTask(subTask22.getId(), subTask22);

        System.out.println("-".repeat(10));

        System.out.println(tm.getTaskList());
        System.out.println(tm.getEpicList());
        System.out.println(tm.getSubTaskList());
    }
}