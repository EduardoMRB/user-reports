(ns ^:figwheel-always user-reports.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [user-reports.route :as route]
            [user-reports.handlers :as handlers]
            [user-reports.subs :as subs]))

(enable-console-print!)

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
  [:a.list-group-item {:href (route/path-for :ticket :id (:id ticket))}
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

(defn input-val
  "Takes an event and returns the value of it's target"
  [e]
  (-> e .-target .-value))

(defn handle-message-send [ticket user message]
  (when (not (empty? @message))
    (rf/dispatch [:send-message (:id ticket) {:text    @message
                                              :sent-by @user}])
    (reset! message "")))

(defn ticket-card [ticket]
  (let [active-user (rf/subscribe [:active-user])
        message     (atom "")
        messages    (rf/subscribe [:ticket-messages (:id ticket)])]
    [:div.col-sm-12
     [:h1 (:title ticket)]
     [:p (:description ticket)]
     [:p (str "Reportado por: " (:issued-by ticket)
              " em: " (localize-date (:issued-at ticket)))]
     (when (seq @messages)
       [:div.col-sm-12 {:style {:margin-bottom "40px"}}
        [:h2 "Mensagens"]
        (for [message (or @messages [])]
          [:div.row {:style {:margin-bottom "11px"}}
           [:div.col-sm-12
            [:div.col-sm-1
             [:img.img-circle.avatar {:src   (get-in message [:sent-by :avatar])
                                      :title (get-in message [:sent-by :name])}]]
            [:div.col-sm-10
             [:div.message-popup {:style {:min-height 50
                                          :background-color "#fff"
                                          :border "1px #ccc solid"}}
              [:p {:style {:padding "5px"}} (:text message)]]]]])])
     [:div.col-sm-12
      [:div.col-sm-1
       [:img.img-circle.avatar {:src (:avatar @active-user)}]]
      [:div.col-sm-10
       [:div.form-group
        [:div.message-popup
         [:textarea.form-control.message {:placeholder "Escreva sua resposta"
                                          :on-change #(reset! message (input-val %))}]]]
       [:button.btn.btn-success.pull-right {:on-click #(handle-message-send ticket active-user message)}
        "Enviar"]]]]))

(defn fetch-ticket [db ticket-id]
  (->> @db
       :tickets
       (filter #(= (:id %) (js/parseInt ticket-id)))
       (first)))

(defn navigation []
  (let [db (rf/subscribe [:db])]
    (when (:active-ticket @db)
      [:div.container
       [:div.row
        [:div.btn-group
         [:a.btn.btn-default {:href (route/path-for :index)}
          [:span.glyphicon.glyphicon-arrow-left " Voltar"]]]]])))

(defn tickets-app []
  (let [db (rf/subscribe [:db])]
    [:div
     ^{:key "navbar"} [navbar]
     [:div.spacer {:style {:height "48px"}}]
     [:div.container.main
      ^{:key "navigation"} [navigation]
      (if (:active-ticket @db)
        (let [active-ticket (js/parseInt (-> @db :active-ticket :id))]
          ^{:key (str "ticket-" active-ticket)} [ticket-card (get-in @db [:tickets active-ticket])])
        (list
         ^{:key "toolbar"} [toolbar]
         [:hr]
         [:div.row
          [:div.list-group
           (for [[_ ticket] (:tickets @db)]
             ^{:key (str "row-" (:title ticket))} [ticket-row ticket])]]))]]))

(defn ^:export init []
  (rf/dispatch-sync [:initialize-db])
  (route/init)
  (reagent/render-component [tickets-app]
                            (. js/document (getElementById "app"))))

(init)


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
