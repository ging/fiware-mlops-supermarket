const express = require("express");
const cors = require("cors");
const app = express();
app.use(cors());
const server = require("http").Server(app);
const bodyParser = require("body-parser");
const fetch = require("node-fetch");

const URL_CB = process.env.URL_CB || "http://localhost:1026";
const PORT = process.env.PORT ? process.env.PORT : 3000;
console.log("Orion URL: " + URL_CB);

const URL_CB_UPDATE = `${URL_CB}/ngsi-ld/v1/entities/urn:ngsi-ld:Supermarket:001/attrs`;
const URL_CB_GET = `${URL_CB}/ngsi-ld/v1/entities/?type=Supermarket`;

const generateNextDate = (startDate) => {
  const timeDiff = 3600000 * 25;
  return new Date(startDate.getTime() + timeDiff);
};

const updateEntity = (data) => {
  fetch(URL_CB_UPDATE, {
    body: JSON.stringify(data),
    headers: { "Content-Type": "application/json" },
    method: "PATCH",
  })
    .then((res) => {})
    .catch((e) => {
      console.error(e);
      console.log("Update error");
    });
};

const updateStatusSupermarket = (date) => {
  try {
    const dateObservation = generateNextDate(date);
    let data = {
      year: {
        value: dateObservation.getFullYear(),
        type: "Property",
      },
      weekDay: {
        value: dateObservation.getDay(),
        type: "Property",
      },
      time: {
        value: dateObservation.getHours(),
        type: "Property",
      },
      month: {
        value: dateObservation.getMonth(),
        type: "Property",
      },
      day: {
        value: dateObservation.getDate(),
        type: "Property",
      },
      dateObserved: {
        value: dateObservation.toISOString(),
        type: "Property"
      },
      occupancy: {
        value: Math.floor(Math.random() * 5) + 1, 
        type: "Property"
      }
    };
    updateEntity(data);
    setTimeout(() => {updateStatusSupermarket(dateObservation)}, 10000);
  } catch (err) {
    console.log("Error while updating the entity");
    console.error(err);
    setTimeout(() => {updateStatusSupermarket(dateObservation)}, 10000);
  }
};

updateStatusSupermarket(new Date());

server.listen(PORT, function () {
  console.log("Listening on port " + PORT);
});

app.use(express.static("public"));
app.use(bodyParser.text());
app.use(bodyParser.json());

app.get("/entities", (req, res) => {
  fetch(URL_CB_GET, {
    method: "GET",
  })
    .then((response) => response.json())
    .then((data) => {
      res.send(data)
    })
    .catch((e) => {
      console.error(e);
      console.log("Update error");
      res.status(500);
      res.send("Error in server");
    });
});
