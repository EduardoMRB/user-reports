(ns user-reports.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello world!"
                          :tickets [{:title "Problemas no filtro de eventos"
                                     :description "Quando filtro por data, o sistema apresenta um erro"
                                     :type "bug"
                                     :app "Potência"
                                     :issued-at #inst "2016-02-03"
                                     :issued-by "Carlos Andrade"
                                     :images [{:title "screenshot"
                                               :url "http://whatbettyknows.com/wp-content/uploads/2014/03/FB-DST-bug.jpg"}]
                                     :open? true
                                     :responded? false}
                                    {:title "Fonte do site"
                                     :description "Trocar fonte do site para Helvetica"
                                     :type "suggestion"
                                     :app "Potência"
                                     :issued-at #inst "2016-02-02"
                                     :issued-by "Nora Ramos"
                                     :images [{:title "screenshot"
                                               :url "http://whatbettyknows.com/wp-content/uploads/2014/03/FB-DST-bug.jpg"}]
                                     :open? false
                                     :responded? true}]}))

(defn navbar []
  [:nav.navbar.navbar-inverse.navbar-fixed-top
   [:div.navbar-inner
    [:div.container-fluid
     [:a.navbar-brand {:title "Tickets! informe problemas e dê sugestões com agilidade"} "Tickets!"]]]])

(defn toolbar []
  [:div.row
   [:div.btn-group
    [:button.btn.btn-success {:type "button"} "Abrir ticket"]]])

(defn- done? [ticket]
  (not (:open? ticket)))

(defn ticket-row [ticket]
  [:a.list-group-item {:href "#"}
   [:span {:class (case (:type ticket)
                    "bug" "fa fa-bug bug item-badge"
                    "suggestion" "fa fa-microphone item-badge")}]
   [:span.list-text.name (:issued-by ticket)]
   [:span.list-text {:style {:text-decoration (if (done? ticket)
                                                "line-through"
                                                "initial")}}
    (:title ticket)]
   [:span.list-text.text-muted (str " - " (:app ticket))]
   [:span.list-text.badge (.toLocaleDateString (:issued-at ticket))]
   (when (seq (:images ticket))
     [:span.pull-right
      [:span.glyphicon.glyphicon-paperclip]])])

(defn tickets-app []
  [:div
   [navbar]
   [:div.spacer {:style {:height "48px"}}]
   [:div.container.main
    [toolbar]
    [:hr]
    [:div.row
     [:div.list-group
      (for [ticket (:tickets @app-state)]
        [ticket-row ticket])]]]])

(reagent/render-component [tickets-app]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
