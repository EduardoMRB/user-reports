(ns user-reports.server
  (:gen-class)
  (:require [io.pedestal.http :as server]
            [user-reports.service :as service]
            [ns-tracker.core :refer [ns-tracker]]))

(defonce runnable-service (server/create-server service/service))

(def modified-namespaces
  (ns-tracker "src"))

(defn run-dev
  "The entry-point for development"
  [& args]
  (println "\nCreating your [DEV] server...")
  (-> service/service ;; start with production configuration
      (merge {:env :dev
              ;; do not block thread that starts web server
              ::server/join? false
              ::server/port 3000
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ;; ::server/routes #(deref #'service/routes)
              ::server/routes (fn []
                                (doseq [ns-sym (modified-namespaces)]
                                  (require ns-sym :reload))
                                @#'service/routes)
              ;; all origins are allowed in dev mode
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}})
      ;; Wire up interceptor chains
      server/default-interceptors
      server/dev-interceptors
      server/create-server
      server/start))

(defn -main
  "Entry point for production"
  [& args]
  (println "Creating server.")
  (server/start runnable-service))
