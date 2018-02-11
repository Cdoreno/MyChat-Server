package serverproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SM extends Thread {

//--------------------------------------------------------Atributos-------------------------------------------------------------------//      
    private final int puerto;
    private final ServerProject sp;
    private BufferedReader entradaDatos;

//-------------------------------------------------------Constructor-----------------------------------------------------------------//      
    public SM(int puerto, ServerProject sp) {
        this.puerto = puerto;
        this.sp = sp;
    }

//-----------------------------------------------------Bucle del thread---------------------------------------------------------------//
    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(this.puerto);
            while (true) {
                Socket socket = ss.accept();
                detectarCliente(socket);
            }
        } catch (IOException ex) {
            System.out.println("Ha habido un problema al abrir el puerto o el puerto elegido ya está ocupado.");
        }
    }

//----------------------------------------------------Métodos privados-------------------------------------------------------------//
    //Método para añadir un cliente de tipo cliente
    private void addClient(Socket socket, String nombre) {
        if (nombre.equals("")) {
            nombre = "Anónimo";
        }
        sp.addCliente(socket, nombre);
    }

    //Método para añadir un cliente de tipo servidor
    private void addClient(Socket socket) {
        if (this.sp.getServidor() == null) {
            this.sp.addServer(socket);
            System.out.println("Servidor conectado.");
        } else {
            System.out.println("Ha intentado conectarse un nuevo servidor.");
        }
    }

    //Método para detectar el tipo de cliente
    private void detectarCliente(Socket socket) throws IOException {
        String msg = recibirMsg(socket);
        String id = msg.substring(0, 1);
        String nombre = msg.substring(1);
        if (id != null) {
            switch (id) {
                case "$":
                    addClient(socket);
                    break;
                case "&":
                    addClient(socket, nombre);
                    break;
                default:
                    System.out.println("Se ha detectado una conexión con un identificador no válido.");
                    break;
            }
        } else {
            System.out.println("Se ha cerrado la conexión antes de identificar el tipo de cliente.");
        }
    }

    //Recibir el mensaje
    private String recibirMsg(Socket socket) throws IOException {
        this.entradaDatos = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return this.entradaDatos.readLine();
    }

}
