(ns procrastopation.application
  (:gen-class)
  (:require [com.stuartsierra.component :as component]

            [muuntaja.core :as muuntaja]
            [muuntaja.middleware :as muuntaja.mw]
            [muuntaja.format.transit :as muuntaja.transit]
            [procrastopation.transit :as transit]

            [procrastopation.components.server-info :refer [server-info]]
            [procrastopation.components.mood :refer [mood-routes]]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.http-kit :refer [new-web-server]]
            [procrastopation.config :refer [config]]
            [procrastopation.routes :refer [home-routes]]))

(defn encode-local-date-transit [handler]
  (muuntaja.mw/wrap-format
   handler
   (-> muuntaja/default-options
       (assoc-in [:formats "application/transit+json" :encoder]
                 [#(muuntaja.transit/encoder :json
                    (assoc % :handlers transit/writers))])
       (assoc-in [:formats "application/transit+json" :decoder]
                 [#(muuntaja.transit/decoder :json
                    (assoc % :handlers transit/readers))]))))

(defn app-system [config]
  (component/system-map
   :muuntaja   (new-middleware {:middleware [encode-local-date-transit]})
   :routes     (new-endpoint home-routes)
   :mood-endpoint (-> (new-endpoint mood-routes)
                      (component/using [:muuntaja]))
   :middleware (new-middleware {:middleware (:middleware config)})
   :handler    (-> (new-handler)
                   (component/using [:routes :mood-endpoint :middleware]))
   :http       (-> (new-web-server (:http-port config))
                   (component/using [:handler]))
   :server-info (server-info (:http-port config))))

(defn -main [& _]
  (let [config (config)]
    (-> config
        app-system
        component/start)))
