package queue;

public class ArrayQueueADT {
    private Object[] elements;
    private int arrSize;
    private int head;
    private int tail;
    private int size;

    public ArrayQueueADT() {
        elements = new Object[4];
        arrSize = 4;
    }

    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    public static Object element(ArrayQueueADT queue) {
        return queue.elements[queue.head];
    }

    public static Object peek(ArrayQueueADT queue) {
        return queue.elements[(queue.tail-1+queue.arrSize)%queue.arrSize];
    }

    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    public static void clear(ArrayQueueADT queue) {
        while (queue.head != queue.tail) {
            queue.elements[queue.head] = null;
            queue.head = (queue.head + 1) % queue.arrSize;
        }
        queue.size = 0;
    }

    private static void copyMove(ArrayQueueADT queue, Object[] copy) {
        int dequeSize = size(queue);
        for (int i = 0; i < dequeSize; i++ ) {
            copy[i] = queue.elements[queue.head];
            queue.head = (queue.head + 1) % queue.arrSize;
        }
        queue.elements = copy;
        queue.head = 0;
        queue.tail = dequeSize;
    }

    private static void enlarge(ArrayQueueADT queue) {
        Object[] elementsCopy = new Object[queue.arrSize * 2];
        copyMove(queue, elementsCopy);
        queue.arrSize *= 2;
    }

    private static void shrink(ArrayQueueADT queue) {
        Object[] elementsCopy = new Object[queue.arrSize / 2];
        copyMove(queue, elementsCopy);
        queue.arrSize /= 2;
    }

    public static void enqueue(ArrayQueueADT queue, Object x) {
        if (queue.head == (queue.tail + 1) % queue.arrSize) {
            enlarge(queue);
        }

        queue.elements[queue.tail] = x;
        queue.tail = (queue.tail + 1) % queue.arrSize;
        queue.size++;
    }

    public static Object remove(ArrayQueueADT queue) {
        if (size(queue) < queue.arrSize / 4) {
            shrink(queue);
        }

        queue.tail = (queue.tail - 1 + queue.arrSize) % queue.arrSize;
        Object res = queue.elements[queue.tail];
        queue.elements[queue.tail] = null;
        queue.size--;
        return res;
    }

    public static void push(ArrayQueueADT queue, Object x) {
        if ((queue.head - 1 + queue.arrSize) % queue.arrSize == queue.tail) {
            enlarge(queue);
        }

        queue.head = (queue.head - 1 + queue.arrSize) % queue.arrSize;
        queue.elements[queue.head] = x;
        queue.size++;
    }

    public static Object dequeue(ArrayQueueADT queue) {
        if (size(queue) < queue.arrSize / 4) {
            shrink(queue);
        }

        Object res = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = (queue.head + 1) % queue.arrSize;
        queue.size--;
        return res;
    }

    public static int indexOf(ArrayQueueADT queue, Object x) {
        for (int i = queue.head, cnt = 0; i != queue.tail; i = (i + 1) % queue.arrSize, cnt++) {
            if (queue.elements[i].equals(x)) {
                return cnt;
            }
        }

        return -1;
    }

    public static int lastIndexOf(ArrayQueueADT queue, Object x) {
        int res = -1;
        for (int i = queue.head, cnt = 0; i != queue.tail; i = (i + 1) % queue.arrSize, cnt++ ) {
            if (queue.elements[i].equals(x)) {
                res = cnt;
            }
        }

        return res;
    }
}
