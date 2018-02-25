(ns wordcount.core
  (:require [wordcount.pages :refer :all]
            [wordcount.words :refer :all])
  (:gen-class))

(defn count-words-sequential [pages]
  (frequencies (mapcat get-words pages)))


(defn -main [& args]
  (time (count-words-sequential (take 100000 (get-pages "/Users/bob/Downloads/wikipedia-dump.xml"))))
  (shutdown-agents))
