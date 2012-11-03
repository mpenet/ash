# Ash

<img src="http://cloud.github.com/downloads/mpenet/ash/logo.jpg"
 alt="Cute bot" title="Cute bot" align="right" />

> It's a robot. Ash is a god damn robot!

A simple IRC bot, based on pircbotx, started as a fork of clj-irc.

### Why another one?
Because I am bored, with the flu, and need one that
works with grove.io and that is stupidly simple and extensible.


## Installation

```clojure
[cc.qbits/ash "0.0.1-SNAPSHOT"]
```

## Usage

```clojure
(require
  [ash.bot :as irc]
  [ash.plugins.clojure :as plugin-clojure])

(-> (irc/make-bot :server-password "meh"
                  :nick "just-a-bot"
                  :name "just-a-bot"
                  :password "1234"
                  :host "meh.irc.grove.io"
                  :port 6667
                  :channels ["#foo" "#bar"]
                  :auto-reconnect true)
    (plugin-clojure/handler))
```

## License

Copyright © 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
