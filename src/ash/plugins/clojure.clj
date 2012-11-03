(ns ash.plugins.clojure
  (:require
   [ash.bot :as irc]
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

(defn exec
  [m]
  (try (sb (read-string (.substring
                         (:content m) 1)))
       (catch Exception e e)))

(defn handler [bot]
  (irc/listen bot :on-message
              (fn [m _]
                (cond
                  (re-find #"^," (:content m))
                  (irc/send-message bot
                                    (:channel m)
                                    (exec m))))))
