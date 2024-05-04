import java.io.*;
import java.net.*;
public class ClientThread extends Thread{
    private final String serverIp;
    private final int port;
    private final int message;
    private final int clientNumber;

    public ClientThread(String serverIp, int port, int message, int clientNumber){
        this.serverIp = serverIp;
        this.port = port;
        this.message = message;
        this.clientNumber = clientNumber;
    }

    public void run(){
        try (Socket socket = new Socket(serverIp, port)) {
            System.out.println("Cliente #"+clientNumber+" conectado.");
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            output.println(message);
            System.out.println("Cliente #"+clientNumber+" envia: " + message);
            int response = Integer.parseInt(input.readLine());
            System.out.println("Cliente #"+clientNumber+" recibe: " + response);

        } catch (UnknownHostException ex) {
            System.out.println("Cliente #"+clientNumber+" servidor no encontrado: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Cliente #"+clientNumber+" I/O error: " + ex.getMessage());
        }
    }
}
