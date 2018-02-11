package serverproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente implements Runnable {

//--------------------------------------------------------Atributos-------------------------------------------------------------------//      
    protected BufferedReader entradaDatos;
    private final String nombre;
    protected PrintWriter salidaDatos;
    protected Socket socket;
    protected ServerProject sp;

//-------------------------------------------------------Constructor-----------------------------------------------------------------//     
    public Cliente(Socket socket, String nombre, ServerProject sp) {
        this.nombre = nombre;
        this.socket = socket;
        this.sp = sp;
    }

//-----------------------------------------------------Bucle del thread---------------------------------------------------------------//    
    @Override
    public void run() {
        try {

            Boolean fin = false;
            while (!fin) {

                if (this.socket != null) {
                    tratarMsg();
                } else {
                    System.out.println("Se ha cerrado el socket.");
                }

            }

            this.socket.close();
        } catch (IOException ex) {
            System.out.println("Se ha cerrado la conexión o ha habido un problema al recibir el mensaje.");
        }

    }
    //----------------------------------------------------Métodos públicos--------------------------------------------------------------//  
    //Enviar mensaje al cliente.

    public void txMsg(String msg) {
        try {
            if (!this.socket.isClosed()) {
                this.salidaDatos = new PrintWriter(this.socket.getOutputStream(), true);
                this.salidaDatos.println(msg);
            }
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

//----------------------------------------------------Métodos privados-------------------------------------------------------------//
    //Método para recibir mensajes del cliente
    private String recibirMsg() throws IOException {
        this.entradaDatos = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        return entradaDatos.readLine();
    }

    //Método para tratar el mensaje y enviarlo
    private void tratarMsg() throws IOException {
        String msg = recibirMsg();
        String id = msg.substring(0, 3);
        if (id != null) {
            switch (id) {
                case "msg":
                    this.sp.broadcastMsg(this, msg);
                    System.out.println(msg);
                    break;
                case "bye":
                    this.sp.removeCliente(this);
                    break;
            }
        } else {
            this.sp.removeCliente(this);
        }
    }

//-------------------------------------------------------Gets & Sets------------------------------------------------------------------//    
    public String getNombre() {
        return nombre;
    }

    public Socket getSocket() {
        return this.socket;
    }

}
