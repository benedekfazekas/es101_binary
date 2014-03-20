# es101_binary

demonstrate problem with es101 returning binary field as string

## Usage

edit `resources/es.config` to set where the ES cluster is

pls see `article_mapping.json` for the mapping

there is also an alias on article, set up like this:
```
curl -XPOST "$ES_HOST/_aliases" -d '{ "actions" : [{ "add" : { "index" : "article-with-light", "alias" : "article" } } ]}'
```

from the repl:
```clj
es101-binary.core> (index-article-light "44" {:foo "barbaz"})
{:index "article-with-light",
 :_index "article-with-light",
 :id "44",
 :_id "44",
 :type "article",
 :_type "article",
 :version 1,
 :_version 1}
contemplate => nil
es101-binary.core> (def myarticle (fetch-articles [44]))
#<Var@78fac96c:
  ({:articleLight "TlBZARhcGwAAAAIOAAAAA2Zvbw0AAAAGYmFyYmF6"})>
contemplate => nil
es101-binary.core> (resolve-article-light myarticle)
IllegalArgumentException No matching field found: toBytes for class java.lang.String  clojure.lang.Reflector.getInstanceField (Reflector.java:271)
```

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
