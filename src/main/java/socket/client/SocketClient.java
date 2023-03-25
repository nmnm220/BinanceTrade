package socket.client;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private String serverName;
    private int port;
    private final Socket socket;
    private final PrintWriter printWriter;
    private final InputStream inputStream;
    private final BufferedReader inputBuffer;

    public SocketClient(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
        try {
            socket = new Socket(serverName, port);
            OutputStream outToServer = socket.getOutputStream();
            printWriter = new PrintWriter(outToServer, true);

            inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            inputBuffer = new BufferedReader(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendData(String textToSend) {
        printWriter.println(textToSend);
    }

    public String getData() {
        try {
            if (inputStream != null)
                return inputBuffer.readLine();
            else return "no data";
        } catch (IOException e) {
            System.out.println(e);
            return e.getMessage();
        }
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
