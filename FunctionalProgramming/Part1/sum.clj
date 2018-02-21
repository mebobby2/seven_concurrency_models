; (load-file "sum.clj")
; (def numbers (into [] (range 0 10000000)))
; (time (sum/sum numbers))
; (time (sum/parallel-sum numbers))

(ns sum
  (:require [clojure.core.reducers :as r]))

(defn recursive-sum [numbers]
  (if (empty? numbers)
  0
  (+ (first numbers) (recursive-sum (rest numbers)))))

(defn reduce-sum [numbers]
  (reduce (fn [acc x] (+ acc x)) 0 numbers))

(defn sum [numbers]
  (reduce + numbers))


(defn parallel-sum [numbers]
  (r/fold + numbers))
