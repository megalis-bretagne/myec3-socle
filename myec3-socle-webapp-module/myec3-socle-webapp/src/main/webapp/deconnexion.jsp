<!DOCTYPE html>
<html>
<head></head>
<body><img SRC="img/loading.gif" style="position: absolute; width: 20%; top: 30%; left: 40%;">
<iframe style="display: none;"
        src='https://sso-preprod.megalis.bretagne.bzh/auth/realms/megalis/protocol/openid-connect/logout'></iframe>
<iframe style="display: none;" src='https://marches-preprod.megalis.bretagne.bzh/oauth2callback?session=logout'></iframe>
<iframe style="display: none;" src='https://pastell-preprod.megalis.bretagne.bzh/oauth2callback?session=logout'></iframe>
<script type="text/javascript"> window.onload = function () {
    window.location.assign("https://www-preprod.megalis.bretagne.bzh");
}</script>
</body>
</html>