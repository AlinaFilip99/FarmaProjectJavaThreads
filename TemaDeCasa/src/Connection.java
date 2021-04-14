import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket client;
    ArrayList<Fabrica> fabrici;

    public Connection(Socket aClient, ArrayList<Fabrica> f) {
        try {
            client = aClient;//portul clientului
            this.fabrici = f;//lista cu fabrici
            in = new DataInputStream(client.getInputStream());//data citita
            out = new DataOutputStream(client.getOutputStream());//data trimisa
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try { 
            String data = in.readUTF();
            boolean primit = false;//consideram ca nu am primit nimic
            if (data.equals("Trimite doza")) {//daca primim mesajul trimite doza
                for (int i = 0; i < fabrici.size(); i++) {//pentru fiecare fabrica
                    if (fabrici.get(i).take()) {//daca exista doze in fabrica
                        System.out.println("Vaccin primit " + i);
                        out.writeUTF("DA");//trimitem raspuns ca am trimis un vaccin
                        primit = true;//am primit cererea
                        break;
                    }
                }
                if (!primit) {
                    out.writeUTF("NU");
                }
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        }
        finally {
            try {
                client.close();
            } catch (IOException e) {
                 }
        }
    }
}
