(ns ash.bot
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log])
  (:import [org.pircbotx PircBotX User Channel]
           [org.pircbotx.hooks ListenerAdapter Event]
           [org.pircbotx.hooks.events MessageEvent]))

(defn join-channels
  [^PircBotX bot channels]
  (log/info (format "join %s" channels))
  (doseq [chan channels]
    (.joinChannel bot chan))
  bot)

(defn channels
  [^PircBotX bot]
  (->> bot .getChannelsNames (into #{})))

(defn format-message
  ^String [message & {:keys [prefix?]}]
  (if prefix?
    (str "⇒ " message)
    message))

(defn send-message
  [^PircBotX bot ^String target message & [prefix?]]
  (if (sequential? message)
    (doseq [m message]
      (.sendMessage bot
                    target
                    (format-message m :prefix? prefix?)))
    (.sendMessage bot
                  target
                  (format-message message :prefix? prefix?)))
  bot)

(defn disconnect
  [^PircBotX bot]
  (log/info "disconnect")
  (.disconnect bot)
  bot)

(defn reconnect
  [^PircBotX bot]
  (log/info "reconnect")
  (.reconnect bot)
  bot)

(defn shutdown
  [^PircBotX bot]
  (log/info "shutdown")
  (.shutdown bot)
  bot)

(defn make-message
  [^MessageEvent event]
  {:user (.. event getUser getNick)
   :channel (.. event getChannel getName)
   :timestamp (.getTimestamp event)
   :content  (.getMessage event)})

(defn respond
  [event message]
  (doseq [part (string/split message #"\n")]
    (.respond ^Event event part)))

(defmulti listen (fn [bot listener-type & more]
                   listener-type))

(defmethod listen :on-message
  [^PircBotX bot _ handler]
  (.. bot getListenerManager
      (addListener (proxy [ListenerAdapter] []
                     (onMessage [event]
                       (let [message (make-message event)]
                         (handler message (fn [response]
                                            (respond event response))))))))
  bot)

(defmethod listen :on-disconnect
  [^PircBotX bot _ handler]
  (.. bot getListenerManager
      (addListener (proxy [ListenerAdapter] []
                     (onDisconnect [event]
                       (handler event)))))
  bot)

(defn auto-reconnect
  [^PircBotX bot channels & {:keys [max-tries]
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
    (.connect bot
              ^String host
              (int port)
              ^String server-password)
    (when password
      (.identify bot password))
    (when channels
      (join-channels bot channels))
    (when auto-reconnect
      (ash.bot/auto-reconnect bot channels))
    bot))
