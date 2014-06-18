(println "\t--> MASA data")

(ns cadejo.instruments.masa.data
  (:use [cadejo.instruments.masa.program :only [save-program masa bank]])
  (:use [cadejo.instruments.masa.genpatch]))

(.register-function! bank
                     :random
                     cadejo.instruments.masa.genpatch/random-masa-program)

(save-program 0 "Barrows"
  (masa :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000]
        :registration [    7     5     2     5     1     6     7     6     4]
        :pedals       [ 0.00  0.00  0.00  0.00  0.00  0.00  0.00  0.00  0.00]
        :percussion   [    0     0     0     0     0     0     0     0     0]
        :amp          0.20
        :pedal-sens   0.00
        :decay        0.20
        :sustain      0.90
        :vrate        5.00
        :vsens        0.01
        :vdepth       0.00
        :vdelay       4.65
        :scanner-delay       0.01
        :scanner-delay-mod   0.74
        :scanner-mod-rate    0.37
        :scanner-mod-spread  0.44
        :scanner-scan-rate   0.59
        :scanner-crossmix    0.91
        :scanner-mix         0.29
        :reverb-size         0.85
        :reverb-damp         0.68
        :reverb-mix          0.40))

(save-program 1 "Stanton"
  (masa :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000]
        :registration [    5     7     4     3     3     1     0     2     0]
        :pedals       [ 0.00  0.00  0.00  0.00  0.00  0.00  0.00  0.00  0.00]
        :percussion   [    0     0     0     0     1     0     0     1     0]
        :amp          0.28
        :pedal-sens   0.00
        :decay        0.20
        :sustain      0.90
        :vrate        5.44
        :vsens        0.01
        :vdepth       0.00
        :vdelay       3.31
        :scanner-delay       0.01
        :scanner-delay-mod   0.00
        :scanner-mod-rate    1.60
        :scanner-mod-spread  0.00
        :scanner-scan-rate   5.44
        :scanner-crossmix    0.40
        :scanner-mix         0.30
        :reverb-size         0.50
        :reverb-damp         0.50
        :reverb-mix          0.20))

(save-program 2 "Jimmy Smith" "Pedal control drawbars 4 & 9"
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    8     8     8     0     0     0     0     0     0 ]
      :pedals       [+0.00 +0.00 +0.00 +1.00 +0.00 +0.00 +0.00 +0.00 +0.25 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.000  :vdepth 0.000    :vsens 0.020      :vdelay 0.000
      :scanner-:delay-mod 0.200      :scanner-delay 0.010
      :scanner-mod-rate  1.000       :scanner-mod-spread 0.000
      :scanner-scan-rate 7.000       :scanner-crossmix 0.200
      :scanner-mix 0.200 
      :room-size 0.500               :reverb-damp 0.500
      :reverb-mix 0.200
      :amp 0.20                      :pedal-sens 0.00))

(save-program 3 "Bro Jack"
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    8     0     0     0     0     0     8     8     8 ]
      :pedals       [+0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.000  :vdepth 0.000    :vsens 0.010      :vdelay 0.000
      :scanner-:delay-mod 0.200      :scanner-delay 0.010
      :scanner-mod-rate  1.000       :scanner-mod-spread 0.000
      :scanner-scan-rate 7.000       :scanner-crossmix 0.200
      :scanner-mix 0.100 
      :room-size 0.500               :reverb-damp 0.500
      :reverb-mix 0.200
      :amp 0.20                      :pedal-sens 0.00))

(save-program 4 "Tibia"
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    8     0     8     8     0     8     0     0     8 ]
      :pedals       [+0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
      :scanner-:delay-mod 0.000      :scanner-delay 0.010
      :scanner-mod-rate  7.000       :scanner-mod-spread 0.000
      :scanner-scan-rate 0.100       :scanner-crossmix 0.200
      :scanner-mix 0.200 
      :room-size 0.500               :reverb-damp 0.500
      :reverb-mix 0.200
      :amp 0.141                     :pedal-sens 0.00))

(save-program 5 "Hallow Out" "Same as Tiba except with pedal" 
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    0     0     8     8     0     8     0     0     8 ]
      :pedals       [+1.00 +0.00 -1.00 -1.00 +0.00 -1.00 +0.00 +0.00 +0.00 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
      :scanner-:delay-mod 0.100      :scanner-delay 0.010
      :scanner-mod-rate  7.000       :scanner-mod-spread 0.000
      :scanner-scan-rate 0.100       :scanner-crossmix 0.200
      :scanner-mix 0.200 
      :room-size 0.500               :reverb-damp 0.500
      :reverb-mix 0.300
      :amp 0.20                      :pedal-sens 0.00))

(save-program 6 "Gospel"
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    8     0     8     8     0     8     0     0     8 ]
      :pedals       [+0.00 +0.75 +0.00 +0.00 +0.00 -1.00 +0.00 +0.00 +0.50 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
      :scanner-:delay-mod 0.100      :scanner-delay 0.010
      :scanner-mod-rate  7.000       :scanner-mod-spread 0.000
      :scanner-scan-rate 3.500       :scanner-crossmix 0.200
      :scanner-mix 0.200 
      :room-size 0.500               :reverb-damp 0.500
      :reverb-mix 0.300
      :amp 0.141                     :pedal-sens 0.00))

(save-program 7 "Annointed"
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    8     4     8     6     0     0     0     4     6 ]
      :pedals       [-1.00 -1.00 +0.00 -0.25 +0.00 +0.00 +0.00 -1.00 -1.00 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
      :scanner-:delay-mod 0.200      :scanner-delay 0.010
      :scanner-mod-rate  6.000       :scanner-mod-spread 0.000
      :scanner-scan-rate 4.000       :scanner-crossmix 0.200
      :scanner-mix 0.150 
      :room-size 0.600               :reverb-damp 0.500
      :reverb-mix 0.400
      :amp 0.398                     :pedal-sens 0.00))

(save-program 8 "Gamba/Gadekt 8"
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    0     0     3     4     8     4     4     4     3 ]
      :pedals       [-0.00 -0.00 +1.00 -0.00 -0.25 +0.00 -1.00 -1.00 -0.00 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
      :scanner-:delay-mod 0.200      :scanner-delay 0.010
      :scanner-mod-rate  1.000       :scanner-mod-spread 0.000
      :scanner-scan-rate 7.000       :scanner-crossmix 0.200
      :scanner-mix 0.100 
      :room-size 0.600               :reverb-damp 0.500
      :reverb-mix 0.200
      :amp 0.20                      :pedal-sens 0.00))


(save-program 9 "Flute 8/Aeoline"
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    0     0     8     7     2     3     2     0     0 ]
      :pedals       [-0.00 -0.00 -0.50 +0.50 -1.00 +1.00 +1.00 +1.00 +0.70 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
      :scanner-:delay-mod 0.200      :scanner-delay 0.010
      :scanner-mod-rate  1.000       :scanner-mod-spread 0.000
      :scanner-scan-rate 7.000       :scanner-crossmix 0.200
      :scanner-mix 0.100 
      :room-size 0.600               :reverb-damp 0.500
      :reverb-mix 0.200
      :amp 0.100                     :pedal-sens 0.00))

(save-program 10 "Bass Horn"
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    0     6     7     8     8     8     8     0     0 ]
      :pedals       [-0.00 -0.00 -0.50 -0.50 -0.00 +1.00 +1.00 +1.00 +0.70 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
      :scanner-:delay-mod 0.200      :scanner-delay 0.010
      :scanner-mod-rate  7.000       :scanner-mod-spread 0.000
      :scanner-scan-rate 0.100       :scanner-crossmix 0.200
      :scanner-mix 0.100 
      :room-size 0.600               :reverb-damp 0.500
      :reverb-mix 0.200
      :amp 0.141                     :pedal-sens 0.00))

(save-program 11 "Bassoon 8/16"
   (masa
    :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
    :registration [    0     8     7     5     0     0     0     0     0 ]
    :pedals       [-0.00 -0.75 -0.50 -0.75 -0.00 +0.00 +0.00 +0.00 +0.00 ]
    :percussion   [    0     0     0     0     0     0     0     0     0 ]
    :decay 0.20   :sustain 0.80
    :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
    :scanner-:delay-mod 0.200      :scanner-delay 0.010
    :scanner-mod-rate  7.000       :scanner-mod-spread 0.000
    :scanner-scan-rate 0.100       :scanner-crossmix 0.200
    :scanner-mix 0.100 
    :room-size 0.600               :reverb-damp 0.500
    :reverb-mix 0.200
    :amp 0.30                      :pedal-sens 0.00))

(save-program 12 "Contra Celeste 16/Ocatve Celeste 4"
   (masa
    :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
    :registration [    6     7     8     7     6     5     0     0     0 ]
    :pedals       [-1.00 -1.00 -1.00 +0.00 +0.00 +1.00 +0.50 +0.50 +0.00 ]
    :percussion   [    0     0     0     0     0     0     0     0     0 ]
    :decay 0.20   :sustain 0.80
    :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
    :scanner-:delay-mod 0.200      :scanner-delay 0.010
    :scanner-mod-rate  7.000       :scanner-mod-spread 0.000
    :scanner-scan-rate 0.100       :scanner-crossmix 0.200
    :scanner-mix 0.100 
    :room-size 0.600               :reverb-damp 0.500
    :reverb-mix 0.200
    :amp 0.20                      :pedal-sens 0.00))

(save-program 13 "Clarinet 4/8"
   (masa
    :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
    :registration [    0     0     8     4     8     0     4     3     0 ]
    :pedals       [+0.00 +0.00 +0.00 -1.00 +0.00 +0.00 +1.00 +0.25 +0.00 ]
    :percussion   [    0     0     0     0     0     0     0     0     0 ]
    :decay 0.20   :sustain 0.80
    :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
    :scanner-:delay-mod 0.200      :scanner-delay 0.010
    :scanner-mod-rate  7.000       :scanner-mod-spread 0.000
    :scanner-scan-rate 0.100       :scanner-crossmix 0.200
    :scanner-mix 0.100 
    :room-size 0.600               :reverb-damp 0.500
    :reverb-mix 0.200
    :amp 0.20                      :pedal-sens 0.00))

(save-program 14 "Diapason 8/16"
   (masa
    :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
    :registration [    0     0     7     8     6     4     3     3     0 ]
    :pedals       [+1.00 +0.50 -0.50 -0.50 -0.25 +0.00 -1.00 -1.00 +0.00 ]
    :percussion   [    0     0     0     0     0     0     0     0     0 ]
    :decay 0.20   :sustain 0.80
    :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
    :scanner-:delay-mod 0.200      :scanner-delay 0.010
    :scanner-mod-rate  7.000       :scanner-mod-spread 0.000
    :scanner-scan-rate 0.100       :scanner-crossmix 0.200
    :scanner-mix 0.100 
    :room-size 0.600               :reverb-damp 0.500
    :reverb-mix 0.200
    :amp 0.20                      :pedal-sens 0.00))

(save-program 15 "English Horn 8/16"
   (masa
    :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
    :registration [    0     0     4     6     8     8     6     4     0 ]
    :pedals       [+0.50 +0.70 +1.00 +0.00 -0.70 -0.70 -0.70 +0.00 +0.00 ]
    :percussion   [    0     0     0     0     0     0     0     0     0 ]
    :decay 0.20   :sustain 0.80
    :vrate 7.000  :vdepth 0.000    :vsens 0.02      :vdelay 0.000
    :scanner-:delay-mod 0.200      :scanner-delay 0.010
    :scanner-mod-rate  7.000       :scanner-mod-spread 0.000
    :scanner-scan-rate 0.100       :scanner-crossmix 0.200
    :scanner-mix 0.100 
    :room-size 0.600               :reverb-damp 0.500
    :reverb-mix 0.200
    :amp 0.20                      :pedal-sens 0.00))

(save-program 16  "Odd 1"
   (masa
      :harmonics    [0.500 1.500 2.500 3.500 5.500 6.500 8.500 9.500 11.500]
      :registration [    6     4     1     3     6     0     2     5     3 ]
      :pedals       [+0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 4.874  :vdepth 0.264    :vsens 0.006      :vdelay 7.614
      :scanner-:delay-mod 0.253      :scanner-delay 0.010
      :scanner-mod-rate  4.137       :scanner-mod-spread 2.800
      :scanner-scan-rate 2.292       :scanner-crossmix 0.509
      :scanner-mix 0.000 
      :room-size 0.586               :reverb-damp 0.160
      :reverb-mix 0.664
      :amp 0.20                      :pedal-sens 0.00))

(save-program 17  "Harmonic Stadium"
   (masa
      :harmonics    [1.000 2.000 3.000 4.000 5.000 6.000 7.000 8.000 9.000 ]
      :registration [    2     4     1     6     4     6     2     8     0 ]
      :pedals       [+0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 +0.00 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.865  :vdepth 0.002    :vsens 0.641      :vdelay 1.129
      :scanner-:delay-mod 0.309      :scanner-delay 0.010
      :scanner-mod-rate  1.969       :scanner-mod-spread 1.299
      :scanner-scan-rate 6.937       :scanner-crossmix 0.803
      :scanner-mix 0.000 
      :room-size 0.867               :reverb-damp 0.027
      :reverb-mix 0.268
      :amp 0.20                      :pedal-sens 0.00))

(save-program 18  "Cathedral"
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    0     6     8     0     8     8     5     7     0 ]
      :pedals       [+0.54 -0.70 +0.19 +0.53 +0.70 -0.05 -0.39 +0.39 +0.90 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.653  :vdepth 0.477    :vsens 0.011      :vdelay 1.201
      :scanner-:delay-mod 0.846      :scanner-delay 0.010
      :scanner-mod-rate  3.244       :scanner-mod-spread 3.021
      :scanner-scan-rate 0.184       :scanner-crossmix 0.761
      :scanner-mix 0.000 
      :room-size 0.719               :reverb-damp 0.270
      :reverb-mix 0.793
      :amp 0.20                      :pedal-sens 0.00))

(save-program 19  "Louis Rosen"
   (masa
      :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]
      :registration [    6     7     1     0     7     8     6     2     6 ]
      :pedals       [-0.72 +0.64 -0.51 -0.55 +0.21 -0.96 -0.56 -0.41 -0.86 ]
      :percussion   [    0     0     0     0     0     0     0     0     0 ]
      :decay 0.20   :sustain 0.80
      :vrate 7.074  :vdepth 0.000    :vsens 0.004      :vdelay 1.207
      :scanner-:delay-mod 0.229      :scanner-delay 0.010
      :scanner-mod-rate  2.744       :scanner-mod-spread 2.160
      :scanner-scan-rate 0.238       :scanner-crossmix 0.054
      :scanner-mix 0.449 
      :room-size 0.744               :reverb-damp 0.407
      :reverb-mix 0.400
      :amp 0.20                      :pedal-sens 0.00))

(save-program 127 :random "Random" "Generate random MASA program" nil)
