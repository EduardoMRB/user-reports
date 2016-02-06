(ns user-reports.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [user-reports.route :as route]))

(enable-console-print!)

(defonce db {:text "Hello world!"
             :tickets [{:id 1
                        :title "Problemas no filtro de eventos"
                        :description "Quando filtro por data, o sistema apresenta um erro"
                        :type "bug"
                        :app "Potência"
                        :issued-at #inst "2016-02-03"
                        :issued-by "Carlos Andrade"
                        :images [{:title "screenshot"
                                  :url "http://whatbettyknows.com/wp-content/uploads/2014/03/FB-DST-bug.jpg"}]
                        :open? true
                        :responded? false}
                       {:id 2
                        :title "Fonte do site"
                        :description "Trocar fonte do site para Helvetica"
                        :type "suggestion"
                        :app "Potência"
                        :issued-at #inst "2016-02-02"
                        :issued-by "Nora Ramos"
                        :images [{:title "screenshot"
                                  :url "http://whatbettyknows.com/wp-content/uploads/2014/03/FB-DST-bug.jpg"}]
                        :open? false
                        :responded? true}]})

(rf/register-handler
 :initialize-db
 (fn [_ _] db))

(rf/register-sub
 :tickets
 (fn [db _]
   (reaction (:tickets @db))))

(rf/register-sub
 :db
 (fn [db _]
   (reaction @db)))

(rf/register-handler
 :index
 (fn [db _]
   (dissoc db :active-ticket)))

(rf/register-handler
 :ticket-clicked
 (fn [db [_ ticket-id]]
   (assoc db :active-ticket {:id ticket-id})))

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

(defn- localize-date [^Date date]
  (.toLocaleDateString date))

(defn ticket-row [ticket]
  [:a.list-group-item {:href "#"
                       :on-click #(rf/dispatch [:ticket-clicked (:id ticket)])}
   [:span {:class (case (:type ticket)
                    "bug" "fa fa-bug bug item-badge"
                    "suggestion" "fa fa-microphone item-badge")}]
   [:span.list-text.name (:issued-by ticket)]
   [:span.list-text {:style {:text-decoration (if (done? ticket)
                                                "line-through"
                                                "initial")}}
    (:title ticket)]
   [:span.list-text.text-muted (str " - " (:app ticket))]
   [:span.list-text.badge (localize-date (:issued-at ticket))]
   (when (seq (:images ticket))
     [:span.pull-right
      [:span.glyphicon.glyphicon-paperclip]])])

(defn ticket-card [ticket]
  (println ticket)
  [:div
   [:h1 (:title ticket)]
   [:p (:description ticket)]
   [:p (str "Reportado por: " (:issued-by ticket)
            " em: " (localize-date (:issued-at ticket)))]])

(defn fetch-ticket [db id]
  (->> @db
       :tickets
       (filter #(= (:id %) id))
       (first)))

(defn navigation []
  (let [db (rf/subscribe [:db])]
    (when (:active-ticket @db)
      [:div.container
       [:div.row
        [:div.btn-group
         [:button.btn.btn-default {:on-click #(rf/dispatch [:index])}
          [:span.glyphicon.glyphicon-arrow-left " Voltar"]]]]])))

(defn tickets-app []
  (let [db (rf/subscribe [:db])]
    [:div
     ^{:key "navbar"} [navbar]
     [:div.spacer {:style {:height "48px"}}]
     [:div.container.main
      ^{:key "navigation"} [navigation]
      (if (:active-ticket @db)
        (let [active-ticket (-> @db :active-ticket :id)]
          ^{:key (str "ticket-" active-ticket)} [ticket-card (fetch-ticket db active-ticket)])
        (list
         ^{:key "toolbar"} [toolbar]
         [:hr]
         [:div.row
          [:div.list-group
           (for [ticket (:tickets @db)]
             ^{:key (str "row-" (:title ticket))} [ticket-row ticket])]]))]]))

(defn ^:export init []
  (rf/dispatch-sync [:initialize-db])
  (route/init)
  (reagent/render-component [tickets-app]
                            (. js/document (getElementById "app"))))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
