document.addEventListener("DOMContentLoaded", initScript);



function initScript() {
    const url = "http://localhost:8080";
    const item3Endpoint = "/sale/clientReport";
    const item4Endpoint = "/sale/dailyReport";
    const item5Endpoint = "/sale/mostSold";
    let item3Btn = document.querySelector("#consigna3");
    let item4Btn = document.querySelector("#consigna4");
    let item5Btn = document.querySelector("#consigna5");
    let content = document.querySelector(".content");

    addEvents();

    function addEvents() {
        item3Btn.addEventListener("click", getClientsReport);
        item4Btn.addEventListener("click", getDailyReport);
        item5Btn.addEventListener("click", getMostSoldProduct);
    }

    function getClientsReport() {
        fetch(url + item3Endpoint)
            .then(response => response.json())
            .then(clients => {
                content.innerHTML = "<h2>Reporte de Ventas por Cliente<h2/> <br/>";
                for (const client of clients) {
                    console.log(client);
                    content.innerHTML += "Nombre: " + client.name + "<br/>" + "Monto Total: $" + client.total + "<hr/>";
                }
            })
            .catch(e => console.log(e))
    }

    function getDailyReport() {
        fetch(url + item4Endpoint)
            .then(response => response.json())
            .then(dailyReportMap => {
                content.innerHTML = "<h2>Reporte de Ventas Diario<h2/> <br/>";
                let sales = [];
                for (const day in dailyReportMap) {
                    for (let i = 0; i < dailyReportMap[day].length; i++) {
                        sales.push(dailyReportMap[day][i]);
                    }    
                }
                sales.forEach(sale => {
                    let productsNames = "";
                    sale.products.forEach(product => {
                        productsNames += product.name + "<br/>";
                    });
                    content.innerHTML += "Fecha: " + sale.date + "<br/>"
                    + "Venta Nro: " + sale.id + "<br/>"
                    + "Cliente: " + sale.client.name + "<br/>"
                    + "Productos:<br/>"
                    + productsNames + "<hr/>"
                });
            })
            .catch(e => console.log(e))
    }

    function getMostSoldProduct() {
        fetch(url + item5Endpoint)
            .then(response => response.json())
            .then(product => {
                console.log(product);
                content.innerHTML = "<h2>Producto Mas Vendido<h2/>" + "<br/>"
                + "Nombre: " + product.product_name + "<br/>"
                + "Total Recaudado: $" + product.total + "<hr/>";
            })
            .catch(e => console.log(e))
    }
}