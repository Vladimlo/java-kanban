package managers.history_managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private Map<Integer, Node> history = new HashMap<>();

    private static class Node {
        Node prev;
        Node next;
        Task data;

        Node(Node prev, Node next, Task data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node removedNode = history.get(id);

        if (removedNode != null) removeNode(removedNode);

        history.remove(id);
    }

    public void linkLast(Task task) {
        Node newNode = new Node(tail, null, task);

        if (history.isEmpty()) {
            newNode = new Node(null, null, task);
            head = newNode;
        } else {
            tail.next = newNode;
        }

        tail = newNode;

        if (history.size() == 1) {
            head.next = tail;
        }

        remove(task.getId());
        history.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {
        Node currentNode = head;
        List<Task> result = new ArrayList<>();

        if (history.isEmpty()) return result;

        boolean tailIncluded = false;

        while (!tailIncluded) {
            if (currentNode == tail) tailIncluded = true;
            result.add(currentNode.data);
            currentNode = currentNode.next;
        }

        return result;
    }

    private void removeNode(Node removedNode) {
        Node next = removedNode.next;
        Node prev = removedNode.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            removedNode.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            removedNode.next = null;
        }
    }
}
