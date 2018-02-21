; (load-file "src/wordcount/word_frequencies.clj")
; (wordcount.word-frequencies/word-frequencies ["one" "potato" "two" "potato" "three" "potato" "four"])

(ns wordcount.word-frequencies)

(defn word-frequencies [words]
  (reduce
    (fn [counts word] (assoc counts word (inc (get counts word 0))))
    {} words))
