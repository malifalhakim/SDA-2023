import java.io.PrintWriter;
import java.util.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

class Pengunjung{
    int id;
    String jenis;
    long poin;
    long uang;
    int totalWahana;

    public Pengunjung(int id,String jenis,long uang){
        this.id = id;
        this.jenis = jenis;
        this.poin = 0;
        this.uang = uang;
        this.totalWahana = 0;
    }

    void updateStatusPengunjung(long poinWahana,long harga){
        this.poin += poinWahana;
        this.uang -= harga;
        this.totalWahana += 1;
    }

    String cariRencanaTerbaik(ArrayList<Wahana> daftarWahana,Rencana[][][] dp){
        int jumlahWahana = daftarWahana.size();
        int budget = (int) this.uang;

        Rencana rencanaTerbaik = rencanaHelper(1,0,jumlahWahana,daftarWahana,budget,dp);

        StringBuilder result = new StringBuilder();
        long maxPoin = rencanaTerbaik.poin;
        result.append(maxPoin);
        for (int id: rencanaTerbaik.listID){
            result.append(" ");
            result.append(id);
        }

        return result.toString();
    }

    Rencana rencanaHelper(int id,int idSebelum, int jumlahWahana, ArrayList<Wahana> daftarWahana, int uang, Rencana[][][] dp){
        if (id > jumlahWahana || uang <= 0) {
            return new Rencana(uang);
        }

        if (idSebelum == 0){
            if (dp[id][uang][2] != null)
                return dp[id][uang][2];
        } else {
            if (dp[id][uang][idSebelum%2] != null && id % 2 != idSebelum % 2)
                return dp[id][uang][idSebelum%2];
        }


        Rencana excludeWahanaSekarang = rencanaHelper(id + 1, idSebelum, jumlahWahana, daftarWahana, uang, dp);

        if ((uang >= daftarWahana.get(id - 1).harga) &&  ((id % 2 != idSebelum % 2) || (idSebelum == 0))) {
            Rencana rencanaSekarang = new Rencana(0);
            rencanaSekarang.listID.add(id);
            rencanaSekarang.spent += daftarWahana.get(id - 1).harga;
            rencanaSekarang.poin += daftarWahana.get(id - 1).poin;
            Rencana includeWahanaSekarang = rencanaSekarang.tambahRencana(rencanaHelper(id + 1, id, jumlahWahana, daftarWahana,
                    uang - (int) daftarWahana.get(id - 1).harga, dp));

            if (includeWahanaSekarang.compareTo(excludeWahanaSekarang) > 0) {
                if (idSebelum == 0)
                    return dp[id][uang][2] = includeWahanaSekarang;
                return dp[id][uang][idSebelum%2] = includeWahanaSekarang;
            }
        }
        if (idSebelum == 0)
            return dp[id][uang][2] = excludeWahanaSekarang;
        return dp[id][uang][idSebelum%2] = excludeWahanaSekarang;
    }
}

class PengunjungComparator implements Comparator<ArrayList<Integer>>{
    @Override
    public int compare(ArrayList<Integer> a1, ArrayList<Integer> a2) {
        if (a1.get(0) > a2.get(0))
            return 1;
        else if (a1.get(0).equals(a2.get(0))){
            if (a1.get(1) > a2.get(1))
                return 1;
            else
                return -1;
        } else
            return -1;
    }
}

class Wahana{
    int id;
    long harga;
    long poin;
    int kapasitas;
    int kapasitasFT;
    PriorityQueue<ArrayList<Integer>> antreanR;
    PriorityQueue<ArrayList<Integer>> antreanFT;

    public Wahana(int id,long harga,long poin,int kapasitas,double persentaseFT){
        this.id = id;
        this.harga = harga;
        this.poin = poin;
        this.kapasitas = kapasitas;
        this.kapasitasFT = (int) Math.ceil((persentaseFT*kapasitas)/100);
        this.antreanR = new PriorityQueue<>(new PengunjungComparator());
        this.antreanFT = new PriorityQueue<>(new PengunjungComparator());
    }

    int tambahkanPengunjung(Pengunjung p){
        if (p.uang < this.harga)
            return -1;

        ArrayList<Integer> infoPengunjung = new ArrayList<>();
        infoPengunjung.add(p.totalWahana);
        infoPengunjung.add(p.id);

        if (p.jenis.equals("FT")){
            this.antreanFT.add(infoPengunjung);
        } else{
            this.antreanR.add(infoPengunjung);
        }
        return this.antreanR.size() + this.antreanFT.size();
    }

    String mainkanWahana(ArrayList<Pengunjung> listPengunjung,Deque<Pengunjung> daftarKeluar){
        int banyakFT = 0;
        int banyakPengunjung = 0;

        StringBuilder idBermain = new StringBuilder();

        while((!this.antreanFT.isEmpty()) && (banyakFT < this.kapasitasFT)){
            Pengunjung p = listPengunjung.get(this.antreanFT.poll().get(1) - 1);
            if (p.uang >= this.harga){
                banyakFT++;
                banyakPengunjung++;
                p.updateStatusPengunjung(this.poin,this.harga);
                idBermain.append(p.id);
                idBermain.append(" ");
                if (p.uang == 0)
                    daftarKeluar.add(p);
            }
        }

        while((!this.antreanR.isEmpty()) && (banyakPengunjung < this.kapasitas)){
            Pengunjung p = listPengunjung.get(this.antreanR.poll().get(1) - 1);
            if (p.uang >= this.harga) {
                banyakPengunjung++;
                p.updateStatusPengunjung(this.poin,this.harga);
                idBermain.append(p.id);
                idBermain.append(" ");
                if (p.uang == 0)
                    daftarKeluar.add(p);
            }
        }

        while((!this.antreanFT.isEmpty()) && (banyakPengunjung < this.kapasitas)){
            Pengunjung p = listPengunjung.get(this.antreanFT.poll().get(1) - 1);
            if (p.uang >= this.harga){
                banyakPengunjung++;
                p.updateStatusPengunjung(this.poin,this.harga);
                idBermain.append(p.id);
                idBermain.append(" ");
                if (p.uang == 0)
                    daftarKeluar.add(p);
            }
        }

        String idBermainstr = idBermain.toString();

        if (idBermainstr.length() == 0){
            idBermainstr += -1;
        }

        return idBermainstr;
    }

    int cariUrutanBermain(int idPengunjung,ArrayList<Pengunjung> listPengunjung){
        PriorityQueue<ArrayList<Integer>> antreanRcopy = new PriorityQueue<>(this.antreanR);
        PriorityQueue<ArrayList<Integer>> antreanFTcopy = new PriorityQueue<>(this.antreanFT);

        int urutan = 1;

        while (!(antreanRcopy.isEmpty() && antreanFTcopy.isEmpty())){
            int banyakFT = 0;
            int banyakPengunjung = 0;

            while((!antreanFTcopy.isEmpty()) && (banyakFT < this.kapasitasFT)){
                Pengunjung p = listPengunjung.get(antreanFTcopy.poll().get(1) - 1);
                if (p.uang >= this.harga){
                    banyakFT++;
                    banyakPengunjung++;
                    if (p.id == idPengunjung)
                        return urutan;
                    urutan++;
                }
            }

            while((!antreanRcopy.isEmpty()) && (banyakPengunjung < this.kapasitas)){
                Pengunjung p = listPengunjung.get(antreanRcopy.poll().get(1) - 1);
                if (p.uang >= this.harga){
                    banyakPengunjung++;
                    if (p.id == idPengunjung)
                        return urutan;
                    urutan++;
                }
            }

            while((!antreanFTcopy.isEmpty()) && (banyakPengunjung < this.kapasitas)){
                Pengunjung p = listPengunjung.get(antreanFTcopy.poll().get(1) - 1);
                if (p.uang >= this.harga){
                    banyakPengunjung++;
                    if (p.id == idPengunjung)
                        return urutan;
                    urutan++;
                }
            }
        }
        return -1;
    }
}

class Rencana{
    long poin;
    int uang;
    ArrayList<Integer> listID;
    long spent;

    public Rencana(int uang){
        this.uang = uang;
        this.poin = 0;
        this.listID = new ArrayList<>();
        this.spent = 0;
    }

    static int compareList(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        for (int i = 0; i < Math.min(list1.size(), list2.size()); i++) {
            if (!list1.get(i).equals(list2.get(i)))
                return list2.get(i) - list1.get(i);
        }
        return list2.size() - list1.size();
    }

    public int compareTo(Rencana o) {
        if (this.poin != o.poin)
            return (int) (this.poin - o.poin);
        if (this.spent != o.spent)
            return (int)(o.spent - this.spent);
        return compareList(this.listID,o.listID);
    }

    Rencana tambahRencana(Rencana otherRencana){
        Rencana rencanaBaru =  new Rencana(this.uang + otherRencana.uang);
        rencanaBaru.poin = this.poin + otherRencana.poin;
        rencanaBaru.spent = this.spent + otherRencana.spent;
        rencanaBaru.listID.addAll(this.listID);
        rencanaBaru.listID.addAll(otherRencana.listID);
        return rencanaBaru;
    }
}
public class TugasPemrograman1 {
    private static InputReader in;
    private static PrintWriter out;
    static ArrayList<Wahana> listWahana = new ArrayList<>();
    static ArrayList<Pengunjung> listPengunjung = new ArrayList<>();
    static Deque<Pengunjung> daftarKeluar = new ArrayDeque<>();
    static Rencana[][][] dp;

    // Method A
    static int A(int idPengunjung, int idWahana){
        return listWahana.get(idWahana).tambahkanPengunjung(listPengunjung.get(idPengunjung));
    }

    static String E(int idWahana){
        return listWahana.get(idWahana - 1).mainkanWahana(listPengunjung,daftarKeluar);
    }

    static int S(int idPengunjung, int idWahana){
        return listWahana.get(idWahana - 1).cariUrutanBermain(idPengunjung,listPengunjung);
    }

    static long F(int pos){
        if (daftarKeluar.isEmpty())
            return -1;
        if (pos == 0)
            return daftarKeluar.removeFirst().poin;
        return daftarKeluar.removeLast().poin;
    }

    static String O(int idPengunjung){
        return listPengunjung.get(idPengunjung - 1).cariRencanaTerbaik(listWahana,dp);
    }

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read Input
        // Wahana
        int M = in.nextInt();
        for (int i = 0; i < M; i++){
            long h = in.nextLong();
            long p = in.nextLong();
            int kp = in.nextInt();
            int ft = in.nextInt();
            listWahana.add(new Wahana(i+1,h,p,kp,ft));
        }

        long maxBudget = 0;

        // Pengunjung
        int N = in.nextInt();
        for (int i = 0; i < N; i++){
            String t = in.next();
            long u = in.nextLong();
            if (u > maxBudget)
                maxBudget = u;
            listPengunjung.add(new Pengunjung(i+1,t,u));
        }


        boolean isFirstQ = true;

        // Query
        int T = in.nextInt();
        for (int i = 0; i < T; i++){
            String perintah = in.next();
            if (perintah.equals("A")){
                int idPengunjung = in.nextInt();
                int idWahana = in.nextInt();
                out.println(A(idPengunjung-1,idWahana-1));
            } else if (perintah.equals("E")){
                int idWahana = in.nextInt();
                out.println(E(idWahana));
            } else if (perintah.equals("S")){
                int idPengunjung = in.nextInt();
                int idWahana = in.nextInt();
                out.println(S(idPengunjung,idWahana));
            } else if (perintah.equals("F")){
                int p = in.nextInt();
                out.println(F(p));
            } else{
                if (isFirstQ){
                    dp = new Rencana[M + 1][(int) maxBudget + 1][3];
                    isFirstQ = false;
                }
                int idPengunjung = in.nextInt();
                out.println(O(idPengunjung));
            }
        }

        // don't forget to close the output
        out.close();
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the usual Scanner(System.in) and System.out
    // use these classes to avoid your fast algorithm gets Time Limit Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong(){
            return Long.parseLong(next());
        }

    }
}
