(ns meridian.test-clj-jts
  {:author "James Sofra"}

  (:use clojure.test)
  (:require [meridian.clj-jts :as jts]
            [meridian.flatland :as fl])
  (:import [com.vividsolutions.jts.geom
            Coordinate Point LineString LinearRing Polygon
            MultiPoint MultiLineString MultiPolygon]))

(def point-coords [1.0 1.0])
(def line-string-coords [[2.0 8.0] [4.0 3.0]])
(def linear-ring-coords  [[0.0 0.0] [10.0 0.0] [10.0 10.0]
                          [0.0  10.0] [0.0 0.0]])
(def polygon-coords [[[1.0 1.0] [100.0 1.0] [100.0 100.0]
                      [1.0 100.0] [1.0  1.0]]
                     [[5.0 5.0] [20.0 5.0] [20.0 20.0]
                      [5.0 20.0] [5.0 5.0]]
                     [[50.0 50.0] [80.0 50.0] [80.0  80.0]
                      [50.0 80.0] [50.0 50.0]]])
(def multi-point-coords [[1.0 20.0] [45.0 5.0] [10.0 34.0]])
(def multi-line-string-coords [[[5.0 5.0] [2.0 5.0] [9.0  4.0]]
                               [[6.0 4.0] [8.0  3.0] [2.0 3.0]]])
(def multi-polygon-coords [[[[1.0 1.0] [100.0 1.0] [100.0 100.0]
                             [1.0 100.0] [1.0 1.0]]]
                           [[[4.0 4.0] [10.0 4.0] [10.0 10.0]
                             [4.0 10.0] [4.0 4.0]]]])

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
       (instance? class (jts/map->jts {:type shape :coordinates coords}))
       Point :Point point-coords
       LineString :LineString line-string-coords
       LinearRing :LinearRing linear-ring-coords
       Polygon :Polygon polygon-coords
       MultiPoint :MultiPoint multi-point-coords
       MultiLineString :MultiLineString multi-line-string-coords
       MultiPolygon :MultiPolygon multi-polygon-coords))

(deftest jts-conversions-test
  (are [shape coords] (= (fl/map->geometry {:type shape :coordinates coords})
                         (fl/->geometry (jts/map->jts {:type shape :coordinates coords})))
       :Point point-coords
       :LineString line-string-coords
       :LinearRing linear-ring-coords
       :Polygon polygon-coords
       :MultiPoint multi-point-coords
       :MultiLineString multi-line-string-coords
       :MultiPolygon multi-polygon-coords))
