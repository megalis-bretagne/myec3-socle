<!DOCTYPE html>
<html>
<head>
    <script type="text/javascript" src="./static/js/jquery-3.4.1.min.js"></script>
</head>
<body>
<div onclick="deconnexion('https://sangoku.sib.fr/secure/redirect_uri?logout=https%3A%2F%2Fsangoku.sib.fr',true);">Single Sign-Out</div>
<div onclick="deconnexion('https://sangoku.sib.fr/index.php',true);">true</div>
<div onclick="deconnexion('https://sangoku.sib.fr/index.php',false);">false</div>
<!--<iframe style="display: none;" src='https://sso-preprod.megalis.bretagne.bzh/auth/realms/megalis/protocol/openid-connect/logout'></iframe>-->
<!--<iframe style="display: none;" src='http://sangoku:60080/secure/redirect_uri?logout=http%3A%2F%2Fsangoku%3A60080'></iframe>-->
<!--<iframe style="display: none;" src='https://combrit-socle.sib.fr/j_spring_security_logout'></iframe>-->
<!--<iframe style="display: none;" src='https://combrit.sib.fr/secure/deconnexion.jsp'></iframe>-->
<script type="text/javascript">function deconnexion (lien, cred) {
    $.ajax({
        url: lien,
        xhrFields: {
            withCredentials: cred
        },
        success: function( result ) {
            console.log("OK");
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
// window.onload=deconnexion("https://sangoku.sib.fr/index.php");
</script>
</body>
</html>