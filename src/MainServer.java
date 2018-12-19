
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 
  
 * @author Jose Ramón Rodríguez Rodríguez & Javier Almodovar Villacañas
 */
public class MainServer {
    //Mensaje de entrada
    public static final String MSG_HANDSHAKE="Servidor HTTP/1.1 iniciándose...";
    //Se establece servidor SOCKET como nulo
    private static ServerSocket mMainServer= null;
    
     
    public static void main(String[] args)  throws FileNotFoundException {//añadimos excepcion
       
        try {
            
            mMainServer= new ServerSocket(80);
            System.out.println(MSG_HANDSHAKE);
            while(true) {
                Socket socket =mMainServer.accept();
                 System.out.println("Conexión entrante desde: "+socket.getInetAddress().toString());
                 //Creación de hebra
                 Thread connection= new Thread(new HttpSocketConnection(socket));
                 connection.start();
            }
        } catch (java.net.BindException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex2){
            System.err.println(ex2.getMessage());
        }
    }  
}