import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.pow;

public class Heys {

    public static String GenBit(int n) {
        StringBuilder a = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int num = (1 + (int) (Math.random() * 100)) % 2;
            a.append(num);
        }
        return String.valueOf(a);
    }

    public static String xor(String a, String b) {
        StringBuilder res = new StringBuilder();
        if (a.length() != b.length()) {
            if (a.length() > b.length()) {
                for (int i = 0; i < a.length() - b.length(); i++) {
                    b = "0" + b;
                }
            } else {
                for (int i = 0; i < b.length() - a.length(); i++) {
                    a = "0" + a;
                }
            }
        }
        for (int i = 0; i < a.length(); i++) {
            if ((a.charAt(i) + b.charAt(i) - 96) == 0 || (a.charAt(i) + b.charAt(i) - 96) == 2) res.append(0);
            else res.append(1);
        }
        return String.valueOf(res);
    }

    public static ArrayList<String> Sfunction(String S, String x, int n) {
        ArrayList<String> block = Blocks(4, x);
        ArrayList<String> newBlock = new ArrayList<>();
        String[] smas = S.split(",");
        for (int i = 0; i < block.size(); i++) {
            String a = block.get(i);
            BigInteger temp = new BigInteger(a, 2);
            String b = smas[temp.intValue()];
            BigInteger place = new BigInteger(b, 16);
            String temp1 = place.toString(2);
            while (temp1.length() != 4) {
                temp1 = "0" + temp1;
            }
            newBlock.add(temp1);
        }
        return newBlock;
    }

    public static ArrayList<String> Blocks(int n, String S) {
        ArrayList<String> blocks = new ArrayList<>();
        for (int i = 0; i < S.length() / 4; i++) {
            String k = S.substring(i * n, n + i * n);
            blocks.add(k);
        }
        return blocks;
    }

    public static String VectorfromBlocks( ArrayList<String> S)
    {
        String a = "";
        for (int i = 0; i < S.size(); i++)
        {
            a = a + S.get(i);
        }
        return a;
    }

    public static String Reshuffle(ArrayList<String> S, int n) {
        int[] res = new int[S.size() * S.size()];
        for (int i = 0; i < S.size(); i++) {
            for (int j = 0; j < S.size(); j++) {
                res[(i + j * n)] = S.get(i).charAt(j) - 48;
            }
        }
        String result = Arrays.toString(res);
        result = result.substring(1, result.length() - 1);
        result = result.replaceAll(", ", "");
        return result;
    }

    public static String HeysCipher(int n, String x, String k, String S) {
        for (int i = 6; i > 0; i--) {
            String substring = k.substring(i * 16, 16 + i * 16);
            x = xor(x, substring);
            System.out.println("key_i ,i=" + i + "k = " + substring);
            System.out.println("xor result : " + x);
            x = Reshuffle(Sfunction(S, x, n), n);
            System.out.println();
            System.out.println("X_i = " + x);
        }
        System.out.println("After 6s round:");
        System.out.println(x);
        System.out.println("k_7 = " + k.substring(0, 16));
        x = xor(x, k.substring(0, 16));
        System.out.println("Final =: " + x);
        return x;
    }

    public static ArrayList<Double> DifferS(String S) {
        ArrayList<String> x = Xgen();
        double p = 1 / pow(2, 4);
        ArrayList<Double> abprob = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            HashMap<String, Double> P = new HashMap<>();
            for (int j = 0; j < 16; j++) {
                ArrayList<String> xs = Sfunction(S, x.get(j), 1);
                String xoa = xor(x.get(j), x.get(i));
                ArrayList<String> xas = Sfunction(S, xoa, 1);
                xoa = xor(xs.get(0), xas.get(0));
                if (P.containsKey(xoa)) {
                    P.computeIfPresent(xoa, (k, v) -> v + p);
                } else {
                    P.put(xoa, p);
                }
            }
            for (int k = 0; k < 16; k++) {
                abprob.add(P.getOrDefault(x.get(k), 0.0));
            }
        }
        return abprob;
    }

    private static int mulscalar(String a, String b)
    {
        int bit = 0;
        for(int i = 0; i < a.length(); i++)
        {
            bit = ((a.charAt(i) - 48)*(b.charAt(i) - 48) + bit)%2;
        }
        return bit;
    }

    public static ArrayList<Double> LinearS(String S) {
        ArrayList<String> x = Xgen();
        double p = 1 / pow(2, 4);
        ArrayList<Double> abprobnew = new ArrayList<>();
        for(int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                double pro = 0.0;
                for(int k = 0; k < 16; k++)
                {
                    int a = mulscalar(x.get(i), x.get(k));
                    int b = mulscalar(x.get(j), Sfunction(S, x.get(k), 1).get(0));
                    if((a + b)%2 == 1) pro = pro - 1;
                    else pro = pro + 1;
                }
                pro = pro*p;
                pro = pow(pro, 2);
                abprobnew.add(pro);
            }
        }
        return abprobnew;
    }

    public static HashMap<String, Double> mix (ArrayList<HashMap<String,Double>> line) {
        ArrayList<String> xvar = Xgen();
        HashMap<String, Double> mixall = new HashMap<>();
        for (int i = 0; i < 16; i++) {
            if (line.get(0).containsKey(xvar.get(i))) {
                String a = xvar.get(i);
                for (int j = 0; j < 16; j++) {
                    if (line.get(1).containsKey(xvar.get(j))) {
                        String b = xvar.get(j);
                        for (int k = 0; k < 16; k++) {
                            if (line.get(2).containsKey(xvar.get(k))) {
                                String c = xvar.get(k);
                                for (int l = 0; l < 16; l++) {
                                    if (line.get(3).containsKey(xvar.get(l))) {
                                        String d = a + b + c + xvar.get(l);
                                        d = Reshuffle(Blocks(4,d),4);
                                        double prob = line.get(0).get(a) * line.get(1).get(b) * line.get(2).get(c) * line.get(3).get(xvar.get(l));
                                        mixall.put(d,prob);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return mixall;
    }

    public static ArrayList<String> HighDiffersecond(ArrayList<String> x, String S, String alpha)
    {
        ArrayList<Double> abprob = DifferS(S);
        System.out.println(" a = " + alpha);
        double p = 1 / pow(2, 4);
        double pw = 0.1;
        HashMap<String, Double> P = new HashMap<>();

        HashMap<String, Double> Pold = new HashMap<>();
        Pold.put(alpha,1.0);
        for(int i = 0; i < 5; i++)
        {
            ArrayList<ArrayList<String>> Bagblocks = new ArrayList<>();
            ArrayList<Double> prob = new ArrayList<>();
            Pold.forEach((key, value) -> {
                Bagblocks.add( Blocks(4, key));
                prob.add(value);
            });
            for(int j = 0; j < Bagblocks.size(); j++)
            {
                ArrayList<String> line = Bagblocks.get(j);
                HashMap<String, Double> Pev = new HashMap<>();
                double pq = prob.get(j);
                ArrayList<HashMap<String, Double>> mixline = new ArrayList<>();
                for(int k = 0; k < line.size(); k++)
                {
                    HashMap<String, Double> mixblock = new HashMap<>();
                    String a = line.get(k);
                    for (int l = 0; l < 16; l++) {
                        double pt = abprob.get((new BigInteger(a, 2)).intValue() * 16 + l);
                        if (pt > pw) {
                            mixblock.put(x.get(l), pt);
                        }
                    }
                    mixline.add(mixblock);
                }
                Pev.putAll(mix(mixline));
                Pev.forEach((key, value) -> {
                    if(P.containsKey(key))
                    {
                        P.put(key, value*pq + P.get(key));
                    }
                    else P.put(key, value*pq);
                });
            }
            Pold.clear();
            P.forEach((key, value) -> {
                if(value > 0.0005)
                {
                    Pold.put(key, value);
                }
            });
            P.clear();
        }
        List<Double> employeeById = new ArrayList<>(Pold.values());
        Collections.sort(employeeById);
        ArrayList<String> betta = new ArrayList<>();
        Pold.forEach((key, value) -> {
            if(!Blocks(4,key).contains("0000"))
            {
                betta.add(key);
            }
        });
        System.out.println("Potencial differencials:");
        System.out.println(betta);
        return betta;
    }

    public static String tolength (BigInteger a)
    {
        String c = a.toString(2);
        while (c.length() != 16) c = "0" + c;
        return c;
    }

    public static void Attack(ArrayList<String> b, ArrayList<String> C, ArrayList<String> Cmut, ArrayList<String> keyvar, String a, String S)
    {
        HashMap<String, Integer> p = new HashMap<>();
        for(int l = 0; l < b.size(); l++)
        {
            for(int i = 0; i < 65535; i++)
            {
                for(int j = 0; j < 10000; j++)
                {
                    String c = C.get(j);
                    String ca = Cmut.get(j);
                    c = xor(c,keyvar.get(i));
                    ca = xor(ca, keyvar.get(i));
                    c = Reshuffle(Blocks(4,c),4);
                    c = VectorfromBlocks(Sfunction(S, c, 4));
                    ca = VectorfromBlocks(Sfunction(S, Reshuffle(Blocks(4,ca),4), 4));
                    BigInteger xr = new BigInteger(xor(c, ca), 2);
                    if(xr.equals(new BigInteger(b.get(l), 2)))
                    {
                        String finalKeyvar = keyvar.get(i);
                        if(!p.containsKey(finalKeyvar)) p.put(finalKeyvar, 1);
                        else {
                            int value = p.get(finalKeyvar);
                            p.put(finalKeyvar, value + 1);
                        }

                    }
                }
            }

            AtomicReference<String> mostprob = new AtomicReference<>("");
            p.forEach((keys, value) -> {
                int maxvalue = Collections.max(p.values());
                if(value == maxvalue)
                {
                    mostprob.set(keys);
                }
            });
            System.out.println(mostprob);
            System.out.println(p.get(mostprob));
            System.out.println(p);
        }


    }

    public static ArrayList<String> keyGen()
    {
        BigInteger a = new BigInteger("0", 2);
        ArrayList<String> key = new ArrayList<>();
        for(int i = 0; i < 65535; i++)
        {
            String keyvar = tolength(a);
            key.add(keyvar);
            a = a.add(BigInteger.ONE);
        }
        return key;
    }
    public static ArrayList<String> alphaGeneration(ArrayList<String> x)
    {
        ArrayList<String> alphas = new ArrayList<>();
        for (int q = 1; q < x.size(); q++)
        {
            String alpha = x.get(q) + "000000000000";
            alphas.add(alpha);
            alpha = "0000" + x.get(q) + "00000000";
            alphas.add(alpha);
            alpha = "00000000" + x.get(q) + "0000";
            alphas.add(alpha);
            alpha = "000000000000" + x.get(q);
            alphas.add(alpha);
        }
        return alphas;
    }

    public static void allalphaAttack(ArrayList<String> x, String S, String Srev) throws IOException {
        ArrayList<String> alphas = alphaGeneration(x);
        ArrayList<String> sixteenlenght = keyGen();
        for (int q = 0; q < alphas.size(); q++)
        {
            String alpha = alphas.get(q);
            ArrayList<String> b = HighDiffersecond(x,S,alpha);
            if(b.size() != 0)
            {
                GenerateMaterial(alpha, sixteenlenght);
                Scanner in = new Scanner(System.in);
                System.out.print("Please put in C:\\ your c.txt and print 'yes' ");
                String a = in.next();
                ArrayList<String> C = Byteread("C:\\Users\\Asus\\Downloads\\heys.exe\\c.txt");
                ArrayList<String> Cmut = Byteread("C:\\Users\\Asus\\Downloads\\heys.exe\\cmut.txt");
                Attack(b, C, Cmut, sixteenlenght, alpha, Srev);
            }
        }
    }

    public static ArrayList<String> Xgen()
    {
        ArrayList<String> x = new ArrayList<>();
        BigInteger a = new BigInteger("0", 10);
        BigInteger one = new BigInteger("1", 10);
        String temp = "0000";
        for (int i = 0; i < 16; i++) {
            while (temp.length() != 4) {
                temp = "0" + temp;
            }
            x.add(temp);
            a = a.add(one);
            temp = a.toString(2);
        }
        return x;
    }

    public static ArrayList<String> Byteread(String file) throws IOException {
        ArrayList<String> text = new ArrayList<>();
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        for (int i = 0; i < data.length; i += 2) {
            int el1 = data[i] & 0xFF;
            int el2 = data[i + 1] & 0xFF;
            int combined = (el2 << 8) ^ el1;
            String a = Integer.toBinaryString(combined);
            while (a.length() != 16) a = "0" + a;
            text.add(a);
        }
        return text;
    }

    private static byte[] toBytes(int value) {
        return new byte[]{(byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF)};
    }

    public static void GenerateMaterial(String a, ArrayList<String> key) throws IOException {
        File myObj = new File("C:\\Users\\Asus\\Downloads\\heys.exe\\p.txt");
        File myObjmut = new File("C:\\Users\\Asus\\Downloads\\heys.exe\\pmut.txt");
        FileOutputStream fos = new FileOutputStream(myObj);
        FileOutputStream fosmut = new FileOutputStream(myObjmut);
        for (int i = 0; i < 10000; i++)
        {
            String x = key.get(i);
            fos.write(toBytes(Integer.parseInt(x,2)));
            fosmut.write(toBytes(Integer.parseInt(xor(x,a),2)));
        }
    }

    public static void main (String[]args) throws Exception {
        int n = 4;
        String x = GenBit(16);
        System.out.println("x0 = " + x);
        String k = GenBit(112);
        System.out.println("k = " + k);
        String S = "3,8,D,9,6,B,F,0,2,5,C,A,4,E,1,7";
        String Srev = "7,E,8,0,C,9,4,F,1,3,B,5,A,2,D,6";
        /*System.out.println("x =" + x);
        HeysCipher(n, x, k, S);
        ArrayList<String> xbag = Xgen();
        allalphaAttack(xbag,S, Srev);*/
        System.out.println(LinearS(S));
    }

}

