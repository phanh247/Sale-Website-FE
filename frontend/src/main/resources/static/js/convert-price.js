$(document).ready(function() {
	function convertPrice() {
        const priceStr = $(".dbPrice").text();
        const price = Number(Math.floor(priceStr));
        $(".dbPrice").text(price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ","));
    }

    convertPrice();
    
    
    function convertTablePrice() {

        const rowLength = $("table tr").length;
        for (let i=0; i<rowLength; i++) {
            let oldPrice = $("table").find("tr").eq(i).find(".dbPrices").text();
            let newPrice = Number(Math.floor(oldPrice));
            $("table").find("tr").eq(i).find(".dbPrices").text(newPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, "."));
        }
        
    }

    convertTablePrice();
})