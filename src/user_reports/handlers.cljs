(ns user-reports.handlers
  (:require [re-frame.core :as rf :refer [register-handler]]
            [user-reports.db :as db]))

(register-handler
 :initialize-db
 (fn [_ _] db/db))

(rf/register-handler
 :index
 (fn [db _]
   (dissoc db :active-ticket)))

(rf/register-handler
 :ticket
 (fn [db [_ ticket-id]]
   (assoc db :active-ticket {:id ticket-id})))

(rf/register-handler
 :send-message
 (fn [db [_ ticket-id message]]
   (if (not (empty? message))
     (update-in db [:tickets ticket-id]
                (fn [ticket]
                  (update-in ticket [:messages] #(conj % message))))
     db)))
