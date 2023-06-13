const highlightNewElement = (element) => {
  element.classList.add("highlightElement");
  setTimeout(function() {
    element.classList.remove("highlightElement");
  }, 1000);
}
const queryDataFromOrion = async (date) => {
  try {
    const response = await fetch("/entities", {
      headers: { "Content-Type": "application/json" },
      method: "GET",
    });

    const data = await response.json();
    const supermarketEntity = data.find((entity) => entity.id === "urn:ngsi-ld:Supermarket:001");
    const supermarketForecastEntity = data.find((entity) => entity.id === "urn:ngsi-ld:SupermarketForecast:001");

    const dateObserved = supermarketEntity.dateObserved.value;

    if (date !== dateObserved && dateObserved === supermarketForecastEntity.dateObserved.value) {
      const elementDateTime = document.getElementById("dateTime");
      const elementCurrentState = document.getElementById("currentStatus")
      const elementForecastState = document.getElementById("forecastStatus")
      elementDateTime.innerHTML = supermarketEntity.dateObserved.value;
      elementCurrentState.innerHTML = supermarketEntity.occupancy.value
      elementForecastState.innerHTML = supermarketForecastEntity.occupancy.value

      
      highlightNewElement(elementDateTime);
      highlightNewElement(elementCurrentState);
      highlightNewElement(elementForecastState);
      return dateObserved;
    }

    return date;
  } catch (error) {
    console.error(error);
    console.log("Update error");
    return date;
  }
};

const updateDataInWeb = async (date) => {
  try {
    const updatedDate = await queryDataFromOrion(date);
    setTimeout(() => {
      updateDataInWeb(updatedDate);
    }, 1000);
  } catch (error) {
    setTimeout(() => {
      updateDataInWeb(date);
    }, 1000);
  }
};

updateDataInWeb(new Date().toISOString());
