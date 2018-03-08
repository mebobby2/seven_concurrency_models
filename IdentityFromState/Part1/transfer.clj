(ns transfer)

(def attempts (atom 0))
(def transfers (agent 0)) ; agents are transaction aware

(defn transfer [from to amount]
  (dosync
    (swap! attempts inc) ; side-effect in transaction - DON'T DO THIS
    (send transfers inc)
    (alter from - amount)
    (alter to + amount)))


(def checking (ref 10000))
(def savings (ref 20000))

(defn stress-thread [from to iterations amount]
  (Thread. #(dotimes [_ iterations] (transfer from to amount))))

(defn -main [& args]
  (println "Before: Checking = " @checking " Savings = " @savings)
  (let [t1 (stress-thread checking savings 100 100)
        t2 (stress-thread savings checking 200 100)]
    (.start t1)
    (.start t2)
    (.join t1)
    (.join t2))
  (await transfers)
  (println "Attempts: " @attempts)
  (println "Transfers: " @transfers)
  (println "After: Checking = " @checking " Savings = " @savings))
