

curl orion:1026/ngsi-ld/v1/entities -s -S -H 'Content-Type: application/ld+json' -d @- <<EOF
{
    "id": "urn:ngsi-ld:Supermarket:001",
    "type": "Supermarket",
    "capacity": {
        "value": 50,
        "type": "Property"
      },
      "occupancy": {
        "value": 20,
        "type": "Property"
      },
      "year":{
        "value": 2023,
        "type": "Property"
      },
      "month":{
        "value": 11,
        "type": "Property"
      },
      "day":{
        "value": 22,
        "type": "Property"
      },
      "time": {
        "value": 22,
        "type": "Property"
      },
      "weekDay": {
        "value": 5,
        "type": "Property"
      },
    "@context": [
      "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
    ]
}
EOF





curl orion:1026/ngsi-ld/v1/entities -s -S -H 'Content-Type: application/ld+json' -d @- <<EOF
{
    "id": "urn:ngsi-ld:SupermarketForecast:001",
    "type": "SupermarketForecast",
    "capacity": {
        "value": 0,
        "type": "Property"
      },
      "occupancy": {
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