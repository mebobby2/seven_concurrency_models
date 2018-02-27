(ns wordcount.core
  (:require [wordcount.pages :refer :all]
            [wordcount.words :refer :all])
  (:gen-class))

(defn count-words-sequential [pages]
  (frequencies (mapcat get-words pages)))

(defn count-words-parallel [pages]
  (reduce (partial merge-with +)
    (pmap #(frequencies (get-words %)) pages)))

(defn count-words-batch [pages]
  (reduce (partial merge-with +)
    (pmap count-words-sequential (partition-all 100 pages))))

; Elapsed time: 2213.701866 msecs
; (defn -main [& args]
;   (time (count-words-sequential (take 10000 (get-pages "/Users/bob/Downloads/wikipedia-dump.xml"))))
;   (shutdown-agents))

; Elapsed time: 2067.365061 msecs
; (defn -main [& args]
;   (time (count-words-parallel (take 10000 (get-pages "/Users/bob/Downloads/wikipedia-dump.xml"))))
;   (shutdown-agents))

; Elapsed time: 1423.556727 msecs
(defn -main [& args]
  (time (count-words-batch (take 10000 (get-pages "/Users/bob/Downloads/wikipedia-dump.xml"))))
  (shutdown-agents))


; The batch version is the fastest. Why batch is faster than the parallel one?
; The parallel version merges after each page is processed. This means more merging.
; The batch version merges after each batch (which contains 100 pages) is processed.
; This results in less merges needed, hence its faster.
; This is the same as the Java versions of WordCount. Hence, the motto is, the less
; merges the better!
