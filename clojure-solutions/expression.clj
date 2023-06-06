(load-file "proto.clj")

(defn constant [value] (constantly value))

(defn variable [name] (fn [mp] (get mp name)))

(defn genFunc [func] (fn [& args] (fn [mp] (apply func (map #(% mp) args)))))
(def add (genFunc +))
(def subtract (genFunc -))
(def multiply (genFunc *))

(defn divide ([arg] (fn [mp] (/ 1 (double (arg mp)))))
  ([div & args] (fn [mp] (/ (div mp) (double (apply * (map #(% mp) args)))))))

(defn negate [val] (fn [arg] (- (val arg))))

(defn exp [val] (fn [arg] (Math/exp (val arg))))

(defn sumexp [& args] (fn [mp] (apply + (map #((exp %) mp) args))))

(defn softmax [& args] (fn [mp] (/ ((exp (first args)) mp) (double ((apply sumexp args) mp)))))

(def exprName {'+ add '- subtract '* multiply '/ divide 'negate negate 'sumexp sumexp 'softmax softmax})

(defn parse [args names vals] (cond (number? args) ((first vals) args)
                                    (symbol? args) ((second vals) (name args))
                                    :else (apply (get names (first args)) (map #(parse % names vals) (rest args)))))

(defn parseFunction [str] (parse (read-string str) exprName [constant variable]))

(def oper (field :oper))
(def subexpr (field :subexpr))
(def value (field :value))
(def varn (field :varn))
(def sym (field :sym))
(def _diff (field :diff))

(declare zero one)

(def ConstPrototype {:oper (fn [obj] (constant (value obj))) :diff (fn [obj] (constantly zero))})
(defn _Constant [this value] (assoc this
                               :sym (str value)
                               :value value))
(def Constant (constructor _Constant ConstPrototype))
(def zero (Constant 0))
(def one (Constant 1))

(def VariablePrototype {:oper (fn [obj] (variable (varn obj)))
                        :diff (fn [obj] (fn [var] (if (= var (varn obj)) one zero)))})
(defn _Variable [this name] (assoc this
                              :sym name
                              :varn name))
(def Variable (constructor _Variable VariablePrototype))

(defn diff [obj var] (((_diff obj) obj) var))
(defn evaluate [obj mp] (((oper obj) obj) mp))

(defn toString [obj] (if (contains? obj :subexpr)
                       (if (== (count (subexpr obj)) 0)
                         (str "(" (sym obj) " )")
                         (str "(" (sym obj) (apply str (map #(str " " (toString %)) (subexpr obj))) ")"))
                       (str (sym obj))))

(defn createOperation [sym eval funcDiff]
  (let [prot {:sym  sym
              :oper (fn [obj] (apply eval (map #((oper %) %) (subexpr obj))))
              :diff (fn [obj] (fn [var] (funcDiff var (subexpr obj) (map #(diff % var) (subexpr obj)))))}]
    (fn [& args] {:prototype prot :subexpr args})))

(def Add (createOperation "+" add (fn [var args dif] (apply Add dif))))

(def Negate (createOperation "negate" negate (fn [var args dif] (apply Negate dif))))

(def Subtract (createOperation "-" subtract (fn [var args dif] (apply Subtract dif))))

(def Multiply (createOperation "*" multiply
                               (fn [var args dif] (let [vecargs (vec args)]
                                                    (apply Add (for [i (range (count args))]
                                                                 (apply Multiply (assoc vecargs i (nth dif i)))))))))

(def Divide (createOperation "/" divide (fn [var args dif]
                                          (let [mul (apply Multiply (rest args))]
                                            (if (== (count args) 1)
                                              (Divide (Subtract (first dif))
                                                      (Multiply (first args) (first args)))
                                              (apply Subtract (for [i (range (count args))]
                                                                (Divide (Multiply (first args) (nth dif i))
                                                                        (Multiply (nth args i) mul)))))))))

(def Exp (createOperation "exp" exp (fn [var args dif] (apply Multiply (apply Exp args) dif))))

(def Sumexp (createOperation "sumexp" sumexp (fn [var args dif] (diff (apply Add (map Exp args)) var))))

(def Softmax (createOperation "softmax" softmax (fn [var args dif]
                                                  (diff (Divide (Exp (first args)) (apply Sumexp args)) var))))

(def objName {'+ Add '- Subtract '* Multiply '/ Divide 'negate Negate 'sumexp Sumexp 'softmax Softmax})
(defn parseObject [str] (parse (read-string str) objName [Constant Variable]))
