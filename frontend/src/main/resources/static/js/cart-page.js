$(document).ready(function () {

    let rows = rowCount();
    display(rows);
    calcBill(rows);


    $('input.input-qty').each(function () {
        var $this = $(this),
            qty = $this.parent().find('.is-form'),
            min = Number($this.attr('min')),
            max = Number($this.attr('max'))
        if (min == 0) {
            var d = 0
        } else d = min
        $(qty).on('click', function () {
            if ($(this).hasClass('minus')) {
                if (d > min) d += -1
            } else if ($(this).hasClass('plus')) {
                var x = Number($this.val()) + 1
                if (x <= max) d += 1
            }
            $this.attr('value', d).val(d)
        })
    })

    $('input.is-form').click(function () {
        var thisItem = $(this);
        let priceItem = parseInt(thisItem.closest('tr').find('.price-item').text().replace(/[^a-z0-9\s]/gi, ''));
        let quantityItem = parseInt(thisItem.closest('tr').find('input.input-qty').val());
        let total = Intl.NumberFormat("de-DE").format(priceItem * quantityItem);
        thisItem.closest('tr').find('.total-price-item').html(total + 'đ');
        calcItems(thisItem);
        calcBill(rows);
    })

    function calcItems(thisItem) {
        let priceItem = parseInt(thisItem.closest('tr').find('.price-item').text().replace(/[^a-z0-9\s]/gi, ''));
        let quantityItem = parseInt(thisItem.closest('tr').find('input.input-qty').val());
        thisItem.closest('tr').find('.total-price-item').html((Intl.NumberFormat("de-DE").format(priceItem * quantityItem)) + 'đ');
    
    }

    function display(rows) {
        const myTable = $('.my-cart').find('table');
        for (let index = 1; index < rows; index++) {
            var indexRow = `tr:eq(${index})`;
            var priceItem = parseInt(myTable.find(indexRow).find('.price-item').text().replace(/[^a-z0-9\s]/gi, ''));
            var quantityItem = parseInt(myTable.find(indexRow).find('input.input-qty').val());
            myTable.find(indexRow).find('.total-price-item').html((Intl.NumberFormat("de-DE").format(priceItem * quantityItem)) + 'đ');
        	
        }
    }

    function calcBill(rows) {
        const myTable = $('.my-cart').find('table');
        let price = 0;
        let abc = 0
        for (let index = 1; index < rows; index++) {
            var indexRow = `tr:eq(${index})`;
            price = price + parseInt(myTable.find(indexRow).find('.total-price-item').text().replace(/[^a-z0-9\s]/gi, ''));
        }
        $('.total-all-item').html((Intl.NumberFormat("de-DE").format(price)) + 'đ');
        $('.total-bill').html((Intl.NumberFormat("de-DE").format(price + parseInt($('.delivery-fee').text().replace(/[^a-z0-9\s]/gi, '')))) + 'đ');
    }

    function rowCount(){
        var rowCount =  $('.my-cart').find('table').find('tr').length;
        return rowCount;
    }

    $('.minus').click(function () {
        var quantityItem =  parseInt($(this).closest('td').find('input.input-qty').val());
        if (quantityItem == 1) {
            $(this).closest('tr').remove();
        } 
        rowCount();
        calcBill(rowCount());
    })

    $('.plus').click(function () {
        var quantityItem =  parseInt($(this).closest('td').find('input.input-qty').val());
        if (quantityItem > 1) {
            rowCount();
            calcBill(rowCount());
        } 
    })
})
