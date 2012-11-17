
# Ash

<img src="http://cloud.github.com/downloads/mpenet/ash/ash.jpg"
 alt="Cute bot" title="Cute bot" align="right"  />

> It's a robot. Ash is a god damn robot!

A simple IRC bot, based on pircbotx, started as a fork of clj-irc.

### Why another one?

Because I need one that works with grove.io, that is very simple,
extensible and supports webhooks.

See [plugins](https://github.com/mpenet/ash/tree/master/src/qbits/ash/plugins) for
examples. You can add your own event listeners by extending the `qbits.ash.bot/listen`
multimethod.

See [echoweb](https://github.com/mpenet/ash/blob/master/src/qbits/ash/plugins/echoweb.clj)
for a basic example of webhook integration.

## Installation

```clojure
[cc.qbits/ash "0.2.4"]
```

## Usage

```clojure
(require
  '[qbits.ash :as ash]
  '[qbits.ash.webhooks :as webhooks]

  '[qbits.ash.plugins.clojure :as clj]
  '[qbits.ash.plugins.google :as goog]
  '[qbits.ash.plugins.echoweb :as echoweb])

(-> (ash/make-bot :server-password "meh"
                  :nick "just-a-bot"
                  :name "just-a-bot"
                  :password "1234"
                  :host "meh.irc.grove.io"
                  :port 6667
                  :channels ["#foo" "#bar"]
                  :auto-reconnect true)
    clj/handler
    goog/handler
    echoweb/handler)

(webhooks/start-server)
```

### Plugins

Plugins are very easy to implement:

```clojure
(ns yourbot.plugins.meh
    (:require [qbits.ash :as ash]))

(defn handler
  [bot]
  (ash/listen bot :on-message
      (fn [event]
          (when (re-find #"sayhi" (:content event))
              ;; reply knows about context form the event passed
              ;; (privmsg, channel msg, etc)
              (ash/reply bot event "Hello world"))))))
```

For a webook:

```clojure


(defn handler [bot]
  (ash/listen bot :on-webhook
              :post #"^/say-hi"
              (fn [request]
                (ash/send-message "#somechan" "hohai")))

  (ash/listen bot :on-webhook
              :get #"^/say-hi-foo"
              (fn [request]
                (ash/send-message "foo" "hohai foo")))
```



## License

Copyright © 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
