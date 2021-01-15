/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


goToPage = pageid => {
    $.ajax({
        url: 'http://localhost:8080/walletwebversion/resources/javaee8/getPage/'
                + pageid,
        beforeSend: function () {
            // setting a timeout
            $('.page').html('<div class="loading-container"><img src="static/images/loading.gif" alt=""/></div')
        },
        success: function (data) {
            setTimeout(() => { $('.page').html(data) }, 500);
        }
    });
}

uuidv4 = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}