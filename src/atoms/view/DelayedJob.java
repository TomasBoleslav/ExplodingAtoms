package atoms.view;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public class DelayedJob {
    public DelayedJob(int delay, Action action) {
        this.delay = delay;
        actions = new ArrayList<>();
        actions.add(action);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void run() {
        Timer timer = new Timer(delay, e -> {
            if (shouldCancelFunc != null && shouldCancelFunc.call()) {
                return;
            }
            for (Action action : actions) {
                action.run();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void setShouldCancelFunc(Func<Boolean> shouldCancelFunc) {
        this.shouldCancelFunc = shouldCancelFunc;
    }

    private final int delay;
    private final List<Action> actions;
    private Func<Boolean> shouldCancelFunc;
}
