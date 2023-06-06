package queue;

public class ArrayQueueModule {
    private static Object[] elements = new Object[4];
    private static int arrSize = 4;
    private static int head;
    private static int tail;
    private static int size;

    public static int size() {
        return size;
    }

    public static Object element() {
        return elements[head];
    }

    public static Object peek() {
        return elements[(tail-1+arrSize)%arrSize];
    }

    public static boolean isEmpty() {
        return size() == 0;
    }

    public static void clear() {
        while (head != tail) {
            elements[head] = null;
            head = (head + 1) % arrSize;
        }
        size = 0;
    }

    private static void copyMove(Object[] copy) {
        int dequeSize = size();
        for (int i = 0; i < dequeSize; i++ ) {
            copy[i] = elements[head];
            head = (head + 1) % arrSize;
        }
        elements = copy;
        head = 0;
        tail = dequeSize;
    }

    private static void enlarge() {
        Object[] elementsCopy = new Object[arrSize * 2];
        copyMove(elementsCopy);
        arrSize *= 2;
    }

    private static void shrink() {
        Object[] elementsCopy = new Object[arrSize / 2];
        copyMove(elementsCopy);
        arrSize /= 2;
    }

    public static void enqueue(Object x) {
        if (head == (tail + 1) % arrSize) {
            enlarge();
        }

        elements[tail] = x;
        tail = (tail + 1) % arrSize;
        size++;
    }

    public static Object remove() {
        if (size() < arrSize / 4) {
            shrink();
        }

        tail = (tail - 1 + arrSize) % arrSize;
        Object res = elements[tail];
        elements[tail] = null;
        size--;
        return res;
    }

    public static void push(Object x) {
        if ((head - 1 + arrSize) % arrSize == tail) {
            enlarge();
        }

        head = (head - 1 + arrSize) % arrSize;
        elements[head] = x;
        size++;
    }

    public static Object dequeue() {
        if (size() < arrSize / 4) {
            shrink();
        }

        Object res = elements[head];
        elements[head] = null;
        head = (head + 1) % arrSize;
        size--;
        return res;
    }

    public static int indexOf(Object x) {
        for (int i = head, cnt = 0; i != tail; i = (i + 1) % arrSize, cnt++) {
            if (elements[i].equals(x)) {
                return cnt;
            }
        }

        return -1;
    }

    public static int lastIndexOf(Object x) {
        int res = -1;
        for (int i = head, cnt = 0; i != tail; i = (i + 1) % arrSize, cnt++ ) {
            if (elements[i].equals(x)) {
                res = cnt;
            }
        }

        return res;
    }
}
