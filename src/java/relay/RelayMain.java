package relay;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RelayMain {
  private final Logger logger = Logger.getLogger(RelayMain.class.getName());

  private final int port;

  public static void main(String args[]) throws Exception {
    if (args.length != 1) {
      System.err.println("relay.RelayMain 1228");
      System.exit(-1);
    }
    new RelayMain(args).run();
  }

  public RelayMain(String args[]) {
    port = Integer.parseInt(args[0]);
  }

  // @formatter:off
  void run() throws Exception {
    DecisionThread decisionThread = new DecisionThread();
    decisionThread.start();
    ServerSocket serverSocket = new ServerSocket(port);
    while (true) {
      logger.info("Listening on port " + port + "...");
      Socket socket = serverSocket.accept();
      logger.info("... client connected!");
      PushManager manager = new PushManager(socket);
      try {
        manager.awaitSubscribeMessage();
        decisionThread.setObserver(manager);
        manager.pushObservations();
      } catch (Exception e) {
        logger.log(Level.WARNING, "Connection problem", e);
      } finally {
        socket.close();
      }
    }
  }
}
