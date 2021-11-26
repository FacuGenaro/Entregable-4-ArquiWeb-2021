document.addEventListener("DOMContentLoaded", initScript);



function initScript() {
    const products = "./product/";
    const clients = "./client/";
    const sales = "./sale/";
    const item3Endpoint = "./sale/clientReport";
    const item4Endpoint = "./sale/dailyReport";
    const item5Endpoint = "./sale/mostSold";
    let productsBtn = document.querySelector("#Productos");
    let clientsBtn = document.querySelector("#Clientes");
    let salesBtn = document.querySelector("#Ventas");
    let item3Btn = document.querySelector("#Consigna3");
    let item4Btn = document.querySelector("#Consigna4");
    let item5Btn = document.querySelector("#Consigna5");
    let content = document.querySelector(".content");
    //Botones para los forms POST
    let clientSubmit = document.querySelector("#client-submit");
    let productSubmit = document.querySelector("#product-submit");
    let saleSubmit = document.querySelector("#sale-submit");
    //Botones para los forms PUT
    let updateClientSubmit = document.querySelector("#update-client-submit");
    let updateProductSubmit = document.querySelector("#update-product-submit");
    //array para almacenar los productos cargados a una compra
    let cart = [];
    updateCartBtn = document.querySelector("#add-product");
    //Elementos select del formulario de agregar venta
    let clientSelect = document.querySelector("#client");
    let updateClientSelect = document.querySelector("#update-client-select");
    getClientOptions();
    let productSelect = document.querySelector("#product");
    let updateProductSelect = document.querySelector("#update-product-select");
    getProductOptions();



    addEvents();

    function addEvents() {
        //Eventos para los GET
        productsBtn.addEventListener("click", getProducts);
        clientsBtn.addEventListener("click", getClients);
        salesBtn.addEventListener("click", getSales);
        item3Btn.addEventListener("click", getClientsReport);
        item4Btn.addEventListener("click", getDailyReport);
        item5Btn.addEventListener("click", getMostSoldProduct);
        //Eventos para los PUT
        updateClientSubmit.addEventListener("click", updateClient);
        updateProductSubmit.addEventListener("click", updateProduct);
        //Eventos para los formularios
        clientSubmit.addEventListener("click", postClient);
        productSubmit.addEventListener("click", postProduct);
        updateCartBtn.addEventListener("click", updateCart);
        saleSubmit.addEventListener("click", postSale);
    }

    //Sirve para generar los option que van dentro del select de clientes
    function getClientOptions() {
        fetch(clients)
            .then(response => response.json())
            .then(clients => {
                clientSelect.innerHTML = "";
                for (const client of clients) {
                    let option = document.createElement("option");
                    let updateOption = document.createElement("option");
                    option.value = client.id;
                    option.textContent = client.name;
                    updateOption.value = client.id;
                    updateOption.textContent = client.name;
                    clientSelect.appendChild(option);
                    updateClientSelect.appendChild(updateOption);
                }
            })
    }

    //Sirve para generar los option que van dentro del select de productos
    function getProductOptions() {
        fetch(products)
            .then(response => response.json())
            .then(products => {
                productSelect.innerHTML = "";
                for (const product of products) {
                    let option = document.createElement("option");
                    option.value = product.id;
                    option.textContent = product.name;
                    let updateOption = document.createElement("option");
                    updateOption.value = product.id;
                    updateOption.textContent = product.name;
                    productSelect.appendChild(option);
                    updateProductSelect.appendChild(updateOption);
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
                    content.innerHTML += "<div> Id: " + product.id + "<br/>" + "Nombre: " + product.name + "<br/>" + "Precio: $" + product.price + "<input type=" + "button" + " value=" + "Borrar" + " class=product-delete" + " data-id=" + product.id + ">" + "</div>" + "<hr/>";
                }
                let btns = document.querySelectorAll(".product-delete");
                for (let index = 0; index < btns.length; index++) {
                    btns[index].addEventListener("click", () => {
                        deleteProduct(btns[index].dataset.id)
                    });
                }
            })
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

    function updateProduct() {
        var data = {
            "id": updateProductSelect.value,
            "name": document.querySelector("#new-product-name").value,
            "price": parseInt(document.querySelector("#new-product-price").value)
        };
        console.log(data);

        fetch(products, {
                method: 'PUT',
                body: JSON.stringify(data),
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(res => res.json())
            .then(response => console.log('Success:', response))
            .catch(error => console.log(error));
    }

    function deleteProduct(id) {
        var myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");
        var requestOptions = {
            method: 'DELETE',
            headers: myHeaders,
            redirect: 'follow'
        };
        fetch(products + id, requestOptions)
            .then(response => response.text())
            .then(result => console.log(result))
            .catch(error => console.log('error', error));
    }

    function getClients() {
        fetch(clients)
            .then(response => response.json())
            .then(clients => {
                content.innerHTML = "<h2>Listado de Clientes </h2> <br/>";
                for (const client of clients) {
                    content.innerHTML += "<div> Id: " + client.id + "<br/>" + "Nombre: " + client.name + "<input type=" + "button" + " value=" + "Borrar" + " class=client-delete" + " data-id=" + client.id + ">" + "</div>" + "<hr/>";
                }
                let btns = document.querySelectorAll(".client-delete");
                for (let index = 0; index < btns.length; index++) {
                    btns[index].addEventListener("click", () => {
                        deleteClient(btns[index].dataset.id)
                    });
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

        fetch(clients, requestOptions)
            .then(response => response.text())
            .then(result => console.log(result))
            .catch(error => console.log('error', error));
    }

    function deleteClient(id) {
        var myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");
        var requestOptions = {
            method: 'DELETE',
            headers: myHeaders,
            redirect: 'follow'
        };
        fetch(clients + id, requestOptions)
            .then(response => response.text())
            .then(result => console.log(result))
            .catch(error => console.log('error', error));
    }

    function updateClient() {
        var data = {
            "id": updateClientSelect.value,
            "name": document.querySelector("#new-client-name").value
        };
        console.log(data);

        fetch(clients, {
                method: 'PUT',
                body: JSON.stringify(data),
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(res => res.json())
            .then(response => console.log('Success:', response))
            .catch(error => console.log(error));
    }

    

    function deleteSale(id) {
        var myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");
        var requestOptions = {
            method: 'DELETE',
            headers: myHeaders,
            redirect: 'follow'
        };
        fetch(sales + id, requestOptions)
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
                    content.innerHTML += saleInfo + clientInfo + productsInfo + "<input type=" + "button" + " value=" + "Borrar" + " class=sale-delete" + " data-id=" + sale.id + ">" + "</div>" + "<hr/>";
                }
                let btns = document.querySelectorAll(".sale-delete");
                for (let index = 0; index < btns.length; index++) {
                    btns[index].addEventListener("click", () => {
                        deleteSale(btns[index].dataset.id)
                    });
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
                    "Unidades vendidas: " + mostSold.quantity + "<hr/>";
            })
            .catch(e => console.log(e))
    }
}