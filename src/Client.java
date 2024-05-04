import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.math.*;
import java.util.Base64;

public class Client {

    public static void main(String[] args){

    }

    private final static String PADDING = "PKCS5Padding";

    public static byte[] cifrar(SecretKey llave, String texto){

        byte[] textocifrado;
        try{
            Cipher cifrador = Cipher.getInstance(PADDING);
            byte[] textoClaro = texto.getBytes();

            cifrador.init(Cipher.ENCRYPT_MODE, llave);
            textocifrado = cifrador.doFinal(textoClaro);

            return textocifrado;
        } catch (Exception e){
            System.out.println("Exception: " + e.getMessage());

            return null;
        }
    }



    public static byte[] getDigest(String algorithm, byte[] buffer){
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(buffer);
            return digest.digest();

        } catch (Exception e){
            return null;
        }
    }

}

