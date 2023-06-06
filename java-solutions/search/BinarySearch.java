package search;

public class BinarySearch {
    //arr.length > 0 -> min(arr[i]) <= val <= max(arr[i]) && for all i in [0, arr.length-1], arr[i] > arr[i+1]
    //arr[-1] = +inf and arr[arr.length] = -inf is assumed
    public static int iterativeSearch(int val, int[] arr) {
        //Pred
        int posl = -1;
        //arr[posl'] > val
        int posr = arr.length;
        //arr[posl'] > val && arr[posr'] <= val

        //arr[posl'] > val && arr[posr'] <= val
        while (posl != posr - 1) {
            //arr[posl'] > val && arr[posr'] <= val && posr - posl > 1
            // (posr > posl since arr[posl'] > val >= arr[posr'] && function Pred
            int middle = (posl + posr) / 2;
            //posl' < middle < posr' as posl' <= posr' - 2 and posr' >= posl' + 2
            // -> posl'+1 <= middle <= posr'-1
            if (arr[middle] > val) {
                //arr[middle] > val && && arr[posr'] <= val
                posl = middle;
                //arr[posl'] > val && arr[posr'] <= val
            } else {
                //arr[middle] <= val 7& arr[posl'] > val
                posr = middle;
                //arr[posl'] > val && arr[posr'] <= val
            }
            //arr[posl'] > val && arr[posr'] <= val
        }

        return posr;
        //arr[posr'] <= val && arr[posr'] > arr[i] for all i > posr && arr[posl'] > val
        // && arr[posl'] < arr[j] for all j < posl' && posl' == posr' - 1
        // -> posr' is the minimal index with arr[posr'] <= val
    }
    //R is the minimal arr index, so that arr[R] <= val

    //arr.length > 0 -> min(arr[i]) <= val <= max(arr[i]) && for all i in [0, arr.length-1], arr[i] > arr[i+1]
    //arr[-1] = +inf and arr[arr.length] = -inf is assumed
    //arr[posl] > val && arr[posr] <= val
    public static int recursiveSearch(int val, int[] arr, int posl, int posr) {
        if (posl == posr - 1) {
            //posl + 1 = posr
            return posr;
            //posl is maximum index with arr[posl] > val and posr is the next index with arr[posr] <= val
            // -> posr is the minimum index with arr[posr] <= val
        } else {
            //arr[posl'] > val && arr[posr'] <= val && posr - posl > 1
            // (posr > posl since arr[posl'] > val >= arr[posr'] && function Pred
            int middle = (posl + posr) / 2;
            //posl' < middle < posr' as posl' <= posr' - 2 and posr' >= posl' + 2
            // -> posl'+1 <= middle <= posr'-1
            if (arr[middle] > val) {
                //arr[middle] > val && middle > posl -> for all i between posl and middle arr[i] > val
                return recursiveSearch(val, arr, middle, posr);
                //arr[posr'] <= val && arr[posr'] > arr[i] for all i > posr && arr[posl'] > val
                // && arr[posl'] < arr[j] for all j < posl' && posl' == posr' - 1
                // -> posr' is the minimal index with arr[posr'] <= val
            } else {
                //arr[middle] <= val && middle < posr -> for all i between middle and posr arr[i] <= val
                return recursiveSearch(val, arr, posl, middle);
                //arr[posr'] <= val && arr[posr'] > arr[i] for all i > posr && arr[posl'] > val
                // && arr[posl'] < arr[j] for all j < posl' && posl' == posr' - 1
                // -> posr' is the minimal index with arr[posr'] <= val
            }
        }
    }
    //R is the minimal arr index, so that arr[R] <= val

    //args != null
    public static void main(String[] args) {
        int val = Integer.parseInt(args[0]);
        int[] interactive = new int[args.length-1];

        //for all i in [0, args.length-1], args[i] represents an int
        for (int i = 1; i < args.length; i++) {
            interactive[i-1] = Integer.parseInt(args[i]);
        }
        //interactive.length = args.length
        // and for all i in [0, interactive.length] interactive[i] is the int representation of args[i]


        //We assume that interactive[-1] = +inf and interactive[interactive.length] = -inf
        //for all i in [0, interactive.length-1], interactive[i] > interactive[i+1]
        //System.out.println(iterativeSearch(val, interactive));
        System.out.println(recursiveSearch(val, interactive, -1, interactive.length));
        //O(utput) is the minimal index in interactive such that interactive[O] <= val
    }
}
