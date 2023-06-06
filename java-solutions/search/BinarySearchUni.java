package search;

public class BinarySearchUni {
    //Pred: arr is not empty, and there exists a single k such that for all i: -1 < i < k-1, arr[i] > arr[i+1],
    // and for all j: k <= j < arr.length, arr[j] < arr[j+1] and arr[k] <= arr[k-1]
    public static int iterativeSearchUni(int[] arr) {
        //Pred and
        //indices apart from posl and posr lie in [0, arr.length-1], arr[-1] and arr[arr.length] are assumed to be +inf
        int posl = -1;
        //for all i < posl, arr[i] > arr[i+1]
        int posr = arr.length-1;
        //for all i < posl, arr[i] > arr[i+1] && for all j >= posr, arr[j] < arr[j+1]

        //for all i < posl, arr[i] > arr[i+1] && for all j >= posr, arr[j] < arr[j+1]
        while (posl != posr - 1) {
            //Pred && posr - posl > 1 (posl < posr because of function Pre)
            int middle = (posl + posr) / 2;
            //posl < middle < posr as posl <= posr - 2 and posr >= posl + 2
            // -> posl+1 <= middle <= posr-1
            if (arr[middle] >= arr[middle+1]) {
                //arr[middle] >= arr[middle+1]
                if (arr[middle] == arr[middle+1]) {
                    //arr[middle] = arr[middle+1]
                    return middle+1;
                    //R is the k since it is the only index that allows arr[k-1] = arr[k]
                }
                //arr[middle] > arr[middle+1] && arr[posl] < arr[posl-1]
                // -> from function Pred for all j in [posl, middle], arr[j] > arr[j+1]
                // -> from previous posl condition for all j in [0, middle], arr[j] > arr[j+1]
                posl = middle;
                //for all i < posl', arr[i] > arr[i+1]
            } else {
                //arr[middle] < arr[middle+1] && arr[posr] < arr[posr+1]
                // -> from function Pred for all j in [middle, posr], arr[j] < arr[j+1]
                // -> from previous posr condition for all j in [middle, arr.length), arr[j] < arr[j+1]
                posr = middle;
                //for all i >= posr', arr[posr'] < arr[posr'+1]
            }
            //for all i < posl', arr[i] > arr[i+1] && for all j >= posr', arr[j] < arr[j+1]
        }
        //for all i < posl, arr[i] > arr[i+1] && for all j >= posr, arr[j] < arr[j+1] and posl == posr-1

        return posr;
    }
    //Post: R is the k, since for all i in (-1, R-1), arr[i] > arr[i+1]
    // and for all j in [R, arr.length), arr[j] < arr[j+1]

    //Pred: arr is not empty and there exists a single k such that for all i: -1 < i < k-1, arr[i] > arr[i+1]
    // and for all j: k <= j < arr.length, arr[j] < arr[j+1] and arr[k] <= arr[k-1]
    // -1 <= posl, posr <= arr.length-1, arr[-1] and arr[arr.length] are assumed to be +inf,
    // other indices lie in [0, arr.length-1]
    // for all i < posl, arr[i] > arr[i+1] && for all j >= posr, arr[j] < arr[j+1]
    public static int recursiveSearchUni(int[] arr, int posl, int posr) {
        //Pred
        if (posl == posr - 1) {
            //for all i < posl, arr[i] > arr[i+1] && for all j >= posr, arr[j] < arr[j+1]
            return posr;
            //Post
        } else {
            //Pred && posr - posl > 1(posr > posl because of function Pre)
            int middle = (posl + posr) / 2;
            //posl < middle < posr as posl <= posr - 2 and posr >= posl + 2
            // -> posl+1 <= middle <= posr-1
            if (arr[middle] >= arr[middle+1]) {
                //arr[middle] > arr[middle+1] && arr[posl] < arr[posl-1]
                // -> from function Pred for all j in [posl, middle], arr[j] > arr[j+1]
                // -> from previous posl condition for all j in [0, middle], arr[j] > arr[j+1]
                posl = middle;
                //for all i < posl', arr[i] > arr[i+1]
            } else {
                //arr[middle] < arr[middle+1] && arr[posr] < arr[posr+1]
                // -> from function Pred for all j in [middle, posr], arr[j] < arr[j+1]
                // -> from previous posr condition for all j in [middle, arr.length), arr[j] < arr[j+1]
                posr = middle;
                //for all i >= posr', arr[posr'] < arr[posr'+1]
            }
            return recursiveSearchUni(arr, posl, posr);
        }
    }
    //Post: R is the k, since for all i in (-1, R-1), arr[i] > arr[i+1]
    // and for all j in [R, arr.length), arr[j] < arr[j+1]

    //args != null and all args[i] represent integer numbers
    //arr is an int representation of args: there exists a single k such that for all i: -1 < i < k, arr[i] < arr[i-1]
    //and for all j: k <= j < arr.length, arr[j] < arr[j+1] and arr[k] <= arr[k-1]
    public static void main(String[] args) {
        int[] interactive = new int[args.length];

        for (int i = 0; i < args.length; i++) {
            interactive[i] = Integer.parseInt(args[i]);
        }

        //System.out.println(iterativeSearchUni(interactive));
        System.out.println(recursiveSearchUni(interactive, -1, interactive.length-1));
        //for all i: -1 < i < k-1, arr[i] > arr[i+1], for all j: k <= j < arr.length, arr[j] < arr[j+1]
        // -> k is the number of elements in the descending array
    }
}
