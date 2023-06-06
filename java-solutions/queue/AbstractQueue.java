package queue;

import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
    protected int size;

    public void clear() {
        clearImpl();

        size = 0;
    }

    public void enqueue(Object x) {
        enqueueImpl(x);

        size++;
    }

    public Object dequeue() {
        assert size >= 1;
        Object res = dequeueImpl();
        size--;

        return res;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }


    private void massPredChange(Predicate<Object> predicate) {
        for (int i = 0, num = size; i < num; i++) {
            Object curr = dequeue();
            if(!predicate.test(curr)) {
                enqueue(curr);
            }
        }
    }

    public void removeIf(Predicate<Object> predicate) {
        massPredChange(predicate);
    }

    public void retainIf(Predicate<Object> predicate) {
        massPredChange(predicate.negate());
    }

    public void takeWhile(Predicate<Object> predicate) {
        for (int i = 0, num = size; i < num; i++) {
            Object curr = dequeue();
            if (!predicate.test(curr)) {
                for (; i < num-1; i++) {
                    dequeue();
                }
                break;
            } else {
                enqueue(curr);
            }
        }
    }

    public void dropWhile(Predicate<Object> predicate) {
        for (int i = 0, num = size; i < num; i++) {
            Object curr = element();
            if (!predicate.test(curr)) {
                break;
            } else {
                dequeue();
            }
        }
    }

    protected abstract void clearImpl();

    protected abstract void enqueueImpl(Object x);

    protected abstract Object dequeueImpl();
}
