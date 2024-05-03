import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Server {

    private final static String PADDING = "PKCS5Padding";


    private static long power(long a, long b, long p)
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
