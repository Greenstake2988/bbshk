
(require '[clojure.string :as str]
         '[babashka.pods :as pods])

(pods/load-pod 'org.babashka/mysql "0.0.8")
(require '[pod.babashka.mysql :as mysql])

(def inventario (atom {}))
(def next-id (atom 0))

(defn leer-linea []
  (flush)
  (read-line))

(defn leer-entero [prompt]
  (print prompt)
  (let [input (leer-linea)]
    (try
      (Integer/parseInt input)
      (catch Exception _ (do (println "Numero invalido.") nil)))))

(defn leer-decimal [prompt]
  (print prompt)
  (let [input (leer-linea)]
    (try
      (Double/parseDouble input)
      (catch Exception _ (do (println "Número inválido.") nil)))))

(defn agregar-item [nombre cantidad precio]
  (let [id (swap! next-id inc)]
    (swap! inventario assoc id {:nombre nombre
                                :cantidad cantidad
                                :precio precio})
    (println (str "Item agregado con ID " id ": " nombre))))

(defn editar-item [id]
  (if-let [item (get @inventario id)]
    (do
      (println (str "Actualizando item con ID " id ": " (:nombre item)))

      (print "Nuevo nombre (deja vacío para mantener): ")
      (let [nuevo-nombre (leer-linea)
            nombre (if (str/blank? nuevo-nombre) (:nombre item) nuevo-nombre)]

        (print "Nueva cantidad (deja vacío para mantener): ")
        (let [cantidad-str (leer-linea)
              cantidad (if (str/blank? cantidad-str)
                         (:cantidad item)
                         (Integer/parseInt cantidad-str))]

          (print "Nuevo precio (deja vacío para mantener): ")
          (let [precio-str (leer-linea)
                precio (if (str/blank? precio-str)
                         (:precio item)
                         (Double/parseDouble precio-str))]

            (swap! inventario assoc id {:nombre nombre
                                        :cantidad cantidad
                                        :precio precio})
            (println "Item actualizado.")))))
    (println "ID no encontrado.")))

(defn eliminar-item [id]
  (if (contains? @inventario id)
    (do
      (swap! inventario dissoc id)
      (println (str "Item con ID " id " eliminado.")))
    (println "ID no encontrado.")))

(defn mostrar-inventario []
  (println "\nInventario actual:")
  (doseq [[id {:keys [nombre cantidad precio]}] @inventario]
    (println (format "%d: %s | Cantidad: %d | Precio: %.2f"
                     id nombre cantidad precio)))
  (println))

(defn menu []
  (println "==== Inventario ====")
  (println "1. Agregar item")
  (println "2. Eliminar item")
  (println "3. Mostrar Inventario")
  (println "4. Editar")
  (println "5. Salir")
  (println "Elige una opcion: "))

(defn -main []
  (loop []
    (menu)
    (let [opcion (leer-linea)]
      (case opcion
        "1" (do
              (print "Nombre del item: ")
              (let [nombre (leer-linea)
                    cantidad (leer-entero "Cantidad: ")
                    precio (leer-decimal "Precio: ")]
                (if (and cantidad precio)
                  (agregar-item nombre cantidad precio)
                  (println "Error: cantidad o precio invalido. Intenta nuevamente.")))
              (recur))
        "2" (do
              (print "ID del item a eliminar: ")
              (let [id-str (leer-linea)
                    id (try
                         (Integer/parseInt id-str)
                         (catch Exception _ nil))]
                (if id
                  (eliminar-item id)
                  (println "ID invalido")))
              (recur))
        "3" (do
              (mostrar-inventario)
              (recur))
        "4" (do
              (print "ID del item a actualizar: ")
              (let [id-str (leer-linea)
                    id (try
                         (Integer/parseInt id-str)
                         (catch Exception _ nil))]
                (if id
                  (editar-item id)
                  (println "ID invalido")))
              (recur))
        "5" (println "Saliendo...")
        (do
          (println "Opcion invalida")
          (recur))))))

;; Ejecutar si se corre como script
(when (= *file* (System/getProperty "babashka.file"))
  (-main))
