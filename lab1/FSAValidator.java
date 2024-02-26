package lab1;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class FSAValidator {
    public static void main(String[] args) throws Exception {
        // read data from the file and output to file
        BufferedReader in = new BufferedReader(new FileReader("fsa.txt"));
        BufferedWriter out = new BufferedWriter(new FileWriter("result.txt"));
        String Sstates = in.readLine();
        String Salpha = in.readLine();
        String Sinit = in.readLine();
        String Sfin = in.readLine();
        String Strans = in.readLine();
        // containers to keep data properly
        HashMap<Integer, String> states = new HashMap<>();
        HashMap<Integer, String> alpha = new HashMap<>();
        HashSet<Integer> init = new HashSet<>();
        HashSet<Integer> fin = new HashSet<>();
        String cur = "";
        // check string for existence
        if(Sstates.isEmpty() || Salpha.isEmpty() || Sinit.isEmpty() || Sfin.isEmpty() || Strans.isEmpty()){
            out.write("Error:\nE5: Input file is malformed");
            in.close();
            out.close();
            return;
        }
        // parse states
        boolean open = false;
        for(int i = 0; i < Sstates.length(); i++){
            if(open){
                if(Sstates.charAt(i) == ',' || Sstates.charAt(i) == ']'){
                    if(!cur.isEmpty())
                        states.put(states.size(), cur);
                    cur = "";
                }
                else{
                    cur = cur + Sstates.charAt(i);
                }
            }
            else{
                cur = cur + Sstates.charAt(i);
            }
            if(Sstates.charAt(i) == '['){
                if(!cur.equals("states=[")){
                    out.write("Error:\nE5: Input file is malformed");
                    in.close();
                    out.close();
                    return;
                }
                open = true;
                cur = "";
            }

        }
        // graph representation
        @SuppressWarnings("unchecked")
        ArrayList<Pair> connects[] = new ArrayList[states.size()];
        for(int i = 0; i < states.size(); i++){
            ArrayList<Pair> tmp = new ArrayList<>();
            connects[i] = tmp;
        }
        // parse alpha
        open = false;
        for(int i = 0; i < Salpha.length(); i++){
            if(open){
                if(Salpha.charAt(i) == ',' || Salpha.charAt(i) == ']'){
                    if(!cur.isEmpty())
                        alpha.put(alpha.size(), cur);
                    cur = "";
                }
                else{
                    cur = cur + Salpha.charAt(i);
                }
            }
            else{
                cur = cur + Salpha.charAt(i);
            }
            if(Salpha.charAt(i) == '['){
                if(!cur.equals("alpha=[")){
                    out.write("Error:\nE5: Input file is malformed");
                    in.close();
                    out.close();
                    return;
                }
                open = true;
                cur = "";
            }
        }
        //parse initial state
        open = false;
        for(int i = 0; i < Sinit.length(); i++){
            if(open){
                if(Sinit.charAt(i) == ']'){
                    if(cur.isEmpty()){
                        out.write("Error:\nE4: Initial state is not defined");
                        in.close();
                        out.close();
                        return;
                    }
                    int x = -1;
                    for(int j = 0; j < states.size() && x < 0; j++)
                        if(states.get(j).equals(cur))
                            x = j;
                    if(x < 0){
                        out.write("Error:\nE1: A state '" + cur + "' is not in the set of states");
                        in.close();
                        out.close();
                        return;
                    }
                    init.add(x);
                    cur = "";
                }
                else{
                    cur = cur + Sinit.charAt(i);
                }
            }
            else{
                cur = cur + Sinit.charAt(i);
            }
            if(Sinit.charAt(i) == '['){
                if(!cur.equals("init.st=[")){
                    out.write("Error:\nE5: Input file is malformed");
                    in.close();
                    out.close();
                    return;
                }
                open = true;
                cur = "";
            }

        }
        //parse final state
        open = false;
        for(int i = 0; i < Sfin.length(); i++){
            if(open){
                if(Sfin.charAt(i) == ',' || Sfin.charAt(i) == ']'){
                    int x = -1;
                    for(int j = 0; j < states.size() && x < 0; j++)
                        if(states.get(j).equals(cur))
                            x = j;
                    if(x < 0 && !cur.isEmpty()){
                        out.write("Error:\nE1: A state '" + cur + "' is not in the set of states");
                        in.close();
                        out.close();
                        return;
                    }
                    if(x >= 0)
                        fin.add(x);
                    cur = "";
                }
                else{
                    cur = cur + Sfin.charAt(i);
                }
            }
            else{
                cur = cur + Sfin.charAt(i);
            }
            if(Sstates.charAt(i) == '['){
                if(!cur.equals("fin.st=[")){
                    out.write("Error:\nE5: Input file is malformed");
                    in.close();
                    out.close();
                    return;
                }
                open = true;
                cur = "";
            }
        }
        //parse transitions
        open = false;
        for(int i = 0; i < Strans.length(); i++){
            if(open){
                if(Strans.charAt(i) == ',' || Strans.charAt(i) == ']'){
                    String Sfrom = "", Show = "", Sto = "";
                    int from = -1, how = -1, to = -1;
                    String tCur = "";
                    for(int j = 0; j <= cur.length(); j++){
                        if(j == cur.length() || cur.charAt(j) == '>'){
                            if(Sfrom.isEmpty())
                                Sfrom = tCur;
                            else if(Show.isEmpty())
                                Show = tCur;
                            else if(Sto.isEmpty())
                                Sto = tCur;
                            else{
                                out.write("Error:\nE5: Input file is malformed");
                                in.close();
                                out.close();
                                return;
                            }
                            tCur = "";
                        }

                        else
                            tCur = tCur + cur.charAt(j);
                    }
                    for(int j = 0; j < states.size() && from < 0; j++)
                        if(states.get(j).equals(Sfrom))
                            from = j;
                    for(int j = 0; j < alpha.size() && how < 0; j++)
                        if(alpha.get(j).equals(Show))
                            how = j;
                    for(int j = 0; j < states.size() && to < 0; j++)
                        if(states.get(j).equals(Sto))
                            to = j;
                    if(from < 0){
                        out.write("Error:\nE1: A state '" + Sfrom + "' is not in the set of states");
                        in.close();
                        out.close();
                        return;
                    }
                    if(how < 0){
                        out.write("Error:\nE3: A transition '" + Show + "' is not represented in the alphabet");
                        in.close();
                        out.close();
                        return;
                    }
                    if(to < 0){
                        out.write("Error:\nE1: A state '" + Sto + "' is not in the set of states");
                        in.close();
                        out.close();
                        return;
                    }
                    cur = "";
                    Pair p = new Pair(to, how);
                    connects[from].add(p);
                }
                else{
                    cur = cur + Strans.charAt(i);
                }
            }
            else{
                cur = cur + Strans.charAt(i);
            }
            if(Strans.charAt(i) == '['){
                if(!cur.equals("trans=[")){
                    out.write("Error:\nE5: Input file is malformed");
                    in.close();
                    out.close();
                    return;
                }
                open = true;
                cur = "";
            }
        }
        // check if characters in states are valid
        for(int i = 0; i < states.size(); i++){
            String str = states.get(i);
            for(int j = 0; j < str.length(); j++){
                if(!((str.charAt(j) >= 'a' && str.charAt(j) <= 'z') || (str.charAt(j) >= '0' && str.charAt(j) <= '9') || (str.charAt(j) >= 'A' && str.charAt(j) <= 'Z'))){
                    out.write("Error:\nE5: Input file is malformed");
                    in.close();
                    out.close();
                    return;
                }
            }
        }
        // check if characters in alpha are valid
        for(int i = 0; i < alpha.size(); i++){
            String str = alpha.get(i);
            for(int j = 0; j < str.length(); j++){
                if(!((str.charAt(j) >= 'a' && str.charAt(j) <= 'z') || (str.charAt(j) >= '0' && str.charAt(j) <= '9') || str.charAt(j) == '_') || (str.charAt(j) >= 'A' && str.charAt(j) <= 'Z')){
                    out.write("Error:\nE5: Input file is malformed");
                    in.close();
                    out.close();
                    return;
                }
            }
        }

        boolean complete = true, warn1 = false, warn2 = false;
        boolean join[] = new boolean[states.size()];
        boolean used[] = new boolean[states.size()];
        for(int i = 0; i < states.size(); i++){
            join[i] = false;
            used[i] = false;
        }
        int k = states.size();
        for(int i : init)
            k = Math.min(i, k);
        // check if there more than one link component
        dfs(k, used, connects);
        // warning check
        for(int i = 0; i < states.size(); i++)
            if(!used[i])
                warn2 = true;
        // warning check + complete check
        for(int i = 0; i < states.size(); i++){
            HashSet<Integer> t = new HashSet<>();
            for(int j = 0; j < connects[i].size(); j++){
                t.add(connects[i].get(j).transition);
                if(i != connects[i].get(j).state){
                    join[i] = true;
                    join[connects[i].get(j).state] = true;
                }

            }
            if(t.size() < alpha.size())
                complete = false;
        }
        // warning
        for(int i = 0; i < states.size(); i++){
            if(!join[i]){
                out.write("Error:\nE2: Some states are disjoint");
                in.close();
                out.close();
                return;
            }
        }
        if(complete)
            out.write("FSA is complete");
        else
            out.write("FSA is incomplete");
        if(fin.isEmpty())
            warn1 = true;
        if(warn1 || warn2)
            out.write("\nWarning:");
        if(warn1)
            out.write("\nW1: Accepting state is not defined");
        if(warn2)
            out.write("\nW2: Some states are not reachable from the initial state");
        in.close();
        out.close();
    }
    // dfs implementation
    public static void dfs(int x, boolean visited[], ArrayList<Pair> graph[]){
        visited[x] = true;
        for(int i = 0; i < graph[x].size(); i++){
            int to = graph[x].get(i).state;
            if(!visited[to])
                dfs(to, visited, graph);
        }
    }
}
// class represents to_state and transition_type from certain state
class Pair{
    int state, transition;
    Pair(int state_, int transition_){
        state = state_;
        transition = transition_;
    }
}