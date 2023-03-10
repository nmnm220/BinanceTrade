package socket.client;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    String serverName;
    int port;
    Socket client;

    public SocketClient(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
        try {
            client = new Socket(serverName, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendData(String textToSend) {
        try {
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF(textToSend);
            //client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getData() {
        InputStream inFromServer = null;
        try {
            inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            //client.close();
            if (in != null)
                return in.readUTF();
            else return null;
        } catch (IOException e) {
            System.out.println(e);
            return e.getMessage();
        }
    }

    public void closeConnection() {
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
