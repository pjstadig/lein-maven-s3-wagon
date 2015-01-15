(ns pjstadig.maven.wagon.S3Wagon
  (:require [aws.sdk.s3 :as s3]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:import (org.apache.maven.wagon Wagon)
           (org.apache.maven.wagon.authentication AuthenticationInfo)
           (org.apache.maven.wagon.events TransferEvent)
           (org.apache.maven.wagon.resource Resource))
  (:gen-class
   :implements [org.apache.maven.wagon.Wagon]
   :state state
   :init init))

(defn -init []
  [[] (atom {:kuali (org.kuali.maven.wagon.S3Wagon.)})])

(defn -addSessionListener [this listener]
  (log/trace "addSessionListener" listener)
  (swap! (.state this) update-in [:session-listeners] (fnil conj #{}) listener)
  (.addSessionListener (:kuali @(.state this)) listener))

(defn -addTransferListener [this listener]
  (log/trace "addTransferListener" listener)
  (swap! (.state this) update-in [:transfer-listeners] (fnil conj #{}) listener)
  (.addTransferListener (:kuali @(.state this)) listener))

(defn access-key-id [auth]
  (or (and auth (.getUserName auth))
      (System/getenv "AWS_ACCESS_KEY_ID")))

(defn secret-access-key [auth]
  (or (and auth (.getPassword auth))
      (System/getenv "AWS_SECRET_ACCESS_KEY")))

(defn security-token [auth]
  (System/getenv "AWS_SECURITY_TOKEN"))

(defn -connect
  ([this source]
     (log/trace "connect" source)
     (.connect (:kuali @(.state this)) source nil nil))
  ([this source auth-or-proxy]
     (log/trace "connect" source auth-or-proxy)
     (if (instance? AuthenticationInfo auth-or-proxy)
       (.connect this source auth-or-proxy nil)
       (.connect this source nil auth-or-proxy)))
  ([this source auth proxy]
     (log/trace "connect" source auth proxy)
     (let [creds {:access-key (access-key-id auth)
                  :secret-key (secret-access-key auth)
                  :token (security-token auth)}
           bucket (.getHost source)
           prefix (.getBasedir source)]
       (log/debug "bucket and prefix" bucket prefix)
       (swap! (.state this) assoc
              :creds creds
              :bucket bucket
              :prefix prefix))
     (.connect (:kuali @(.state this)) source auth proxy)))

(defn -disconnect [this]
  (log/trace "disconnect")
  (.disconnect (:kuali @(.state this))))

(defn strip-leading-slash [s]
  (if (.startsWith s "/")
    (subs s 1)
    s))

(defn strip-trailing-slash [s]
  (if (.endsWith s "/")
    (subs s 0 (dec (count s)))
    s))

(defn resource-key [prefix resource-name]
  (str (-> prefix
           strip-leading-slash
           strip-trailing-slash)
       "/"
       (strip-leading-slash resource-name)))

(defn make-resource [resource-name {:keys [content-length last-modified]}]
  (doto (Resource. resource-name)
    (.setContentLength content-length)
    (.setLastModified (.getTime last-modified))))

(defmulti notify-transfer-event (fn [listeners event & args] (.getEventType event)))

(defmethod notify-transfer-event TransferEvent/TRANSFER_INITIATED
  [listeners event]
  (doseq [listener listeners]
    (.transferInitiated listener event)))

(defmethod notify-transfer-event TransferEvent/TRANSFER_STARTED
  [listeners event]
  (doseq [listener listeners]
    (.transferStarted listener event)))

(defmethod notify-transfer-event TransferEvent/TRANSFER_PROGRESS
  [listeners event buf len]
  (doseq [listener listeners]
    (.transferProgress listener event buf len)))

(defmethod notify-transfer-event TransferEvent/TRANSFER_COMPLETED
  [listeners event]
  (doseq [listener listeners]
    (.transferCompleted listener event)))

(defmethod notify-transfer-event TransferEvent/TRANSFER_ERROR
  [listeners event]
  (doseq [listener listeners]
    (.transferError listener event)))

(defn -get [this resource-name destination]
  (log/trace "get" resource-name destination)
  (let [{:keys [creds bucket prefix transfer-listeners] :as state} @(.state this)
        key (resource-key prefix resource-name)
        _ (log/debug "key" key)
        object-meta (s3/get-object-metadata creds bucket key)
        resource (make-resource resource-name object-meta)]
    (letfn [(make-event [type-or-ex]
              (doto (if (instance? Exception type-or-ex)
                      (TransferEvent. this
                                      resource
                                      type-or-ex
                                      TransferEvent/REQUEST_GET)
                      (TransferEvent. this
                                      resource
                                      type-or-ex
                                      TransferEvent/REQUEST_GET))
                (.setLocalFile destination)))
            (notify
              ([type-or-ex]
                 (notify-transfer-event transfer-listeners
                                        (make-event type-or-ex)))
              ([type-or-ex buf len]
                 (notify-transfer-event transfer-listeners
                                        (make-event type-or-ex)
                                        buf
                                        len)))]
      (notify TransferEvent/TRANSFER_INITIATED)
      (notify TransferEvent/TRANSFER_STARTED)
      (let [buf (byte-array 1024)]
        (try
          (with-open [content (:content (s3/get-object (:creds state) (:bucket state) key))
                      out (io/output-stream destination)
                      tmp (io/output-stream "/tmp/pjstadig")]
            (loop [len (.read content buf)]
              (cond
               (zero? len) (recur (.read content buf))
               (pos? len) (do (.write out buf 0 len)
                              (.write tmp buf 0 len)
                              (notify TransferEvent/TRANSFER_PROGRESS buf len)
                              (recur (.read content buf))))))
          (catch Exception e
            (notify e)
            (throw e))))
      (notify TransferEvent/TRANSFER_COMPLETED)))
  #_(.get (:kuali @(.state this)) resource-name destination))

(defn -getFileList [this destination-directory]
  (log/trace "getFileList" destination-directory)
  (.getFileList (:kuali @(.state this)) destination-directory))

(defn -getIfNewer [this resource-name destination timestamp]
  (log/trace "getIfNewer" resource-name destination timestamp)
  (.getIfNewer (:kuali @(.state this)) resource-name destination timestamp))

(defn -getReadTimeout [this]
  (log/trace "getReadTimeout")
  (.getReadTimeout (:kuali @(.state this))))

(defn -getRepository [this]
  (log/trace "getRepository")
  (.getRepository (:kuali @(.state this))))

(defn -getTimeout [this]
  (log/trace "getTimeout")
  (.getTimeout (:kuali @(.state this))))

(defn -hasSessionListener [this listener]
  (log/trace "hasSessionListener" listener)
  (.hasSessionListener (:kuali @(.state this)) listener))

(defn -hasTransferListener [this listener]
  (log/trace "hasTransferListener" listener)
  (.hasTransferListener (:kuali @(.state this)) listener))

(defn -isInteractive [this]
  (log/trace "isInteractive")
  (.isInteractive (:kuali @(.state this))))

(defn -put [this source destination]
  (log/trace "put" source destination)
  (.put (:kuali @(.state this)) source destination))

(defn -putDirectory [this source-directory destination-directory]
  (log/trace "putDirectory" source-directory destination-directory)
  (.putDirectory (:kuali @(.state this)) source-directory destination-directory))

(defn -removeSessionListener [this listener]
  (log/trace "removeSessionListener" listener)
  (swap! (.state this) update-in [:session-listeners] disj listener)
  (.removeSessionListener (:kuali @(.state this)) listener))

(defn -removeTransferListener [this listener]
  (log/trace "removeTransferListener" listener)
  (swap! (.state this) update-in [:transfer-listeners] disj listener)
  (.removeTransferListener (:kuali @(.state this)) listener))

(defn -resourceExists [this resource-name]
  (log/trace "resourceExists" resource-name)
  (let [{:keys [creds bucket prefix] :as state} @(.state this)
        key (resource-key prefix resource-name)]
    (s3/object-exists? creds bucket key))
  #_(.resourceExists (:kuali @(.state this)) resource-name))

(defn -setInteractive [this interactive]
  (log/trace "setInteractive" interactive)
  (.setInteractive (:kuali @(.state this)) interactive))

(defn -setReadTimeout [this timeout-value]
  (log/trace "setReadTimeout" timeout-value)
  (.setReadTimeout (:kuali @(.state this)) timeout-value))

(defn -setTimeout [this timeout-value]
  (log/trace "setTimeout" timeout-value)
  (.setTimeout (:kuali @(.state this)) timeout-value))

(defn -supportsDirectoryCopy [this]
  (log/trace "supportsDirectoryCopy")
  (.supportsDirectoryCopy (:kuali @(.state this))))
