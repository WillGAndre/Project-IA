import java.util.*;
import java.lang.*;

class Coor {
  int x;
  int y;

  Coor(int x, int y) {
    this.x = x;
    this.y = y;
  }

  int getX() {
    return x;
  }

  int getY() {
    return y;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + x;
    result = 31 * result + y;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    Coor e = null;
    if (obj instanceof Coor)
      e = (Coor) obj;

    if (this.getX() == e.getX() && this.getY() == e.getY())
      return true;
    else
      return false;
  }
}

class recP1 {

  /*
    Id -> Identificador do Retangulo
    Coor -> Coordenadas do respetivo vertice
    Ocorr -> Ocorrencia
  */

  static LinkedList<LinkedList> list_list;
  static LinkedList<Coor> all_Points; /* Guardamos os pontos numa lista
                                      de listas.
                                      */
  static Hashtable<Integer,LinkedList> id_Coor; // key -> id; Value -> Lista (Coor)
  static Hashtable<Coor,Integer> coor_Ocorr; // key -> coor; Value -> Ocorr

  static int numRec;
  static int numInstan;
  static boolean arr_Rec[];

  static LinkedList<Coor> guards;
  static int maxGuards;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    list_list = new LinkedList<>();
    all_Points = new LinkedList<>();
    coor_Ocorr = new Hashtable<>();
    id_Coor = new Hashtable<>();
    guards = new LinkedList<>();

    numInstan = in.nextInt();
    LinkedList<Coor> res_insta[] = new LinkedList[numInstan+1];

    for (int index = 1; index <= numInstan; index++) {
      read_Input(in);

      arr_Rec = new boolean[numRec];
      for (int i = 0; i < numRec; i++) {arr_Rec[i] = false;}

      int counter = numRec; // Number of Rectangles

      // /* Comment off if numInstan > 1
      System.out.println("Definir subconjunto? (y or n)");
      char ch = in.next().charAt(0);
      if (ch=='y') {counter = check_Inst(in);}
      // */
      maxGuards = roundUp(counter,3);

      readList();
      garbageColl();

      res_insta[index] = readGuards();

      list_list.clear(); id_Coor.clear(); // Garbage Collection
      guards.clear();
      coor_Ocorr.clear();
      System.out.println("*" + " " + "Instan :" + " " + index + " " + "*");
      print_Guards(res_insta[index]);
    }
  }

  static int check_Inst(Scanner in) {
    // Hard Reset
    for (int i = 0; i < numRec; i++) {arr_Rec[i] = true;}
    int id_Rec = 0;
    int counter_insta = 0;
    System.out.println("Rec Id ?");
    System.out.println("Number of rectangles: " + numRec);
    System.out.println("If id_Rec = 0 -> Quit");
    while (id_Rec != -1) {
      id_Rec = in.nextInt();
      id_Rec -= 1; // arr_Rec :: [0;numRec-1]
      if (id_Rec >= 0 && id_Rec < numRec) {
        arr_Rec[id_Rec] = false; // Quero cobrir
        counter_insta++;
      }
    }
    return counter_insta;
  }

  static void read_Input(Scanner in) {
    numRec = in.nextInt();

    for (int i = 1; i <= numRec; i++) {
      int id = in.nextInt();
      int numVert = in.nextInt();
      LinkedList<Coor> temp = new LinkedList<>();
      for (int j = 1; j <= numVert; j++) {
        int x = in.nextInt();
        int y = in.nextInt();
        Coor p = new Coor(x,y);

        all_Points.add(p);
        temp.add(p);
      }
      id_Coor.put(id, temp);
      list_list.add(temp);
    }
  }

  static void readList() {
      for (int i = 0; i < list_list.size(); i++) {
        if (arr_Rec[i] == false) { // Rec que ainda nao foi visitado
          LinkedList<Coor> list = list_list.get(i);
          for (int j = 0; j < list.size(); j++) {
            Coor c1 = list.get(j);
            int occr_c1 = 0;

              for (int l = 0; l < list_list.size(); l++) {
                if (arr_Rec[l] == false) { // Rec que ainda nao foi visitado
                  LinkedList<Coor> list2 = list_list.get(l);
                  for (int k = 0; k < list2.size(); k++) {
                    Coor c2 = list2.get(k);

                    if ((c1.getX() == c2.getX()) && (c1.getY() == c2.getY())) {
                      occr_c1++;
                    }
                  }
                }
              }
              if (!coor_Ocorr.contains(c1)) {
                coor_Ocorr.put(c1,occr_c1);
              } else {
                coor_Ocorr.replace(c1,occr_c1);
              }
              update_List_Guards(c1,occr_c1);
              garbageColl();
          }
        }
      }
  }

  static void update_List_Guards(Coor c1, int occr_c1) {
    int flg = 0;
    if (guards.isEmpty()) {
      guards.addFirst(c1);
    } else {
      if (occr_c1 >= coor_Ocorr.get(guards.peek()))
        guards.addFirst(c1);
      else {
        for (int j = 0; j < guards.size(); j++) {
          Coor c2 = guards.get(j);
          if (occr_c1==coor_Ocorr.get(c2)) {
            guards.add(j,c1);
            flg = 1;
            break;
          }
        }
        if (flg == 0)
          guards.addLast(c1);
      }
    }
  }

  static void garbageColl() {
    for (int i = 0; i < guards.size(); i++) {
      Coor c1 = guards.get(i);
      for (int j=i+1; j < guards.size(); j++) {
        Coor c2 = guards.get(j);
        if ((c1.getX() == c2.getX()) && (c1.getY() == c2.getY())) {
          guards.remove(j);
        }
      }
    }
  }

  static void updateStates(Coor c, LinkedList<Coor> res) {
    LinkedList<Coor> verts = new LinkedList<>();
    for (int id = 1; id <= numRec; id++) {
        verts = id_Coor.get(id);
        for (int i = 0; i < verts.size(); i++) {
          Coor c2 = verts.get(i);
          if ((c.getX() == c2.getX()) && (c.getY() == c2.getY())) {
            if (arr_Rec[(id-1)] == false) {
              arr_Rec[(id-1)] = true;
              if (!res.contains(c))
                res.add(c);
            }
          }
        }
    }
    guards.clear();
    readList();
  }

  static LinkedList<Coor> readGuards() {
    LinkedList<Coor> res = new LinkedList<>();

    while (!checkGoal(arr_Rec)) {
      Coor c = guards.removeFirst();
      updateStates(c,res);
    }

    System.out.println("Goal Found !");
    return res;
  }

  static void print_Guards(LinkedList<Coor> a) {
    for (int i = 0; i < a.size(); i++) {
      Coor c = a.get(i);
      System.out.println("Guards: " + "(" + c.getX() + "," + c.getY() + ")");
    }
    System.out.println("Guards Counted: " + a.size());
    System.out.println("Max Guards: " + maxGuards);
  }

  static boolean checkGoal(boolean a[]) {
    boolean finish = true;
      for (int i = 0; i < numRec; i++) {
        if (a[i]==false) {finish = false;}
      }
    return finish;
  }

  static int roundUp(int num, int divisor) {
    return (num + divisor - 1) / divisor;
  }

}
