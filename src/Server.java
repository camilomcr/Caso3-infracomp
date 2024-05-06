import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        ServerThread serverThread = new ServerThread(Server.port, 0);
        serverThread.start();

    }

    public final static int keySize = 256;
    public final static int port = 12345;
    public static final BigInteger G = BigInteger.valueOf(2);
    public final static BigInteger P= new BigInteger(("00:a0:79:33:f3:ba:d3:e0:12:c4:69:56:6f:99:61:76:c5:b3:f0:f7:8c:35:8e:b6:34:68:24:10:fa:59:79:c0:9a:25:a4:86:e1:db:f5:12:e8:62:5f:b1:ea:40:cf:42:fe:1e:76:7a:88:b8:be:fd:71:06:d7:d6:61:96:45:96:86:4b:ec:26:1a:71:ea:5d:35:20:2f:6b:d6:a2:d2:3a:86:4b:6f:cc:0f:fe:d0:16:9b:d1:89:48:49:c3:21:a3:37:3f:0a:d3:f3:49:ca:5c:3a:02:cd:ee:f7:cf:c1:61:8b:8d:e2:aa:69:c9:1b:8b:4e:1f:12:4d:64:9e:1a:c9:df").replace(":",""), 16);


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
