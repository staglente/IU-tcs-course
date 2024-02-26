package lab2;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FSAToRegExp {
    public static boolean checkOnMalformed(ArrayList<String> data){
        if(data.size() != 5)
            return true;
        if(data.get(0).length() < 10 || !data.get(0).substring(0,8).equals("states=[") || data.get(0).charAt(data.get(0).length() - 1) != ']')
            return true;
        if(data.get(1).length() < 8 || !data.get(1).substring(0,7).equals("alpha=[") || data.get(1).charAt(data.get(1).length() - 1) != ']')
            return true;
        if(data.get(2).length() < 10 || !data.get(2).substring(0,9).equals("initial=[") || data.get(2).charAt(data.get(2).length() - 1) != ']')
            return true;
        if(data.get(3).length() < 12 || !data.get(3).substring(0,11).equals("accepting=[") || data.get(3).charAt(data.get(3).length() - 1) != ']')
            return true;
        if(data.get(4).length() < 8 || !data.get(4).substring(0,7).equals("trans=[") || data.get(4).charAt(data.get(4).length() - 1) != ']')
            return true;
        return false;
    }
    public static void main(String[] args) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader("input.txt"))) {
            BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));
            ArrayList<String> iData = new ArrayList<>();
            String str = in.readLine();
            while(str != null){
                iData.add(str);
                str = in.readLine();
            }
            if(checkOnMalformed(iData)){
                out.write("Error:\nE0: Input file is malformed");
                out.close();
                System.exit(0);
            }

            HashMap<String, Integer> st = new HashMap<>();
            String[] stringStates = iData.get(0).substring(8, iData.get(0).length() - 1).split(",");
            if(iData.get(2).substring(9, iData.get(2).length() - 1).isEmpty()){
                out.write("Error:\n" + "E4: Initial state is not defined");
                out.close();
                System.exit(0);
            }
            int cnt = 1;
            for(int i = 0; i < stringStates.length; i++){
                String curState = stringStates[i];
                if(curState.equals(iData.get(2).substring(9, iData.get(2).length() - 1)))
                    st.put(curState, 0);
                else if(!st.containsKey(curState)){
                    st.put(curState, cnt);
                    cnt++;
                }
            }
            if(!st.containsKey(iData.get(2).substring(9, iData.get(2).length() - 1))){
                out.write("Error:\n" + "E1: A state '" + iData.get(2).substring(9, iData.get(2).length() - 1) + "' is not in the set of states");
                out.close();
                System.exit(0);
            }
            String[] stringAlpha = iData.get(1).substring(7, iData.get(1).length() - 1).split(",");
            HashSet<String> tr = new HashSet<>();
            for(int i = 0; i < stringAlpha.length; i++)
                tr.add(stringAlpha[i]);
            String[] stringAccepting = iData.get(3).substring(11, iData.get(3).length() - 1).split(",");
            for(int i = 0; i < stringAccepting.length; i++){
                if(!st.containsKey(stringAccepting[i]) && !stringAccepting[i].isEmpty()){
                    out.write("Error:\n" + "E1: A state '" + stringAccepting[i] + "' is not in the set of states");
                    out.close();
                    System.exit(0);
                }
            }
            ArrayList<ArrayList<Edge>> a = new ArrayList<>();
            for(int i = 0; i < st.size(); i++)
                a.add(new ArrayList<>());

            String[] stringTrans = iData.get(4).substring(7, iData.get(4).length() - 1).split(",");
            boolean[] connected = new boolean[st.size()];
            boolean thirdError = false;
            for(int i = 0; i < st.size(); i++)
                connected[i] = false;
            String temp = "";
            for(int i = 0; i < stringTrans.length; i++){
                String curTrans = stringTrans[i];
                String[] s = curTrans.split(">");
                if(!st.containsKey(s[0])){
                    out.write("Error:\n" + "E1: A state '" + s[0] + "' is not in the set of states");
                    out.close();
                    System.exit(0);
                }
                if(!st.containsKey(s[2])){
                    out.write("Error:\n" + "E1: A state '" + s[2] + "' is not in the set of states");
                    out.close();
                    System.exit(0);
                }
                int from = st.get(s[0]), to = st.get(s[2]);
                String t = s[1];
                if(!tr.contains(t)){
                    thirdError = true;
                    temp = t;
                }
                Edge e = new Edge(to, t);
                a.get(from).add(e);
                if(from != to){
                    connected[from] = true;
                    connected[to] = true;
                }

            }
            if(!st.containsKey(iData.get(2).substring(9, iData.get(2).length() - 1))){
                out.write("Error:\n" + "E3: A transition '" + iData.get(2).substring(9, iData.get(2).length() - 1) + "' is not represented in the alphabet");
                out.close();
                System.exit(0);
            }
            if(iData.get(2).substring(9, iData.get(2).length() - 1).isEmpty()){
                out.write("Error:\n" + "E4: Initial state is not defined");
                out.close();
                System.exit(0);
            }
            if(st.size() > 1){
                for(int i = 0; i < st.size(); i++){
                    if(!connected[i]){
                        out.write("Error:\n" + "E2: Some states are disjoint");
                        out.close();
                        System.exit(0);
                    }
                }
            }
            if(thirdError){
                out.write("Error:\n" + "E3: A transition '" + temp + "' is not represented in the alphabet");
                out.close();
                System.exit(0);
            }

            for(int i = 0; i < st.size(); i++){
                HashSet<String> t = new HashSet<>();
                for(Edge edge : a.get(i)){
                    if(t.contains(edge.getTransitionAlpha())){
                        out.write("Error:\n" + "E5: FSA is nondeterministic");
                        out.close();
                        System.exit(0);
                    }
                    t.add(edge.getTransitionAlpha());
                }
            }

            if(iData.get(3).substring(11, iData.get(3).length() - 1).isEmpty()){
                out.write("{}");
                out.close();
                System.exit(0);
            }

            String regExp[][][] = new String[st.size() + 1][st.size()][st.size()];
            for(int i = 0; i <= st.size(); i++)
                for(int j = 0; j < st.size(); j++)
                    for(int k = 0; k < st.size(); k++)
                        regExp[i][j][k] = "";

            for(int i = 0; i < st.size(); i++){
                for(int j = 0; j < st.size(); j++){
                    String tempS = "";
                    for(Edge edge : a.get(i)){
                        if(edge.getToState() == j){
                            if(tempS.isEmpty())
                                tempS += edge.getTransitionAlpha();
                            else
                                tempS += '|' + edge.getTransitionAlpha();
                        }
                    }
                    if(i == j){
                        if(tempS.isEmpty())
                            tempS += "eps";
                        else
                            tempS += "|eps";
                    }
                    if(tempS.isEmpty())
                        tempS += "{}";
                    regExp[0][i][j] = tempS;
                }
            }
            for(int k = 1; k <= st.size(); k++)
                for(int i = 0; i < st.size(); i++)
                    for(int j = 0; j < st.size(); j++)
                        regExp[k][i][j] = "(" + regExp[k - 1][i][k - 1] + ")(" + regExp[k - 1][k - 1][k - 1] + ")*(" + regExp[k - 1][k - 1][j] + ")|(" + regExp[k - 1][i][j] + ")";
            String result = "";
            for(int i = 0; i < stringAccepting.length; i++){
                int v = st.get(stringAccepting[i]);
                if(!result.isEmpty())
                    result += '|';
                result += regExp[st.size()][0][v];
            }
            out.write(result);
            out.close();
        }
    }
}

class Edge{
    private int toState;
    private String transitionAlpha;
    Edge(int toState, String transitionAlpha){
        this.toState = toState;
        this.transitionAlpha = transitionAlpha;
    }
    public int getToState(){
        return toState;
    }
    public String getTransitionAlpha(){
        return transitionAlpha;
    }
}