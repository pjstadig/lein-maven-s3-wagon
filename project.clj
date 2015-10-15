(defproject lein-maven-s3-wagon "0.2.5"
  :description "A leiningen plugin for publishing to a private S3
  maven repository."
  :url "http://github.com/pjstadig/lein-maven-s3-wagon/"
  :license {:name "Mozilla Public License, v. 2.0"
            :url "http://mozilla.org/MPL/2.0/"}
  :dependencies [[joda-time "2.8.1"]
                 [pjstadig/maven-s3-wagon "1.3.4" :exclusions [joda-time]]]
  :deploy-repositories [["releases" {:url "https://clojars.org/repo/"
                                     :creds :gpg}]]
  :eval-in :leiningen)
