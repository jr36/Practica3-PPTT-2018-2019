
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Joserra
 */
public class MainServer {

   //Mensaje de entrada
    public static final String Mensaje="Servidor HTTP/1.1 iniciando...";
    
    //Establecemos servidor Socket como nulo
    private static ServerSocket server=null;    
    
    public static void main(String[] args) throws IOException {
        
        try{
            server = new ServerSocket(80);
            System.out.println(Mensaje);
            while(true){
                Socket s=server.accept();
                System.out.println("Conexion entrante desde: "+s.getInetAddress().toString());
               HttpSocketConnection conn=new HttpSocketConnection(s);
               new Thread((Runnable) conn).start();  
            }
        }catch (UnknownHostException ex){
                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (IOException ex){
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
