(ns bionic-eye.core
  (:require [clojure.java.io :as io])
  (:import [org.opencv.core Core Point MatOfRect Scalar]
           [org.opencv.imgcodecs Imgcodecs]
           [org.opencv.imgproc Imgproc]
           [org.opencv.objdetect CascadeClassifier]
           (clojure.lang RT)))

; The opencv native lib can only be loaded once per java process
; There's probably a better way to enforce that, but at least this
; lets you reload this file in the repl without blowing it up.
(defonce opencv-native-loaded?
         (do (RT/loadLibrary Core/NATIVE_LIBRARY_NAME)
             true))

(defn resource-path [filename] (-> filename io/resource .getPath))
(def frontal-face-xml (resource-path "lbpcascade_frontalface.xml"))
(def lena (resource-path "lena.png"))

(defn load-image
  "Load an image from the file at `path`.  Returns an OpenCV Mat object"
  [path]
  {:pre [(-> path io/as-file .exists)]}
  (Imgcodecs/imread path))

(defn face-rects
  [image]
  (let [detector (CascadeClassifier. frontal-face-xml)
        faces (MatOfRect.)]
    (.detectMultiScale detector image faces)
    (.toList faces)))

(defn point [x y] (Point. (double x) (double y)))
(defn rgb-color [r g b] (Scalar. (double r) (double g) (double b)))

(defn draw-rects!
  ([image rects]
   (draw-rects! image rects (rgb-color 0 255 0)))

  ([image rects color]
  (doseq [rect rects]
    (let [x (.x rect) y (.y rect)
          w (.width rect) h (.height rect)]
      (Imgproc/rectangle image (point x y) (point (+ x w) (+ y h)) color)))
  image))

(defn face-detect
  [input-path output-path]
  (let [image (load-image input-path)
        faces (face-rects image)]
    (draw-rects! image faces)
    (Imgcodecs/imwrite output-path image)))

(comment
  (face-detect lena "/Users/yusef/Desktop/lena-face.png")
  )