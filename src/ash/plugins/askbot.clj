(ns ash.plugins.askbot
  (:require
   [ash.bot :as irc]))

(def answers
  [["Hell no!"
    "Nay!"
    "That sounds like a bad idea"
    "No sir"
    "No master"
    "I wouldn't do that"
    "Please no"
    "Sarrah Connor?\nOups wrong channel\nI think you're right"]
   ["Hell yes!"
    "Yay!"
    "Absolutely"
    "Yes sir"
    "Yes master"
    "That sounds like a great idea"
    "I would do that"
    "Sarrah Connor?\nOups wrong channel\nI think you're wrong"]])

(defn ask []
  (let [a (answers (rand-int 2))]
    (a (rand-int (count a)))))

(defn handler
  ""
  [bot]
  (let [bot-name (.getName bot)]
    (irc/listen bot :on-message
                (fn [event]
                  (when (.endsWith (:content event) (str bot-name "?"))
                    (irc/reply bot event (ask)))))))
