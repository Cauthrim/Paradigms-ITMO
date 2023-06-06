package queue;

import java.util.function.Predicate;

public interface Queue {
    /*
    Model: a[1]...a[n]
    Invariant: for all i=1...n a[i] != null

    Assume immutable(n) as for i = 1...n a'[i] = a[i]
     */

    //Pred: n >= 1
    //Post: R == a[n] && n' == n-1 && for i = 1...n' a'[i] = a[i+1]
    Object dequeue();

    //Pred: x != null
    //Post: n' = n + 1 && a'[n'] == x && immutable(n)
    void enqueue(Object x);

    //Pred: true
    //Post: n' = 0
    void clear();

    //Pred: true
    //Post: R == n && n' == n && immutable(n)
    int size();

    //Pred: n >= 1
    //Post: R == a[1] && n' == n && immutable(n)
    Object element();

    //Pred: true
    //Post: (R is true if n == 0, else R is false) && n' == n && immutable(n)
    boolean isEmpty();

    //Pred: predicate != null
    //Post: n' = n - k, where k is the number of i, such that test(a[i]) == true,
    //for each pair i and j, where i < j and test(a[i]) == test(a[j]) == false, a'[i'] = a[i], a'[j'] = a[j], i' < j'
    void removeIf(Predicate<Object> predicate);

    //Pred: predicate != null
    //Post: n' = n - k, where k is the number of i, such that test(a[i]) == false,
    //for each pair i and j, where i < j and test(a[i]) == test(a[j]) == true, a'[i'] = a[i], a'[j'] = a[j], i' < j'
    void retainIf(Predicate<Object> predicate);

    //Pred: predicate != null
    //n' is the length of the biggest prefix where for each a[i] test(a[i]) == true
    //for i = 1...n' a'[i] = a[i]
    void takeWhile(Predicate<Object> predicate);

    //Pred: predicate != null
    //Post: n' = n - k, where k is the length of the biggest prefix where for each a[i] test(a[i]) == true,
    //for i == 1...n-k a'[i] = a[i+k]
    void dropWhile(Predicate<Object> predicate);
}
