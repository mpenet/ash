(ns ash.plugins.bitbucket
  (:require
   [ash.bot :as irc]
   [clj-http.client :as client]))

(defn get-issue
  [user password account repo id]
  (or (-> (client/get (format "https://api.bitbucket.org/1.0/repositories/%s/%s/issues/%s"
                              account repo id)
                   {:basic-auth [user password]
                    :throw-exceptions false
                    :as :json})
          :body
          ((fn [issue]
             (format "Issue #%s %s: https://bitbucket.org/%s/%s/issue/%s"
                     id
                     (:title issue)
                     account
                     repo
                     id))))
      (format "No result for %s" id)))

(defn handler [bot {:keys [login password account repo]}]
  (irc/listen bot :on-message
              (fn [m _]
                (when (re-find #"^\#.+" (:content m))
                  (irc/send-message bot
                                    (:channel m)
                                    (get-issue login
                                               password
                                               account
                                               repo
                                               (.substring (:content m) 1))
                                    true)))))