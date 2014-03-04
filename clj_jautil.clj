(ns clj-jautil)

(def UNICODE_MAP
  {:hiragana {:begin \u3040 :end \u309F}
   :katakana {:begin \u30A0 :end \u30FF}
   :katakana_ext {:begin \u31F0 :end \u31FF}
   :halfkana {:begin \uFF60 :end \uFF9F}
   :halfalpha_upper {:begin \A :end \Z}
   :halfalpha_lower {:begin \a :end \z}
   :fullalpha_upper {:begin \uFF21 :end \uFF3A}
   :fullalpha_lower {:begin \uFF41 :end \uFF5A}
   :fullnum {:begin \uFF10 :end \uFF19}
   :halfnum {:begin \0 :end \9}
   :kanji {:begin \u4E00 :end \u9FFF}
   :kanji_ext {:begin \uF900 :end \uFAFF}
   }
  )

(defn in-range [c-int r]
  (and (>= c-int (int (r :begin))) (<= c-int (int (r :end))))
  )

(defn char-shift [c-int from-r to-r]
  (if (in-range c-int from-r)
    (+ c-int (- (int (from-r :begin))) (int (to-r :begin))) c-int)
  )


(defn char->ctype
  "input: c: character
   output: character type (str, A / N / H / K / C)
  "
  [c]
  (let [c-int (int c)]
    (cond
     (= c \・) "."
     (= c \々) "C"     
     (in-range c-int (UNICODE_MAP :katakana)) "K"
     (in-range c-int (UNICODE_MAP :hiragana)) "H"
     (in-range c-int {:begin \A :end \Z}) "A"
     (in-range c-int (UNICODE_MAP :fullalpha_upper)) "Z"
     (in-range c-int {:begin \a :end \z}) "a"
     (in-range c-int (UNICODE_MAP :fullalpha_lower)) "z"
     (in-range c-int {:begin \0 :end \9}) "n"
     (in-range c-int (UNICODE_MAP :fullnum)) "N"
     
     (in-range c-int (UNICODE_MAP :katakana_ext)) "K"

     (#{\一 \二 \三 \四 \五 \六 \七 \八 \九 \〇 \十 \百 \千 \万 \億} c) "S"
     (in-range c-int (UNICODE_MAP :kanji)) "C"
     (in-range c-int (UNICODE_MAP :kanji_ext)) "C"
     :else "O"
     )
    )
  )

(defn str-shift-fn [char-shift-fn]
  (fn [s]
    (->> s
         (map
          (fn [c]
            (-> c int char-shift-fn char)))
         (apply str))
    )
  )

(def fullnum->halfnum
  (str-shift-fn
   (fn [c-int] (char-shift c-int (UNICODE_MAP :fullnum) (UNICODE_MAP :halfnum)))))

(def halfnum->fullnum
  (str-shift-fn
   (fn [c-int] (char-shift c-int (UNICODE_MAP :halfnum) (UNICODE_MAP :fullnum)))))

(def fullalpha->halfalpha
  (str-shift-fn
   (fn [c-int] (-> c-int (char-shift (UNICODE_MAP :fullalpha_lower) (UNICODE_MAP :halfalpha_lower))
                         (char-shift (UNICODE_MAP :fullalpha_upper) (UNICODE_MAP :halfalpha_upper))))))

(def halfalpha->fullalpha
  (str-shift-fn
   (fn [c-int] (-> c-int (char-shift (UNICODE_MAP :halfalpha_lower) (UNICODE_MAP :fullalpha_lower))
                         (char-shift (UNICODE_MAP :halfalpha_upper) (UNICODE_MAP :fullalpha_upper))))))

(def fullalpha-lower
  (str-shift-fn
   (fn [c-int] (char-shift c-int (UNICODE_MAP :fullalpha_upper) (UNICODE_MAP :fullalpha_lower)))))

(def kata->hira
  (str-shift-fn
   (fn [c-int] (if (= c-int (int \ー)) c-int
                   (char-shift c-int (UNICODE_MAP :katakana) (UNICODE_MAP :hiragana))))))

(def hira->kata
  (str-shift-fn
   (fn [c-int] (if (= c-int (int \ー)) c-int
                   (char-shift c-int (UNICODE_MAP :hiragana) (UNICODE_MAP :katakana))))))

(defn -main [& argv]
   (println (map char->ctype "Aaあア漢％＄百"))
   (let [s "Ｃｌｏｊｕｒｅ Programming （クロージャー）１２３456! を楽しもう"]
     (println (fullnum->halfnum s))
     (println (halfnum->fullnum s))
     (println (fullalpha->halfalpha s))
     (println (halfalpha->fullalpha s))
     (println (fullalpha-lower s))
     (println (kata->hira s))
     (println (hira->kata s))))
