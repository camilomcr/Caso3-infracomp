import java.io.*;
import java.net.*;
public class ServerThread extends Thread{
    private final int port;
    private final int serverNumber;
    public ServerThread(int port, int serverNumber){
        this.port=port;
        this.serverNumber=serverNumber;
    }

    public void run(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server #"+serverNumber+" escuchando.");

            try (Socket socket = serverSocket.accept()) {
                System.out.println("Server #"+serverNumber+" conectado.");

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                // Read message from client
                int messageFromClient = Integer.parseInt(input.readLine());
                System.out.println("Server #"+serverNumber+" mensaje leido:" + messageFromClient);

                // Send a response message to the client
                output.println(messageFromClient-1);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}
