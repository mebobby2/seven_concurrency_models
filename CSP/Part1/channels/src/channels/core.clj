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
