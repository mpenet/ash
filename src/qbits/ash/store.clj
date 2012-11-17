(ns qbits.ash.store
  (:import [org.mapdb DB DBMaker]))

(defonce db (-> (DBMaker/newFileDB (java.io.File. "brain"))
                .closeOnJvmShutdown
                .compressionEnable
                .make))

(defprotocol PStore
  (put! [this id value])
  (del! [this] [this id]))

(extend-type java.util.AbstractMap
  PStore

  (put! [this id value]
    (.put this id value)
    (.commit db))

  (del!
    ([this] (.clear this))
    ([this id]
       (.remove this id))))