(ns qbits.ash.plugins.askbot
  (:require
   [qbits.ash :as ash]))

(def answers
  [["Hell no!"
    "Nay!"
    "That sounds like a bad idea"
    "No sir"
    "No master"
    "I wouldn't do that"
    "Please no"
    "Sarrah Connor?\nOops wrong channel\nI think you're right"]
   ["Hell yes!"
    "Yay!"
    "Absolutely"
    "Yes sir"
    "Yes master"
    "That sounds like a great idea"
    "I would do that"
    "Sarrah Connor?\nOops wrong channel\nI think you're wrong"]])

(defn ask []
  (let [a (answers (rand-int 2))]
    (a (rand-int (count a)))))

(defn handler
  [bot]
  (let [ptn (re-pattern (format "%s\\s*\\?$" (.getName bot)))]
    (ash/listen bot :on-message
                (fn [event]
                  (when (re-find ptn (:content event))
                    (ash/reply bot event (ask)))))))
