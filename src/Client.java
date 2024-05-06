import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;
import java.math.*;

public class Client {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        int clientNumber = 0;
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("1) Nueva conexion cliente-servidor.");
            System.out.println("2) Cerrar cliente.");
            int option = scanner.nextInt();
            if (option==1) {
                System.out.print("Ingresa el numero a enviar al servidor: ");
                int number = scanner.nextInt();
                clientNumber++;
                new ClientThread(Client.serverIp, Client.port, number, clientNumber).start();
            }else{
                break;
            }
        }

    }
    public final static int keySize = 256;
    public final static int port = 12345;
    public final static String serverIp = "localhost";

    public static String cypher(byte[] key, String content){
        try {

            if (key.length != 32) {
                throw new IllegalArgumentException("Tamano invalido de la llave (32 bytes).");
            }

            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);


            SecretKey secretKey = new SecretKeySpec(key, "AES");


            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);


            byte[] encryptedBytes = cipher.doFinal(content.getBytes());

            byte[] encryptedIVAndText = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, encryptedIVAndText, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(encryptedIVAndText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String decypher(byte[] key, String content){

        try {

            byte[] encryptedIVAndText = Base64.getDecoder().decode(content);

            byte[] iv = new byte[16];
            System.arraycopy(encryptedIVAndText, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            byte[] encryptedBytes = new byte[encryptedIVAndText.length - iv.length];
            System.arraycopy(encryptedIVAndText, iv.length, encryptedBytes, 0, encryptedBytes.length);

            SecretKey secretKey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[][] getAndSplitDigest(BigInteger key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            byte[] keyBytes = key.toByteArray();

            md.update(keyBytes);
            byte[] digestBytes = md.digest();

            byte[] cypherKey = new byte[32];
            byte[] hmacKey = new byte[32];
            System.arraycopy(digestBytes, 0, cypherKey, 0, 32);
            System.arraycopy(digestBytes, 32, hmacKey, 0, 32);

            return new byte[][]{cypherKey, hmacKey};
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo no disponible", e);
        }
    }

    public static String computeHMAC(String data, byte[] key) {
        try {

            if (key.length != 32) {
                throw new IllegalArgumentException("Tamano invalido de la llave (32 bytes).");
            }

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA256");
            mac.init(keySpec);

            byte[] hmacBytes = mac.doFinal(data.getBytes());

            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyHMAC(String receivedMessage, String receivedHmac, byte[] key) {
        String computedHmac = computeHMAC(receivedMessage, key);
        return computedHmac.equals(receivedHmac);
    }

}

