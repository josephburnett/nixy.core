(ns nixy.guide-test
  (:require
   [clojure.string :as str]
   [cljs.test :refer-macros [deftest is testing use-fixtures]]
   [nixy.fixtures :refer [merge-state]]
   [nixy.state :as state]
   [nixy.guide :as ng]
   [nixy.command :as command]))

(use-fixtures :each
  (merge-state
   {:terminal {:fs :nixy}}))

(deftest pred-args->guide-test
  (let [pred #(or (str/starts-with? " etc" %)
                  (str/starts-with? " var" %))
        given (partial ng/pred-args->guide :t pred)]
    (is (= (given "") {" " #{:t}}))
    (is (= (given " ") {"e" #{:t} "v" #{:t}}))
    (is (= (given " e") {"t" #{:t}}))
    (is (= (given " et") {"c" #{:t}}))
    (is (= (given " etc") {}))
    (is (= (given " v") {"a" #{:t}}))
    (is (= (given " va") {"r" #{:t}}))
    (is (= (given " var") {}))))

(defmethod command/args-pred :guide-test
  [_ _ args]
  (str/starts-with? " a\n" args))

(deftest state->guide-test
  (let [state (assoc-in @state/app-state
                        [:nixy :filesystem :root "bin"]
                        {"cd" {:args-pred :guide-test}})
        given #(ng/state->guide (assoc-in state [:terminal :line] %))]
    (is (= (given "") {"c" #{:command}}))
    (is (= (given "c") {"d" #{:command}}))
    (is (= (given "cd") {" " #{:args}}))
    (is (= (given "cd ") {"a" #{:args}}))
    (is (= (given "cd a") {"\n" #{:args}}))))
