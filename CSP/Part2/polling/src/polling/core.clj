(ns polling.core
  (:require [clojure.core.async :as async :refer :all
              :exclude [map into reduce merge partition partition-by take]]))

; Problem: Parking calls need to be made directly within a go block. Clojure's
; macro system wont be able to perform its magic otherwise.
; Hence, inside the action function, you cannot use <! or >!
(defn poll-fn [interval action]
  (let [seconds (* interval 1000)]
    (go (while true
          (action)
          (<! (timeout seconds))))))
; (poll-fn 10 #(println "Polling at:" (System/currentTimeMillis)))

; Solution
(defmacro poll [interval & body]
  `(let [seconds# (* ~interval 1000)]
      (go (while true
            (do ~@body)
            (<! (timeout seconds#))))))

; (poll 10
;   (println "Polling at:" (System/currentTimeMillis))
;   (println (<! ch)))
