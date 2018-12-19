
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;



/**
 *
 * @author Jose Ramon Rodríguez Rodríguez & Javier Almodovar Villacañas
 */
public class Cliente implements Runnable{
    //Variables
    private String mId="";
    
    //Contructor de cliente
    public Cliente(String id){
        mId=id;
    }
    
    //Método run
    public synchronized void run() {
   
        try{       
            //Determinamos la dirección IP de un host dando un nombre.
            InetAddress destination = InetAddress.getByName("www10.ujaen.es");
            System.out.println("-------------------\r\nIniciando cliente "+mId+"\r\n--------------------");
            
            System.out.println("Conectando con socket "+destination.toString());
            //Creamos el socket
            Socket socket = new Socket(destination,80);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.write("GET / HTTP/1.1\r\nhost:www10.ujaen.es\r\nConnection:close\r\n\r\n".getBytes());
            String line="";
            int i=0;
            while((line=input.readLine())!=null) {
                if(i==0)
                    System.out.println("<"+mId+"> "+line);
                i++;
            }   
            
        }catch (UnknownHostException e) {
            System.out.println("\tUnable to find address for");
        } catch(IOException ex){
        
            System.out.println("\tError: " +ex.getMessage()+"\r\n"+ex.getStackTrace());
        }
    }
    
}

