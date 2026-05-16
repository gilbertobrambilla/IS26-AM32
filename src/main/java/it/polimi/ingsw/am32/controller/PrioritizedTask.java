package it.polimi.ingsw.am32.controller;

/**
 * Wrapper class used to assign execution priorities to runnable tasks.
 */
public class PrioritizedTask implements Runnable, Comparable<PrioritizedTask> {
    private final int priority;
    private final Runnable task;

    /**
     * A new prioritized task is constructed.
     *
     * @param priority the priority level of the task
     * @param task the runnable task to be executed
     */
    public PrioritizedTask(int priority, Runnable task) {
        this.priority = priority;
        this.task = task;
    }

    /**
     * The underlying task is executed.
     */
    @Override
    public void run() {
        // underlying task is executed
        this.task.run();
    }

    /**
     * The priorities are compared.
     *
     * @param other the other prioritized task to compare to
     * @return the comparison result
     */
    @Override
    public int compareTo(PrioritizedTask other) {
        // priorities are compared
        return Integer.compare(this.priority, other.priority);
    }
}
