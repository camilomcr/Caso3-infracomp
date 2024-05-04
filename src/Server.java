import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.*;
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
