package it.polimi.ingsw.am32.controller;

/**
 * Wrapper class used to assign execution priorities to runnable tasks.
 */
public class PrioritizedTask implements Runnable, Comparable<PrioritizedTask> {
    private final int priority;
    private final Runnable task;

    /**
     * Constructs a new prioritized task.
     *
     * @param priority the priority level of the task (lower value means higher priority).
     * @param task the runnable task to be executed.
     */
    public PrioritizedTask(int priority, Runnable task) {
        this.priority = priority;
        this.task = task;
    }

    @Override
    public void run() {
        // underlying task is executed
        this.task.run();
    }

    @Override
    public int compareTo(PrioritizedTask other) {
        // priorities are compared
        return Integer.compare(this.priority, other.priority);
    }
}
