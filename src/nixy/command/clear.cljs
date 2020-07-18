(ns nixy.command.clear
  (:require
   [nixy.command :as command]))

(defmethod command/exec :clear [_ state _ _]
  {:stdout []
   :state
   (as-> state s
     (assoc-in s [:terminal :output] [])
     (assoc-in s [:terminal :line] []))})
