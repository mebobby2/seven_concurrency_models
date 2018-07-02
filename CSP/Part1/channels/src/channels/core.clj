(ns channels.core
  (:require [clojure.core.async :as async :refer :all
              :exclude [map into reduce merge partition partition-by take]]))


(defn readall!! [ch]
  (loop [coll []]
    (if-let [x (<!! ch)]
      (recur (conj coll x))
      coll)))

(defn writeall!! [ch coll]
  (doseq [x coll]
    (>!! ch x))
  (close! ch))

; (def dc (chan (dropping-buffer 5)))
; (onto-chan dc (range 0 10))
; (<!! (async/into [] dc))
; [0 1 2 3 4]

; (def sc (chan (sliding-buffer 5)))
; (onto-chan sc (range 0 10))
; (<!! (async/into [] sc))
; [5 6 7 8 9]

(defn go-add [x y]
  (<!! (nth (iterate #(go (inc (<! %))) (go x)) y)))

; 1. The anonymous function #(go (inc (<! %))) creates a go block that
;    takes a single channel argument, reads a single value from it,
;    and returns a channel containing that value incremented by one.
; 2. This function is passed to iterate with an initial value of
;    (go x) (a channel that simply has the value x written to it).
;    Recall that iterate returns a lazy sequence of the form
;    (x (f x) (f (f x)) (f (f (f x))) ...)
; 3. We read the y-th element of this sequence with nth, the value of
;    which will be a channel containing the result of incrementing x y times.
; 4. Finally, we read the value of that channel with <!!

(defn map-chan [f from]
  (let [to (chan)]
    (go-loop []
      (when-let [x (<! from)]
        (>! to (f x))
        (recur))
      (close! to))
    to))


; A Concurrent Sieve of Eratosthenes

(defn factor? [x y]
  (zero? (mod y x)))

(defn get-primes [limit]
  (let [primes (chan)
        numbers (to-chan (range 2 limit))]
    (go-loop [ch numbers]
      (when-let [prime (<! ch)]
        (>! primes prime)
        (recur (remove< (partial factor? prime) ch)))
      (close! primes))
    primes))
; (let [primes (get-primes 100000)]
;   (loop []
;     (when-let [prime (<!! primes)]
;       (println prime)
;       (recur))))


(defn get-primes-timeout []
  (let [primes (chan)
        numbers (to-chan (iterate inc 2))]
      (go-loop [ch numbers]
        (when-let [prime (<! ch)]
          (>! primes prime)
          (recur (remove< (partial factor? prime) ch)))
        (close! primes))
      primes))

; (let [primes (get-primes-timeout)
;       limit (timeout (* 10 1000))]
;   (loop []
;     (alt!! :priority true
;       limit nil
;       primes ([prime] (println prime) (recur))))))
