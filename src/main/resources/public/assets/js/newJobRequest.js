// Login with EPS Auth
function callLogin() {
    request({url: "/login_epsauth"}).then(data => {
        console.log("Requested login")
    }).catch(onerror => {
        console.log("Logging in Error: " + onerror)
    });
}