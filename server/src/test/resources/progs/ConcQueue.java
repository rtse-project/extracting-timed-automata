public class ConcQueue {
    private Pointer head, tail;
    public ConcQueue() { // set head and tail to a dummy node
        head = new Pointer(new Element(), 0);
        head.ptr.next = new Pointer(null, 0);
        tail = new Pointer(head.ptr, head.count);
    }
    public void Enqueue(String data) {
        Pointer ltail = new Pointer(null, 0);
        Pointer lnext = new Pointer(null, 0);
        Element node = new Element();
        node.data = data;
        node.next = new Pointer(null, 0);
        while (true) {
            tail.copyTo(ltail); // make a local copy of tail
            ltail.ptr.next.copyTo(lnext); // copy  tail.next;
            if (lnext.ptr == null) {
                // if successful swing next to new node
                if (ltail.ptr.next.CAS(lnext, node, lnext.count + 1))
                    break; // if successful, enqueue is done
            } else  // Need to swing tail to last node
                tail.CAS(ltail, lnext.ptr, ltail.count + 1);
        }
        // Try to swing tail to inserted node
        tail.CAS(ltail, node, ltail.count + 1);
    }
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
