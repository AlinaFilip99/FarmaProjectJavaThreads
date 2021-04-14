import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RobotT extends Thread{
	private ServerSocket listen; 
    private ArrayList<Fabrica> fabrici;

    public RobotT(int Port, ArrayList<Fabrica> f) {
        this.fabrici = f;//creeam robotul de tranposport cu portul corespunzator
        try {
            listen = new ServerSocket(Port);
        } catch (IOException ex) {
            Logger.getLogger(RobotT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            Socket client;
            try {
                client = listen.accept();//acepta cererea
                Connection c = new Connection(client, this.fabrici);
                c.start();//initializeaza o conexiune
            } catch (IOException e) {
                System.out.println("Listen socket:" + e.getMessage());
            }
        }
    }

}
