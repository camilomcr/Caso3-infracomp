import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.NoSuchAlgorithmException;

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
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenerator.init(Client.keySize);
        SecretKey y = keyGenerator.generateKey();
        byte[] keyBytes = y.getEncoded();
        BigInteger yNumber = new BigInteger(1, keyBytes);

        try (Socket socket = new Socket(serverIp, port)) {
            System.out.println("Cliente #"+clientNumber+" conectado.");
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BigInteger G = new BigInteger(input.readLine());
            System.out.println("Cliente #"+clientNumber+" recibe G: " + G);
            BigInteger P = new BigInteger(input.readLine());
            System.out.println("Cliente #"+clientNumber+" recibe P: " + P);
            BigInteger GxP = new BigInteger(input.readLine());
            System.out.println("Cliente #"+clientNumber+" recibe G^x%P: " + GxP);
            BigInteger GyP= G.modPow(yNumber,P);
            output.println(GyP);
            System.out.println("Cliente #"+clientNumber+" envia G^y%P: " + GyP);
            BigInteger masterKey = GxP.modPow(yNumber, P);
            System.out.println("Cliente #"+clientNumber+" calcula llave maestra: " + masterKey);
            byte[][] keys = Client.getAndSplitDigest(masterKey);
            byte[] cypherKey = keys[0];
            byte[] hmacKey = keys[1];
            String cypherMessage = Client.cypher(cypherKey, message+"");
            output.println(cypherMessage);
            System.out.println("Cliente #"+clientNumber+" envia el mensaje " + message + " cifrado: " +cypherMessage);
            String hmacMessage = Client.computeHMAC(message+"", hmacKey);
            output.println(hmacMessage);
            System.out.println("Cliente #"+clientNumber+" envia el HMAC: " + hmacMessage);
            String cypherResponse = input.readLine();
            String response = Client.decypher(cypherKey, cypherResponse);
            System.out.println("Cliente #"+clientNumber+" recibe la respuesta cifrada: " + cypherResponse);
            System.out.println("Cliente #"+clientNumber+" decifra la respuesta: " + response);
            String hmacResponse = input.readLine();
            boolean hmacCheck = Client.verifyHMAC(response, hmacResponse, hmacKey);
            System.out.println("Cliente #"+clientNumber+" HMAC coincide: " + hmacCheck);

        } catch (UnknownHostException ex) {
            System.out.println("Cliente #"+clientNumber+" servidor no encontrado: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Cliente #"+clientNumber+" I/O error: " + ex.getMessage());
        }
    }
}
