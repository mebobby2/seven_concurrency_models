(defproject wordcount "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [rome/rome "1.0"]
                 [http-kit "2.1.16"]]
  :main wordcount.core
  :jvm-opts ^:replace ["-Xms4G" "-Xmx4G" "-XX:NewRatio=8" "-server"])
