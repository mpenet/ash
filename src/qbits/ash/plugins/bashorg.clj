(ns qbits.ash.plugins.bashorg
  (:require
   [qbits.ash :as ash]
   [clj-http.client :as client])
  (:import [org.jsoup Jsoup]))


(defn get-quote
  []
  (-> (Jsoup/connect "http://bash.org/?random1")
      (.get)
      (.select ".qt")
      (.first)
      (.html)
      (.replaceAll "&gt;" ">")
      (.replaceAll "&lt;" "<")
      (.split "<br />")
      (#(map clojure.string/trim %))
      seq))

(defn handler [bot]
  (ash/listen bot :on-message
              (fn [event]
                (when-let [t (second (re-find #"^\!b" (:content event)))]
                  (ash/reply bot event (get-quote) true)))))
