(defproject lein-maven-s3-wagon "0.2.4-SNAPSHOT"
  :description "A leiningen plugin for publishing to a private S3
  maven repository."
  :url "http://github.com/pjstadig/lein-maven-s3-wagon/"
  :license {:name "Mozilla Public License, v. 2.0"
            :url "http://mozilla.org/MPL/2.0/"}
  :repositories [["private" {:url "s3p://pjstadig-maven/releases"
                             :username :env/aws_access_key_id
                             :password :env/aws_secret_access_key}]]
  :dependencies [[org.apache.maven.wagon/wagon-provider-api "2.2"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [pjstadig/maven-s3-wagon "1.3.3"]
                 [prismatic/schema "0.3.3"]
                 [log4j "1.2.17"]
                 [joda-time "2.2"]
                 [clj-aws-s3 "0.3.10" :exclusions [joda-time]]]
  :plugins [[lein-maven-s3-wagon "0.2.4-SNAPSHOT"]]
  :profiles {:dev {:dependencies [[foo "0.1.0"]]}}
  :aot [pjstadig.maven.wagon.S3Wagon])
