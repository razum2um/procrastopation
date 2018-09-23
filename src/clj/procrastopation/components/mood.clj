(ns procrastopation.components.mood
  (:require [clojure.java.io :as io]
            [duratom.core :as duratom]
            [java-time :as java-time]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]))

(def state (atom {}))
#_(def state (duratom/duratom :local-file
                    :file-path "/tmp/mood.nippy"
                    :init {}))

(defn recv-mood [req]
  (let [{:keys [mood]} (:body-params req)
        today (java-time/local-date)
        state* (swap! state assoc today mood)]
    {:body state*}))

(defn mood-routes [endpoint]
  (routes
   (POST "/mood" req (recv-mood req))))
