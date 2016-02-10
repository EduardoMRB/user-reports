(ns user-reports.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf :refer [register-sub]]))

(register-sub
 :tickets
 (fn [db _]
   (reaction (:tickets @db))))

(register-sub
 :db
 (fn [db _]
   (reaction @db)))

(register-sub
 :active-user
 (fn [db _]
   (reaction (:active-user @db))))

(register-sub
 :ticket-messages
 (fn [db [_ ticket-id]]
   (reaction (get-in @db [:tickets ticket-id :messages]))))
