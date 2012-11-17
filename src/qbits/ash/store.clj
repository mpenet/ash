(ns qbits.ash.store
  (:import
   [java.security MessageDigest]
   [org.mapdb DB DBMaker]))

(defonce db (-> (DBMaker/newFileDB (java.io.File. "brain"))
                .closeOnJvmShutdown
                .make))

(defprotocol PStore
  (fetch [this id])
  (put! [this id value]))

(extend-type java.util.AbstractMap
  PStore
  (put! [this id value]
    (.put this id value)
    (.commit db))

  (fetch [this id]
    (.get this id)))