function deconnexion(logoutSSO){
    if (logoutSSO){
        deconnexionSSO();
    } else {
        deconnexionApplis();
    }
}

function deconnexionSSO () {
    var req = new XMLHttpRequest();
    req.withCredentials = true;
    req.open('POST', 'https://combrit-sso.sib.fr/auth/realms/megalis/protocol/openid-connect/logout', true);
    // Just like regular ol' XHR
    req.onreadystatechange = function() {
        if (req.readyState === 4) {
            if (req.status >= 200 && req.status < 400) {
                // JSON.parse(req.responseText) etc.
                log.trace("ok");
            } else {
                // Handle error case
                log.trace("ko");
            }
        }
    };
    req.send();

    // $.ajax({
    //     url: "https://combrit-sso.sib.fr/auth/realms/megalis/protocol/openid-connect/logout",
    //     xhrFields: {
    //         withCredentials: true
    //     },
    //     success: function( result ) {
    //         deconnexionApplis();
    //     },
    //     error: function (result) {
    //         console.log("KO")
    //     }
    // });

}

function deconnexionApplis(){
    jQuery.ajax({
        url: "https://sangoku.sib.fr/deconnexion.php",
        xhrFields: {
            withCredentials: true
        },
        success: function( result ) {
            window.location.href = "https://combrit.sib.fr"
        },
        error: function (result) {
            console.log("KO")
        }
    });
}