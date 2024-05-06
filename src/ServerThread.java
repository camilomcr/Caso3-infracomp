import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.net.*;
import java.security.NoSuchAlgorithmException;

public class ServerThread extends Thread{
    private final int port;
    private final int serverNumber;
    public ServerThread(int port, int serverNumber){
        this.port=port;
        this.serverNumber=serverNumber;
    }

    public void run() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenerator.init(Server.keySize);
        SecretKey x = keyGenerator.generateKey();
        byte[] keyBytes = x.getEncoded();
        BigInteger xNumber = new BigInteger(1, keyBytes);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server #" + serverNumber + " escuchando.");
            while(true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Server #" + serverNumber + " conectado.");

                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                    output.println(Server.G);
                    System.out.println("Server #" + serverNumber + " envia G: " + Server.G);
                    output.println(Server.P);
                    System.out.println("Server #" + serverNumber + " envia P: " + Server.P);
                    BigInteger GxP = Server.G.modPow(xNumber, Server.P);
                    output.println(GxP);
                    System.out.println("Server #" + serverNumber + " envia G^x%P: " + GxP);
                    BigInteger GyP = new BigInteger(input.readLine());
                    System.out.println("Server #" + serverNumber + " recibe G^y%P: " + GyP);
                    BigInteger masterKey = GyP.modPow(xNumber, Server.P);
                    System.out.println("Server #" + serverNumber + " calcula llave maestra: " + masterKey);
                    byte[][] keys = Server.getAndSplitDigest(masterKey);
                    byte[] cypherKey = keys[0];
                    byte[] hmacKey = keys[1];
                    Instant t = Instant.now();
                    String cypherMessage = input.readLine();
                    String message = Server.decypher(cypherKey, cypherMessage);
                    Duration timeElapsed = Duration.between(t, Instant.now());
                    System.out.println("Server #" + serverNumber + " recibe el mensaje cifrado: " + cypherMessage);
                    System.out.println("Server #" + serverNumber + " decifra el mensaje: " + message);
                    System.out.println("Server #"+serverNumber+" tiempo de descifrado (ns): " + timeElapsed.toNanos());
                    String hmacMessage = input.readLine();
                    t = Instant.now();
                    boolean hmacCheck = Server.verifyHMAC(message, hmacMessage, hmacKey);
                    timeElapsed = Duration.between(t, Instant.now());
                    System.out.println("Server #" + serverNumber + " HMAC coincide: " + hmacCheck);
                    System.out.println("Server #"+serverNumber+" tiempo de verificacion HMAC (ns): " + timeElapsed.toNanos());
                    String response = (Integer.parseInt(message) - 1) + "";
                    String cypherResponse = Server.cypher(cypherKey, response);
                    System.out.println("Server #" + serverNumber + " envia la respuesta " + response + " cifrada: " + cypherResponse);
                    output.println(cypherResponse);
                    String hmacResponse = Server.computeHMAC(response, hmacKey);
                    output.println(hmacResponse);
                    System.out.println("Server #" + serverNumber + " envia el HMAC: " + hmacResponse);

                } catch (IOException e) {
                    System.out.println("Server #" + serverNumber + " exception: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Server #" + serverNumber + " exception: " + e.getMessage());
        }



    }
}
