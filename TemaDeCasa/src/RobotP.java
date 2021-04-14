import java.util.Random;

public class RobotP extends Thread{
	int col, row, count=0, q, r, max, min=0, active=0;
	Fabrica buffer;//FABRICA DE CARE APARTINE ROBOTUL
	public RobotP(int q,int r,int max) {
		this.r=r;
		this.q=q;
		this.max=max;
	}
	// functii de setare a datelor robotului de productie 
	//si extragere pentru afisare sau utilizare inafara clasei
	public int getActive() {
		return active;
	}
	
	public void setFabrica(Fabrica b) {
		this.buffer=b;
	}
	
	public void setRow(int r) {
		this.row=r;
	}
	
	public void setCol(int c) {
		this.col=c;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public static void asteapta(int x) {//FUNCTIE DE ASTEPTARE
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
						try {
							int i = min;
							while ((i % q) != r) { i++; } // DETERMINA PRIMUL VACCIN
							while (i <= max) {//DACA NU AM AJUNS LA NUMARUL MAXIM DE VACCINE
								active=1;//1 DACA ESTE ACTIV
								if(checkmove()==true) {//DACA SE POATE MISCA
									buffer.append(i, row, col); count++;//ADAUGA UN VACCIN IN FABRICA
									System.out.println("Robot "+r+" produced "+i+"Fabrica: "+buffer.getID());
									Thread.sleep(30/1000);//DUPA CE A PRODUS UN VACCIN ASTEAPTA 30 MILLISECUNDE
									i += q;//MODIFICA i
								}
							}
							active=0;//CAND A PRODUS TOT CE TREBUIE DEVINE 0
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					
		}
	
	public boolean checkmove() {//FUNCTIE PENTRU VERIFICAREA POSIBILITATII UNEI MISCARI
		if(col+1<buffer.getDimension() && buffer.getValMat(row, col+1)==0) {//MISCARE LA DREAPTA
			buffer.setValMat(row, col, 0);//MODIFICAM VALOAREA PE POZITIA ROBOTULUI IN 0
			col=col+1;//MODIFICAM VALOAREA COLOANEI
			buffer.setValMat(row,  col, 1);//SETAM VALOAREA 1 PE NOUA POZITIE
			return true;//RETURNAM TRUE PT CA AM FACUT O MISCARE
		} else if(row+1<buffer.getDimension() && buffer.getValMat(row+1, col)==0) {//MISCARE IN JOS
			buffer.setValMat(row, col, 0);
			row=row+1;
			buffer.setValMat(row,  col, 1);
			return true;
		} else if(col-1>=0 && buffer.getValMat(row, col-1)==0) {//MISCARE LA STANGA
			buffer.setValMat(row, col, 0);
			col=col-1;
			buffer.setValMat(row,  col, 1);
			return true;
		} else if(row-1>=0 && buffer.getValMat(row-1, col)==0) {//MISCARE IN SUS
			buffer.setValMat(row, col, 0);
			row=row-1;
			buffer.setValMat(row,  col, 1);
			return true;
		} else {//DACA NU SE POATE MISCA
			Random rand= new Random();
			try {
				Thread.sleep((rand.nextInt((50-10)+1)+10)/1000);//ASTEAPTA INTRE 10 SI 50 DE MILLISECUNDE
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

}
