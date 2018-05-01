(ns dining-philosphers-stm.core)

; # is known as the dispatch character. It tells the Clojure reader how to interpret the next character using.
; #() is an annoymous function

(def philosophers (into [] (repeatedly 5 #(ref :thinking))))

(defn think [n]
  (println "Philosopher" n " is thinking")
  (Thread/sleep (rand 1000)))

(defn eat [n]
  (println "Philosopher" n " is eating")
  (Thread/sleep (rand 1000)))

; First attempt
; There is a problem:
; The problem is that we’re accessing the values of left and right with @.
; Clojure’s STM guarantees that no two transactions will make inconsistent
; modifications to the same ref, but we’re not modifying left or right,
; just examining their values. Some other transaction could modify them,
; invalidating the condition that adjacent philosophers can’t eat
; simultaneously.
; (defn claim-chopsticks [philosopher left right]
;   (dosync
;     (when (and (= @left :thinking) (= @right :thinking))
;       (ref-set philosopher :eating))))

; Second attempt
; Use ensure.  ensure ensures that the value of the ref it returns won’t
; be changed by another transaction.
; A nice property of this program is that because it does not use locks,
; it's impossible for deadlock to occur.
(defn claim-chopsticks [philosopher left right]
  (dosync
    (when (and (= (ensure left) :thinking) (= (ensure right) :thinking))
      (ref-set philosopher :eating))))

(defn release-chopsticks [philosopher]
  (dosync (ref-set philosopher :thinking)))

(defn philosopher-thread [n]
  (Thread.
    #(let [philosopher (philosophers n)
           left (philosophers (mod (- n 1) 5))
           right (philosophers (mod (+ n 1) 5))]
      (while true
        (think n)
        (when (claim-chopsticks philosopher left right)
          (eat n)
          (release-chopsticks philosopher))))))


(defn -main [& args]
  (let [threads (map philosopher-thread (range 5))]
    (doseq [thread threads] (.start thread))
    (doseq [thread threads] (.join thread))))
