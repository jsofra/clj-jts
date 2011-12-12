(ns clj-jts.test.core
  {:author "James Sofra"}
  
  (:use clojure.test)
  (:require [clj-jts.core :as jts])
  (:import [com.vividsolutions.jts.geom
            Coordinate Point LineString LinearRing Polygon
            MultiPoint MultiLineString MultiPolygon]))

(def point-coords {:x 1.0 :y 1.0})
(def line-string-coords [{:x 2.0 :y 8.0} {:x 4.0 :y 3.0}])
(def linear-ring-coords  [{:x 0.0 :y 0.0} {:x 10.0 :y 0.0} {:x 10.0 :y 10.0}
                           {:x 0.0 :y 10.0} {:x 0.0 :y 0.0}])
(def polygon-coords {:shell [{:x 1.0 :y 1.0} {:x 100.0 :y 1.0} {:x 100.0 :y 100.0}
                             {:x 1.0 :y 100.0} {:x 1.0 :y 1.0}]
                     :holes [[{:x 5.0 :y 5.0} {:x 20.0 :y 5.0} {:x 20.0 :y 20.0}
                              {:x 5.0 :y 20.0} {:x 5.0 :y 5.0}]
                             [{:x 50.0 :y 50.0} {:x 80.0 :y 50.0} {:x 80.0 :y 80.0}
                              {:x 50.0 :y 80.0} {:x 50.0 :y 50.0}]]})
(def multi-point-coords [{:x 1.0 :y 20.0} {:x 45.0 :y 5.0} {:x 10.0 :y 34.0}])
(def multi-line-string-coords [[{:x 5.0 :y 5.0} {:x 2.0 :y 5.0} {:x 9.0 :y 4.0}]
                               [{:x 6.0 :y 4.0} {:x 8.0 :y 3.0} {:x 2.0 :y 3.0}]])
(def multi-polygon-coords [{:shell [{:x 1.0 :y 1.0} {:x 100.0 :y 1.0} {:x 100.0 :y 100.0}
                                    {:x 1.0 :y 100.0} {:x 1.0 :y 1.0}]}
                           {:shell [{:x 4.0 :y 4.0} {:x 10.0 :y 4.0} {:x 10.0 :y 10.0}
                                    {:x 4.0 :y 10.0} {:x 4.0 :y 4.0}]}])

(deftest jts-coordinate-test
  (is (instance? Coordinate (jts/coordinate point-coords))))

(deftest jts-factory-fns-test
  (are [class jts-fn coords]
       (instance? class (jts-fn coords))
       Point jts/point point-coords
       LineString jts/line-string line-string-coords
       LinearRing jts/linear-ring linear-ring-coords
       Polygon jts/polygon polygon-coords
       MultiPoint jts/multi-point multi-point-coords
       MultiLineString jts/multi-line-string multi-line-string-coords
       MultiPolygon jts/multi-polygon multi-polygon-coords))

(deftest jts-geometry-factory-test
  (are [class shape coords]
       (instance? class (jts/geometry {:shape shape :coords coords}))
       Point :point point-coords
       LineString :line-string line-string-coords
       LinearRing :linear-ring linear-ring-coords
       Polygon :polygon polygon-coords
       MultiPoint :multi-point multi-point-coords
       MultiLineString :multi-line-string multi-line-string-coords
       MultiPolygon :multi-polygon multi-polygon-coords))

(deftest jts-conversions-test
  (are [shape coords] (= {:shape shape :coords coords}
                         (jts/->shape-data (jts/geometry {:shape shape :coords coords})))
       :point point-coords
       :line-string line-string-coords
       :linear-ring linear-ring-coords
       :polygon polygon-coords
       :multi-point multi-point-coords
       :multi-line-string multi-line-string-coords
       :multi-polygon multi-polygon-coords))
