(ns tollbooth)

(defstruct spot :car :booth) ;Maybe add spawn
(defstruct car :wait-time)
(def test-car (struct car (rand-int 10)))
(def running true)
(def world
  (vector (ref (struct spot nil false))
          (ref (struct spot nil false))
          (ref (struct spot nil false))
          (ref (struct spot nil true))))


(defn place [x]
  (if (< x (count world))
  (nth world x)
  -1
  ))

(defn create-car [loc]
  (let [p (place loc)
              c (struct car (rand-int 1000))]
    (dosync
          (alter p assoc :car c))))

(defn car-exit [spot]
  (let [car (:car spot)]
  (dosync
    (
    (alter spot assoc :car nil)))))

(defn move [curr-spot next-spot]
  (dosync
    (let [car (:car curr-spot)]
    (alter next-spot assoc :car car)
    (alter curr-spot dissoc :car)
    ))
  )

(defn setup []
   (dosync 
     (create-car 0)
     (agent 0)))

(def car (setup))
(println car)

(defn drive
  [loc]
  (let [p (place loc)
        c (:car @p)
        sleep-time (:wait-time c)
        space-ahead (place (inc loc))]
  (do
  (. Thread (sleep sleep-time))
  (println c)
        (do 
          (send-off car drive)
          (if
            (:booth p)
            (car-exit p)
            (move p space-ahead))))))

(defn print-world []
  (dorun (map (fn [spot] 
                (if (not (nil? (:car @spot)))
                  (println "|X|")
                  (println "|_|")
                  )) 
                world)))
(println "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
(def animator (agent nil))
(defn animate [x]
  (when running
    (send-off *agent* #'animate))
  (do
    (print-world)
    (println))
  (. Thread (sleep 500))
  nil)
;(print-world)
(send-off animator animate)
(send-off car drive)

