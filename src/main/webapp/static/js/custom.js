/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//walletwebversion
//walletweb-1.0-SNAPSHOT

resetFieldsError = el => {
    el.removeClass('is-invalid');
    el.parent().find('span.error').addClass('hidden');

}

highlightError = (el, msg, isComplexStructure) => {
    el.addClass('is-invalid');
    console.log(el.parent().find('span.error'));
    if (!isComplexStructure) {
        el.parent().find('span.error').removeClass('hidden');
        el.parent().find('span.error').html(msg);
    } else {
        $('#' + el.attr('data-label-ref')).find('span.error').removeClass('hidden');
        $('#' + el.attr('data-label-ref')).find('span.error').html(msg);
    }

}

checkFields = (isComplexStructure) => {
    let checkFormResult = true;
    $('.check-form-field').each(function () {
        let el = $(this);
        if (el.hasClass("not-empty") && el.val().length === 0) {
            highlightError(el, '( The field cannot be empty! )', isComplexStructure);
            checkFormResult = false;
        }
        if (el.hasClass("min-length") && el.val().length < el.attr('data-min-length')) {
            highlightError(el, '( Minimum length required: ' + el.attr('data-min-length') + ' )', isComplexStructure);
            checkFormResult = false;
        }
        if (el.hasClass('is-integer') && isNaN(parseInt(el.val()))) {
            highlightError(el, '( Invalid input )', isComplexStructure);
            checkFormResult = false;
        }
    })

    return checkFormResult;
}

goToPage = (pageid) => {
    
    let pageBean = {};
    pageBean['pageId'] = pageid;
    pageBean['contextRoot'] = window.webappname;
    
    $.ajax({
        headers: {
            'Content-Type': "application/json"
        },
        type: 'POST',
        url: window.webappname + '/resources/javaee8/getPage',
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(pageBean),
        beforeSend: function () {
            $('.page').html('<div class="loading-container"><img src="static/images/loading.gif" alt=""/></div');
        },
        success: function (data) {
            setTimeout(() => {
                $('.page').html(data['pageContent']);
            }, 500);
        },
        error: function(data) {
            alert('General error retrieving page');
        }
    });

    
}

uuidv4 = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

populateUserMenu = dataInputUserWallet => {
    window.signedResponseBean = dataInputUserWallet;
    console.log(dataInputUserWallet);
    $('#wallet-address').html(dataInputUserWallet['walletAddress']);
    $('#wallet-key').html(dataInputUserWallet['walletKey']);
    $.ajax({
        headers: {
            'Content-Type': "application/json"
        },
        type: 'POST',
        url: window.webappname + '/resources/javaee8/getWalletIdenticon',
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(dataInputUserWallet),
        success: function (dataRes) {
            $('#wallet-identicon').attr('src', 'data:image/png;base64, ' + dataRes['identiconUrlBase64']);
        }
    });

    $.ajax({
        headers: {
            'Content-Type': "application/json"
        },
        type: 'POST',
        url: window.webappname + '/resources/javaee8/getWalletCrc',
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(dataInputUserWallet),
        success: function (dataRes) {
            $('#wallet-crc').html(dataRes['crcAddress']);
        }
    });

    getAddressBalance(dataInputUserWallet['walletAddress']);
};

getAddressBalance = (walletAddress) => {
    let dataForBalance = {};
    dataForBalance['data'] = walletAddress;
    dataForBalance['endpoint'] = $('.env-select-balance').val();

    $.ajax({
        headers: {
            'Content-Type': "application/json"
        },
        type: 'POST',
        url: window.webappname + '/resources/javaee8/getWalletBalances',
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(dataForBalance),
        success: function (dataRes) {
            dataRes['greenBalance'] /= Math.pow(10, 9);
            dataRes['redBalance'] /= Math.pow(10, 9);
            dataRes['greenPenalty'] /= Math.pow(10, 9);
            dataRes['redPenalty'] /= Math.pow(10, 9);

            $('#wallet-tkg').html(new Number(dataRes['greenBalance']).toLocaleString("de-DE"));
            $('#wallet-tkr').html(new Number(dataRes['redBalance']).toLocaleString("de-DE"));
            $('#wallet-ftkg').html(new Number(dataRes['greenPenalty']).toLocaleString("de-DE"));
            $('#wallet-ftkr').html(new Number(dataRes['redPenalty']).toLocaleString("de-DE"));
        }
    });
}

fillItb = (
        from,
        to,
        message,
        notBefore,
        redValue,
        greenValue,
        transactionType,
        transactionHash,
        epoch,
        slot) => {
    window.signedResponseBean['request']['itb']['from'] = from;
    window.signedResponseBean['request']['itb']['to'] = to;
    window.signedResponseBean['request']['itb']['message'] = message;
    window.signedResponseBean['request']['itb']['notBefore'] = notBefore;
    window.signedResponseBean['request']['itb']['redValue'] = redValue;
    window.signedResponseBean['request']['itb']['greenValue'] = greenValue;
    window.signedResponseBean['request']['itb']['transactionType'] = transactionType;
    window.signedResponseBean['request']['itb']['transactionHash'] = transactionHash;
    window.signedResponseBean['request']['itb']['epoch'] = epoch;
    window.signedResponseBean['request']['itb']['slot'] = slot;
}