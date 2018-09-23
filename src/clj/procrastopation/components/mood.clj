(ns procrastopation.components.mood
  (:require [clojure.java.io :as io]
            [duratom.core :as duratom]
            [java-time :as java-time]
            [taoensso.nippy :as nippy]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]])
  (:import [java.time.LocalDate]))

(nippy/extend-freeze java.time.LocalDate :java.time/local-date
                     [x data-output]
                     (.writeUTF data-output (str x)))

(nippy/extend-thaw :java.time/local-date
                   [data-input]
                   (java-time/local-date (.readUTF data-input)))

#_(def state (atom {}))
(def state (duratom/duratom :local-file
                            :file-path "mood.nippy"
                            :init {}
                            :rw {:read nippy/thaw-from-file
                                 :write nippy/freeze-to-file}))

(defn recv-mood [req]
  (let [{:keys [mood]} (:body-params req)
        today (java-time/local-date)
        state* (swap! state assoc today mood)]
    {:body state*}))

(defn mood-routes [endpoint]
  (routes
   (POST "/mood" req (recv-mood req))))
