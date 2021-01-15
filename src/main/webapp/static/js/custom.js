/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


goToPage = pageid => {
    $.ajax({
        url: 'http://localhost:8080/walletwebversion/resources/javaee8/getPage/' 
                + pageid,
        success: function (data) {
            $('.page').html(data);
        }
    });
}

uuidv4 = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}