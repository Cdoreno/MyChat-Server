package serverproject;

import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public final class ServerProject {

//--------------------------------------------------------Atributos-------------------------------------------------------------------//      
    private final ArrayList<Cliente> clientes;
    private OutServer os;
    private Server servidor;
    private SM sm;
    private int puertoLocal;
    private int puertoDestino;
    private String urlDestino;

//-------------------------------------------------------Constructor-----------------------------------------------------------------//      
    public ServerProject() {
        crearServerGUI();
        addOutServer(this.puertoDestino, this.urlDestino);
        addSM(this.puertoLocal);
        this.clientes = new ArrayList();
    }

//-----------------------------------------------------Main de la clase---------------------------------------------------------------//
    public static void main(String[] args) {
        ServerProject serverProject = new ServerProject();
    }

//----------------------------------------------------Métodos públicos--------------------------------------------------------------//     
    //Añado un cliente al array ya existente de clientes
    public void addCliente(Socket socket, String nombre) {
        Cliente c = new Cliente(socket, nombre, this);
        this.clientes.add(c);
        new Thread(c).start();
        System.out.println("Cliente conectado: " + nombre);
    }

    //Añado el otro server
    public void addServer(Socket socket) {
        this.servidor = new Server(socket, this);
        new Thread(servidor).start();
    }

//Tratamiento y envio de los mensajes    
    //Mando un mensaje a todos los clientes
    public void broadcastMsg(Cliente senderClient, String msg) {
        //Recorremos cada cliente y revisamos que no sea el que lo manda
        String linea = msg.substring(4);
        for (Cliente cliente : this.clientes) {
            if (cliente == null) {
                this.removeCliente(cliente);
            }
            if (cliente != senderClient) {
                cliente.txMsg(senderClient.getNombre() + " dice: " + linea);
            }
        }
        if (!(senderClient instanceof Server) && this.servidor != null) {
            relayToOutServer(msg);
        }
    }

    //Mando un mensaje al otro servidor, para que lo distribuya a sus clientes.
    public void relayToOutServer(String msg) {
        this.servidor.txMsg(msg);
    }

    //Quitar cliente desconectado del Array de clientes
    public void removeCliente(Cliente disconnectedClient) {
        this.clientes.remove(disconnectedClient);
        System.out.println("Cliente desconectado: " + disconnectedClient.getNombre());
    }

//----------------------------------------------------Métodos privados-------------------------------------------------------------//
    //Envia una señal de busqueda al servidor en caso de no tener.
    private void addOutServer(int puerto, String url) {
        this.os = new OutServer(puerto, url, this);
        this.os.start();
    }

    //Creo el servidor multithreading
    private void addSM(int puerto) {
        this.sm = new SM(puerto, this);
        this.sm.start();
    }

    //Método para crear la interfaz de configuración del servidor
    private void crearServerGUI() {
        JTextField local = new JTextField();
        JTextField destino = new JTextField();
        JTextField url = new JTextField();

        local.setText("8888");
        url.setText("localhost");
        destino.setText("8881");

        Object[] message = {
            "Puerto Local:", local,
            "Dirección destino:", url,
            "Puerto destino:", destino
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Iniciar servidor", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            this.puertoLocal = Integer.parseInt(local.getText());
            this.puertoDestino = Integer.parseInt(destino.getText());
            this.urlDestino = url.getText();
            System.out.println("Iniciando el servidor...");
        } else {
            System.out.println("Inicio de servidor cancelado.");
            System.exit(0);
        }
    }

//-------------------------------------------------------Gets & Sets------------------------------------------------------------------//
    public Server getServidor() {
        return servidor;
    }

    public void setServidor(Server servidor) {
        this.servidor = servidor;
    }
}
