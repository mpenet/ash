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

(defn put
  ""
  [trigger fact]
  (.put facts (make-id trigger) fact)
  (.commit store/db))

(defn fetch
  ""
  [trigger]
  (.get facts (make-id trigger)))

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
                  (when-let [value (fetch fact)]
                    (ash/reply bot event value true)))))
  ;; store
  (ash/listen bot :on-message
              (fn [event]
                (when-let [fact (next (re-find #"^fact!! (.+): (.+)"
                                               (:content event)))]
                  (put (first fact)
                       (second fact))
                  (ash/reply bot event "Fact saved!")))))
