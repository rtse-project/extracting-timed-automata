public class SeqQueue {
    class Element {
        public String data;
        public Element next;
        public Element(String s, Element e) {
            data = s;
            next = e;
        }
    }
    public Element head, tail;
    public SeqQueue() {
        head = null;
        tail = null;
    }
    public SeqQueue(SeqQueue copy) {
        Element node;
        head = copy.head;
        tail = copy.tail;
        for (Element i = head; i != null; i = i.next)
            node = new Element(i.data, i.next);
    }
    public void Enqueue(String data) {
        Element temp = new Element(data, null);
        if (tail == null) {
            tail = temp;
            head = tail;
        } else {
            tail.next = temp;
            tail = temp;
        }
    }
    public String Dequeue() {
        if (head == null) return null;
        String returnval = head.data;
        head = head.next;
        return returnval;
    }
}
