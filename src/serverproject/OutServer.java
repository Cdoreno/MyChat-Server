package serverproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutServer extends Thread {

//--------------------------------------------------------Atributos-------------------------------------------------------------------//      
    private final String id;
    private final int puerto;
    private PrintWriter salidaDatos;
    private final ServerProject sp;
    private final String url;

//-------------------------------------------------------Constructor-----------------------------------------------------------------//      
    public OutServer(int puerto, String url, ServerProject sp) {
        this.url = url;
        this.puerto = puerto;
        this.sp = sp;
        this.id = "$";
    }

//-----------------------------------------------------Bucle del thread---------------------------------------------------------------//    
    @Override
    public void run() {
        while (true) {
            esperarTiempo();
            if (this.sp.getServidor() == null) {
                try {
                    Socket socket = new Socket(this.url, this.puerto);
                    openIO(socket);
                    this.sp.addServer(socket);
                    System.out.println("Servidor conectado.");
                } catch (IOException ex) {
                    System.out.println("No se encuentra otro servidor. \nVolviendo a conectar");
                }
            } else {
                if (this.sp.getServidor().getSocket().isClosed()) {
                    System.out.println("Se ha caido la conexión con el otro servidor.");
                    this.sp.setServidor(null);
                }
            }
        }
    }

//----------------------------------------------------Métodos privados-------------------------------------------------------------//
    //Abrir comunicación con el otro servidor
    private void openIO(Socket socket) {
        try {
            this.salidaDatos = new PrintWriter(socket.getOutputStream(), true);
            this.salidaDatos.println(this.id);
        } catch (IOException ex) {
            System.out.println("No se encuentra otro servidor. \nVolviendo a conectar");
        }
    }

    //Método para hacer esperar al Thread
    private void esperarTiempo() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(OutServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
