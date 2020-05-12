import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

class Rectangle_BranchBound{
	private int[] aVertices;
	private int iNumVertices = 0;
	private int iGuarded = 0; //How many guards see this rectangle
	private int iSub = 0;

	Rectangle_BranchBound(int iNumVertices){
		aVertices = new int[iNumVertices];
	}
    
    Rectangle_BranchBound(Rectangle_BranchBound oRectangle){
        this.aVertices = new int[oRectangle.GetVertices().length];
        for(int i = 0; i < oRectangle.GetVertices().length; i++){
            this.aVertices[i] = oRectangle.GetVertices()[i];
        }
		this.iNumVertices = oRectangle.GetVertices().length;
		this.iGuarded = oRectangle.GetGuarded();
		this.iSub = oRectangle.GetSub();
	}
    
	public void AddVertice(int iVertice){
		this.aVertices[iNumVertices] = iVertice;
		iNumVertices++;
	}

	public void AddGuard(){
		this.iGuarded++;
	}

	public void DecreaseGuard(){
		this.iGuarded--;
	}

	public int GetGuarded(){
		return this.iGuarded;
	}

	public int[] GetVertices(){
		return this.aVertices;
	}
	
	public int GetSub() {
		return this.iSub;
	}
	
	public void SetSub(int sub) {
		this.iSub = sub;
	}
}

class Vertice_BranchBound{
	private static int iNumVertices = 0;
	private int id;
	private LinkedList<Integer> lRectangles;
	private int iNumRectangles = 0;
	private int x;
	private int y;
	private boolean bHasGuard = false;

	Vertice_BranchBound(int x, int y){
		lRectangles = new LinkedList<Integer>();
		this.x = x;
		this.y = y;
		this.id = iNumVertices;
		iNumVertices++;
	}

	Vertice_BranchBound(Vertice_BranchBound oVertice){
		this.id = oVertice.GetId();
		this.lRectangles = new LinkedList<Integer>();
		for(int i = 0; i < oVertice.GetRectangles().size(); i++){
			this.lRectangles.addLast(oVertice.GetRectangles().get(i));
			this.iNumRectangles++;
		}
		this.x = oVertice.GetX();
		this.y = oVertice.GetY();
		this.bHasGuard = oVertice.GetGuarded();
	}

	

	public void AddRectangle(int iRectangle){
		this.lRectangles.addLast(iRectangle);
		iNumRectangles++;
	}

	public boolean CompareToXY(int x, int y){
		if(this.x == x && this.y == y){
			return true;
		}else{
			return false;
		}
	}

	
	public int GetX(){ return this.x; }
	public int GetY(){ return this.y; }
	public LinkedList<Integer> GetRectangles(){ return this.lRectangles; }
	public int GetId(){ return this.id; }
	public boolean GetGuarded(){ return this.bHasGuard; }
	public void SetGuarded(Rectangle_BranchBound[] aRectangles){ 
		this.bHasGuard = true; 
		for(int i = 0; i < this.lRectangles.size(); i++){
			int iPositionRectangle = this.lRectangles.get(i);
			aRectangles[iPositionRectangle].AddGuard();
		}
	}
	public void UnsetGuarded(Rectangle_BranchBound[] aRectangles){ 
		this.bHasGuard = false; 
		for(int i = 0; i < this.lRectangles.size(); i++){
			int iPositionRectangle = this.lRectangles.get(i);
			aRectangles[iPositionRectangle].DecreaseGuard();
		}
	}
}

class State_BranchBound implements Comparable<State_BranchBound>{
	private LinkedList<Vertice_BranchBound> lGuardedVertices;
	private Rectangle_BranchBound[] aRectangles;
	private int numOfGuards = 0;
	private int maxVerticeIndex = 0;
	private int fitness = 0;

	State_BranchBound(LinkedList<Vertice_BranchBound> lVertices, Rectangle_BranchBound[] aRectangles){
		this.lGuardedVertices = new LinkedList<>();
		for(int i = 0; i < lVertices.size(); i++){
			this.lGuardedVertices.addLast(new Vertice_BranchBound(lVertices.get(i)));
			if(lVertices.get(i).GetGuarded()) {
				this.numOfGuards++;
				maxVerticeIndex = i;
			}
		}
		
		this.aRectangles = new Rectangle_BranchBound[aRectangles.length];
		for(int i = 0; i < aRectangles.length; i++){
			this.aRectangles[i] = new Rectangle_BranchBound(aRectangles[i]);
		}
		
		this.fitness = this.GetFitness();
	}
    
    public State_BranchBound GetNextState(){
		State_BranchBound oNextState = new State_BranchBound(this.lGuardedVertices, this.aRectangles);

		for(int i = 0; i < this.lGuardedVertices.size(); i++ ) {
			if(!this.lGuardedVertices.get(i).GetGuarded()) {
				Vertice_BranchBound oVertice = this.lGuardedVertices.get(i);
				oVertice.SetGuarded(this.aRectangles);
				State_BranchBound oState = new State_BranchBound(this.lGuardedVertices, this.aRectangles);
				if(oState.GetFitness() > oNextState.GetFitness()){
					oNextState = new State_BranchBound(this.lGuardedVertices, this.aRectangles);
				}
				oVertice.UnsetGuarded(this.aRectangles);
			}
		}

		return oNextState;
	}
    
	public LinkedList<State_BranchBound> GetNextStates(){
		LinkedList<State_BranchBound> lNextStates = new LinkedList<>();

		for(int i = this.maxVerticeIndex + 1; i < this.lGuardedVertices.size(); i++ ) {
			if(!this.lGuardedVertices.get(i).GetGuarded()) {
				this.lGuardedVertices.get(i).SetGuarded(this.aRectangles);
				State_BranchBound nState = new State_BranchBound(this.lGuardedVertices, this.aRectangles);
				lNextStates.addLast(nState);
				this.lGuardedVertices.get(i).UnsetGuarded(this.aRectangles);
			}
		}
		
		return lNextStates;
	}

	public int GetFitness(){
		int iFitness = 0;
		if(this.fitness == 0) {
			for(int i = 0; i < this.aRectangles.length; i++){
				int iGuards = this.aRectangles[i].GetGuarded();
				if(iGuards > 0){
					iFitness += this.aRectangles[i].GetSub() * 3 - (iGuards - 1);
				}
			}
		}else {
			iFitness = this.fitness;
		}
		return iFitness;
	}

	public LinkedList<Vertice_BranchBound> GetGuardedVertices(){
		return this.lGuardedVertices;
	}
	
	public int GetNumOfGuards() {
		return this.numOfGuards;
	}

	public Rectangle_BranchBound[] GetRectangles(){
		return this.aRectangles;
	}

	public void PrintRectangles(){
		for(int i = 0; i < this.aRectangles.length; i++){
			System.out.println(this.aRectangles[i].GetGuarded());
		}
	}

	public void PrintVertices(){
		for(int i = 0; i < this.lGuardedVertices.size(); i++){
			System.out.println(this.lGuardedVertices.get(i).GetX());
		}
	}

	@Override
	public int compareTo(State_BranchBound o) {
		if(this.GetFitness() > o.GetFitness()) {
			return 1;
		}
		if(this.GetFitness() < o.GetFitness()) {
			return -1;
		}
		if(this.GetFitness() == o.GetFitness()) {
			if(this.GetNumOfGuards() < o.GetNumOfGuards()) {
				return 1;
			}
			if(this.GetNumOfGuards() > o.GetNumOfGuards()) {
				return -1;
			}
			if(this.GetNumOfGuards() == o.GetNumOfGuards()) {
				return 0;
			}
		}
		return 0;
	}
}

class Field_BranchBound{
	int[][] field;
	char[][] cField;
	int x;
	int y;
	
	Field_BranchBound(LinkedList<Vertice_BranchBound> vertices, Rectangle_BranchBound[] rectangles){
		int maxX = -1;
		int maxY = -1;
		for(int i = 0; i < vertices.size(); i++) {
			if(vertices.get(i).GetX() > maxX) {
				maxX = vertices.get(i).GetX();
			}
			if(vertices.get(i).GetY() > maxY) {
				maxY = vertices.get(i).GetY();
			}
		}
		this.field = new int[maxX * 2 + 1][maxY * 2 + 1];
		this.cField = new char[maxX * 2 + 1][maxY * 2 + 1];
		this.x = maxX * 2;
		this.y = maxY * 2;
		for(int i = 0; i < vertices.size(); i++) {
			int v = 1;
			char c = 'o';
			if(vertices.get(i).GetGuarded()) {
				v = 2;
				c = 'x';
			}
			this.field[vertices.get(i).GetX() * 2][vertices.get(i).GetY() * 2] = v;
			this.cField[vertices.get(i).GetX() * 2][vertices.get(i).GetY() * 2] = c;
		}
		
		for(int count = 0; count < rectangles.length; count++) {
			Rectangle_BranchBound rec = rectangles[count];
			int[] recVert = rec.GetVertices();
			int maxRecX = -1;
			int minX = 9999;
			int maxRecY = -1;
			int minY = 9999;
			for(int i = 0; i < recVert.length; i++) {
				int x = vertices.get(recVert[i]).GetX() * 2;
				int y = vertices.get(recVert[i]).GetY() * 2;
				if(x > maxRecX) {
					maxRecX = x;
				}
				if(x < minX) {
					minX = x;
				}
				if(y > maxRecY) {
					maxRecY = y;
				}
				if(y < minY) {
					minY = y;
				}
			}
			int i;
			int j;
			for(i = minX; i < maxRecX; i++) {
				if(this.field[i][minY] != 1 && this.field[i][minY] != 2) {
					this.cField[i][minY] = '-';
				}
				if(this.field[i][maxRecY] != 1 && this.field[i][maxRecY] != 2) {
					this.cField[i][maxRecY] = '-';
				}
			}
			for(i = minY; i < maxRecY; i++) {
				if(this.field[minX][i] != 1 && this.field[minX][i] != 2) {
					this.cField[minX][i] = '|';
				}
				if(this.field[maxRecX][i] != 1 && this.field[maxRecX][i] != 2) {
					this.cField[maxRecX][i] = '|';
				}
			}
		}
		
	}
	public void PrintField() {
		for(int i = this.y; i >= 0; i--) {
			for(int j = 0; j <= this.x; j++) {
				System.out.print(this.cField[j][i]);
			}
			System.out.println();
		}
	}
}

public class Guardas_BranchBound {
	
	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		int iNumInstancias = reader.nextInt();
		for(int numInstancia = 0; numInstancia < iNumInstancias; numInstancia++){
			Rectangle_BranchBound[] aRectangles;
			LinkedList<Vertice_BranchBound> lVertices = new LinkedList<Vertice_BranchBound>();

			int iNumRectangles = reader.nextInt();
			
			aRectangles = new Rectangle_BranchBound[iNumRectangles];
			for (int numRectangle = 0; numRectangle < iNumRectangles ; numRectangle++ ) {
				int iRectangle = reader.nextInt() - 1;
				int iNumVertices = reader.nextInt();
				
				aRectangles[iRectangle] = new Rectangle_BranchBound(iNumVertices);
				
				for(int numVertices = 0; numVertices < iNumVertices; numVertices++){
					int x = reader.nextInt();
					int y = reader.nextInt();
					int iVerticePosition = -1;

					for(int i = 0; i < lVertices.size(); i++){
						if(lVertices.get(i).CompareToXY(x, y)) {
							iVerticePosition = i;
							break;
						}
					}

					Vertice_BranchBound oVertice;
					if(iVerticePosition == -1){
						oVertice = new Vertice_BranchBound(x,y);
						iVerticePosition = lVertices.size();
						lVertices.addLast(oVertice);	
					} else {
						oVertice = lVertices.get(iVerticePosition);
					}
					
					lVertices.get(iVerticePosition).AddRectangle(iRectangle);
					aRectangles[iRectangle].AddVertice(iVerticePosition);
				}
			}
			
			//Subconjunto, se número lido fôr 0 então termina a leitura do subconjunto
			int iNumberOfRectanglesToCheck = 0;
			int iRectangle = reader.nextInt();
			boolean sub = false;
			while(iRectangle != 0) {
				aRectangles[iRectangle - 1].SetSub(1);
				iNumberOfRectanglesToCheck++;
				sub = true;
				iRectangle = reader.nextInt();
			}
			if(!sub) {
				iNumberOfRectanglesToCheck = iNumRectangles;
				for (int k = 0; k < aRectangles.length; k++) {
					aRectangles[k].SetSub(1);
				}
			}
			//
			
			//Insert you algorithm here -->
			State_BranchBound state = new State_BranchBound(lVertices,aRectangles);
			LinkedList<State_BranchBound> stateList = new LinkedList<State_BranchBound>();
			LinkedList<State_BranchBound> solutionsList = new LinkedList<State_BranchBound>();
			int maxFitness = 0;
			stateList.addLast(state);
			for(int i = 0; i < stateList.size(); i++) {
				State_BranchBound oState = stateList.get(i);
				LinkedList<State_BranchBound> newStatesList = oState.GetNextStates();
				
				if(solutionsList.isEmpty()) {
					solutionsList.addLast(oState);
				}else {
					if(solutionsList.get(0).compareTo(oState) == -1) {
						solutionsList = new LinkedList<State_BranchBound>();
						solutionsList.addLast(oState);
					}
					if(solutionsList.get(0).compareTo(oState) == 0) {
						solutionsList.addLast(oState);
					}
				}
				
				newStatesList.sort(null);
				
				if(i + 1 >= stateList.size()) {
					stateList.addAll(newStatesList);
				}else{
					if(!newStatesList.isEmpty()) {
						if(stateList.get(i+1).GetNumOfGuards() + 2 == newStatesList.get(0).GetNumOfGuards()) {
							stateList.addAll(i + 1, newStatesList);
						}else{
							//Vê qual é o último nó da profundidade acima que será o maior porque foi ordenado
							int j = 1;
							while(i + j < stateList.size() && stateList.get(i + j).GetNumOfGuards() + 1 == newStatesList.get(0).GetNumOfGuards()) {
								j++;
							}
							State_BranchBound compareState = stateList.get(i + j - 1);
							//
							while(!newStatesList.isEmpty() && newStatesList.getFirst().compareTo(compareState) == -1) {
								newStatesList.removeFirst();
							}
							stateList.addAll(i + 1, newStatesList);
						}
					}
				}
			}
			for(int j = 0; j < solutionsList.size(); j++) {
				state = solutionsList.get(j);
				int numGuard = 1;
				System.out.println("Instance " + (numInstancia + 1) + ":");
				System.out.println("State_BranchBound Fitness: " + iNumberOfRectanglesToCheck * 3 + " / " + state.GetFitness());
				for(int i = 0; i < state.GetGuardedVertices().size(); i++) {
					if(state.GetGuardedVertices().get(i).GetGuarded()) {
						System.out.println("Guarda " + numGuard + ": ( " + state.GetGuardedVertices().get(i).GetX() + ", " + state.GetGuardedVertices().get(i).GetY() + " )");
						numGuard++;
					}
				}
				System.out.println();
				System.out.println("Field_BranchBound: ");
				
				Field_BranchBound f = new Field_BranchBound(state.GetGuardedVertices(), state.GetRectangles());
				f.PrintField();
			}
			
			//
			
			//Print Field solution
//			int numGuard = 1;
//			System.out.println("Instance " + (numInstancia + 1) + ":");
//			System.out.println("State_BranchBound Fitness: " + iNumberOfRectanglesToCheck * 3 + " / " + state.GetFitness());
//			for(int i = 0; i < state.GetGuardedVertices().size(); i++) {
//				if(state.GetGuardedVertices().get(i).GetGuarded()) {
//					System.out.println("Guarda " + numGuard + ": ( " + state.GetGuardedVertices().get(i).GetX() + ", " + state.GetGuardedVertices().get(i).GetY() + " )");
//					numGuard++;
//				}
//			}
//			System.out.println();
//			System.out.println("Field_BranchBound: ");
//			
//			Field_BranchBound f = new Field_BranchBound(state.GetGuardedVertices(), state.GetRectangles());
//			f.PrintField();
			//
		}


		System.out.println("Done");
	}

}
