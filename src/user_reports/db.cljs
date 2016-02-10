(ns ^:figwheel-no-load user-reports.db)

(defonce db {:tickets {1 {:id 1
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
                       2 {:id 2
                          :title "Fonte do site"
                          :description "Trocar fonte do site para Helvetica"
                          :type "suggestion"
                          :app "Potência"
                          :issued-at #inst "2016-02-02"
                          :issued-by "Nora Ramos"
                          :images [{:title "screenshot"
                                    :url "http://whatbettyknows.com/wp-content/uploads/2014/03/FB-DST-bug.jpg"}]
                          :open? false
                          :responded? true} }
             :active-user {:role :admin
                           :name "Eduardo Borges"
                           :avatar "https://pt.gravatar.com/userimage/9168074/0dc7b20f32a468d9dab2004326c305e2.jpg?size=200"}})
