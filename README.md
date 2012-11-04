# Ash

<img src="http://cloud.github.com/downloads/mpenet/ash/logo.jpg"
 alt="Cute bot" title="Cute bot" align="right" />

> It's a robot. Ash is a god damn robot!

A simple IRC bot, based on pircbotx, started as a fork of clj-irc.

### Why another one?
Because I am bored, with the flu, and need one that
works with grove.io and that is very simple, extensible and supports webhooks.

See [plugins](https://github.com/mpenet/ash/tree/master/src/ash/plugins) for
examples. You can add your own event listeners by extending the `ash.irc/listen`
multimethod.

See [echoweb](https://github.com/mpenet/ash/blob/master/src/ash/plugins/echoweb.clj)
for a basic example of webhook intergration,

## Installation

```clojure
[cc.qbits/ash "0.1.1"]
```

## Usage

```clojure
(require
  '[ash.bot :as irc]
  '[ash.webhooks :as webhooks]

  '[ash.plugins.clojure :as clj]
  '[ash.plugins.google :as goog]
  '[ash.plugins.echoweb :as echoweb])

(-> (irc/make-bot :server-password "meh"
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

## License

Copyright © 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
