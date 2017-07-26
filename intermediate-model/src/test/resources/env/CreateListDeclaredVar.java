public class ABC {
    public String Dequeue() {
        Pointer ltail = new Pointer(null, 0);
        Pointer lhead = new Pointer(null, 0);
        Pointer lnext = new Pointer(null, 0);
        while (true) {
            tail.copyTo(ltail); // make a local copy of tail
            head.copyTo(lhead); // make a local copy of head
            lhead.ptr.next.copyTo(lnext); // copy head.next;
            if (lhead.ptr == ltail.ptr) { //queue empty or tail is behind
                if (lnext.ptr == null) return null; // queue is empty
                tail.CAS(ltail, lnext.ptr, ltail.count + 1); //advance tail
            } else {
                String return_val = lnext.ptr.data;
                if (head.CAS(lhead, lnext.ptr, lhead.count + 1))
                    return return_val; // dequeue is done
            }
        }
    }
}