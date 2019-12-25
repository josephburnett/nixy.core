(ns ^:figwheel-hooks nixy.guide
  (:require
   [clojure.string :as str]))

(def valid-keys
  ["a" "b" "c" "d" "e" "f" "g" "h" "i" "j" "k" "l"
   "m" "n" "o" "p" "q" "r" "s" "t" "u" "v" "w" "x"
   "y" "z" " "])

(defn pred-args->guide [tag pred args]
  (reduce
   #(if (pred (str/join (concat args %2)))
      (assoc %1 %2 #{tag})
      %1)
   {}
   valid-keys))

(defn state->guide [state]
  (let [line (get-in state [:terminal :line])
        command-names (keys (get-in state [:filesystem "bin"]))]
    (if-let [name (first (filter #(str/starts-with? line %) command-names))]
      ;; guide from command arguments predicate
      (let [command (get-in state [:filesystem "bin" name])
            args (subs line (count name))]
        (pred-args->guide :args (:args command) args))
      ;; guide from list of possible commands
      (let [pred (fn [l] (not (empty? (filter #(str/starts-with? % l) command-names))))]
        (pred-args->guide :command pred line)))))
