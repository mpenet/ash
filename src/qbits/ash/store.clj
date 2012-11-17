(ns qbits.ash.store
  (:import
   [java.security MessageDigest]
   [org.mapdb DB DBMaker]))

(defonce db (-> (DBMaker/newFileDB (java.io.File. "brain"))
                .closeOnJvmShutdown
                .make))

(defprotocol PStore
  (fetch [this id])
  (put! [this id value])
  (del! [this] [this id])
  (exists? [this id]))

(extend-type java.util.AbstractMap
  PStore
  (fetch [this id]
    (.get this id))

  (put! [this id value]
    (.put this id value)
    (.commit db))

  (del!
    ([this] (.clear this))
    ([this id]
       (.remove this id)))

  (exists? [this id]
    (.containsKey this id)))