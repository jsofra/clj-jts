(ns meridian.clj-jts.core
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

(defonce ^:private ^GeometryFactory geom-factory (GeometryFactory.))

(defn ^Coordinate coordinate
  "Return a JTS Coordinate given a map with x, y, z keys.
   The z key is optional.
   e.g. (coordinate [1 1])
        (coordinate [1 1 1])"
  [[x y & [z]]]
  (if z (Coordinate. x y z) (Coordinate. x y)))

(defn ^"[Lcom.vividsolutions.jts.geom.Coordinate;" coord-array
  "Return an array of Corrdinate instances given a collection of coord maps."
  [coords]
  (into-array Coordinate
              (map #(coordinate %) coords)))

(defn ^Point point
  "Return a JTS Point given a coord map.
   e.g. (point [1 1])
        (point [1 1 1])"
  [coord]
  (.createPoint geom-factory (coordinate coord)))

(defn ^LineString line-string
  "Return a JTS LineString given a collection of coord maps.
   e.g. (line-string [[2 8] [4 3]])"
  [coords]
  (.createLineString geom-factory (coord-array coords)))

(defn ^LinearRing linear-ring
  "Return a JTS LinearRing given a collection of coord maps.
   The first and last coord must be the same.
   e.g. (linear-ring [[0 0] [10 0] [10 10]
                      [0 10] [0 0]])"
  [coords]
  (.createLinearRing geom-factory (coord-array coords)))

(defn ^Polygon polygon
  "Return a JTS Polygon given collections of coord maps that should
  form valid linear-rings, one for the exterior polygon shell and
  several for the interior holes. The holes key is optional.
   e.g. (polygon [[[1 1] [100 1] [100 100] [1 100] [1 1]]])

        (polygon [[[1 1] [100 1] [100 100] [1 100] [1 1]]
                  [[5 5] [20 5] [20 20] [5 20] [5 5]]
                  [[50 50] [80 50] [80 80] [50 80] [50 50]]])"
  [[shell & holes]]
  (.createPolygon geom-factory (linear-ring shell)
     (into-array LinearRing
                 (map #(linear-ring %) holes))))

(defn ^MultiPoint multi-point
  "Return a JTS MultiPoint given a colletion of coord maps.
   e.g. (multi-point [[1 20] [45 5] [10 34]])"
  [coords]
  (.createMultiPoint geom-factory (coord-array coords)))

(defn ^MultiLineString multi-line-string
  "Return a JTS MultiLineString given a collection of line-string coords.
   e.g. (multi-line-string [[[5 5] [2 5] [9 4]]
                            [[6 4] [8 3] [2 3]]])"
  [line-string-coords]
  (.createMultiLineString
   geom-factory (into-array LineString
                            (map #(line-string %) line-string-coords))))

(defn ^MultiPolygon multi-polygon
  "Return a JTS MultiPolgon given a collection of polygon coords.
   e.g. (multi-polygon [[[[1 1] [100 1] [100 100] [1 100] [1 1]]]
                        [[[4 4] [10 4] [10 10] [4 10] [4 4]]]])"
  [polygon-coords]
  (.createMultiPolygon
   geom-factory (into-array Polygon (map #(polygon %) polygon-coords))))

(defn geometry
  "Provides a common interface for creating JTS geometry instances.
   Takes a map specifying the type of shape and the coordinates that form it.
   e.g. (geometry {:type :point :coordinates [1 1]})"
  [{:keys [type coordinates]}]
  ((case type
     :Point point
     :LineString line-string
     :LinearRing linear-ring
     :Polygon polygon
     :MultiPoint multi-point
     :MultiLineString multi-line-string
     :MultiPolygon multi-polygon) coordinates))

(defn ^GeometryCollection geometry-collection
  "Return a JTS GeometryCollection give a collection of shape maps.
   e.g. (geometry-collection [{:type :point :coordinates [4, 4]}
                              {:type :line-string
                               :coordinates [[3 9] [2 7]]}])"
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

(defn coord-map
  "Convert a JTS Coordinate to a map."
  [^Coordinate coordinate]
  (let [coord {:x (.x coordinate) :y (.y coordinate) :z (.z coordinate)}]
    (if (.isNaN (:z coord)) (dissoc coord :z) coord)))

(defn coord-vec
  "Convert a JTS Coordinate to a vector."
  [^Coordinate coordinate]
  (if (.isNaN (.z coordinate))
    [(.x coordinate) (.y coordinate)]
    [(.x coordinate) (.y coordinate) (.z coordinate)]))

(defn get-point-coord
  "Return a vector coord."
  [^Point point]
  (coord-vec (.getCoordinate point)))

(defn get-coords
  "Return a vec of coords for a JTS Geometry."
  [^Geometry geometry]
  (mapv coord-vec (.getCoordinates geometry)))

(defn get-multi-coords
  "Return a vec of coord data for a JTS GeometryCollection."
  [^GeometryCollection multi-geometry coords-fn]
  (mapv coords-fn (get-geometries multi-geometry)))

(defn get-shell-coords
  "Return coord data that represents the shell of a JTS Polygon."
  [^Polygon geometry]
  (get-coords (.getExteriorRing geometry)))

(defn get-hole-coords
  "Return a vec of coord data that represents the holes of a JTS Polygon."
  [^Polygon geometry]
  (mapv get-coords (get-interior-rings geometry)))

(defn get-polygon-coords
  "Return a map that holds the shell and holes coord data for a JTS Polygon."
  [^Polygon geometry]
  (into [] (concat [(get-shell-coords geometry)]
                   (get-hole-coords geometry))))

(defprotocol JTSConversions
  (->shape-data [geometry]
    "Return a map of shape data given a JTS geometry instance.
     e.g.
     (let [jts-point (point [1 2])]
       (->shape-data jts-point))
     ;=> {:type :point, :coordinates [1.0, 2.0]}

     (let [shape-data (->shape-data (point [1 2]))]
       (geometry shape-data))
     ;=> #<Point POINT (1 2)>"))

(extend-protocol JTSConversions
  Point
  (->shape-data [geometry]
    {:type :Point :coordinates (get-point-coord geometry)})
  LineString
  (->shape-data [geometry]
    {:type :LineString :coordinates (get-coords geometry)})
  LinearRing
  (->shape-data [geometry]
    {:type :LinearRing :coordinates (get-coords geometry)})
  Polygon
  (->shape-data [geometry]
    {:type :Polygon :coordinates (get-polygon-coords geometry)})
  MultiPoint
  (->shape-data [geometry]
    {:type :MultiPoint
     :coordinates (get-multi-coords geometry get-point-coord)})
  MultiLineString
  (->shape-data [geometry]
    {:type :MultiLineString
     :coordinates (get-multi-coords geometry get-coords)})
  MultiPolygon
  (->shape-data [geometry]
    {:type :MultiPolygon
     :coordinates (get-multi-coords geometry get-polygon-coords)})
  GeometryCollection
  (->shape-data [geometry]
    {:type :GeometryCollection
     :coordinates (mapv ->shape-data (get-geometries geometry))}))
