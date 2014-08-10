(ns sgwr.element
  "Defines common methods for all drawing elements")

(defprotocol Element

  ;; (is-text?   ;; use element-type insted
  ;;   [this]
  ;;   "Predicate true if this element is text")

  (element-type 
    [this]
    "Returns unique keyword identifying element class")

  (attributes
    [this]
    "Returns attributes for this element")

  (attributes!
    [this att]
    "Sets attributes for this element.")

  (construction-points
    [this]
    "Returns a vector of points which defines this elements
     geometry. The result is always a nested vector")

  (position!
    [this points]
    "Sets the position and geometry of this element.
     points is always a nested vector.")

  (shape 
    [this cs]
    "Return an instance of java.awt.Shape for this element using the 
     as represented in the coordinate system sc.")

  (color
    [this]
    "Convenience method returns element's color")

  (stroke
    [this]
    "Returns instance of BasicStroke for this element.
     stoke is not utilized by all elements.")

  (hidden?
    [this]
    "Convenience predicate returns true if this element is hidden")

  (filled? 
    [this]
    "Convenience predicate returns true if this element is filled
     Not all elements utilize the filled attribute.")

  (selected?
    [this]
    "Convenience predicate returns true if this element is
     selected.")

  (clone 
    [this]
    "Return a new instance of this element with identical state.")

  (to-string 
    [this])
 
  )
