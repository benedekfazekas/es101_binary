(ns es101-binary.core
  (:require [clojurewerkz.elastisch.native.document :as doc]
            [clojurewerkz.elastisch.native.response :as res]
            [clojurewerkz.elastisch.native :as native]
            [clojurewerkz.elastisch.query :as q]
            [taoensso.nippy :as nippy]))

(defn init-client []
  (let [{:keys [es-url es-cluster] :as config} (-> "es.config"
                                                   clojure.java.io/resource
                                                   slurp
                                                   read-string)]
    (if (and es-url es-cluster)
        (let [pairs (->> (clojure.string/split es-url #",")
                         (map #(clojure.string/split % #":"))
                         (map #(vector (first %) (Integer/parseInt (last %)))))]
          (println (format "Connecting to %s on cluster %s" es-url es-cluster))
          (native/connect! pairs {"cluster.name" es-cluster
                                  "client.transport.ping_timeout" "10s"})
          :ok)
        :fail)))

(defn- fetch
  "Fetch entities from ES. For example
     Get a specific page - (fetch :page {:term {:pageId 66}})
     Get all pages (fetch :page)"
  ([index]
     (fetch index nil))
  ([index query]
     (fetch index index query))
  ([index type query]
     (fetch index type query nil))
  ([index type query fields]
     (->> (doc/search (name index) (name type)
                      :query (or query {:match_all {}})
                      :fields (when (not-empty fields) (mapv name fields))
                      :size 1000)
          res/hits-from
          (map (if fields :_fields :_source))
          doall)))

(defn resolve-article-light [article-list]
  (->> article-list
       (pmap (comp nippy/thaw (fn [d] (.toBytes d)) :articleLight))
       doall))

(defn fetch-articles [ids]
  (fetch :article :article (q/ids "article" ids) [:articleLight]))

(defn index-asset [asset-type id content]
  (doc/put (name asset-type) (name asset-type) id content))

(defn index-article-light [id content]
  (index-asset :article id {:articleId id
                            :articleLight (nippy/freeze content)}))
