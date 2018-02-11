package serverproject;

import java.net.Socket;

public class Server extends Cliente {

    public Server(Socket socket, ServerProject sp) {
        super(socket, "Servidor", sp);
    }

}
