# curl -v orion:1026/v2/subscriptions -s -S -H 'Content-Type: application/json' -d @- <<EOF
# {
#   "description": "A subscription to get ticket predictions",
#   "subject": {
# 	"entities": [
#   	{
#     	"id": "ReqTicketPrediction1",
#     	"type": "ReqTicketPrediction"
#   	}
# 	],
# 	"condition": {
#   	"attrs": [
#       "predictionId",
#       "socketId",
#       "year",
#       "month",
#       "day",
#       "time",
#       "weekDay"
#   	]
# 	}
#   },
#   "notification": {
# 	"http": {
#   	"url": "http://spark-submit-predict:9001"
# 	},
# 	"attrs": [
#       "predictionId",
#       "socketId",
#       "year",
#       "month",
#       "day",
#       "time",
#       "weekDay"
# 	]
#   },
#   "expires": "2040-01-01T14:00:00.00Z",
#   "throttling": 5
# }
# EOF

curl -v  orion:1026/ngsi-ld/v1/subscriptions/ -s -S -H 'Content-Type: application/ld+json' -d @- <<EOF
{
  "description": "A subscription to get updates the Supermarket",
  "type": "Subscription",
  "entities": [{
    "id": "urn:ngsi-ld:ReqPrediction1",
    "type": "ReqTicketPrediction"
    }],
  "watchedAttributes": [
      "predictionId",
      "socketId",
      "year",
      "month",
      "day",
      "time",
      "weekDay"
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