(ns logger)

(defn now []
  (System/currentTimeMillis))

(def log-entries (agent []))

(defn log [entry]
  (send log-entries conj [(now) entry]))
