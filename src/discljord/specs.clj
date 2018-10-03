(ns discljord.specs
  (:require [clojure.core.async :as a]
            [clojure.spec.alpha :as s]))

;; -------------------------------------------------
;; generic specs

(s/def ::url string?)
(s/def ::token string?)

(s/def ::future any?)
(s/def ::promise any?)
(s/def ::channel (partial satisfies? clojure.core.async.impl.protocols/Channel))

(defn atom-of?
  "Takes a spec, and returns a spec for a clojure.lang.Atom
  containing a value of that spec."
  [s]
  (fn [x]
    (and (instance? clojure.lang.Atom x)
         (s/valid? s @x))))

(s/def ::snowflake string?)

(s/def ::channel-id ::snowflake)

;; -------------------------------------------------
;; discljord.connection specs

(s/def ::shard-id int?)
(s/def ::shard-count pos-int?)
(s/def ::gateway (s/keys :req [::url ::shard-count]))

(s/def ::session-id (s/nilable string?))
(s/def ::seq (s/nilable int?))
(s/def ::buffer-size number?)
(s/def ::disconnect boolean?)
(s/def ::retries number?)
(s/def ::max-retries number?)
(s/def ::max-connection-retries number?)
(s/def ::shard-state (s/keys :req-un [::session-id ::seq
                                      ::buffer-size ::disconnect
                                      ::max-connection-retries]))
(s/def ::init-shard-state ::shard-state)

(s/def ::connection any?)

(s/def ::query string?)
(s/def ::limit number?)

(s/def ::name string?)
(s/def ::type (s/or :keyword #{:game :stream :music}
                    :int int?))
(s/def ::activity (s/keys :req-un [::name ::type]
                          :opt-un [::url]))

(s/def ::idle-since number?)
(s/def ::status #{:online :offline :invisible :idle :dnd})
(s/def ::afk boolean?)

(s/def ::mute boolean?)
(s/def ::deaf boolean?)

;; -------------------------------------------------
;; discljord.messaging specs

(s/def ::major-variable-type #{::guild-id ::channel-id ::webhook-id})
(s/def ::major-variable-value ::snowflake)
(s/def ::major-variable (s/keys :req [::major-variable-type
                                      ::major-variable-value]))

(s/def ::action keyword?)

(s/def ::endpoint (s/keys :req [::action]
                          :opt [::major-variable]))

(s/def ::rate number?)
(s/def ::remaining number?)
(s/def ::reset number?)
(s/def ::global boolean?)
(s/def ::rate-limit (s/keys :req [::rate ::remaining ::reset]
                            :opt [::global]))

(s/def ::endpoint-specific-rate-limits (s/map-of ::endpoint ::rate-limit))
(s/def ::global-rate-limit ::rate-limit)

(s/def ::rate-limits (s/keys :req [::endpoint-specific-rate-limits]
                             :opt [::global-rate-limit]))

(s/def ::process (s/keys :req [::rate-limits
                               ::channel
                               ::token]))

(s/def ::message (s/and string?
                        #(< (count %) 2000)))
