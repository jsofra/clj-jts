# Meridian clj-jts

Meridian clj-jts is a library for creating JTS (Java Topology Suite) geometry from Clojure. It allows for the creation of JTS instances from Clojure data structures and conversion of JTS instance to Clojure data structures.

More information on JTS can be found at: [Vivid Solutions] (http://www.vividsolutions.com/jts/main.htm)

Meridian clj-jts provides an implementation of the abstractions defined in Meridian [Shapes] (http://github.com/jsofra/shapes) library.

Meridian clj-jts now uses the geometry structures defined in the [GeoJSON] (http://www.geojson.org/) specification to maintain Meridian Shapes compatiblity and to conform to a standard.

## Installation

Add the following dependency to your `project.clj` file:

    [meridian/clj-jts "0.1.0"]

## Usage

```clojure
user> (require '[meridian.clj-jts :as jts])


;; Coordinate functions for creating JTS Coordinates

user> (jts/coordinate [1 2])
#<Coordinate (1.0, 2.0, NaN)>

user> (jts/coordinate [1 2 3])
#<Coordinate (1.0, 2.0, 3.0)>


;; Geometry functions for creating JTS Geometry from Clojure data

user> (jts/point [1 2])
#<Point POINT (1 2)>

user> (jts/line-string [[2 8] [4 3]])
#<LineString LINESTRING (2 8, 4 3)>

user> (jts/linear-ring [[0 0] [10 0] [10 10] [0 10] [0 0]])
#<LinearRing LINEARRING (0 0, 10 0, 10 10, 0 10, 0 0)>

user> (jts/polygon [[[1 1] [100 1] [100 100] [1 100] [1 1]]])
#<Polygon POLYGON ((1 1, 100 1, 100 100, 1 100, 1 1))>

user> (jts/polygon [[[1 1] [100 1] [100 100] [1 100] [1 1]]
                    [[5 5] [20 5] [20 20] [5 20] [5 5]]
                    [[50 50] [80 50] [80 80] [50 80] [50 50]]])
#<Polygon POLYGON ((1 1, 100 1, 100 100, 1 100, 1 1), (5 5, 20 5, 20 20, 5 20, 5 5), (50 50, 80 50, 80 80, 50 80, 50 50))>

user> (jts/multi-point [[1 20] [45 5] [10 34]])
#<MultiPoint MULTIPOINT (1 20, 45 5, 10 34)>

user> (jts/multi-line-string [[[5 5] [2 5] [9 4]]
                              [[6 4] [8 3] [2 3]]])
#<MultiLineString MULTILINESTRING ((5 5, 2 5, 9 4), (6 4, 8 3, 2 3))>

user> (jts/multi-polygon [[[[1 1] [100 1] [100 100] [1 100] [1 1]]]
                           [[[4 4] [10 4] [10 10] [4 10] [4 4]]]])
#<MultiPolygon MULTIPOLYGON (((1 1, 100 1, 100 100, 1 100, 1 1)), ((4 4, 10 4, 10 10, 4 10, 4 4)))>


;; Common interface for creating JTS Geometry through the map->jts function

user> (jts/map->jts {:type :point :coordinates [1 1]})
#<Point POINT (1 1)>

user> (jts/map->jts {:type :line-string :coordinates [[2 8] [4 3]]})
#<LineString LINESTRING (2 8, 4 3)>


;; Convert JTS Geometry to Clojure data

user> (jts/->shape (jts/point [1 1]))
{:type :point :coordinates [1.0 1.0}}

user> (jts/->shape (jts/line-string [[2 8] [4 3]]))
{:type :line-string :coordinates [[2.0 8.0] [4.0 3.0]]}
```

## Changes

Since 0.0.1
* Major breaking changes since v0.0.1
   - Now comforms to GeoJSON structures
   - Implements that Meridian Shapes abstraction so that operations can be performed using that abstraction

## License

Copyright © James Sofra, 2013

Distributed under the Eclipse Public License, the same as Clojure.
