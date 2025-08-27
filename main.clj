#!/usr/bin/env bb

(require '[cheshire.core :as json])

(spit "info.json"
      (json/generate-string
       {:user (System/getProperty "user.name")
        :shell (System/getenv "SHELL")} true))
