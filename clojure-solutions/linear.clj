(defn checkVec [& args]
  (every? #(and (vector? %) (every? number? %) (== (count %) (count (first args)))) args))

(defn checkMat [& args]
  (every? #(and (vector? %) (apply checkVec %) (== (count %) (count (first args)))
                (== (count (first %)) (count (first (first args))))) args))

(defn tensFunc [fn args]
  (letfn [(rec [& args] (if (number? (first args)) (apply fn args) (apply mapv rec args)))]
    (apply rec args)))

(defn vecFunc [fn & args]
  {:pre  [(apply checkVec args)]}
  (tensFunc fn args))

;(def v+ (makeV +))
(def v+ (partial vecFunc +))

(def v- (partial vecFunc -))

(def v* (partial vecFunc *))

(def vd (partial vecFunc /))

(defn binVect [v1 v2]
  (let [a1 (nth v1 0) a2 (nth v1 1) a3 (nth v1 2)
        b1 (nth v2 0) b2 (nth v2 1) b3 (nth v2 2)]
    (vector (- (* a2 b3) (* a3 b2))
            (- (- (* a1 b3) (* a3 b1)))
            (- (* a1 b2) (* a2 b1)))))

(defn vect [& args]
  {:pre  [(apply checkVec args)]}
  (reduce binVect args))

(defn scalar [& args]
  {:pre  [(apply checkVec args)]}
  (apply + (apply v* args)))

(defn v*s [vec & args]
  {:pre  [(checkVec vec)]}
  (let [arg (apply * args)]
    (mapv #(* % arg) vec)))

(defn matrFunc [fn & args]
  {:pre  [(apply checkMat args)]}
  (tensFunc fn args))

(def m+ (partial matrFunc +))

(def m- (partial matrFunc -))

(def m* (partial matrFunc *))

(def md (partial matrFunc /))

(defn m*s [matr & args]
  {:pre  [(checkMat matr) (every? number? args)]}
  (let [arg (apply * args)]
    (mapv #(v*s % arg) matr)))

(defn m*v [matr vect]
  {:pre  [(checkMat matr)]}
  (mapv #(apply + (v* % vect)) matr))

(defn transpose [matr]
  {:pre  [(checkMat matr)]}
  (apply mapv vector matr))

(defn v*m [vec matr] (mapv #(apply + (v* % vec)) matr))
(defn binM*M [m1 m2] (let [m2 (transpose m2)] (mapv #(v*m % m2) m1)))
(defn m*m [& args]
  {:pre  [(every? checkMat args)]}
  (reduce binM*M args))

(defn broad [dim1 dim2 v] (if (= dim2 dim1) v (recur dim1 (conj dim2 (nth dim1 (count dim2)))
                                                     (vec (repeat (nth dim1 (count dim2)) v)))))

(defn checkNil [arg] (cond (number? arg) true (empty? arg) false :else (every? checkNil arg)))

(defn getDim [arg]
  (letfn [(recurDim [currDim currArg]
            (if (number? currArg) currDim
                                  (recur (conj currDim (count currArg)) (first currArg))))]
    (recurDim [] arg)))

(defn getMadim [& args] (let [dims (mapv #(getDim %) args) ma (apply max (map count dims))]
                          (vec (rseq (some #(when (== (count %) ma) %) dims)))))

(defn broadTensSet [& args]
  (let [maDim (apply getMadim args)] (mapv #(broad maDim (vec (reverse (getDim %))) %) args)))

(defn checkTens [& args]
  (and (every? checkNil args) (let [maDim (apply getMadim args)]
                                (every? #(let [dim (reverse (getDim %))]
                                           (= dim (subvec maDim 0 (count dim)))) args))))

(defn checkedTensFunc [fn & args]
  {:pre  [(apply checkTens args)]}
  (let [args (apply broadTensSet args)] (tensFunc fn args)))

(def hb+ (partial checkedTensFunc +))

(def hb- (partial checkedTensFunc -))

(def hb* (partial checkedTensFunc *))

(def hbd (partial checkedTensFunc /))
