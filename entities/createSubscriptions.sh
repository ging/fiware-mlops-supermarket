curl -v  orion:1026/ngsi-ld/v1/subscriptions/ -s -S -H 'Content-Type: application/ld+json' -d @- <<EOF
{
  "description": "A subscription to get updates the Supermarket",
  "type": "Subscription",
  "entities": [{
    "id": "urn:ngsi-ld:Supermarket:001",
    "type": "Supermarket"
    }],
  "watchedAttributes": [
      "year",
      "month",
      "day",
      "time",
      "weekDay",
      "occupancy"
    ],
  "notification": {
    "endpoint": {
      "uri": "http://spark-submit-predict:9001",
      "accept": "application/json"
    }
  },
    "@context": [
        "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
    ] 
}
EOF
