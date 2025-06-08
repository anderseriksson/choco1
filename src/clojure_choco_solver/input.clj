(ns clojure-choco-solver.input)


(def ledare #{"Anders" "Tobias" "Jonas N" "Anna" "Jonas S"})






(def patruller #{"Utmanarna" "Äventyrarna" "Upptäckarna 1" "Upptäckarna 2"})

(def kalenderpass '("Söndag eftermiddag" "Söndag kväll" "Måndag förmiddag"))

(def aktivitet #{"Segla 2-krona 1" "Segla 2-krona 2" "Kanot" "Landhajk"})
(def ledarspecifik-aktivitet #{"Förbereda landhajk"})
(def ledar-aktivitet (into aktivitet ledarspecifik-aktivitet))