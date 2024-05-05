import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
public class Server {

    public static void main(String[] args) throws InterruptedException {
        int serverNumber=0;
        while(true){
            serverNumber++;
            ServerThread serverThread = new ServerThread(Server.port, serverNumber);
            serverThread.start();
            serverThread.join();
        }

    }
    public final static String PADDING = "PKCS5Padding";
    public final static int port = 12345;
    public final static int G=2;
    public final static BigInteger P= new BigInteger(("00:a0:79:33:f3:ba:d3:e0:12:c4:69:56:6f:99:61:76:c5:b3:f0:f7:8c:35:8e:b6:34:68:24:10:fa:59:79:c0:9a:25:a4:86:e1:db:f5:12:e8:62:5f:b1:ea:40:cf:42:fe:1e:76:7a:88:b8:be:fd:71:06:d7:d6:61:96:45:96:86:4b:ec:26:1a:71:ea:5d:35:20:2f:6b:d6:a2:d2:3a:86:4b:6f:cc:0f:fe:d0:16:9b:d1:89:48:49:c3:21:a3:37:3f:0a:d3:f3:49:ca:5c:3a:02:cd:ee:f7:cf:c1:61:8b:8d:e2:aa:69:c9:1b:8b:4e:1f:12:4d:64:9e:1a:c9:df").replace(":",""), 16);

    public static long power(long a, long b, long p)
    {
        if (b == 1)
            return a;
        else
            return (((long)Math.pow(a,b)) %p);
    }

    public static byte[] descifrar(SecretKey llave, byte[] texto){
        byte[] textoClaro;

        try{
            Cipher cifrador = Cipher.getInstance(PADDING);
            cifrador.init(Cipher.DECRYPT_MODE, llave);
            textoClaro = cifrador.doFinal(texto);
        } catch (Exception e){
            System.out.println("Excepcion: " + e.getMessage());
            return null;
        }
        return textoClaro;
    }


}
