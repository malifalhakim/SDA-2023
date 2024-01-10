import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class TugasPemrograman3 {
    private static InputReader in;
    private static PrintWriter out;
    private static Graph graph;
    static ArrayList<Long>[] djikstraMemo; // Penyimpanan Djikstra
    static ArrayList<Integer> specialRooms = new ArrayList<>(); // Penyimpanan ID yang berkategori special

    // Method Query M -> Mencari Jumlah Harta Karun Terbanyak
    static int M(long groupSize){
        ArrayList<Long> minDist = djikstraMemo[1];
        if (minDist == null){
            // Lakukan djikstra jika djikstra untuk source 1 belum dilakukan
            minDist = djikstraMemo[1] = graph.djikstra(1);
        }

        // Hitung Treasure yang bisa didapat
        int countTreasure = 0;
        for (int id:specialRooms){
            if (minDist.get(id) <= groupSize)
                countTreasure++;
        }

        return countTreasure;
    }

    // Method Query S -> Mencari Harta Karun Terdekat
    static long S(int idStart){
        long minGroup = Long.MAX_VALUE; // Variabel untuk size group terkecil
        long sizeGroup;  // Variabel penampung sementara size group untuk suatu special rooms

        // Cek sizeGroup terkecil dari setiap ruangan dan ubah minGroup jika size group lebih kecil
        for (int id:specialRooms){
            if (djikstraMemo[idStart] == null){
                ArrayList<Long> minDist = djikstraMemo[id];
                if (minDist == null){
                    minDist = djikstraMemo[id] = graph.djikstra(id);
                }
                sizeGroup = minDist.get(idStart);
            } else {
                sizeGroup = djikstraMemo[idStart].get(id);
            }


            if (sizeGroup < minGroup)
                minGroup = sizeGroup;
        }

        return minGroup;
    }

    // Method Query T -> Menentukan kebisaan untuk mencapai idEnd melalui idMiddle
    static String T(int idStart, int idMiddle, int idEnd,long groupSize){
        String status = "N";
        long minGroup;

        // Mendapatkan jarak terpendek dari idStart ke idMiddle
        ArrayList<Long> minDist = djikstraMemo[idMiddle];
        if (minDist == null){
            minDist = djikstraMemo[idStart];
            if (minDist == null){
                djikstraMemo[idStart] = minDist = graph.djikstra(idStart);
            }
            minGroup = minDist.get(idMiddle);
        } else {
            minGroup = minDist.get(idStart);
        }

        // Mengupdate status kebisaan
        if (groupSize >= minGroup){
            status = "H";
        } else {
            return status;
        }

        // Menghitung jarak terpendak dari idMiddle ke idEnd
        minDist = djikstraMemo[idMiddle];
        if (minDist == null){
            minDist = djikstraMemo[idEnd];
            if (minDist == null){
                djikstraMemo[idEnd] = minDist = graph.djikstra(idEnd);
            }
            minGroup = minDist.get(idMiddle);
        } else {
            minGroup = minDist.get(idEnd);
        }

        // Mengupdate status kebisaan
        if (groupSize >= minGroup){
            status = "Y";
        } else {
            return status;
        }

        return status;
    }

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int banyakVertex = in.nextInt();
        int banyakEdge = in.nextInt();

        // Inisasi graph dan penyimpanan djikstra
        graph = new Graph(banyakVertex);
        djikstraMemo = new ArrayList[banyakVertex + 1];

        for (int i = 1; i <= banyakVertex; i++){
            char type = in.nextChar();
            if (type == 'S')
                specialRooms.add(i);
        }

        for (int i = 0; i < banyakEdge; i++){
            int idVertexA = in.nextInt();
            int idVertexB = in.nextInt();
            long cost = in.nextLong();

            graph.addEdge(idVertexA,idVertexB,cost);
        }

        graph.primMST(); // Membentuk graph mst

        int banyakQuery = in.nextInt();
        for (int i = 0; i < banyakQuery; i++){
            char command = in.nextChar();
            if (command == 'M'){
                long groupSize = in.nextLong();
                out.println(M(groupSize));
            } else if (command == 'S'){
                int idStart = in.nextInt();
                out.println(S(idStart));
            } else {
                int idStart = in.nextInt();
                int idMiddle = in.nextInt();
                int idEnd = in.nextInt();
                long groupSize = in.nextLong();
                out.println(T(idStart,idMiddle,idEnd,groupSize));
            }
        }

        // don't forget to close/flush the output
        out.close();
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
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

        public char nextChar() {
            return next().charAt(0);
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }
}

// Class untuk menggambarkan suatu path yang menghubungkan 2 vertex
// dan weightMax yang harus dilewatinya
class Pair implements Comparable<Pair> {
    int src;
    long weightMax;

    public Pair(int src, long weightMax) {
        this.src = src;
        this.weightMax = weightMax;
    }

    @Override
    public int compareTo(Pair o) {
        return (int) (this.weightMax - o.weightMax);
    }
}

// Class edge yang menghubungkan vertex
class Edge implements Comparable<Edge>{
    int to;
    long weight;

    public Edge(int to, long weight) {
        this.to = to;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge o) {
        return Long.compare(this.weight,o.weight);
    }
}

class Graph {
    public int V; // Banyak Verteks
    public ArrayList<ArrayList<Edge>> adj;  // Adjacency List

    public Graph(int v) {
        this.V = v;
        this.adj = new ArrayList<>();
        for (int i = 0; i <= v; i++)
            this.adj.add(new ArrayList<>());

    }

    // Menghapus isi setiap adjacency list dari setiap vertex
    public void clearAdjacencyList(){
        for (int i = 0; i <= this.V; i++){
            this.adj.get(i).clear();
        }
    }

    // Menambahkan edge (undirected) pada graph
    public void addEdge(int from, int to, long weight) {
        this.adj.get(from).add(new Edge(to, weight));
        this.adj.get(to).add(new Edge(from,weight));
    }

    // Modifikasi djikstra untuk menemukan weight terbesar yang HARUS dilewati untuk berpindah
    // dari satu verteks ke verteks lain
    public ArrayList<Long> djikstra(int source) {
        if (source == 0)
            return null;

        ArrayList<Long> dist = new ArrayList<>();
        for (int i = 0; i <= this.V; i++)
            dist.add(Long.MAX_VALUE);
        dist.set(source, (long) 0);

        MinHeap<Pair> pq = new MinHeap<>();
        pq.add(new Pair(source, 0));

        while (!pq.isEmpty()) {
            Pair curr = pq.poll();
            int v = curr.src; // source
            long w = curr.weightMax;

            if (w > dist.get(v))
                continue;

            for (Edge e : this.adj.get(v)) {
                int u = e.to;
                long weight = e.weight;
                long maxWeightNow = Math.max(dist.get(v),weight);
                if (maxWeightNow < dist.get(u)) {
                    dist.set(u, maxWeightNow);
                    pq.add(new Pair(u, dist.get(u)));
                }
            }
        }

        return dist;
    }

    // Membentuk MST dari graph saat ini
    public void primMST(){
        int[] parent = new int[this.V + 1]; // Array untuk menyimpan predecessor / parent dari suatu verteks
        long[] key = new long[this.V + 1]; // Berisi weight setiap verteks dan parentnya
        boolean[] inMST = new boolean[this.V + 1]; // cek apakah verteks sudah masuk MST

        for (int i = 0; i <= V; i++) {
            parent[i] = -1;
            key[i] = Long.MAX_VALUE;
            inMST[i] = false;
        }

        MinHeap<Edge> pq = new MinHeap<>();
        key[1] = 0;
        pq.add(new Edge(1,key[1]));

        while (!pq.isEmpty()){
            Edge edge = pq.poll();
            int destVertex = edge.to;
            inMST[destVertex] = true;

            for (Edge e: this.adj.get(destVertex)){
                int adjDestVertex = e.to;
                long weight = e.weight;

                if ((!inMST[adjDestVertex]) && (weight < key[adjDestVertex])){
                    parent[adjDestVertex] = destVertex;
                    key[adjDestVertex] = weight;
                    pq.add(new Edge(adjDestVertex, key[adjDestVertex]));
                }
            }
        }

        // Membersihkan adj list setiap verteks dan memasukkan edge baru ke adj list
        this.clearAdjacencyList();
        for (int i = 1; i <= V; i++){
            if (parent[i] != -1){
                this.addEdge(i,parent[i],key[i]);
            }
        }

    }

    // Fungsi untuk memprint graph
    void printGraph()
    {
        for (int i = 1; i <= V; i++) {
            System.out.print(i+ "->");
            for (Edge x : this.adj.get(i)) {
                System.out.printf("[To : %d | Weight : %d]",x.to,x.weight);
            }
            System.out.println();
        }
    }
}


class MinHeap<T extends Comparable<T>> {
    private ArrayList<T> heapArray;

    private int current_heap_size;

    // Constructor
    public MinHeap() {
        heapArray = new ArrayList<>();
        current_heap_size = 0;
    }

    // Swap dua object pada heap
    private void swap(ArrayList<T> arr, int a, int b) {
        T temp = arr.get(a);
        arr.set(a,arr.get(b));
        arr.set(b,temp);
    }

    // Mengembalikan index parent
    private int parent(int index) {
        return (index - 1) / 2;
    }

    // Mengembalikan index node kiri
    private int left(int index) {
        return 2 * index + 1;
    }

    // Mengembalikan index node kanan
    private int right(int index) {
        return 2 * index + 2;
    }


    // Inserts sebuah object baru pada heap
    public void add(T key) {
        int i = current_heap_size;
        heapArray.add(i,key);
        current_heap_size++;

        // Percolate up jika diperlukan
        while (i != 0 && heapArray.get(i).compareTo(heapArray.get(parent(i))) < 0) {
            swap(heapArray, i, parent(i));
            i = parent(i);
        }
    }

    public boolean isEmpty(){
        return (current_heap_size == 0);
    }

    // Mengembalikan dan meremove elemen terkecil pada heap
    public T poll() {
        if (current_heap_size <= 0) {
            return null;
        }

        if (current_heap_size == 1) {
            current_heap_size--;
            return heapArray.get(0);
        }

        // Ambil elemen terkecil
        T root = heapArray.get(0);

        // Hapus elemen terkecil
        heapArray.set(0,heapArray.get(current_heap_size - 1));
        current_heap_size--;
        // percolate down
        MinHeapify(heapArray,current_heap_size,0);

        return root;
    }

    // Percolate Down
    private void MinHeapify(ArrayList<T> arr,int size,int index) {
        int l = left(index);
        int r = right(index);

        int smallest = index;
        if (l < size && arr.get(l).compareTo(arr.get(smallest)) < 0) {
            smallest = l;
        }
        if (r < size && arr.get(r).compareTo(arr.get(smallest)) < 0) {
            smallest = r;
        }

        if (smallest != index) {
            swap(arr, index, smallest);
            MinHeapify(arr,size,smallest);
        }
    }

    @Override
    public String toString() {
        return heapArray.subList(0,current_heap_size).toString();
    }
}

// Referensi :
// 1. Modifikasi Lab 8
// 2. https://www.geeksforgeeks.org/prims-mst-for-adjacency-list-representation-greedy-algo-6/
// 3. https://www.geeksforgeeks.org/binary-heap/