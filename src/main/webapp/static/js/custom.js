/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

resetFieldsError = el => {
    el.removeClass('is-invalid');
    el.parent().find('span.error').addClass('hidden');
    
}

highlightError = (el, msg) => {
    el.addClass('is-invalid');
    el.parent().find('span.error').removeClass('hidden');
    el.parent().find('span.error').html(msg);
}

checkFields = () => {
    let checkFormResult = true;
    $('.check-form-field').each(function () {
        let el = $(this);
        if (el.hasClass("not-empty") && el.val().length === 0) {
            highlightError(el, '( The field cannot be empty! )');
            checkFormResult = false;
        }
        if (el.hasClass("min-length") && el.val().length < el.attr('data-min-length')) {
            highlightError(el, '( Minimum length required: ' + el.attr('data-min-length') + ' )');
            checkFormResult = false;
        }
        if (el.hasClass('is-integer') && isNaN(parseInt(el.val()))) {
            highlightError(el, '( Invalid input )');
            checkFormResult = false;
        }
    })

    return checkFormResult;
}

goToPage = (pageid) => {
    $.ajax({
        url: 'http://localhost:8080/walletwebversion/resources/javaee8/getPage/'
                + pageid,
        beforeSend: function () {
            // setting a timeout
            $('.page').html('<div class="loading-container"><img src="static/images/loading.gif" alt=""/></div')
        },
        success: function (data) {
            setTimeout(() => {
                $('.page').html(data)
            }, 500);
        }
    });
}

uuidv4 = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}