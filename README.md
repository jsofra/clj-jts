# clj-jts

clj-jts is a library for creating JTS (Java Topology Suite) geometry from Clojure. It allows for the creation of JTS instances from Clojure data structures and conversion of JTS instance to Clojure data structures.

More information on JTS can be found at: [Vivid Solutions](http://www.vividsolutions.com/jts/main.htm)

## Usage

```clojure
user> (use 'clj-jts.core)


;; Geometry functions for creating JTS Geometry from Clojure data

user> (point {:x 1 :y 1})
#<Point POINT (1 1)>

user> (point {:x 1 :y 1 :z 1})
#<Point POINT (1 1)>

user> (line-string [{:x 2 :y 8} {:x 4 :y 3}])
#<LineString LINESTRING (2 8, 4 3)>

user> (linear-ring [{:x 0 :y 0} {:x 10 :y 0} {:x 10 :y 10} {:x 0 :y 10} {:x 0 :y 0}])
#<LinearRing LINEARRING (0 0, 10 0, 10 10, 0 10, 0 0)>

user> (polygon {:shell [{:x 1 :y 1} {:x 100 :y 1} {:x 100 :y 100} {:x 1 :y 100} {:x 1 :y 1}]})
#<Polygon POLYGON ((1 1, 100 1, 100 100, 1 100, 1 1))>

user> (polygon {:shell [{:x 1 :y 1} {:x 100 :y 1} {:x 100 :y 100} {:x 1 :y 100} {:x 1 :y 1}]
                :holes [[{:x 5 :y 5} {:x 20 :y 5} {:x 20 :y 20} {:x 5 :y 20} {:x 5 :y 5}]
                        [{:x 50 :y 50} {:x 80 :y 50} {:x 80 :y 80} {:x 50 :y 80} {:x 50 :y 50}]]})
#<Polygon POLYGON ((1 1, 100 1, 100 100, 1 100, 1 1), (5 5, 20 5, 20 20, 5 20, 5 5), (50 50, 80 50, 80 80, 50 80, 50 50))>

user> (multi-point [{:x 1 :y 20} {:x 45 :y 5} {:x 10 :y 34}])
#<MultiPoint MULTIPOINT (1 20, 45 5, 10 34)>

user> (multi-line-string [[{:x 5 :y 5} {:x 2 :y 5} {:x 9 :y 4}]
                          [{:x 6 :y 4} {:x 8 :y 3} {:x 2 :y 3}]])
#<MultiLineString MULTILINESTRING ((5 5, 2 5, 9 4), (6 4, 8 3, 2 3))>

user> (multi-polygon [{:shell [{:x 1 :y 1} {:x 100 :y 1} {:x 100 :y 100} {:x 1 :y 100} {:x 1 :y 1}]}
                      {:shell [{:x 4 :y 4} {:x 10 :y 4} {:x 10 :y 10} {:x 4 :y 10} {:x 4 :y 4}]}])
#<MultiPolygon MULTIPOLYGON (((1 1, 100 1, 100 100, 1 100, 1 1)), ((4 4, 10 4, 10 10, 4 10, 4 4)))>


;; Common interface for creating JTS Geometry through the geometry function

user> (geometry {:shape :point :coords {:x 1 :y 1}})
#<Point POINT (1 1)>

user> (geometry {:shape :line-string :coords [{:x 2 :y 8} {:x 4 :y 3}]})
#<LineString LINESTRING (2 8, 4 3)>


;; Convert JTS Geometry to Clojure data

user> (->shape-data (point {:x 1 :y 1}))
{:shape :point :coords {:x 1.0 :y 1.0}}

user> (->shape-data (line-string [{:x 2 :y 8} {:x 4 :y 3}]))
{:shape :line-string :coords [{:x 2.0 :y 8.0} {:x 4.0 :y 3.0}]}
```
