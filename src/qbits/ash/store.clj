(ns qbits.ash.store
  (:import
   [java.security MessageDigest]
   [org.mapdb DB DBMaker]))

(defonce db (-> (DBMaker/newFileDB (java.io.File. "brain"))
                .closeOnJvmShutdown
                .make))
