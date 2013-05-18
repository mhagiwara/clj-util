(ns clj-util
  (:require [clojure.java.shell :as shell])
  (:require [clojure.tools.logging :as logging])
  )

(defn run-shell [shell & cmds]
  (let [cmd-str (apply str cmds)]
    (logging/info (str "[" shell "] " cmd-str))
    (shell/sh shell "-c" cmd-str)
    )
  )

(defn read-lines [in]
  (line-seq (clojure.java.io/reader in))
  )

(defn -cli-simple [args]
  (defn arg->kw [arg] (keyword (clojure.string/replace arg #"^\-+" "")))
  (loop [args args
         opts {}
         prev nil]
    (if-let [arg (first args)]      
      (if (= (first arg) \-)
        (recur (rest args) (if prev (assoc opts (arg->kw prev) true) opts) arg)
        (recur (rest args) (if prev (assoc opts (arg->kw prev) arg)  opts) nil)
        )
      (if prev (assoc opts (arg->kw prev) true) opts))
    )
  )

(defn cli-simple
  ([] (-cli-simple *command-line-args*))
  ([args] (-cli-simple args))
  )

(defn indexed [coll]
  (map-indexed (fn [x y] [y x]) coll)
  )

;; (defn -main [& args]
;;   (println (cli-simple args))
;;  )
