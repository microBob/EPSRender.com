// SECTION: Values
let userName = "";
let projectType = -1;
let projectLocation = "";

// SECTION: page init
$(function () {
    updateLoginStatus();
})

// SECTION: Login with EPS Auth
function callLogin() {
    request({url: "/login"}).then(data => {
        console.log(data) //should say "Running login"
    }).catch(onerror => {
        console.log("Logging in Error: " + onerror)
    });
}

function updateLoginStatus() {
    request({url: "/update_login_stat"}).then(data => {
        console.log("Updating login status");
        /*
         * 1) show/hide login row if needed
         * 2) show/hide logged in row if needed
         * 3) show username
         * 4) show/hide rest of form and submit btn if logged in
         * 5) edit warning error message if necessary
         */
        let $loginRow = $("#login-row");
        let $loggedInRow = $("#logged-in-row");
        let $postLoginForm = $("#post-login-form");
        let $jobSubmitBtn = $("#job-submit-btn");
        let $warningErrorP = $("#warning-error-p");

        if (data !== "" && data !== "!invalid!") { // has valid login
            console.log("Welcome " + data);
            if (!$loginRow.hasClass("d-none")) {
                // 1
                $loginRow.addClass("d-none");
                // 2
                $loggedInRow.removeClass("d-none");
                // 3
                $("#current-user-lbl").text(data);
                // 4
                $postLoginForm.removeClass("d-none");
                $jobSubmitBtn.removeClass("d-none");
                // 5
                $warningErrorP.text("Please note: the Server will reject invalid job requests.");
                if ($warningErrorP.hasClass("text-warning")) {
                    $warningErrorP.removeClass("text-warning");
                }
            }
        } else { // missing or invalid
            console.log("No valid login :(");
            if ($loginRow.hasClass("d-none")) {
                // 1
                $loginRow.removeClass("d-none");
                // 2
                $loggedInRow.addClass("d-none");
                // 4
                $postLoginForm.addClass("d-none");
                $jobSubmitBtn.addClass("d-none");
            }
            // 5
            if (data === "!invalid!") {
                console.log("Invalid login!");
                $warningErrorP.text("Error: Invalid user account!");
                if (!$warningErrorP.hasClass("text-warning")) {
                    $warningErrorP.addClass("text-warning");
                }
            }
        }
    }).catch(onerror => {
        console.log("Update Login Error: " + onerror)
    });
}