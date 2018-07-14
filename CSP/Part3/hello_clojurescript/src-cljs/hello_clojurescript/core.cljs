(ns hello-clojurescript.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.dom :as dom]
            [cljs.core.async :refer [<! timeout]]))

(defn output [elem message]
  (dom/append elem message (dom/createDom "br")))

(defn start []
  (let [content (dom/getElement "content")]
    (go
      (while true
        (<! (timeout 1000))
        (output content "Hello from task 1")))
    (go
      (while true
        (<! (timeout 1500))
        (output content "Hello from task 2")))))

(set! (.-onload js/window) start)
