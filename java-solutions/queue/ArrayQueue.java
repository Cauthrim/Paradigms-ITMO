package queue;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements;
    private int arrSize;
    private int head;
    private int tail;

    public ArrayQueue() {
        elements = new Object[4];
        arrSize = 4;
    }

    public Object peek() {
        return elements[(tail - 1 + arrSize) % arrSize];
    }

    public Object element() {
        return elements[head];
    }

    @Override
    protected void clearImpl() {
        while (head != tail) {
            elements[head] = null;
            head = (head + 1) % arrSize;
        }
    }

    private void copyMove(Object[] copy) {
        int dequeSize = size();
        for (int i = 0; i < dequeSize; i++ ) {
            copy[i] = elements[head];
            head = (head + 1) % arrSize;
        }
        elements = copy;
        head = 0;
        tail = dequeSize;
    }

    private void enlarge() {
        Object[] elementsCopy = new Object[arrSize * 2];
        copyMove(elementsCopy);
        arrSize *= 2;
    }

    private void shrink() {
        Object[] elementsCopy = new Object[arrSize / 2];
        copyMove(elementsCopy);
        arrSize /= 2;
    }

    @Override
    protected void enqueueImpl(Object x) {
        if (head == (tail + 1) % arrSize) {
            enlarge();
        }

        elements[tail] = x;
        tail = (tail + 1) % arrSize;
    }

    //Pred: n >= 1
    //Post: R == a[n] && n' == n-1 && for i = 1...n' a'[i] = a[i]
    public Object remove() {
        if (size() < arrSize / 4) {
            shrink();
        }

        tail = (tail - 1 + arrSize) % arrSize;
        Object res = elements[tail];
        elements[tail] = null;
        size--;
        return res;
    }

    //Pred: x != null
    //Post: n' = n + 1 && a'[1] == x && for i = 2...n' a'[i] = a[i-1]
    public void push(Object x) {
        if ((head - 1 + arrSize) % arrSize == tail) {
            enlarge();
        }

        head = (head - 1 + arrSize) % arrSize;
        elements[head] = x;
        size++;
    }

    @Override
    protected Object dequeueImpl() {
        if (size() < arrSize / 4) {
            shrink();
        }

        Object res = elements[head];
        elements[head] = null;
        head = (head + 1) % arrSize;
        return res;
    }

    //Pred: x != null
    //Post: R is -1 if there is no i such that a[i] == x, or R is the minimum such i
    public  int indexOf(Object x) {
        for (int i = head, cnt = 0; i != tail; i = (i + 1) % arrSize, cnt++) {
            if (elements[i].equals(x)) {
                return cnt;
            }
        }

        return -1;
    }

    //Pred: x != null
    //Post: R is -1 if there is no i such that a[i] == x, or R is the maximum such i
    public  int lastIndexOf(Object x) {
        int res = -1;
        for (int i = head, cnt = 0; i != tail; i = (i + 1) % arrSize, cnt++ ) {
            if (elements[i].equals(x)) {
                res = cnt;
            }
        }

        return res;
    }
}
