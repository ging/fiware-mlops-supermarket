# curl orion:1026/v2/entities -s -S -H 'Content-Type: application/json' -d @- <<EOF
# {
#   "id": "ReqTicketPrediction1",
#   "type": "ReqTicketPrediction",
#   "predictionId": {
#     "value": 0,
#     "type": "String"
#   },
#   "socketId": {
#     "value": 0,
#     "type": "String"
#   },
#   "year":{
#     "value": 0,
#     "type": "Integer"
#   },
#   "month":{
#     "value": 0,
#     "type": "Integer"
#   },
#   "day":{
#     "value": 0,
#     "type": "Integer"
#   },
#   "time": {
#     "value": 0,
#     "type": "Integer"
#   },
#   "weekDay": {
#     "value": 0,
#     "type": "Integer"
#   }
# }
# EOF




# curl orion:1026/v2/entities -s -S -H 'Content-Type: application/json' -d @- <<EOF
# {
#   "id": "ResTicketPrediction1",
#   "type": "ResTicketPrediction",
#   "predictionId": {
#     "value": 0,
#     "type": "String"
#   },
#   "socketId": {
#     "value": 0,
#     "type": "String"
#   },
#   "predictionValue":{
#     "value": 0,
#     "type": "Float"
#   },
#   "year":{
#     "value": 0,
#     "type": "Integer"
#   },
#   "month":{
#     "value": 0,
#     "type": "Integer"
#   },
#   "day":{
#     "value": 0,
#     "type": "Integer"
#   },
#   "time": {
#     "value": 0,
#     "type": "Integer"
#   }
# }
# EOF

curl orion:1026/ngsi-ld/v1/entities -s -S -H 'Content-Type: application/ld+json' -d @- <<EOF
{
    "id": "urn:ngsi-ld:ReqPrediction1",
    "type": "ReqTicketPrediction",
    "predictionId": {
        "value": 0,
        "type": "Property"
      },
      "socketId": {
        "value": 0,
        "type": "Property"
      },
      "year":{
        "value": 0,
        "type": "Property"
      },
      "month":{
        "value": 0,
        "type": "Property"
      },
      "day":{
        "value": 0,
        "type": "Property"
      },
      "time": {
        "value": 0,
        "type": "Property"
      },
      "weekDay": {
        "value": 0,
        "type": "Property"
      },
    "@context": [
      "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
    ]
}
EOF



curl orion:1026/ngsi-ld/v1/entities -s -S -H 'Content-Type: application/ld+json' -d @- <<EOF
{
  "id": "urn:ngsi-ld:ResPrediction1",
  "type": "ResTicketPrediction",
  "predictionId": {
    "value": "0",
    "type": "Property"
  },
  "socketId": {
    "value": 0,
    "type": "Property"
  },
  "predictionValue":{
    "value": 0,
    "type": "Property"
  },
  "year":{
    "value": 0,
    "type": "Property"
  },
  "month":{
    "value": 0,
    "type": "Property"
  },
  "day": {
    "value": 0,
    "type": "Property"
  },
  "time": {
    "value": 0,
    "type": "Property"
  },
  "@context": [
    "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
  ]
}
EOF
