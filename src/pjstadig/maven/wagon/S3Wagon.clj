(ns pjstadig.maven.wagon.S3Wagon
  (:require [clojure.tools.logging :as log])
  (:import (org.apache.maven.wagon Wagon))
  (:gen-class
   :implements [org.apache.maven.wagon.Wagon]
   :state state
   :init init))

(defn -init []
  [[] (atom {:kuali (org.kuali.maven.wagon.S3Wagon.)})])

(defn -addSessionListener [this listener]
  (log/trace "addSessionListener" listener)
  (swap! (.state this) assoc-in [:session-listeners listener] listener)
  (.addSessionListener (:kuali @(.state this)) listener))

(defn -addTransferListener [this listener]
  (log/trace "addTransferListener" listener)
  (swap! (.state this) assoc-in [:transfer-listeners listener] listener)
  (.addTransferListener (:kuali @(.state this)) listener))

(defn -connect
  ([this source]
     (log/trace "connect" source)
     (.connect (:kuali @(.state this)) source))
  ([this source auth-or-proxy]
     (log/trace "connect" source auth-or-proxy)
     (.connect (:kuali @(.state this)) source auth-or-proxy))
  ([this source auth proxy]
     (log/trace "connect" source auth proxy)
     (.connect (:kuali @(.state this)) source auth proxy)))

(defn -disconnect [this]
  (log/trace "disconnect")
  (.disconnect (:kuali @(.state this))))

(defn -get [this resource-name destination]
  (log/trace "get" resource-name destination)
  (.get (:kuali @(.state this)) resource-name destination))

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
  (swap! (.state this) update-in [:session-listeners] dissoc listener)
  (.removeSessionListener (:kuali @(.state this)) listener))

(defn -removeTransferListener [this listener]
  (log/trace "removeTransferListener" listener)
  (swap! (.state this) update-in [:transfer-listeners] dissoc listener)
  (.removeTransferListener (:kuali @(.state this)) listener))

(defn -resourceExists [this resource-name]
  (log/trace "resourceExists" resource-name)
  (.resourceExists (:kuali @(.state this)) resource-name))

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
