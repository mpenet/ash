(ns ash.plugins.wolfram
  (:require
   [ash.bot :as irc]
   [clj-http.client :as client]
   [clojure.xml :as xml]
   [clojure.zip :as zip]))

(defn search
  [app-id term]
  (if-let [result (-> (client/get (format "http://api.wolframalpha.com/v2/query?"
                                       term app-id)
                               {:query-params {"input" term
                                               "appid" app-id}
                                :throw-exceptions false})

                   :body
                   .getBytes
                   (java.io.ByteArrayInputStream.)
                   xml/parse
                   zip/xml-zip
                   first
                   :content
                   second
                   :content
                   first
                   :content
                   first
                   :content
                   first)]
    (format "%s ⇒ http://www.wolframalpha.com/input/?i=%s"
            result
            (java.net.URLEncoder/encode term))
    (format "No result for %s" term)))

(defn handler [bot app-id]
  (irc/listen bot :on-message
              (fn [event]
                (when-let [t (second (re-find #"^\?w\s+(.+)" (:content event)))]
                  (irc/reply bot event (search app-id t) true)))))
