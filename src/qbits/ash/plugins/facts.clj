(ns qbits.ash.plugins.facts
  (:require
   [qbits.ash :as ash]
   [qbits.ash.store :as store]
   [clojure.string :as string])
  (:import
   [java.security MessageDigest]))

(defonce facts (.getTreeMap store/db "facts"))

(defn make-id
  [fact]
  (apply str (map #(format "%02x" (bit-and % 0xff))
                  (.digest
                   (doto (MessageDigest/getInstance "MD5")
                     (.update (.getBytes (-> fact string/trim string/lower-case))))))))

(defn handler
  [bot]
  ;;ask
  (ash/listen bot :on-message
              (fn [event]
                (when-let [fact (second (re-find
                                         (re-pattern
                                          (format "%s\\s*\\:(.+)"
                                                  (.getName bot)))
                                         (:content event)))]
                  (when-let [value (store/fetch facts (make-id fact))]
                    (ash/reply bot event value true)))))
  ;; store
  (ash/listen bot :on-message
              (fn [event]
                (when-let [fact (next (re-find #"^addfact! (.+): (.+)"
                                               (:content event)))]
                  (store/put! facts
                              (-> fact first make-id)
                              (second fact))
                  (ash/reply bot event "Understood!"))))

  ;; remove
  (ash/listen bot :on-message
              (fn [event]
                (when-let [fact (next (re-find #"^rmfact! (.+)"
                                               (:content event)))]
                  (let [id (-> fact first make-id) ]
                    (if (store/exists? facts id)
                      (do (store/del! facts id)
                          (ash/reply bot event "I don't know what this means anymore."))
                      (ash/reply bot event "I don't know about that, sorry.")))))))