public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epicId = epic.getId();
        epic.subTasksId.add(this.getId());
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
