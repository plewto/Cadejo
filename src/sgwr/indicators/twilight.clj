(ns sgwr.indicators.twilight
  "Defines displaybar cell using 25x35 'twilight' icons
   twilight displaybar is more efficient but less flexible then dot-matrix"
  (:require [sgwr.indicators.cell])
  (:require [sgwr.components.image :as image :reload true])
  (:require [sgwr.components.group :as group])
  (:require [sgwr.util.utilities :as utilities])
  (:import java.awt.image.BufferedImage 
           javax.imageio.ImageIO
           java.io.File))

(def ^:private width 25)
(def ^:private height 35)
(def ^:private char-name-map {\space "space"
                    \A "A" \a "A"
                    \B "B" \b "B"
                    \C "C" \c "C"
                    \D "D" \d "D"
                    \E "E" \e "E"
                    \F "F" \f "F"
                    \G "G" \g "G"
                    \H "H" \h "H"
                    \I "I" \i "I"
                    \J "J" \j "J"
                    \K "K" \k "K"
                    \L "L" \l "L"
                    \M "M" \m "M"
                    \N "N" \n "N"
                    \O "O" \o "O"
                    \P "P" \p "P"
                    \Q "Q" \q "Q"
                    \R "R" \r "R"
                    \S "S" \s "S"
                    \T "T" \t "T"
                    \U "U" \u "U"
                    \V "V" \v "V"
                    \W "W" \w "W"
                    \X "X" \x "X"
                    \Y "Y" \y "Y"
                    \Z "Z" \z "Z"
                    \0 "0"
                    \1 "1"
                    \2 "2"
                    \3 "3"
                    \4 "4"
                    \5 "5"
                    \6 "6"
                    \7 "7"
                    \8 "8"
                    \9 "9"
                    \^ "accent"
                    \@ "ampersand"
                    \& "and"
                    \* "asterisk"
                    \\ "backward_slant"
                    \| "bar"
                    \: "colon"
                    \, "comma"
                    \$ "dollar"
                    \! "exclamation"
                    \/ "forward_slant"
                    \> "greater_then"
                    \{ "left_brace"
                    \( "left_paren"
                    \[ "left_square_brace"
                    \< "less_then"
                    \- "minus"
                    \% "percent"
                    \. "period"
                    \+ "plus"
                    \# "pound"
                    \? "question"
                    \' "quote1"
                    \" "quote2"
                    \} "right_brace"
                    \) "right_paren"
                    \] "right_square_brace"
                    \; "semicolon"
                    \~ "tilde"
                    \_ "underline"
                    \= "equal"})


(def ^:private resource-path "resources/icons/Twilight/")


(defn- read-image [c]
  (let [suffix (get char-name-map c "space")
        filename (format "%s%s.png" resource-path suffix)
        f (File. filename)]
    (try
      (ImageIO/read f)
      (catch Exception ex
        (utilities/warning (format "Can not read image file '%s'" filename))
        nil))))

(defn twilight-cell [grp x-offset y-offset & args]
  (let [img (image/image grp [x-offset y-offset] width height)
        cell (reify sgwr.indicators.cell/Cell
               (cell-width [this] width)
               (cell-height [this] height)
               (colors! [this _ __] nil) ; ignore
               (character-set [this] (keys char-name-map))
               (display! [this c]
                 (image/set-image! img (read-image c))))]
    cell))
