(ns wordcount.core
  (:require [wordcount.pages :refer :all]
            [wordcount.words :refer :all])
  (:gen-class))

(defn count-words-sequential [pages]
  (frequencies (mapcat get-words pages)))

(defn count-words [pages]
  (reduce (partial merge-with +)
    (pmap count-words-sequential (partition-all 100 pages))))

(defn -main [& args]
  (time (count-words (take 100000 (get-pages "/Users/bob/Downloads/wikipedia-dump.xml"))))
  (shutdown-agents))
