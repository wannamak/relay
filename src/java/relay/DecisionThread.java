package relay;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DecisionThread extends Thread {
  private final Logger logger = Logger.getLogger(DecisionThread.class.getName());

  private final static int TIME_BETWEEN_DECISIONS_MS = 5 * 1000;

  private DecisionObserver observer;
  private boolean currentDecision;

  public DecisionThread() {
    super("DecisionThread");
  }

  @Override
  public void run() {
    currentDecision = true;
    observe(currentDecision);
    while (true) {
      safeSleep(TIME_BETWEEN_DECISIONS_MS);
      currentDecision = !currentDecision;
      observe(currentDecision);
    }
  }

  public synchronized void setObserver(DecisionObserver observer) {
    this.observer = observer;
    observe(currentDecision);
  }

  private synchronized void observe(boolean decision) {
    if (observer != null) {
      logger.fine("observing the decision " + decision);
      observer.observe(decision);
    }
  }

  private void safeSleep(long sleepDurationMs) {
    if (sleepDurationMs > 0) {
      try {
        Thread.sleep(sleepDurationMs);
      } catch (InterruptedException e) {
        logger.log(Level.SEVERE, "Error sleeping", e);
      }
    }
  }
}
