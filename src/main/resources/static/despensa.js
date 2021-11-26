document.addEventListener("DOMContentLoaded", initScript);



function initScript() {
    const products = "./product/";
    const clients = "./client/";
    const sales = "./sale/";
    const item3Endpoint = "./sale/clientReport";
    const item4Endpoint = "./sale/dailyReport";
    const item5Endpoint = "./sale/mostSold";
    let productsBtn = document.querySelector("#productos");
    let clientsBtn = document.querySelector("#clientes");
    let salesBtn = document.querySelector("#ventas");
    let item3Btn = document.querySelector("#consigna3");
    let item4Btn = document.querySelector("#consigna4");
    let item5Btn = document.querySelector("#consigna5");
    let content = document.querySelector(".content");
    //Botones para los forms
    let clientSubmit = document.querySelector("#client-submit");
    let productSubmit = document.querySelector("#product-submit");
    let saleSubmit = document.querySelector("#sale-submit");
    //array para almacenar los productos cargados a una compra
    let cart = [];
    updateCartBtn = document.querySelector("#add-product");
    //Elementos select del formulario de agregar venta
    let clientSelect = document.querySelector("#client");
    getClientOptions();
    let productSelect = document.querySelector("#product");
    getProductOptions();


    addEvents();

    function addEvents() {
        productsBtn.addEventListener("click", getProducts);
        clientsBtn.addEventListener("click", getClients);
        salesBtn.addEventListener("click", getSales);
        item3Btn.addEventListener("click", getClientsReport);
        item4Btn.addEventListener("click", getDailyReport);
        item5Btn.addEventListener("click", getMostSoldProduct);
        //Eventos para los formularios
        clientSubmit.addEventListener("click", postClient);
        productSubmit.addEventListener("click", postProduct);
        updateCartBtn.addEventListener("click", updateCart);
        saleSubmit.addEventListener("click", postSale);
    }

    function getClientOptions() {
        fetch(clients)
            .then(response => response.json())
            .then(clients => {
                clientSelect.innerHTML = "";
                for (const client of clients) {
                    let option = document.createElement("option");
                    option.value = client.id;
                    option.textContent = client.name;
                    clientSelect.appendChild(option);
                }
            })
    }

    function getProductOptions() {
        fetch(products)
            .then(response => response.json())
            .then(products => {
                productSelect.innerHTML = "";
                for (const product of products) {
                    let option = document.createElement("option");
                    option.value = product.id;
                    option.textContent = product.name;
                    productSelect.appendChild(option);
                }
            })
    }

    //Actualiza la lista de productos que va a incluir la compra
    function updateCart() {
        const id = productSelect.value;
        fetch(products + id)
            .then(response => response.json())
            .then(product => {
                let selectedProduct = {};
                selectedProduct.id = product.id;
                selectedProduct.name = product.name;
                selectedProduct.price = product.price;
                cart.push(selectedProduct);
            })
    }

    function getProducts() {
        fetch(products)
            .then(response => response.json())
            .then(products => {
                content.innerHTML = "<h2>Listado de Productos </h2> <br/>";
                for (const product of products) {
                    content.innerHTML += "<div> Id: " + product.id + "<br/>" + "Nombre: " + product.name + "<br/>" + "Precio: $" + product.price + "</div>" + "<hr/>";
                }
            })
    }

    function getClients() {
        fetch(clients)
            .then(response => response.json())
            .then(clients => {
                content.innerHTML = "<h2>Listado de Clientes </h2> <br/>";
                for (const client of clients) {
                    content.innerHTML += "<div> Id: " + client.id + "<br/>" + "Nombre: " + client.name + "</div>" + "<hr/>";
                }
            })
    }

    async function getClient(id) {

        try {
            let response = await fetch(clients + id);
            if (response.ok) {
                let cliente = response.json();
                return cliente;
            }
        } catch (error) {
            console.log(error);
        }
    }

    function postClient() {
        let name = document.querySelector("#client-name").value;

        var myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");

        var raw = JSON.stringify({
            "name": name
        });

        var requestOptions = {
            method: 'POST',
            headers: myHeaders,
            body: raw,
            redirect: 'follow'
        };

        fetch("./client/", requestOptions)
            .then(response => response.text())
            .then(result => console.log(result))
            .catch(error => console.log('error', error));
    }

    function postProduct() {
        let name = document.querySelector("#product-name").value;
        let price = document.querySelector("#price").value;
        var myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");

        var raw = JSON.stringify({
            "name": name,
            "price": price
        });

        var requestOptions = {
            method: 'POST',
            headers: myHeaders,
            body: raw,
            redirect: 'follow'
        };

        fetch("./product/", requestOptions)
            .then(response => response.text())
            .then(result => console.log(result))
            .catch(error => console.log('error', error));
    }

    async function postSale() {
        var myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");

        let date = document.querySelector("#date").value;
        let client = await getClient(clientSelect.value);

        var raw = JSON.stringify({
            "date": date,
            "client": client,
            "products": cart
        });
        console.log(raw);
        var requestOptions = {
            method: 'POST',
            headers: myHeaders,
            body: raw,
            redirect: 'follow'
        };

        fetch(sales, requestOptions)
            .then(response => response.text())
            .then(result => console.log(result))
            .catch(error => console.log('error', error));
    }


    function getSales() {
        fetch(sales)
            .then(response => response.json())
            .then(sales => {
                content.innerHTML = "<h2>Listado de Ventas </h2> <br/>";
                for (const sale of sales) {
                    const saleInfo = "<div> Id: " + sale.id + "</br>" + "Fecha: " + sale.date + "</br>";
                    const clientInfo = "Nro de Cliente: " + sale.client.id + "</br>" + "Nombre del Cliente: " + sale.client.name + "</br>";
                    let productsInfo = "";
                    for (let index = 0; index < sale.products.length; index++) {
                        productsInfo += "IdProducto: " + sale.products[index].id + " Nombre: " + sale.products[index].name + "</br>";

                    }
                    content.innerHTML += saleInfo + clientInfo + productsInfo + "</div>" + "<hr/>";

                }
            })
    }

    function getClientsReport() {
        fetch(item3Endpoint)
            .then(response => response.json())
            .then(clientReportItems => {
                content.innerHTML = "<h2>Reporte de Ventas por Cliente<h2/> <br/>";
                for (const clientReportItem of clientReportItems) {
                    content.innerHTML += "Nombre: " + clientReportItem.clientName + "<br/>" + "Monto Total: $" + clientReportItem.total + "<hr/>";
                }
            })
            .catch(e => console.log(e))
    }

    function getDailyReport() {
        fetch(item4Endpoint)
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
                    content.innerHTML += "Fecha: " + sale.date + "<br/>" +
                        "Venta Nro: " + sale.id + "<br/>" +
                        "Cliente: " + sale.client.name + "<br/>" +
                        "Productos:<br/>" +
                        productsNames + "<hr/>"
                });
            })
            .catch(e => console.log(e))
    }

    function getMostSoldProduct() {
        fetch(item5Endpoint)
            .then(response => response.json())
            .then(mostSold => {
                content.innerHTML = "<h2>Producto Mas Vendido<h2/>" + "<br/>" +
                    "Nombre: " + mostSold.productName + "<br/>" +
                    "Total Recaudado: $" + mostSold.quantity + "<hr/>";
            })
            .catch(e => console.log(e))
    }
}