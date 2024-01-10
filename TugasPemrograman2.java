import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

class Siswa implements Comparable<Siswa>{
    static int counterId = 1;
    long poin;
    int counterHukuman;
    int id;

    public Siswa(long poin){
        this.id = counterId;
        this.poin = poin;
        this.counterHukuman = 0;
        counterId++;
    }

    /**
     * Update point siswa
     */
    void addPoint(long poin){
        this.poin = this.poin + poin;
    }

    @Override
    public int compareTo(Siswa o) {
        if (this.poin != o.poin)
            return (int)(this.poin - o.poin);
        return o.id - this.id;
    }

    @Override
    public String toString() {
        return "ID: " + this.id + " Poin: " + this.poin;
    }
}

class Kelas implements Comparable<Kelas>{
    static int counterKelas = 1;
    int id;
    AVLTree rankingTree;
    int banyakSiswaAwal;

    double rataRataKelas;

    public Kelas(int banyakSiswaAwal){
        this.id = counterKelas;
        this.rankingTree = new AVLTree();
        this.banyakSiswaAwal = banyakSiswaAwal;
        this.rataRataKelas = 0;
        counterKelas++;
    }

    /**
     * Update rata-rata kelas
     */
    public void updateRataRata() {
        this.rataRataKelas = this.rankingTree.getTotal(this.rankingTree.root) / this.sizeTree();
    }

    /**
     * Method untuk menambahkan siswa ke rankingTree
     */
    void addSiswa(Siswa siswa){
        this.rankingTree.root = this.rankingTree.insert(this.rankingTree.root,siswa);
    }

    /**
     * Method yang mengembalikan siswa yang dihapus dari rankingTree serta mengupdate rankinTree.root setelah node
     * siswa dihapus
     */
    Siswa deleteSiswa(Siswa siswa){
        Node[] deleteResult = this.rankingTree.deleteNode(this.rankingTree.root,siswa);
        this.rankingTree.root = deleteResult[0];
        Node deletedNode = deleteResult[1];
        return (deletedNode == null) ? null : deletedNode.key;
    }

    /**
     * Hitung banyak siswa lain yang dapat diberikan tutor oleh siswa
     */
    int banyakTutor(Siswa siswa){
        return this.rankingTree.countLessOrEqual(siswa.poin,this.rankingTree.root);
    }

    /**
     * Method untuk mengembalikan list node bersisi siswa yang dihapus
     */
    ArrayList<Siswa> deleteAllSiswa(){
        return this.rankingTree.deleteAll();
    }

    /**
     * Method yang mengembalikan size root
     */
    int sizeTree(){
        return this.rankingTree.size(this.rankingTree.root);
    }

    /**
     * Ambil Siswa dengan peringkat terbaik
     */
    Siswa getSiswaTerbaik(){
        return this.rankingTree.maxValueNode(this.rankingTree.root).key;
    }

    /**
     * Ambil Siswa dengan peringkat terburuk
     */
    Siswa getSiswaTerburuk(){
        return this.rankingTree.minValueNode(this.rankingTree.root).key;
    }

    /**
     * Method untuk mengambil tiga siswa dengan peringkat terendah
     */
    Siswa[] getBottomThree(){
        Siswa[] bottomThree = new Siswa[3];
        for (int i = 0; i < 3; i++){
            Siswa bottomSiswa = this.deleteSiswa(this.rankingTree.minValueNode(this.rankingTree.root).key);
            bottomThree[i] = bottomSiswa;
        }

        return  bottomThree;
    }

    /**
     * Method untuk mengambil tiga siswa dengan peringkat terbaik
     */
    Siswa[] getUpperThree(){
        Siswa[] upperThree = new Siswa[3];
        for (int i = 0; i < 3; i++){
            Siswa upperSiswa = this.deleteSiswa(this.rankingTree.maxValueNode(this.rankingTree.root).key);
            upperThree[i] = upperSiswa;
        }

        return  upperThree;
    }

    @Override
    public int compareTo(Kelas o) {
        if (this.rataRataKelas != o.rataRataKelas)
            return (int) Math.ceil(this.rataRataKelas - o.rataRataKelas);
        return o.id - this.id;
    }

    @Override
    public String toString() {
        return "" + this.id + "Rata-rata: " + this.rataRataKelas;
    }
}

class DoublyLinkedList {
    private int nodeIdCounter = 1;
    ListNode first;
    ListNode current;
    ListNode last;
    int size = 0;

    /**
     * Method untuk menambahkan ListNode ke sisi kiri (prev) atau kanan (next) dari current ListNode
     */
    public ListNode add(Kelas element, char direction) {
        ListNode newNode = new ListNode(element,nodeIdCounter);
        nodeIdCounter++;

        if (size == 0){
            first = newNode;
            last = newNode;
            current = newNode;
            current.next = first;
            current.prev = last;
            size++;
            return null;
        }

        if (direction == 'R'){
            newNode.prev = current;
            newNode.next = current.next;
            newNode.prev.next = newNode;
            newNode.next.prev = newNode;

            if (current == last){
                last = newNode;
            }
            size++;
            return null;
        }

        newNode.prev = current.prev;
        newNode.next = current;
        newNode.prev.next = newNode;
        newNode.next.prev = newNode;

        if (current == first){
            first = newNode;
        }
        size++;
        return null;
    }

    /**
     * Method untuk menambah elemen di akhir linked list
     */
    public ListNode addLast(Kelas element) {
        ListNode newNode = new ListNode(element, nodeIdCounter);
        nodeIdCounter++;

        if (size == 0) {
            first = newNode;
            last = newNode;
            current = newNode;
            current.next = first;
            current.prev = last;
        } else {
            newNode.prev = last;
            newNode.next = first;
            newNode.prev.next = newNode;
            newNode.next.prev = newNode;
            last = newNode;
        }

        size++;
        return null;
    }


    /**
     * Method untuk menghapus ListNode di sisi kiri (prev) atau kanan (next) dari current ListNode
     */
    public ListNode delete(char direction) {
        if (direction == 'R'){
            ListNode temp = current.next;
            if (current.next == last){
                last = current;
            }
            current.next = current.next.next;
            current.next.prev = current;
            return temp;
        }

        ListNode temp = current.prev;
        if (current.prev == first){
            first = current;
        }
        current.prev = current.prev.prev;
        current.prev.next = current;
        return temp;

    }

    /**
     * Method untuk berpindah ke kiri (prev) atau kanan (next) dari current ListNode
     */
    public ListNode move(char direction) {
        if (direction == 'R'){
            current = current.next;
            return current;
        }
        current = current.prev;
        return current;
    }

    /**
     * Method untuk mengunjungi setiap ListNode pada DoublyLinkedList
     */
    public String traverse() {
        ListNode traverseNode = first;
        StringBuilder result = new StringBuilder();
        do {
            result.append(traverseNode + ((traverseNode.next != first) ? " | " : ""));
            traverseNode = traverseNode.next;
        } while (traverseNode != first);

        return result.toString();
    }

    /**
     * Split linked list menjadi 2 (bagian dari merge sort)
     */
    ListNode split(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode temp = slow.next;
        slow.next = null;
        return temp;
    }

    /**
     * Gabungkan dua linked list (bagian dari merge sort)
     */
    ListNode merge(ListNode first, ListNode second) {
        if (first == null) {
            return second;
        }

        if (second == null) {
            return first;
        }

        if (first.element.compareTo(second.element) > 0) {
            first.next = merge(first.next, second);
            first.next.prev = first;
            first.prev = null;
            return first;
        } else {
            second.next = merge(first, second.next);
            second.next.prev = second;
            second.prev = null;
            return second;
        }
    }

    /**
     * Fungsi utama dari proses merge sort
     * Referensi : Geeks for geeks dengan penyesuaian
     */
    ListNode mergeSortFunc(ListNode node)
    {
        if (node == null || node.next == null) {
            return node;
        }

        ListNode second = split(node);

        // Recur for left and right halves
        node = mergeSortFunc(node);
        second = mergeSortFunc(second);

        // Merge the two sorted halves
        return merge(node, second);
    }

    /**
     * Caller method untuk melaksanakan merge sort untuk this doublyLinkedList
     */
    void mergeSort(){
        // mengubah doubly linked agar tidak circular
        this.last.next = null;
        this.first.prev = null;

        // menghilangkan pointer last sementara
        this.last = null;

        this.first = mergeSortFunc(this.first); // didapat sorted doubly linked list dengan pointer first

        // mengembalikan pointer last
        this.last = this.first;
        while(this.last.next != null){
            this.last = this.last.next;
        }

        // mengembalikan sifat circular
        this.last.next = this.first;
        this.first.prev = this.last;
    }
}

class ListNode {

    Kelas element;
    ListNode next;
    ListNode prev;
    int id;

    ListNode(Kelas element, int id) {
        this.element = element;
        this.id = id;
    }

    public String toString() {
        return String.format("(ID:%d Elem:%s)", id, element);
    }
}

class Node
{
    Siswa key;
    int height;
    Node left;
    Node right;
    int size;

    Node(Siswa key)
    {
        this.key = key;
        this.height = 1;
        this.size = 1;
    }
}

/** Referensi: Geeks for geeks dengan penyesuaian */
class AVLTree {
    Node root;

    /**
     * Method untuk mengukur tinggi node
     */
    int height(Node N) {
        if (N == null)
            return 0;
        return N.height;
    }

    /**
     * Mengembalikan ukuran dengan root = Node N
     */
    int size(Node N){
        if (N == null)
            return 0;
        return N.size;
    }

    /**
     * Menjumlahkan semua poin dari key(siswa)
     */
    double getTotal(Node N){
        if (N == null)
            return 0;
        return N.key.poin + getTotal(N.left) + getTotal(N.right);
    }

    /**
     * Method untuk implementasi rotasi ke kanan
     */
    Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        // Update sizes
        y.size = size(y.left) + size(y.right) + 1;
        x.size = size(x.left) + size(x.right) + 1;

        // Return new root
        return x;
    }

    /**
     * Method untuk implementasi rotasi ke kiri
     */
    Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        // Update sizes
        x.size = size(x.left) + size(x.right) + 1;
        y.size = size(y.left) + size(y.right) + 1;

        // Return new root
        return y;
    }

    /**
     * Menghitung status balance. Balance jika |perbedaan tinggi| <= 1
     */
    int getBalance(Node N) {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }

    /**
     * Insert suatu key ke node
     */
    Node insert(Node node, Siswa key) {
        if (node == null)
            return (new Node(key));

        if (key.compareTo(node.key) < 0)
            node.left = insert(node.left, key);
        else if (key.compareTo(node.key) > 0)
            node.right = insert(node.right, key);

        // Update height of this ancestor node
        node.height = 1 + Math.max(height(node.left),
                height(node.right));

        // Update sizes
        node.size = 1 + size(node.left) + size(node.right);

        //Get the balance factor of this ancestor
        //node to check whether this node became
        //unbalanced
        int balance = getBalance(node);

        // If this node becomes unbalanced, then
        // there are 4 cases Left Left Case
        if (balance > 1 && key.compareTo(node.left.key) < 0)
            return rightRotate(node);

        // Right Right Case
        if (balance < -1 && key.compareTo(node.right.key) > 0)
            return leftRotate(node);

        // Left Right Case
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1  && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    /**
     * Cari Node dengan Nilai Minimum
     */
    Node minValueNode(Node node) {
        Node current = node;

        /* loop down to find the leftmost leaf */
        while (current.left != null)
            current = current.left;

        return current;
    }

    /**
     * Carn Node dengan Nilai Maximum
     */
    Node maxValueNode(Node node){
        Node current = node;

        /* loop down to find the rightmost leaf */
        while (current.right != null)
            current = current.right;

        return current;
    }

    /**
     * Delete suatu key (siswa) di sebuah node (root)
     * Kembalikan Node[] dengan index 0 node hasil dan index 1 node yang dihapus
     */
    Node[] deleteNode(Node root, Siswa key) {
        Node deletedNode = null;

        if (root == null)
            return new Node[]{root, null};

        if (key.compareTo(root.key) < 0) {
            Node[] result = deleteNode(root.left, key);
            root.left = result[0];
            deletedNode = result[1];
        } else if (key.compareTo(root.key) > 0) {
            Node[] result = deleteNode(root.right, key);
            root.right = result[0];
            deletedNode = result[1];
        } else {
            deletedNode = root;

            if ((root.left == null) || (root.right == null)) {
                Node temp = null;
                if (temp == root.left)
                    temp = root.right;
                else
                    temp = root.left;

                if (temp == null) {
                    temp = root;
                    root = null;
                } else
                    root = temp;
            } else {
                Node temp = minValueNode(root.right);
                root.key = temp.key;
                Node[] result = deleteNode(root.right, temp.key);
                root.right = result[0];
            }
        }

        if (root == null)
            return new Node[]{root, deletedNode};

        root.height = Math.max(height(root.left), height(root.right)) + 1;
        root.size = 1 + size(root.left) + size(root.right);

        int balance = getBalance(root);

        if (balance > 1 && getBalance(root.left) >= 0)
            return new Node[]{rightRotate(root), deletedNode};

        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return new Node[]{rightRotate(root), deletedNode};
        }

        if (balance < -1 && getBalance(root.right) <= 0)
            return new Node[]{leftRotate(root), deletedNode};

        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return new Node[]{leftRotate(root), deletedNode};
        }

        return new Node[]{root, deletedNode};
    }

    /**
     * Method untuk mengambil list node secara preorder
     */
    ArrayList<Siswa> preorder(Node node, ArrayList<Siswa> res){
        if (node != null){
            res.add(node.key);
            preorder(node.left,res);
            preorder(node.right,res);
        }
        return res;
    }

    /**
     * Method untuk menghapus semua node dan mengambil semua node tersebut
     */
    ArrayList<Siswa> deleteAll(){
        ArrayList<Siswa> res = preorder(root,new ArrayList<>());
        root = null;
        return res;
    }

    /**
     * Hitung banyak node (siswa) yang punya poin sama atau lebih kecil dari "poin"
     */
    int countLessOrEqual(long poin, Node root) {
        if (root == null)
            return 0;

        if (root.key.poin <= poin) {
            int leftSubtreeSize = (root.left != null) ? size(root.left) : 0;
            return leftSubtreeSize + 1 + countLessOrEqual(poin, root.right);
        } else {
            return countLessOrEqual(poin, root.left);
        }
    }
}

    public class TugasPemrograman2 {
        private static InputReader in;
        private static PrintWriter out;
        private static DoublyLinkedList listKelas = new DoublyLinkedList();
        private static ArrayList<Siswa> listSiswa = new ArrayList<>();

        static long T(long poin, int idSiswa){
            // Get Objek Siswa
            if (idSiswa > listSiswa.size()) // Jika tidak ada siswa dengan id = idSiswa
                return -1;
            Siswa siswa = listSiswa.get(idSiswa-1);

            // Delete siswa sementara dari ranking tree kelas sekarang
            Siswa deletedSiswa = listKelas.current.element.deleteSiswa(siswa);
            if (deletedSiswa == null) // Jika siswa tidak ditemukan di kelas ini
                return -1;

            // Tambahkan poin yang didapat dari tugas dan tutor
            long poinTambahan = listKelas.current.element.banyakTutor(siswa);
            if (poinTambahan > poin)
                poinTambahan = poin;
            poin += poinTambahan;
            siswa.addPoint(poin);

            // Masukkan kembali siswa ke ranking tree kelas sekarang
            listKelas.current.element.addSiswa(siswa);
            return siswa.poin;
        }

        static int G(char direction){
            // Pindahkan pointer current dan ambil object Node-nya
            ListNode nodeKelasSekarang = listKelas.move(direction);
            return nodeKelasSekarang.element.id;
        }

        static int C(int idSiswa){
            // Get Objek Siswa
            if (idSiswa > listSiswa.size()) // Jika tidak ada siswa dengan id = idSiswa
                return -1;
            Siswa siswa = listSiswa.get(idSiswa-1);

            // Delete siswa sementara dari ranking tree kelas sekarang
            Siswa deletedSiswa = listKelas.current.element.deleteSiswa(siswa);
            if (deletedSiswa == null) // Jika siswa tidak ditemukan di kelas ini
                return -1;

            // Memberikan hukuman poin = 0 untuk hukuman ke-berapapun
            siswa.poin = 0;
            siswa.counterHukuman += 1;

            // Memberikan hukuman tambahan tergantung byk hukuman yang telah dilakukan
            if (siswa.counterHukuman == 1){
                listKelas.current.element.addSiswa(siswa); // tetap di kelas yg sama
                return 0;
            } else if (siswa.counterHukuman == 2){
                listKelas.last.element.addSiswa(siswa); // pindahkan ke kelas paling buruk
                if (listKelas.current.element.sizeTree() < 6)
                    balancingKelas();
                return listKelas.last.element.id;
            } else {
                // buang dari sekolah
                if (listKelas.current.element.sizeTree() < 6)
                    balancingKelas();
                return siswa.id;
            }
        }

        static void balancingKelas(){
            ArrayList<Siswa> deletedSiswa = listKelas.current.element.deleteAllSiswa();
            if (listKelas.current.next != listKelas.first){ // jika kelas sudah urutan paling akhir
                listKelas.move('R');
                listKelas.delete('L');
            } else {
                listKelas.move('L');
                listKelas.delete('R');
            }
            for (Siswa siswa: deletedSiswa){               // pindahkan semua siswa dari kelas yang sudah dihapus
                listKelas.current.element.addSiswa(siswa);
            }
        }

        static int A(int banyakSiswa){
            // Buat kelas baru
            Kelas kelasBaru = new Kelas(banyakSiswa);

            // Tambahkan sebanyak banyakSiswa di kelas baru tsb
            for (int i = 0; i < banyakSiswa; i++){
                Siswa siswaBaru = new Siswa(0);
                kelasBaru.addSiswa(siswaBaru);
                listSiswa.add(siswaBaru);
            }

            // Tambahkan kelas ke daftar kelas
            listKelas.addLast(kelasBaru);
            return kelasBaru.id;
        }

        static String S(){
            String output = "";

            // Kasus kelas hanya 1
            if (listKelas.size == 1) {
                output = output + "-1 -1";
                return output;
            }


            Siswa[] betterBottomThree;
            Siswa[] worseUpperThree;

            if (listKelas.current.prev != listKelas.last){
                Siswa[] currentUpperThree = listKelas.current.element.getUpperThree(); // dapatkan tiga siswa terbaik sekaligus hapus

                listKelas.move('L');                                      // pindah ke kelas yg lebih baik
                betterBottomThree = listKelas.current.element.getBottomThree();   // dapatkan tiga siswa terburuk sekaligus hapus

                // Tambahkan 3 siswa terburuk ke kelas yg lebih baik
                for (Siswa siswa:currentUpperThree){
                    listKelas.current.element.addSiswa(siswa);
                }

                listKelas.move('R');
            } else {
                betterBottomThree = new Siswa[0];
            }


            if (listKelas.current.next != listKelas.first){
                Siswa[] currentBottomThree = listKelas.current.element.getBottomThree(); // dapatkan tiga siswa terburuk sekaligus hapus

                listKelas.move('R');                                     // pindah ke kelas yg lebih buruk
                worseUpperThree = listKelas.current.element.getUpperThree();     // dapatkan tiga siswa terbaik

                // Tambahkan 3 siswa teburuk dari kelas sebelumnya
                for (Siswa siswa:currentBottomThree){
                    listKelas.current.element.addSiswa(siswa);
                }

                listKelas.move('L');
            } else {
                worseUpperThree = new Siswa[0];
            }

            // Tambahkan 3 siswa terburuk dari kelas lebih baik
            for (Siswa siswa:betterBottomThree){
                listKelas.current.element.addSiswa(siswa);
            }

            // Tambahkan 3 siswa terbaik dari kelas lebih buruk
            for (Siswa siswa: worseUpperThree){
                listKelas.current.element.addSiswa(siswa);
            }

            output += listKelas.current.element.getSiswaTerbaik().id + " " + listKelas.current.element.getSiswaTerburuk().id;
            return output;
        }

        static int K(){
            // Update rata-rata seluruh kelas
            ListNode pointer = listKelas.first;
            do{
                pointer.element.updateRataRata();
                pointer = pointer.next;
            } while(pointer != listKelas.first);

            //Sort doubly linked list
            listKelas.mergeSort();

            // Mencari urutan kelas pakcil sekarang
            pointer = listKelas.first;
            int urutan = 1;
            do{
                if (pointer == listKelas.current)
                    return urutan;
                urutan++;
                pointer = pointer.next;
            } while (pointer != listKelas.first);
            return -1;
        }

        public static void main(String[] args) {
            InputStream inputStream = System.in;
            in = new InputReader(inputStream);
            OutputStream outputStream = System.out;
            out = new PrintWriter(outputStream);

            // Banyak Kelas
            int M = in.nextInt();
            for (int i = 0; i < M; i++){
                // Banyak Siswa Awal Kelas
                int Mi = in.nextInt();
                Kelas kelas = new Kelas(Mi);
                listKelas.addLast(kelas);
            }


            ListNode pointer = listKelas.first; // Objek pointer ke first pointer doubly linked list
            do{
                for (int i = 0; i< pointer.element.banyakSiswaAwal; i++){
                    // Untuk Setiap Siswa Awal Kelas Minta Poin Awal
                    long Pi = in.nextLong();
                    Siswa siswa = new Siswa(Pi);
                    listSiswa.add(siswa);
                    pointer.element.addSiswa(siswa);
                }
                pointer  = pointer.next;
            } while (pointer != listKelas.first);


            // Banyak Query
            int Q = in.nextInt();
            for (int i = 0; i < Q; i++){
                String perintah = in.next();
                if (perintah.equals("T")){
                    long poin = in.nextLong();
                    int idSiswa = in.nextInt();
                    out.println(T(poin,idSiswa));
                } else if (perintah.equals("G")){
                    char direction = in.nextChar();
                    out.println(G(direction));
                } else if (perintah.equals("C")){
                    int idSiswa = in.nextInt();
                    out.println(C(idSiswa));
                } else if (perintah.equals("A")){
                    int banyakSiswa = in.nextInt();
                    out.println(A(banyakSiswa));
                } else if (perintah.equals("S")){
                    out.println(S());
                } else if (perintah.equals("K")){
                    out.println(K());
                }
            }

            out.close();
        }


        // taken from https://www.programiz.com/dsa/avl-tree
        // a method to print the contents of a Tree data structure in a readable
        // format. it is encouraged to use this method for debugging purposes.
        // to use, simply copy and paste this line of code:
        // printTree(tree.root, "", true)
        static void printTree(Node currPtr, String indent, boolean last) {
            if (currPtr != null) {
                out.print(indent);
                if (last) {
                    out.print("R----");
                    indent += "   ";
                } else {
                    out.print("L----");
                    indent += "|  ";
                }
                out.println(currPtr.key);
                printTree(currPtr.left, indent, false);
                printTree(currPtr.right, indent, true);
            }
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

            public char nextChar() {
                return next().charAt(0);
            }

            public int nextInt() {
                return Integer.parseInt(next());
            }

            public long nextLong() {
                return Long.parseLong(next());
            }

        }
}
