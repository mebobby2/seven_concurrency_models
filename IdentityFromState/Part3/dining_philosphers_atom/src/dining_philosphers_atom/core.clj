(ns dining-philosphers-atom.core)

(def philosophers (atom (into [] (repeat 5 :thinking))))

(defn think [n]
  (println "Philosopher" n " is thinking")
  (Thread/sleep (rand 1000)))

(defn eat [n]
  (println "Philosopher" n " is eating")
  (Thread/sleep (rand 1000)))

(defn claim-chopsticks! [philosopher left right]
  (swap! philosophers
    (fn [ps]
      (if (and (= (ps left) :thinking) (= (ps right) :thinking))
      (assoc ps philosopher :eating)
      ps)))
  (= (@philosophers philosopher) :eating))

(defn release-chopsticks [philosopher]
  (swap! philosophers assoc philosopher :thinking))

(defn philosopher-thread [philosopher]
  (Thread.
    #(let [left (mod (- philosopher 1) 5)
           right (mod (+ philosopher 1) 5)]
      (while true
        (think philosopher)
        (when (claim-chopsticks! philosopher left right)
          (eat philosopher)
          (release-chopsticks philosopher))))))


(defn -main [& args]
  (let [threads (map philosopher-thread (range 5))]
    (doseq [thread threads] (.start thread))
    (doseq [thread threads] (.join thread))))
