(ns user-reports.service
  (:require [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ring.util.response :as ring-resp]
            [clojure.java.io :as io]))

(defn home [request]
  (-> (io/resource "public/index.html")
      (slurp)
      (ring-resp/response)
      (ring-resp/content-type "text/html")))

(defroutes routes
  [[["/app" {:any [:home home]}]]])

(def service
  {:env :prod
   ::bootstrap/routes routes
   ::bootstrap/resource-path "/public"
   ::bootstrap/type :jetty
   ::bootstrap/port 8888})
