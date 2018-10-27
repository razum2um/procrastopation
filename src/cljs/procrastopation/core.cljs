(ns procrastopation.core
  (:require [reagent.core :as reagent :refer [atom]]
            [procrastopation.common]
            [procrastopation.transit :as transit]
            [ajax.core :refer [GET POST transit-response-format transit-request-format]]
            [goog.date.Date]))

(enable-console-print!)

;; FIXME: Is this a good idea?
;; Required for using dates as keys etc.
;; From: https://gist.github.com/Deraen/1cd4a15bf622c0a7bcfb
(extend-type goog.date.Date
  IEquiv
  (-equiv [o other]
    (and (instance? goog.date.Date other)
         (identical? (.getTime o) (.getTime other))
         (identical? (.getTimezoneOffset o) (.getTimezoneOffset other))))
  IComparable
  (-compare [o other]
    (- (.getTime o) (.getTime other))))


(def response-format (transit-response-format {:handlers transit/readers}))
(def request-format (transit-request-format {:handlers transit/writers}))

;; date => int
(defonce state (atom {}))

(defn recv-mood [resp]
  (-> resp clj->js js/console.log)
  (reset! state resp))

(defn today []
  (goog.date.Date.))

(defn send-mood [mood]
  (POST "/mood" {:params {:mood mood :date (today)}
                 :format request-format
                 :response-format response-format
                 :handler recv-mood}))

(defn get-mood []
  (GET "/mood" {:response-format response-format
                :handler recv-mood}))

(defn todays-value []
  (some->> @state
           (filter (fn [[k v]] (= k (today))))
           first
           val))

;; views

(defn mood []
  [:div.mood
   [:div.mood-choice.mood-choice-good
    {:on-click #(send-mood 1)}
    [:div.mood-text
     {:class (when (= 1 (todays-value)) "mood-text-emoji")}
     "Good"]]
   [:div.mood-choice.mood-choice-bad
    {:on-click #(send-mood 0)}
    [:div.mood-text
     {:class (when (= 0 (todays-value)) "mood-text-emoji")}
     "Bad"]]])

(defn app []
  (reagent/create-class
   {:display-name  "App"
    :component-did-mount (fn [this] (get-mood))
    :reagent-render (fn [] (mood))}))

(defn render []
  (reagent/render [app] (js/document.getElementById "app")))
