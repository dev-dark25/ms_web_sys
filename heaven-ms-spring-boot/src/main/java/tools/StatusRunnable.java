package tools;

public abstract class StatusRunnable implements Runnable {
    private boolean finish;

    @Override
    public void run() {
        doIt();
        finish = true;
    }

    public abstract void doIt();

    public boolean isFinish() {
        return finish;
    }
}
