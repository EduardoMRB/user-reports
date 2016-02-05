(ns user-reports.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello world!"
                          :tickets [{:title "Problemas no filtro de eventos"
                                     :description "Quando filtro por data, o sistema apresenta um erro"
                                     :type "bug"
                                     :issued-at #inst "2016-02-03"
                                     :issued-by "Carlos Andrade"
                                     :images [{:title "screenshot"
                                               :url "http://whatbettyknows.com/wp-content/uploads/2014/03/FB-DST-bug.jpg"}]
                                     :open? true
                                     :responded? false}]}))

(defn ticket-row [ticket]
  [:div.col-sm.12
   [:span (:title ticket)
    (str "aberto em: " (.toLocaleDateString (:issued-at ticket)))]])

(defn tickets-app []
  [:div.container-fluid
   [:h1 "Lista de tickets"]
   [:div
    (for [ticket (:tickets @app-state)]
      [ticket-row ticket])]])

(reagent/render-component [tickets-app]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
