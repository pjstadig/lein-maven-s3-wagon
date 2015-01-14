(ns pjstadig.maven.wagon.S3Wagon
  (:import (org.apache.maven.wagon Wagon))
  (:gen-class
   :implements [org.apache.maven.wagon.Wagon]
   :state state
   :init init))

(defn -init []
  [[] (ref (org.kuali.maven.wagon.S3Wagon.))])

(defn -addSessionListener [this listener]
  (.addSessionListener @(.state this) listener))

(defn -addTransferListener [this listener]
  (.addTransferListener @(.state this) listener))

(defn -connect
  ([this source]
     (.connect @(.state this) source))
  ([this source auth-or-proxy]
     (.connect @(.state this) source auth-or-proxy))
  ([this source auth proxy]
     (.connect @(.state this) source auth proxy)))

(defn -disconnect [this]
  (.disconnect @(.state this)))

(defn -get [this resource-name destination]
  (.get @(.state this) resource-name destination))

(defn -getFileList [this destination-directory]
  (.getFileList @(.state this) destination-directory))

(defn -getIfNewer [this resource-name destination timestamp]
  (.getIfNewer @(.state this) resource-name destination timestamp))

(defn -getReadTimeout [this]
  (.getReadTimeout @(.state this)))

(defn -getRepository [this]
  (.getRepository @(.state this)))

(defn -getTimeout [this]
  (.getTimeout @(.state this)))

(defn -hasSessionListener [this listener]
  (.hasSessionListener @(.state this) listener))

(defn -hasTransferListener [this listener]
  (.hasTransferListener @(.state this) listener))

(defn -isInteractive [this]
  (.isInteractive @(.state this)))

(defn -put [this source destination]
  (.put @(.state this) source destination))

(defn -putDirectory [this source-directory destination-directory]
  (.putDirectory @(.state this) source-directory destination-directory))

(defn -removeSessionListener [this listener]
  (.removeSessionListener @(.state this) listener))

(defn -removeTransferListener [this listener]
  (.removeTransferListener @(.state this) listener))

(defn -resourceExists [this resource-name]
  (.resourceExists @(.state this) resource-name))

(defn -setInteractive [this interactive]
  (.setInteractive @(.state this) interactive))

(defn -setReadTimeout [this timeout-value]
  (.setReadTimeout @(.state this) timeout-value))

(defn -setTimeout [this timeout-value]
  (.setTimeout @(.state this) timeout-value))

(defn -supportsDirectoryCopy [this]
  (.supportsDirectoryCopy @(.state this)))
