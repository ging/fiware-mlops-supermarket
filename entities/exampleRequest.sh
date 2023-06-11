curl --location --request PATCH 'http://localhost:1026/ngsi-ld/v1/entities/urn:ngsi-ld:ReqPrediction1/attrs' \
--header 'Content-Type: application/json' \
--data-raw '{
   "year": {
      "type":"Property",
      "value": "2023"
   },
   "weekDay":{
      "type":"Property",
      "value": 1
   },
   "time":{
      "type":"Property",
      "value": 14
   },
   "month":{
       "type": "Property",
       "value": 2
   },
   "day":{
       "type": "Property",
       "value": 2
   },
   "predictionId":{
      "type":"Property",
      "value":"p-1662768034900"
   },
   "socketId":{
      "type":"Property",
      "value":"Fn0kKHEF-dOcr311AAAF"
   }
}'
