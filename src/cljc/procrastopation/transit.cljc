(ns procrastopation.transit
  "From: https://gist.github.com/Deraen/eb3f650c472fb1abe970 + equiv https://gist.github.com/onetom/3a6a7bd7e15f01aae4adc1fcd5f0dc3e"
  (:require [cognitect.transit :as transit]
            [clojure.string :as string]
            #?@(:clj [[java-time :as java-time]])
            #?@(:cljs [[goog.string :as gs]
                       goog.date.UtcDateTime
                       goog.date.Date]))
  ;; #?(:clj (:import [org.joda.time]))
  #?(:clj (:import [java.time LocalDate]))
  )

#?(:clj (set! *warn-on-reflection* true))

#?(:cljs
   (extend-type goog.date.Date
     IEquiv
     (-equiv [o other]
       (and (instance? goog.date.Date other)
            (identical? (.getTime o) (.getTime other))
            (identical? (.getTimezoneOffset o) (.getTimezoneOffset other))))
     IComparable
     (-compare [o other]
       (- (.getTime o) (.getTime other)))))

;; (def DateTime #?(:clj org.joda.time.DateTime, :cljs goog.date.UtcDateTime))
(def TransitLocalDate #?(:clj LocalDate
                  :cljs goog.date.Date))

#_(defn write-date-time
  "Represent DateTime in RFC3339 format string."
  [d]
  #?(:clj (.toString (.withZone ^org.joda.time.DateTime d (org.joda.time.DateTimeZone/forID "UTC")))
     :cljs (str (.getUTCFullYear d)
                "-" (gs/padNumber (inc (.getUTCMonth d)) 2)
                "-" (gs/padNumber (.getUTCDate d) 2)
                "T" (gs/padNumber (.getUTCHours d) 2)
                ":" (gs/padNumber (.getUTCMinutes d) 2)
                ":" (gs/padNumber (.getUTCSeconds d) 2)
                "." (gs/padNumber (.getUTCMilliseconds d) 3)
                "Z")))

#_(defn read-date-time
  "Read RFC3339 string to DateTime."
  [s]
  #?(:clj  (org.joda.time.DateTime/parse s)
     :cljs (goog.date.UtcDateTime.fromIsoString s)))

(defn write-local-date
  "Represent Date in YYYY-MM-DD format."
  [x]
  #?(:clj  (.toString ^LocalDate x)
     :cljs (.toIsoString x true false)))

(defn read-local-date
  "Read Date in YYYY-MM-DD format."
  [x]
  #?(:clj (java-time/local-date x)
     :cljs (let [[_ y m d] (re-find #"(\d{4})-(\d{2})-(\d{2})" x)]
             (goog.date.Date. (long y) (dec (long m)) (long d)))))

(def writers
  {;;DateTime  (transit/write-handler (constantly "DateTime") write-date-time)
   TransitLocalDate (transit/write-handler (constantly "LocalDate") write-local-date)
   })

(def readers
  {;;"DateTime" (transit/read-handler read-date-time)
   "LocalDate"     (transit/read-handler read-local-date)})
