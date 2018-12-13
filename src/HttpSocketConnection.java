



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
public class HttpSocketConnection implements Runnable{

    Socket socket= null;
    HttpSocketConnection(Socket s) {
        socket=s;
    }
      @Override
    public void run() {
        DataOutputStream dos = null;
        try {
            System.out.println("Starting new HTTP connection with "+socket.getInetAddress().toString());
            dos = new DataOutputStream(socket.getOutputStream());
            //dos.write("200 OK".getBytes());
            BufferedReader bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = bis.readLine();
            System.out.println("SERVER ["+socket.getInetAddress().toString()+"] received>"+line);
            String response="HTTP/1.1 200 OK\r\nContent-type:text/html\r\nConten-length:39\r\n\r\n";
            String entity="<html><body><h1>HOLA</h1></body></html>";
            try{
            String path= analizeRequest(line);
            
            entity= readEntity(path);
            
            }catch(HttpException400 ex400){
            }
            catch(HttpException405 ex405){
                System.err.println(ex405.getMessage());
                response="HTTP/1.1 405 METHOD NOT SUPPORTED\r\nContent-type:text/html\r\nConten-length:38\r\n\r\n";
                entity="<html><body><h1>405</h1></body></html>";
            }
            catch(HttpException505 ex505){
            }
            finally{
            
            dos.write(response.getBytes());
            dos.flush();
            dos.write(entity.getBytes());
            dos.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(HttpSocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dos.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(HttpSocketConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    /**
     * Este método analiza la lína de petición de HTTP/1.1
     * @param request
     * @return la ruta al recurso a enviar al cliente
     * @throws HttpConnection.HttpException400
     * @throws HttpConnection.HttpException405
     * @throws HttpConnection.HttpException505 
     */
    protected String analizeRequest(String request)throws HttpException400,HttpException405,HttpException505{
    
        if(!request.startsWith("GET ")) throw new HttpException405();
    
        return "/";
    }

    private String readEntity(String path) throws HttpException404{
        //Leer del fichero
        
        return "<html><body><h1>405</h1></body></html>";
        
    }
    
    public class HttpException400 extends IOException{}
    public class HttpException404 extends IOException{}
    public class HttpException405 extends IOException{}
    public class HttpException505 extends IOException{}
    
}