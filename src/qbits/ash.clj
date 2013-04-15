(ns qbits.ash
  (:require
   [clojure.string :as string]
   [clojure.tools.logging :as log])
  (:import
   [org.pircbotx PircBotX]
   [org.pircbotx.hooks ListenerAdapter]
   [org.pircbotx.hooks.events MessageEvent PrivateMessageEvent]))

(defn join-channels
  [^PircBotX bot channels]
  (log/info (format "Joining %s" channels))
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
      (.sendMessage bot target (format-message m :prefix? prefix?)))
    (send-message bot target (string/split (str message) #"\n") prefix?))
  bot)

(defn reply
  [bot {:keys [channel user]} message & [prefix?]]
  (send-message bot (or channel user) message prefix?))

(defn disconnect
  [^PircBotX bot]
  (log/info "Disconnecting")
  (.disconnect bot)
  bot)

(defn reconnect
  [^PircBotX bot]
  (log/info "Reconnecting")
  (.reconnect bot)
  bot)

(defn shutdown
  [^PircBotX bot]
  (log/info "Shutdown")
  (.shutdown bot)
  bot)

(defprotocol PEventDecoder
  (event->map [event]))

(extend-protocol PEventDecoder
  MessageEvent
  (event->map [event]
    {:user (.. event getUser getNick)
     :channel (.. event getChannel getName)
     :timestamp (.getTimestamp event)
     :content (.getMessage event)})

  PrivateMessageEvent
  (event->map [event]
    {:user (.. event getUser getNick)
     :timestamp (.getTimestamp event)
     :content (.getMessage event)})

  Object
  (event->map [event] event))

(defmulti listen (fn [bot listener-type & more]
                   listener-type))

(defmethod listen :on-channel-message
  [^PircBotX bot _ handler]
  (.. bot getListenerManager
      (addListener (proxy [ListenerAdapter] []
                     (onMessage [event]
                       (handler (event->map event))))))
  bot)

(defmethod listen :on-private-message
  [^PircBotX bot _ handler]
  (.. bot getListenerManager
      (addListener (proxy [ListenerAdapter] []
                     (onPrivateMessage [event]
                       (handler (event->map event))))))
  bot)

(defmethod listen :on-message
  [^PircBotX bot _ handler]
  (listen bot :on-channel-message handler)
  (listen bot :on-private-message handler))

(defmethod listen :on-disconnect
  [^PircBotX bot _ handler]
  (.. bot getListenerManager
      (addListener (proxy [ListenerAdapter] []
                     (onDisconnect [event]
                       (handler event)))))
  bot)

(defn auto-reconnect
  "The retries options is a vector of pairs where the first element is
  the sleep time in ms and the second element is the amount of times
  to repeat this stage, if the repeat value is -1 this means try forever.
Once reconnected it resets"
  [^PircBotX bot channels & {:keys [retries]
                             :or {retries [[100 5]
                                           [500 5]
                                           [1500 5]
                                           [15000 5]
                                           [60000 -1]]}}]
  (let [reconnect-fn #(try (reconnect bot)
                           (join-channels bot channels)
                           (catch Exception e
                             (log/warn (str e))))]
    (listen bot :on-disconnect
            (fn [event]
              (loop [retries retries]
                (let [[[wait-ms repeat-n] & more] retries]
                  (java.lang.Thread/sleep wait-ms)
                  (when (nil? (reconnect-fn))
                    (cond
                      (= repeat-n -1)
                      (recur retries)

                      (> (dec repeat-n) 0)
                      (recur (update-in retries [0 1] dec))

                      (= 0 (and (dec repeat-n)
                                (> (count retries) 1)))
                      (recur (subvec retries 1))))))))))

(defn make-bot
  [& {:keys [nick name password host port server-password messages-delay
             channels auto-reconnect verbose]
      :or {nick "ash"
           name "ash"
           port 6667
           messages-delay 1000
           auto-reconnect true
           verbose false}
      :as options}]
  (let [bot (PircBotX.)]
    (.setVerbose verbose)
    (.setName bot name)
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
      (qbits.ash/auto-reconnect bot channels))
    bot))
