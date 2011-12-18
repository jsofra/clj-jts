(ns clj-jts.core
  "clj-jts is a library for creating JTS (Java Topology Suite)
  geometry from Clojure. It allows for the creation of JTS instances
  from Clojure data structures and conversion of JTS instance to
  Clojure data structures.

  More information on JTS can be found at: http://www.vividsolutions.com/jts/main.htm
  "
  {:author "James Sofra"}
  
  (:import [com.vividsolutions.jts.geom
            GeometryFactory Coordinate Geometry GeometryCollection
            Point LineString LinearRing Polygon
            MultiPoint MultiLineString MultiPolygon]))

(def ^:private ^GeometryFactory geom-factory (GeometryFactory.))

(def ^:dynamic coord-keys [:x :y :z])

(defn ^Coordinate coordinate
  "Return a JTS Coordinate given a map with x, y, z keys.
   The z key is optional.
   e.g. (coordinate {:x 1 :y 1})
        (coordinate {:x 1 :y 1 :z 1})"
  [coord-map]
  (let [[x y z] (map coord-map coord-keys)]
    (if z (Coordinate. x y z) (Coordinate. x y))))

(defn ^"[Lcom.vividsolutions.jts.geom.Coordinate;" ->coord-array
  "Return an array of Corrdinate instances given a collection of coord maps."
  [coords]
  (into-array Coordinate
              (map #(coordinate %) coords)))

(defn ^Point point
  "Return a JTS Point given a coord map.
   e.g. (point {:x 1 :y 1})
        (point {:x 1 :y 1 :z 1})"
  [coord]
  (.createPoint geom-factory (coordinate coord)))

(defn ^LineString line-string
  "Return a JTS LineString given a collection of coord maps.
   e.g. (line-string [{:x 2 :y 8} {:x 4 :y 3}])"
  [coords]
  (.createLineString geom-factory (->coord-array coords)))

(defn ^LinearRing linear-ring
  "Return a JTS LinearRing given a collection of coord maps.
   The first and last coord must be the same.
   e.g. (linear-ring [{:x 0 :y 0} {:x 10 :y 0} {:x 10 :y 10}
                      {:x 0 :y 10} {:x 0 :y 0}])"
  [coords]
  (.createLinearRing geom-factory (->coord-array coords)))

(defn ^Polygon polygon
  "Return a JTS Polygon given collections of coord maps that should
  form valid linear-rings, one for the exterior polygon shell and
  several for the interior holes. The holes key is optional.
   e.g. (polygon {:shell [{:x 1 :y 1} {:x 100 :y 1} {:x 100 :y 100}
                          {:x 1 :y 100} {:x 1 :y 1}]})

        (polygon {:shell [{:x 1 :y 1} {:x 100 :y 1} {:x 100 :y 100}
                          {:x 1 :y 100} {:x 1 :y 1}]
                  :holes [[{:x 5 :y 5} {:x 20 :y 5} {:x 20 :y 20}
                           {:x 5 :y 20} {:x 5 :y 5}]
                          [{:x 50 :y 50} {:x 80 :y 50} {:x 80 :y 80}
                           {:x 50 :y 80} {:x 50 :y 50}]]})"
  [{:keys [shell holes]}]
  (.createPolygon geom-factory (linear-ring shell)
     (into-array LinearRing
                 (map #(linear-ring %) holes))))

(defn ^MultiPoint multi-point
  "Return a JTS MultiPoint given a colletion of coord maps.
   e.g. (multi-point [{:x 1 :y 20} {:x 45 :y 5} {:x 10 :y 34}])"
  [coords]
  (.createMultiPoint geom-factory (->coord-array coords)))

(defn ^MultiLineString multi-line-string
  "Return a JTS MultiLineString given a collection of line-string coords.
   e.g. (multi-line-string [[{:x 5 :y 5} {:x 2 :y 5} {:x 9 :y 4}]
                            [{:x 6 :y 4} {:x 8 :y 3} {:x 2 :y 3}]])"
  [line-string-coords]
  (.createMultiLineString
   geom-factory (into-array LineString
                            (map #(line-string %) line-string-coords))))

(defn ^MultiPolygon multi-polygon
  "Return a JTS MultiPolgon given a collection of polygon coords.
   e.g. (multi-polygon [{:shell [{:x 1 :y 1} {:x 100 :y 1} {:x 100 :y 100}
                                 {:x 1 :y 100} {:x 1 :y 1}]}
                        {:shell [{:x 4 :y 4} {:x 10 :y 4} {:x 10 :y 10}
                                 {:x 4 :y 10} {:x 4 :y 4}]}])"
  [polygon-coords]
  (.createMultiPolygon
   geom-factory (into-array Polygon (map #(polygon %) polygon-coords))))

(defn geometry
  "Provides a common interface for creating JTS geometry instances.
   Takes a map specifying the type of shape and the coordinates that form it.
   e.g. (geometry {:shape :point :coords {:x 1 :y 1}})"
  [{:keys [shape coords]}]
  ((ns-resolve 'clj-jts.core (symbol (name shape))) coords))

(defn ^GeometryCollection geometry-collection
  "Return a JTS GeometryCollection give a collection of shape maps.
   e.g. (geometry-collection [{:shape :point :coords {:x 4, :y 4}}
                              {:shape :line-string
                               :coords [{:x 3 :y 9} {:x 2 :y 7}]}])"
  [shapes]
  (.createGeometryCollection
   geom-factory (into-array Geometry (map geometry shapes))))

(defn get-geometries
  "Return a seq of JTS Geometry that form the a Geometry."
  [^Geometry geometry]
  (for [i (range 0 (.getNumGeometries geometry))]
    (.getGeometryN geometry i)))

(defn get-interior-rings
  "Return a seq of JTS LinearRing that represent the holes in a Polygon."
  [^Polygon geometry]
  (for [i (range 0 (.getNumInteriorRing geometry))]
    (.getInteriorRingN geometry i)))

(defn ->coord-map
  "Convert a JTS Coordinate to a map."
  [^Coordinate coordinate]
  (let [[x y z] coord-keys
        coord {x (.x coordinate) y (.y coordinate) z (.z coordinate)}]
    (if (.isNaN (z coord)) (dissoc coord z) coord)))

(defn get-coords
  "Return a vec of map-coords for a JTS Geometry."
  [^Geometry geometry]
  (into [] (map ->coord-map (.getCoordinates geometry))))

(defn get-multi-coords
  "Return a vec of coord data for a JTS GeometryCollection."
  [^GeometryCollection multi-geometry coords-fn]
  (into [] (map coords-fn (get-geometries multi-geometry))))

(defn get-shell-coords
  "Return coord data that represents the shell of a JTS Polygon."
  [^Polygon geometry]
  (get-coords (.getExteriorRing geometry)))

(defn get-hole-coords
  "Return a vec of coord data that represents the holes of a JTS Polygon."
  [^Polygon geometry]
  (into [] (map get-coords (get-interior-rings geometry))))

(defn get-polygon-coords
  "Return a map that holds the shell and holes coord data for a JTS Polygon."
  [^Polygon geometry]
  (let [coords {:shell (get-shell-coords geometry)
                :holes (get-hole-coords geometry)}]
    (if (seq (:holes coords))
      coords
      (dissoc coords :holes))))

(defprotocol JTSConversions
  (->shape-data [geometry]
    "Return a map of shape data given a JTS geometry instance.
     e.g.
     (let [jts-point (point {:x 1 :y 2})]
       (->shape-data jts-point))
     ;=> {:shape :point, :coords {:x 1.0, :y 2.0}}

     (let [shape-data (->shape-data (point {:x 1 :y 2}))]
       (geometry shape-data))
     ;=> #<Point POINT (1 2)>"))

(extend-protocol JTSConversions
  Point
  (->shape-data [geometry]
    {:shape :point :coords (first (get-coords geometry))})
  LineString
  (->shape-data [geometry]
    {:shape :line-string :coords (get-coords geometry)})
  LinearRing
  (->shape-data [geometry]
    {:shape :linear-ring :coords (get-coords geometry)})
  Polygon
  (->shape-data [geometry]
    {:shape :polygon :coords (get-polygon-coords geometry)})
  MultiPoint
  (->shape-data [geometry]
    {:shape :multi-point
     :coords (into [] (flatten (get-multi-coords geometry get-coords)))})
  MultiLineString
  (->shape-data [geometry]
    {:shape :multi-line-string
     :coords (get-multi-coords geometry get-coords)})
  MultiPolygon
  (->shape-data [geometry]
    {:shape :multi-polygon
     :coords (get-multi-coords geometry get-polygon-coords)})
  GeometryCollection
  (->shape-data [geometry]
    {:shape :geometry-collection
     :coords (into [] (map ->shape-data (get-geometries geometry)))}))
