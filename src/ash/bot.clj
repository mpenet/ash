(ns ash.bot
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log])
  (:import [org.pircbotx PircBotX]
           [org.pircbotx.hooks ListenerAdapter]
           [org.pircbotx.hooks.events MessageEvent]))

(defn join-channels
  [bot channels]
  (log/info (format "join %s" channels))
  (doseq [chan channels]
    (.joinChannel bot chan))
  bot)

(defn channels
  [bot]
  (->> bot .getChannelsNames (into #{})))

(defn send-message
  ([bot target message]
     (log/info (format "send %s %s" target message))
     (.sendMessage bot target (str message))
     bot)
  ([bot target message safe?]
     (send-message bot target
                   (if safe?
                     (str "⇒ " message)
                     message))))

(defn disconnect
  [bot]
  (log/info "disconnect")
  (.disconnect bot)
  bot)

(defn reconnect
  [bot]
  (log/info "reconnect")
  (.reconnect bot)
  bot)

(defn shutdown
  [bot]
  (log/info "shutdown")
  (.shutdown bot)
  bot)

(defn format-message
  [event]
  {:user (.. event getUser getNick)
   :channel (.. event getChannel getName)
   :timestamp (.getTimestamp event)
   :content  (.getMessage event)})

(defn respond
  [event message]
  (doseq [part (string/split message #"\n")]
    (.respond event part)))

(defmulti listen (fn [bot listener-type & more]
                   listener-type))

(defmethod listen :on-message
  [bot _ handler]
  (.. bot getListenerManager
      (addListener (proxy [ListenerAdapter] []
                     (onMessage [event]
                       (let [message (format-message event)]
                         (handler message (fn [response]
                                            (respond event response))))))))
  bot)

(defmethod listen :on-disconnect
  [bot _ handler]
  (.. bot getListenerManager
      (addListener (proxy [ListenerAdapter] []
                     (onDisconnect [event]
                       (handler event)))))
  bot)

(defn auto-reconnect
  [bot channels & {:keys [max-tries]
                   :or {max-tries 5}}]
  (let [reconnect-fn #(try (reconnect bot)
                           (join-channels bot channels)
                           (catch Exception _ nil))]
    (listen bot :on-disconnect
            (fn [event]
              (loop [times max-tries]
                (when (and (nil? (reconnect-fn))
                           (pos? times))
                  (recur (dec times)))))))
  bot)

(defn make-bot
  [& {:keys [nick name password host port server-password messages-delay
             channels auto-reconnect]
      :or {nick "ash"
           name "ash"
           port 6667
           messages-delay 1000
           auto-reconnect true}
      :as options}]
  (let [bot (PircBotX.)]
    (.setName bot nick)
    (.setLogin bot name)
    (.setMessageDelay bot messages-delay)
    (.connect bot host port server-password)
    (when password
      (.identify bot password))
    (when channels
      (join-channels bot channels))
    (when auto-reconnect
      (ash.bot/auto-reconnect bot channels))
    bot))
