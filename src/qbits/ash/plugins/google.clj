(ns qbits.ash.plugins.google
  (:require
   [qbits.ash :as ash]
   [clj-http.client :as client]))

(defn search
  [term]
  (or (-> (client/get "http://ajax.googleapis.com/ajax/services/search/web"
                      {:query-params {"v" "1.0" "q" term}
                       :throw-exceptions false
                       :as :json})
          :body
          :responseData
          :results
          first
          :unescapedUrl)
      (format "No result for %s" term)))

(defn handler [bot]
  (ash/listen bot :on-message
              (fn [event]
                (when-let [t (second (re-find #"^\?g\s+(.+)" (:content event)))]
                  (ash/reply bot event (search t) true)))))
