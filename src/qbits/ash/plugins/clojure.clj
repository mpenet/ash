(ns qbits.ash.plugins.clojure
  (:require
   [qbits.ash.bot :as irc]
   [clojail.core :as jail-core]
   [clojail.testers :as jail-testers]
   [clojure.repl :as repl]
   [clojure.string :as string]))

;; Stolen from lazybot!
(defn doc* [v]
  (if (symbol? v)
    (str "Special: " v "; " (:doc (#'clojure.repl/special-doc v)))
    (let [[arglists macro docs]
          (-> v
              meta
              ((juxt :arglists
                     :macro
                     :doc)))
          docs (and docs (string/replace docs #"\s+" " "))]
      (str (and macro "Macro ") arglists "; " docs))))

(def sb (jail-core/sandbox jail-testers/secure-tester
                           :transform pr-str
                           :init '(defmacro doc [s]
                                    `(doc* (var ~s)))))

(defn handler [bot]
  (irc/listen bot :on-message
              (fn [event]
                (when-let [c (second (re-find #"^,(.+)" (:content event)))]
                  (irc/reply bot event
                             (try (sb (read-string c))
                                  (catch Exception e e))
                             true)))))
