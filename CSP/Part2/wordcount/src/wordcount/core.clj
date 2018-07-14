(ns wordcount.core
  (:require [clojure.core.async :as async :refer :all :exclude [map into reduce merge partition partition-by take]]
            [clojure.java.io :as io]
            [wordcount.feed :refer [new-links]]
            [wordcount.http :refer [http-get]]
            [wordcount.words :refer [get-words]]))

(defn get-counts [urls]
  (let [counts (chan)]
    (go (while true
          (let [url (<! urls)]
            (when-let [response (<! (http-get url))]
              (let [c (count (get-words (:body response)))]
                (>! counts [url c]))))))
    counts))

; Usage: lein run feeds.txt
(defn -main [feeds-file]
  (with-open [rdr (io/reader feeds-file)]
    (let [feed-urls (line-seq rdr)
          article-urls (doall (map new-links feed-urls))
          article-counts (doall (map get-counts article-urls))
          counts (async/merge article-counts)]
      (while true
        (println (<!! counts))))))
