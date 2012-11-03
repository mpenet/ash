(ns ash.plugins.bashorg
  (:require
   [ash.bot :as irc]
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
  (irc/listen bot :on-message
              (fn [{:keys [content channel]} _]
                (when-let [t (second (re-find #"^\!b" content))]
                  (irc/send-message bot
                                    channel
                                    (get-quote)
                                    true)))))
