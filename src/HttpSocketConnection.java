



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Jose Ramón Rodríguez Rodríguez & Javier Almodovar Villacañas
 */
public class HttpSocketConnection implements Runnable{
public static final String HTTP_Ok="200"; //Mensaje de estado correcto
    
    //Declaramos los cuatro tipos de errores que nos saldran en la comprobacion telnet
    public static final String HTTP_Bad_Request="400"; //Peticion incorrecta
    public static final String HTTP_Not_Found="404"; //Recurso no encontrado
    public static final String HTTP_Method_Not_Allowed="405"; //Metodo no soportado
    public static final String HTTP_Version_Not_Supported="505"; //Version HTTP no valida, solo funciona HTTP 1.1
    
    
    Socket socket=null;
    
    /**
     * Se recibe el socket conectado con el cliente
     * @param s Socket conectado con el cliente
     */
    public HttpSocketConnection(Socket s){
        socket = s;
    }
    
    
    public void run() {
        String request_line="";
        BufferedReader input;
        DataOutputStream output;
        

        //Definicion de las cabeceras de respuesta
        String connection="";
        String contentType="";
        String server="";
        Date fecha = Fecha();
        String resourceFile="";
        String allow="";
        String contentLength="";
        
       
        try {
            byte[] outdata=null;
            String outmesg="";
            byte[] Recurso=null;
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new DataOutputStream(socket.getOutputStream());
           
            
            request_line= input.readLine();  
            String parts[]=request_line.split(" ");//String parts separa la peticion en tres partes por un espacio-->Ejemplo [GET]" "[/index.html]" "[HTTP/1.1]
                
                if(request_line.startsWith("GET ")){
                  if(parts.length==3)//Si la peticion contiene tres partes
                    {//Se comprueba la version
                        String[] res;
                        res=parts[2].split("/");//Guardamos en String res la parte de la peticion que incluye la version HTTP y separa [HTTP]/[1.1] a traves del "/"
                        if(res[1].equals("1.1")){ //Solo comprueba que version HTTP sea la 1.1. No permite ninguna otra version. En la peticion no es necesario las mayusculas al escribir http/1.1
                             if(parts[1].equalsIgnoreCase("/")){//equalsIgnoreCase ignora que en la peticion se haya escrito /index.html, solo importa el index.html
                             resourceFile="index.html";
                        }else{
                            //parts[1] -->Tipo de contenido
                            resourceFile=parts[1];
                        }
                        //Content-type
                        //Separaremos parte1 y tipo de contenido a traves del "." de tipo de contenido
                      
                        String[] Separar;
                    
                        if(!parts[1].equals("/")){//Comparamos que part[1] sea diferente a "/"
                        
                        Separar= parts[1].split("\\.");//Para poder separar con punto necesitamos poner una barra "\\."
                        
                        if(Separar[1].equals("jpg")){
                            contentType="\r\nContent-type: image/jpeg\r\n";
                        }else if(Separar[1].equals("txt")){
                            contentType="\r\nContent-type: text/plain\r\n";
                        }else{
                            //Como solo podremos disponibles 3, elegimos que si no es una imagen o texto plano, sea:
                            contentType="\r\nContent-type: text/html\r\n";
                        }
                        }else{
                            contentType="\r\nContent-type: text/html\r\n";
                        }
                          //Hemos tenido que crear una nueva variable porque nos sobreescribía outdata
                       Recurso=leerRecurso(resourceFile);
                        
             
                        
                        if(Recurso==null)    {
                            outmesg="HTTP/1.1 404\r\n\r\n<html><body><h1>No encontrado</h1></body></html>";
                            outdata=outmesg.getBytes();    
                        }else{
                            //Si encuentra el recurso
                            outmesg="HTTP/1.1 200";
                            outdata=outmesg.getBytes();
                        }
             
                    }else{
                        outmesg="HTTP/1.1 505\r\n\r\n<html><body><h1>HTTP Version Not Supported</h1></body></html>";
                        outdata=outmesg.getBytes();
                        }
                    }else{
                      outmesg="HTTP/1.1 400\r\n\r\n<html><body><h1>Problema en el cliente</h1></body></html>";
                      outdata=outmesg.getBytes();
                    }
                }else{
                    outmesg="HTTP/1.1 405\r\n\r\n<html><body><h1>Metodo no permitido</h1></body></html>";
                    outdata=outmesg.getBytes(); 
                }
                    
                
                   do{
                request_line= input.readLine();        
                //Escribe host...
                System.out.println(request_line);
            }while(request_line.compareTo("")!=0);
            //CABECERAS.
            //Las cabeceras se mandan después de la línea de estado.
            
            if(outmesg.equals("HTTP/1.1 200")){
            //Cabecera content-length  -->El tamaño del contenido de la petición en bytes
             contentLength="Content-Length: "+ Recurso.length + " \r\n";  
             
             //Cabecera CONNECTION
             connection= "Connection: close \r\n"; //Para informar que no admite conexiones persistentes
            
            //Cabecera DATE
           
            String cabeceraFecha="Date: "+ fecha + " \r\n";
            
            
            //CABECERA ALLOW
            allow="Allow: #GET\r\n"; //El propósito es informar al destinatario de los métodos de solicitud válidos. Solo sera valido metodo GET
            
           //Cabecera SERVER
           server="Server: Servidor HTTP 1.1\r\n\r\n"; //Muestra el tipo de servidor HTTP empleado. Siempre permite HTTP 1.1
            
            //Linea de estado
               output.write(outdata);
               
               //Escribimos cabeceras
               
               output.write(contentType.getBytes()); 
               output.write(contentLength.getBytes());
               output.write(connection.getBytes());
               output.write(cabeceraFecha.getBytes());
               output.write(allow.getBytes());
               output.write(server.getBytes());
             
            if(Recurso!=null){
               //Recurso
               output.write(Recurso);
           } 
           }else{
               
                //Linea de estado
               output.write(outdata);
               
               if(Recurso!=null){
               //Recurso
               output.write(Recurso);
           } 
           }
            input.close();
            output.close();
            socket.close();
        
        } catch (IOException e) {
            System.err.println("Exception" + e.getMessage());
        }
        }

    
    private Date Fecha(){//Con este metodo obtenemos la fecha actual
        java.util.Date fecha = new Date();
        return fecha;
    }
    
    
    private byte[] leerRecurso(String resourceFile){        
        //./ es para el directorio
        File f= new File("./"+resourceFile);
        byte[] bytesArray = null;
        try{
       FileInputStream fis = new FileInputStream (f);
       bytesArray = new byte[(int) f.length()];
       fis.read(bytesArray);
       BufferedInputStream bis = new BufferedInputStream(fis);
       bis.read(bytesArray, 0 , bytesArray.length);

        }catch(IOException e) {
            e.printStackTrace();
        }  
       return bytesArray;
    }
    
    
    
    
}

