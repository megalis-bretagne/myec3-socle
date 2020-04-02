<!DOCTYPE html>
<html>
<head>
    <script type="text/javascript" src="./static/js/jquery-3.4.1.min.js"></script>
</head>
<body>
<script type="text/javascript">
    function deconnexion () {
        $.ajax({
            url: "https://combrit-sso.sib.fr/auth/realms/megalis/protocol/openid-connect/logout",
            xhrFields: {
                withCredentials: true
            },
            success: function( result ) {
                deconnexionApplis();
            },
            error: function (result) {
                console.log("KO")
            }
        });

    // var req = new XMLHttpRequest();
    // // Feature detection for CORS
    //     req.open('GET', 'http://localhost:8087/index.jsp', true);
    //     // Just like regular ol' XHR
    // withCredentials = true;
    // req.onreadystatechange = function() {
    //         if (req.readyState === 4) {
    //             if (req.status >= 200 && req.status < 400) {
    //                 // JSON.parse(req.responseText) etc.
    //                 console.log("ok");
    //             } else {
    //                 // Handle error case
    //                 console.log("ko");
    //             }
    //         }
    //     };
    //     req.send();
    // window.location.assign("http://localhost:8087/index.jsp");
}

    function deconnexionApplis(){
        $.ajax({
            url: "https://sangoku.sib.fr/deconnexion.php",
            xhrFields: {
                withCredentials: true
            },
            success: function( result ) {
                console.log("OK");
            },
            error: function (result) {
                console.log("KO")
            }
        });
    }
window.onload=deconnexion();
</script>
</body>
</html>