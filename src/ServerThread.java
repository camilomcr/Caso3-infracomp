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
            System.out.println("Server #" + serverNumber + " escuchando.");

                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Server #" + serverNumber + " conectado.");

                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                    int messageFromClient = Integer.parseInt(input.readLine());
                    System.out.println("Server #" + serverNumber + " mensaje leido: " + messageFromClient);

                    int response = messageFromClient - 1;
                    output.println(response);
                    System.out.println("Server #" + serverNumber + " response: " + response);

                } catch (IOException e) {
                    System.out.println("Server #" + serverNumber +" exception: "+e.getMessage());
                }


        } catch (IOException e) {
            System.out.println("Server #" + serverNumber +" exception: " + e.getMessage());
        }


    }
}
