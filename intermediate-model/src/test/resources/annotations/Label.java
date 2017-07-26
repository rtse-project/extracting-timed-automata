public class Label {
    public static int foo(int initial_i, int initial_j) {
        int i = initial_i + 1;
        int j = initial_j - 2;
        i++;
        mywhile:
        while (j > 3) {
            j--;
            if (i < 5) {
                break mywhile;
                i += 4000;
            }
        }
    }
    public void issueReopen(){
        outer:
        while (j > 0) {
            j = j * 2;
            while (j < 2) {
                break outer;
                j = j * 2;
            }
            j = i;
        }
    }
}