package queue;

public class LinkedQueue extends AbstractQueue {
    private Node head;
    private Node tail;

    private void init() {
        head = new Node(null, null, null);
        head.next = head;
        head.prev = head;
        tail = head;
    }

    public LinkedQueue() {
        init();
    }

    public Object element() {
        return head.next.element;
    }

    @Override
    protected void clearImpl() {
        init();
    }

    @Override
    protected void enqueueImpl(Object x) {
        tail = new Node(x, tail, null);
        tail.prev.next = tail;
    }

    @Override
    protected Object dequeueImpl() {
        head = head.next;
        head.prev = null;
        return head.element;
    }

    private static class Node {
        private final Object element;
        private Node prev;
        private Node next;

        public Node(Object element, Node prev, Node next) {
            this.element = element;
            this.prev = prev;
            this.next = next;
        }
    }
}
