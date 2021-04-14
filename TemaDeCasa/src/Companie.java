import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

public class Companie {

	static ArrayList<Fabrica> Fabrici = new ArrayList<Fabrica>();
	static ArrayList<RobotP> RobotiP = new ArrayList<RobotP>();
	static int NrRT;
	
	public static void main(String[] args) {
		Random rand=new Random();
		int doze=rand.nextInt();//alegem numarul de doze necesar in mod aleatoriu
		
		int NrF= rand.nextInt((5-2)+1)+2;//DETERMINAREA NUMARULUI DE FABRICI
		System.out.println("Nr fabrici: "+NrF);
		
		for(int i=0;i<NrF;i++) {//PENTRU FIECARE FABRICA
			int N=rand.nextInt((500-100)+1)+100;//DETERMINAREA DIMENSIUNII MATRICEI
			int NrRP=rand.nextInt((N/2-1)+1)+1;//DETERMINAREA NR DE ROBOTI
			for(int j=0;j<NrRP;j++) {
				RobotiP.add(new RobotP(NrRP, j, doze));//CREEAM O LISTA DE ROBOTI PRODUCATORI
			}//FIECARE ROBOT PRODUCE VACCINE DIN NrRP IN NrRP INCEPAND CU j PANA DEPASESTE 
			//SAU AJUNGE LA NR MAXIM DE DOZE NECESARE
			Fabrici.add(new Fabrica(i, N, RobotiP));//ADAUGAM FABRICA LA LISTA DE FABRICI
			//IN TIMP CE ASIGNAM ROBOTI LA FABRICA
			restartRoboti();//RESTARTAM LISTA DE ROBOTI PT FIECARE FABRICA
		}
		for(int i=0;i<NrF;i++) {
			Fabrici.get(i).start();//INCEPE PRODUCTIA
		}
		
		int primulPort = 6750;//portul de pornire
		String NAME = "DESKTOP-CNPQUKJ";
		NrRT=rand.nextInt((10-8)+1)+8;//alegem aleatoriu numarul de roboti transportori intre 10 si 8
		for (int Port = primulPort; Port < primulPort + NrRT; Port++) {
            RobotT robot = new RobotT(Port, Fabrici);//creeam cate un robot la fiecare port
            robot.start();//pornim roboti de transport
        }
		
		int current=0;//numarul curent de doze in companie
		while (current < doze) {//cat timp numarul de doze nu este egal cu cel din plan
            try {
                Socket s;
                for (int serverPort = primulPort; serverPort < primulPort + NrRT; serverPort++) {
                    s = new Socket(NAME, serverPort);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeUTF("Trimite doza");//trimitem o cerere pentru o doza              
                    String data = in.readUTF();  //citim raspunsul primit  
                    
                    if (data.equals("DA")) {//daca doza a fost trimisa
                        current++;//doza a fost primita deci incrementam numarul de doze din companie
                        if (current == doze) {//daca numarul de doze din companie este egal cu cel din plan
                            System.out.println("Plan indeplinit!");
                            //System.exit(0);//oprim executia
                        }
                    }
                    while (!s.isClosed()) {
                        s.close();

                    }
                }
            } catch (UnknownHostException e) {
                System.out.println("Socket:" + e.getMessage());
            } catch (EOFException e) {
                System.out.println("EOF:" + e.getMessage());
            } catch (IOException e) {
                System.out.println("readline:" + e.getMessage());
            }
        }

	}

	public static void restartRoboti() {//FUNCTIE DE GOLIRE A LISTEI DE ROBOTI
		for(int i=0;i<RobotiP.size();i++) {
			RobotiP.remove(i);
		}
	}
}
