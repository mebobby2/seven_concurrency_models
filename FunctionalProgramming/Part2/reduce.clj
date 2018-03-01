(ns part2.reduce
  (:require [clojure.core.protocols :refer [CollReduce coll-reduce]]
            [clojure.core.reducers :refer [CollFold coll-fold]])
  (:require [clojure.core.reducers :as r]))

(defn my-reduce
  ([f coll] (coll-reduce coll f))
  ([f init coll] (coll-reduce coll f init)))

(defn my-fold
  ([reducef coll]
    (my-fold reducef reducef coll))
  ([combinef reducef coll]
    (my-fold 512 combinef reducef coll))
  ([n combinef reducef coll]
    (coll-fold coll n combinef reducef)))

(defn make-reducer [reducible transformf]
  (reify
    CollFold
    (coll-fold [_ n combinef reducef]
      (coll-fold reducible n combinef (transformf reducef)))

    CollReduce
    (coll-reduce [_ f1]
      (coll-reduce reducible (transformf f1) (f1)))
    (coll-reduce [_ f1 init]
      (coll-reduce reducible (transformf f1) init))))

(defn my-map [mapf reducible]
  (make-reducer reducible
    (fn [reducef]
      (fn [acc v]
        (reducef acc (mapf v))))))

(defn parallel-frequencies [coll]
  (r/fold
    (partial merge-with +)
    (fn [counts x] (assoc counts x (inc (get counts x 0))))
    coll))

(defn my-filter [filterf reducible]
  (make-reducer reducible
    (fn [reducef]
      (fn [acc v]
        (if (filterf v)
          (reducef acc v)
          acc)))))

(defn my-flatten [reducible]
  (make-reducer reducible
    (fn [reducef]
      (fn [acc v]
        (if (sequential? v)
          (coll-reduce (my-flatten v) reducef acc)
          (reducef acc v))))))
