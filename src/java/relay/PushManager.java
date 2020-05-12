package relay;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PushManager implements DecisionObserver {
  private final Logger logger = Logger.getLogger(DecisionObserver.class.getName());

  private final Socket socket;
  private final InputStream input;
  private final OutputStream output;

  private final Object wakeup = new Object();
  private boolean decision = false;

  private static final int SUBSCRIBE_TIMEOUT_MS = 10 * 1000;

  public PushManager(Socket socket) throws IOException {
    this.socket = socket;
    this.input = socket.getInputStream();
    this.output = socket.getOutputStream();
  }

  public void pushObservations() throws IOException {
    while (true) {
      try {
        logger.fine("Awaiting any decisions to push");
        synchronized (wakeup) {
          wakeup.wait();
        }
      } catch (InterruptedException e) {
        logger.log(Level.WARNING, "Error waiting", e);
      }
      logger.info("Pushing decision " + decision);
      if (decision) {
        output.write("ON\n".getBytes(StandardCharsets.UTF_8));
      } else {
        output.write("OFF\n".getBytes(StandardCharsets.UTF_8));
      }
    }
  }

  @Override
  public void observe(boolean decision) {
    this.decision = decision;
    synchronized (wakeup) {
      wakeup.notify();
    }
  }

  public void awaitSubscribeMessage() throws IOException {
    logger.info("awaiting SUBSCRIBE ...");
    socket.setSoTimeout(SUBSCRIBE_TIMEOUT_MS);

    List<String> messages = readMessages();
    System.out.println("Subscriber connected:");
    for (String message : messages) {
      System.out.println("  " + message);
    }

    socket.setSoTimeout(0);
  }

  private List<String> readMessages() throws IOException {
    List<String> result = new ArrayList<>();
    int currentByte;
    String currentString = "";
    do {
      currentByte = input.read();
      if (currentByte == -1) {
        throw new IOException("Read returned -1");
      }
      char currentChar = (char) (currentByte & 0xff);
      if (currentChar == '\n') {
        if (currentString.isEmpty()) {
          return result;
        }
        result.add(currentString);
        currentString = "";
      } else {
        currentString += currentChar;
      }
    } while (true);
  }

}
