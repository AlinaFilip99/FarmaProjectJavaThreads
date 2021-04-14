import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Fabrica extends Thread{
	int oldest=0, newest=0;//CONTOR AL CELUI MAI VECHI/NOU VACCIN ADAUGAT
	int ID;//NUMARUL FABRICII
	int Ncitire;//NUMARUL MAXIM DE ROBOTI TRANSPORTORI CARE POT CITI LA UN MOMENT DAT
	int N;//DIMENSIUNEA MATRICEI
	volatile int count=0;//NUMARUL DE DOZE IN FABRICA IN MOD CURENT
	int vaccin[];//LISTA CU VACCINURILE PRODUSE
	int matrice[][];//MATRICEA FABRICII
	ArrayList<RobotP> RobotiP= new ArrayList<RobotP>();//ROBOTI PRODUCATORI AI FABRICII
	
	ReentrantLock lock= new ReentrantLock();//ZAVORUL FABRICII
	Condition stackEmptyCondition= lock.newCondition();//CONDITIE DE GOL 
	Condition stackFullCondition= lock.newCondition();//CONDITIE DE PLIN
	
	public Fabrica(int id, int n, ArrayList<RobotP> R) {
		this.ID=id;
		this.N=n;
		this.RobotiP=R;
		matrice=new int[N][N];
		setMat();//SETAM MATRICEA PE FIECARE POZITIE CU VALOAREA 0
		Random rand= new Random();
		Ncitire=rand.nextInt((10-1)+1)+1;//DETERMINAM NUMARUL MAXIM DE ROBOTI CARE POT CITI LA UN MOMENT DAT
		vaccin=new int[Ncitire];
		for(int i=0;i<RobotiP.size();i++) {//PENTRU FIECARE ROBOT PRODUCATOR
			int row=rand.nextInt((N-1))+1;//DETERMINAM LINIA
			int col=rand.nextInt((N-1))+1;//DETERMINAM COLOANA
			while(matrice[row][col]==1) {//CAT TIMP POZITIA ESTE DEJA OCUPATA
				row=rand.nextInt((N-1))+1;//ALEGEM ALTE VALORI
				col=rand.nextInt((N-1))+1;
			}
			matrice[row][col]=1;//SETAM VALOAREA DIN MATRICE CU 1 PT POZITIA ROBOTULUI
			RobotiP.get(i).setCol(col);//II TRANSMITEM ROBOTULUI CE POZITIE OCUPA
			RobotiP.get(i).setRow(row);
			RobotiP.get(i).setFabrica(this);//SETAM FABRICA PT ROBOT (FABRICA CURENT CARE ARE LISTA DE ROBOTI)
		}
	}
	
	public void run() {

		for (int k=0;k<RobotiP.size();k++) {//INCEPEM PRODUCTIA
			RobotiP.get(k).run();
		}
		
		try {
			while(Active()==true) {//O DATA LA CATEVA SECUNDE FABRICA CERE POZITIILE ROBOTILOR
				//cat timp cel putin unul este activ
				Thread.sleep(10);
				for(int i=0;i<RobotiP.size();i++) {
					int r= RobotiP.get(i).getRow();
				    int c=  RobotiP.get(i).getCol();
				    //System.out.println("Robot: "+r+" "+c);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void append(int v, int row, int col) {//FUNCTIE PENTRU ADAUGAREA VACCINULUI PRODUS DE ROBROT IN LISTA
		try {
			lock.lock();//BLOCAM ZAVORUL
			while(count==Ncitire) {//CAT TIMP LISTA ARE VALOAREA MAXIMA
				stackFullCondition.await();//ASTEPTAM
			}
				vaccin[newest]=v;//ADAUGAM VACCINUL
				newest=(newest+1)%Ncitire;//MODIFICAM INDICATORUL PENTRU CEL MAI RECENT ELEMENT ADAUGAT
				count++;//CRESTEM VALOAREA NUMARULUI DE VACCINE
				stackEmptyCondition.signalAll();//SEMNALAM CA LISTA NU ESTE GOALA
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();//ELIBERAM ZAVORUL
		}
	}
	
	public boolean take() {//FUNCTIE PENTRU EXTRAGEREA UNEI VALORI DIN LISTA DE VACCINE
		int temp;
		try {
			lock.lock();//BLOCAM ZAVORUL
			while(count==0) {//CAT TIMP LISTA ESTE GOALA
				stackEmptyCondition.await();//ASTEPTAM
			}
			temp=vaccin[oldest];//EXTRAGEM CELA MAI VECHE VALOAREA ADAUGATA
			oldest=(oldest+1)%Ncitire;//MODIFICAM INDICATORUL CELUI MAI VECHI ELMENT ADAUGAT
			count--;//SCADEM NUMARUL DE VACCINE DIN LISTA
			stackFullCondition.signalAll();//SEMNALAM CA LISTA ESTE PLINA
			return true;
		} catch(InterruptedException e){
			e.printStackTrace();
		} finally {
			lock.unlock();//ELIBERAM ZAVORUL
		}
		return false;
	}
	
	public void setMat() {//FUNCTIE PENTRU SETAREA MATRICEI CU 0
		for(int i=0;i<N;i++)
			for(int j=0;j<N;j++) {
				matrice[i][j]=0;
			}
	}
	
	public int getValMat(int r, int c) {//FUNCTIE PENTRU CITIREA UNEI VALORI DE LA O POZITIE DATA
		return matrice[r][c];
	}
	
	public void setValMat(int r, int c, int v) {//FUNCTIE PENTRU SETAREA UNEI VALORI PE O POZITIE DATA
		matrice[r][c]=v;
	}
	
	public int getDimension() {//FUNCTIE PENTRU EXTRAGEREA DIMENSIUNII MATRICEI
		return N;
	}
	
	public int getID() {//FUNCTIE PENTRU EXTRAGEREA DIMENSIUNII MATRICEI
		return ID;
	}
	
	public boolean Active() {//functie pentru verificarea daca cel putin un robot producator mai este activ
		boolean status=false;
		for(int i=0;i<RobotiP.size();i++) {
			if(RobotiP.get(i).getActive()==1) {
				status=true;//daca cel putin un robot este activ status devine true
			}
		}
		return status;
	}

}
