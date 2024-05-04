import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Scanner;
import java.math.*;
import java.util.Base64;

public class Client {

    public static void main(String[] args){
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

    public final static String PADDING = "PKCS5Padding";
    public final static int port = 11111;
    public final static String serverIp = "localhost";


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

