import java.util.*;
import java.lang.*;
import java.util.concurrent.*;

class Coor {
  int x;
  int y;

  public Coor(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
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

class guards_dfs_GP {
  static LinkedList<LinkedList> list_list;
  static Hashtable<Integer,LinkedList> id_Coor; // key -> id; Value -> Lista (Coor)

  static int numRec;
  static int numInstan;

  static int maxGuards;

  static Stack<Coor> stk;
  static Hashtable<Coor,Integer> explored;
  static Hashtable<Coor,Integer> sv_depth;


  static Hashtable<Coor,Hashtable> sv_Ocorr;
  static Hashtable<Coor,boolean[]> sv_Arr;
  static Hashtable<Coor,LinkedList> sv_Solut;

  //

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    list_list = new LinkedList<>();
    id_Coor = new Hashtable<>();

    /******************************/

    stk = new Stack<>();
    explored = new Hashtable<>();
    sv_depth = new Hashtable<>();

    /******************************/

    sv_Ocorr = new Hashtable<>();
    sv_Arr = new Hashtable<>();
    sv_Solut = new Hashtable<>();

    final long startTime = System.nanoTime();

    numInstan = in.nextInt();
    LinkedList<Coor> res_insta[] = new LinkedList[numInstan+1];

    for (int index = 1; index <= numInstan; index++) {
      read_Input(in);

      boolean arr_Rec[] = new boolean[numRec];
      for (int i = 0; i < numRec; i++) {arr_Rec[i] = false;}

      int counter = numRec; // Number of Rectangles

      // /* Comment off if numInstan > 1
      //System.out.println("Definir subconjunto? (y or n)");
      //char ch = in.next().charAt(0);
      //if (ch=='y') {counter = check_Inst(in);}
      // */

      maxGuards = roundUp(counter,3);

      Hashtable<Coor,Integer> coor_Ocorr = new Hashtable<>();
      readList(coor_Ocorr, arr_Rec);

      res_insta[index] = str_State(coor_Ocorr, arr_Rec);

      list_list.clear(); id_Coor.clear(); // Garbage Collection
      stk.clear(); sv_depth.clear();
      System.out.println("*" + " " + "Instan :" + " " + index + " " + "*");
      print_Guards(res_insta[index]);
    }
    final long duration = System.nanoTime() - startTime;
    System.out.println("Duration: ");
    System.out.println(duration + " " +"ns");
    System.out.println(TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS) + " " + "s");
  }

  static void print_Guards(LinkedList<Coor> a) {
    for (int i = 0; i < a.size(); i++) {
      Coor c = a.get(i);
      System.out.println("Guards: " + "(" + c.getX() + "," + c.getY() + ")");
    }
    System.out.println("Guards Counted: " + a.size());
    System.out.println("Max Guards: " + maxGuards);
  }

  static boolean checkGoal(Coor c) {
    boolean a[] = sv_Arr.get(c);
    boolean finish = true;
      for (int i = 0; i < numRec; i++) {
        if (a[i]==false) {finish = false;}
      }
    return finish;
  }

  static int roundUp(int num, int divisor) {
    return (num + divisor - 1) / divisor;
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

        temp.add(p);
      }
      id_Coor.put(id, temp);
      list_list.add(temp);
    }
  }

  static int check_Inst(Scanner in, boolean arr_Rec[]) {
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

  /*
    Update coor_Ocorr Hashtable
  */
  static void readList(Hashtable<Coor,Integer> coor_Ocorr, boolean arr_Rec[]) {
      for (int i = 0; i < list_list.size(); i++) {
        if (arr_Rec[i] == false) { // Rec que ainda nao foi visitado
          LinkedList<Coor> list = list_list.get(i);
          for (int j = 0; j < list.size(); j++) {
            Coor c1 = list.get(j);
            int occr_c1 = 0;

              for (int l = 1; l < numRec; l++) {
                if (arr_Rec[(l-1)] == false) { // Rec que ainda nao foi visitado
                  LinkedList<Coor> list2 = id_Coor.get(l);
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
          }
        }
      }
  }

  /*
    Check Rectangles covered and alter occor
  */
  static void updateStates(Coor c) {
    LinkedList<Coor> verts = new LinkedList<>();

    //
    boolean arr_Rec[] = sv_Arr.get(c);
    LinkedList<Coor> res = sv_Solut.get(c);
    Hashtable<Coor,Integer> coor_Ocorr = sv_Ocorr.get(c);
    //

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

    //
    sv_Arr.replace(c,arr_Rec);
    sv_Solut.replace(c,res);
    //

    readList(coor_Ocorr,arr_Rec);

    //
    sv_Ocorr.replace(c,coor_Ocorr);
    //
  }

  /*
    Removes candidate guard
  */
  static void checkNewStates(Coor c) {

    //
    Hashtable<Coor,Integer> coor_Ocorr = sv_Ocorr.get(c);
    boolean arr_Rec[] = sv_Arr.get(c);
    LinkedList<Coor> res = sv_Solut.get(c);
    //

    LinkedList<Coor> cand = gerirViz(coor_Ocorr,arr_Rec);

    while (!cand.isEmpty()) {
      Coor c1 = cand.remove();
        stk.push(c1);
        int cur_depth = sv_depth.get(c)+1;
        sv_depth.put(c1,cur_depth);

        //
          sv_Solut.put(c1,res);
          sv_Arr.put(c1,arr_Rec);
          sv_Ocorr.put(c1,coor_Ocorr);
        //
    }
  }

  /*
    Gerir Vizinhos
  */
  static LinkedList<Coor> gerirViz(Hashtable<Coor,Integer> coor_Ocorr, boolean arr_Rec[]) {
    int id = 0;
    LinkedList<Coor> rec_Verts = new LinkedList<>();

    // Coor candidatos para entrar na fila
    LinkedList<Coor> cand = new LinkedList<>();

    for (int i = 1; i <= numRec; i++) { // Choose Rectangle
      if (arr_Rec[(i-1)] == false) {
        LinkedList<Coor> temp = id_Coor.get(i);

        if (rec_Verts.size()==0) {
          rec_Verts = (LinkedList) temp.clone();
          id = i;
        }
        if (temp.size() < rec_Verts.size()) {
          rec_Verts = (LinkedList) temp.clone();
          id = i;
        }
      }
    }

    Coor c = rec_Verts.get(0);
    for (int i = 0; i < rec_Verts.size(); i++) { // Choose Vert
      Coor c1 = rec_Verts.get(i);
      if (coor_Ocorr.get(c1) > coor_Ocorr.get(c))
        c = c1;
    }

    cand.add(c);
    //return cand;

    // List to save possible incomparable verts
    LinkedList<Coor> incomp_verts = new LinkedList<>();

    for (int i = 0; i < rec_Verts.size(); i++) { // Check for incomparable verts
      Coor c1 = rec_Verts.get(i);
      if (!(c.getX() == c1.getX() && c.getY() == c1.getY())) {
        if (coor_Ocorr.get(c1) == coor_Ocorr.get(c)) {
          incomp_verts.add(c1);
        }
      }
    }

    // Found a possible incomparable candidate
      while (!incomp_verts.isEmpty()) {
        Coor c1 = incomp_verts.remove();

        for (int i = 1; i <= numRec; i++) {
          if (id != i && arr_Rec[(i-1)] == false) {
            LinkedList<Coor> rec_tp = id_Coor.get(i);
            int flg_c = 0;
            int flg_c1 = 0;

            for (int j = 0; j < rec_tp.size(); j++) {
              Coor c_tp = rec_tp.get(j);

              if ((c.getX() == c_tp.getX() && c.getY() == c_tp.getY()))
                flg_c = 1;
              if ((c1.getX() == c_tp.getX() && c1.getY() == c_tp.getY()))
                flg_c1 = 1;
            }

            if (flg_c == 0 && flg_c1 == 1) { // C e C1 sao incomparaveis
              cand.add(c1);
              break;
            }
            if (flg_c == 1 && flg_c1 == 1) { // C e C1 sao comparaveis
              break;
            }
          }
        }
      }

    return cand;
  }


  static LinkedList<Coor> str_State(Hashtable<Coor,Integer> coor_Ocorr, boolean arr_Rec[]) {
    LinkedList<LinkedList> res_optimo = new LinkedList<>();

    Hashtable<Coor,Integer> initial_coor_Ocorr = (Hashtable) coor_Ocorr.clone();
    boolean initial_arr_Rec[] = arr_Rec.clone();

    LinkedList<Coor> cand = gerirViz(coor_Ocorr, arr_Rec);
    /*while (!cand.isEmpty()) { // To check sub-trees
      Coor c1 = cand.remove();

      if (res_optimo.isEmpty()) {
        res_optimo.add(dfs(c1,coor_Ocorr,arr_Rec));
      } else {
          stk.clear(); sv_depth.clear(); // Gb Col
          sv_Ocorr.clear(); sv_Arr.clear(); sv_Solut.clear();
          res_optimo.add(dfs(c1,initial_coor_Ocorr,initial_arr_Rec));
      }
    }
    */

    res_optimo.add(dfs(cand,initial_coor_Ocorr,initial_arr_Rec));
    //print_contents(res_optimo);

    LinkedList<Coor> opt = res_optimo.get(0);
    for (int i = 1; i < res_optimo.size(); i++) {
      LinkedList<Coor> cd = res_optimo.get(i);
      if (cd.size() >= maxGuards && cd.size() < opt.size())
        opt = (LinkedList) cd.clone();
    }

    System.out.println("Done !");
    return opt;
  }



  static LinkedList<Coor> dfs(LinkedList<Coor> cand, Hashtable<Coor,Integer> coor_Ocorr, boolean arr_Rec[]) {
    // Empty list
    LinkedList<Coor> empty = new LinkedList<>();

    while (!cand.isEmpty()) {
      Coor c1 = cand.remove();

      stk.push(c1);
      sv_depth.put(c1,0);

      //
      sv_Ocorr.put(c1,coor_Ocorr);
      sv_Arr.put(c1,arr_Rec);
      LinkedList<Coor> str = new LinkedList<>();
      str.add(c1);
      sv_Solut.put(c1,str);
      //
    }

    while (!stk.isEmpty()) {
      Coor c = stk.pop();
      LinkedList<Coor> res = sv_Solut.get(c);

      //System.out.println("Choosen Guard ("+c.getX()+","+c.getY()+")");
      //System.out.println("Current depth: " + sv_depth.get(c));
      //System.out.println("Ocorr: " + coor_Ocorr.get(c));

      updateStates(c); // Update rectangles covered

      if (checkGoal(c)) { // Check for goal
        //System.out.println("Done");
        return res;
      }

      checkNewStates(c); // Get new states
    }
    System.out.println("Goal not found");
    return empty;
  }

  static void print_contents(LinkedList<LinkedList> a) {
    for (int i = 0; i < a.size(); i++) {
      LinkedList<Coor> b = a.get(i);
      for (int j = 0; j < b.size(); j++) {
        Coor c = b.get(j);
        System.out.println("Choosen Guard ("+c.getX()+","+c.getY()+")");
      }
      System.out.println("***********");
    }
  }
}
