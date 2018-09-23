(ns procrastopation.core
  (:require [reagent.core :as reagent :refer [atom]]
            [procrastopation.common]
            [procrastopation.transit :as transit]
            [ajax.core :refer [GET POST transit-response-format]]))

(enable-console-print!)

(defn mood-sent [resp]
  (-> resp js/console.log))

(defn send-mood [mood]
  (POST "/mood" {:params {:mood mood}
                 :response-format (transit-response-format {:handlers transit/readers})
                 :handler mood-sent}))

;; date => int
(def app-state (atom {"2018-09-23" 1}))

(defn mood []
  [:div.mood
   [:div.mood-choice.mood-choice-good
    {:on-click #(send-mood 1)}
    [:div.mood-text "Good"]]
   [:div.mood-choice.mood-choice-bad
    {:on-click #(send-mood 0)}
    [:div.mood-text "Bad"]]])

(defn render []
  (reagent/render [mood] (js/document.getElementById "app")))
