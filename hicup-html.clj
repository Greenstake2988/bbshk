(require '[hiccup.core :as h])

(def frutas (atom ["Manzana" "Banana" "Naranja"]))

(defn generar-html []
  (h/html
   [:html
    [:head
     [:meta {:charset "UTF-8"}]
     [:title "Frutas con Atoms"]]
    [:body
     [:h1 "Frutas del atom"]
     [:ul
      (for [f @frutas]
        [:li f])]]]))

(defn agregar-fruta [f]
  (swap! frutas conj f))

(defn guardar-html []
  (spit "frutas_atom.html" (generar-html)))

(defn borrar-fruta [f]
  (swap! frutas #(remove (partial = f) %)))
