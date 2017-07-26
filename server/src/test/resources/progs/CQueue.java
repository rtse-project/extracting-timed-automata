public class CQueue {
    private LLSC x;
    public CQueue() {
        x = new LLSC(new SeqQueue());
    }
    public void Enqueue(String data) {
        SeqQueue new_queue;
        ObjPointer local = new ObjPointer();
        while (true) {
            x.load_linked(local);
            new_queue = new SeqQueue((SeqQueue) local.obj);
            new_queue.Enqueue(data);
            if (x.store_conditional(local, new_queue))
                return;
        }
    }
    public String Dequeue() {
        SeqQueue new_queue;
        ObjPointer local = new ObjPointer();
        String returnval;
        while (true) {
            x.load_linked(local);
            new_queue = new SeqQueue((SeqQueue) local.obj);
            returnval = new_queue.Dequeue();
            if (x.store_conditional(local, new_queue))
                return returnval;
        }
    }
}
