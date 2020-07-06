// SECTION: Global values
let userName = "";


// SECTION: page init
$(function () {
    updateLoginStatus();
})


// SECTION: Login with EPS Auth
function updateLoginStatus() {
    request({url: "/update_login_stat"}).then(data => {
        console.log("Updating login status");
        /*
         * 1) show/hide login row if needed
         * 2) show/hide logged in row if needed
         * 3) show username
         * 4) show/hide rest of form and submit btn if logged in
         * 5) edit warning error message if necessary
         * 6) update global data
         */
        let $loginRow = $("#login-row");
        let $loggedInRow = $("#logged-in-row");
        let $postLoginForm = $("#post-login-form");
        let $jobSubmitBtn = $("#job-submit-btn");
        let $warningErrorP = $("#warning-error-p");

        if (data !== "" && data !== "!invalid!") { // has valid login
            console.log("Welcome " + data);
            // 1
            showElement($loginRow, false)
            // 2
            showElement($loggedInRow);
            // 3
            $("#current-user-lbl").text(data);
            // 4
            showElement($postLoginForm);
            showElement($jobSubmitBtn);
            // 5
            $warningErrorP.text("Please note: the Server will reject invalid job requests.");
            removeCSSClass($warningErrorP, "text-warning");
            // 6
            userName = data;
        } else { // missing or invalid
            console.log("No valid login :(");
            // 1
            showElement($loginRow);
            // 2
            showElement($loggedInRow, false);
            // 4
            showElement($postLoginForm, false);
            showElement($jobSubmitBtn, false);
            // 5
            if (data === "!invalid!") {
                console.log("Invalid login!");
                $warningErrorP.text("Error: Invalid user account!");
                addCSSClass($warningErrorP, "text-warning");
            }
        }
    }).catch(onerror => {
        console.log("Update Login Error: " + onerror)
    });
}

// SECTION: validation
// used selectors
let $projectLocationInput = $("#project-location-input");
let $errorMessageLbl = $("#warning-error-p");
let $jobSubmitBtn = $("#job-submit-btn");

// pre-check
let checkForBlender = false;

// check if all required fields are filled
function completionValidation(showIssue = false) {
    /*
     * 1) check if project type is > 2
     * 2) check if use all frames is checked, skip to 5 if so
     * 3) if 1 is true, check if start frame is assigned
     * 3.1) if showIssue, highlight in warning
     * 3.2) show missing start in error
     * 4) check if end frame is assigned too
     * 4.1) check if end frame is less than start frame
     * 4.2) if showIssue, highlight
     * 4.3) show missing end in error
     * 5) check if location is assigned
     * 6) enable add to que if true and disable if failed any
     */

    let requiredFieldMissingMsg = false;
    let smallerEndFrameMsg = false;

    if ($projectTypeSelect.val() > 2) {
        checkForBlender = !$blenderUseAllFramesCheck.prop("checked");
    } else {
        checkForBlender = false;
    }

    if (checkForBlender) {
        if ($startFrameInput.val().toString() === "") {
            requiredFieldMissingMsg = true;
            if (showIssue) {
                addCSSClass($startFrameInput, "bg-warning");
            }
        } else {
            removeCSSClass($startFrameInput, "bg-warning");
        }

        if ($endFrameInput.val().toString() === "") {
            requiredFieldMissingMsg = true;
            if (showIssue) {
                addCSSClass($endFrameInput, "bg-warning");
            }
        } else if ($endFrameInput.val() < $startFrameInput.val()) {
            smallerEndFrameMsg = true;
            if (showIssue) {
                addCSSClass($endFrameInput, "bg-warning");
            }
        } else {
            removeCSSClass($endFrameInput, "bg-warning");
        }
    }

    if ($projectLocationInput.val() === "") {
        requiredFieldMissingMsg = true;
        if (showIssue) {
            addCSSClass($projectLocationInput, "bg-warning")
        }
    } else {
        removeCSSClass($projectLocationInput, "bg-warning");
    }

    if (!requiredFieldMissingMsg && !smallerEndFrameMsg) {
        if ($jobSubmitBtn.prop("disabled")) {
            $jobSubmitBtn.prop("disabled", false);
        }
    } else {
        if (!$jobSubmitBtn.prop("disabled")) {
            $jobSubmitBtn.prop("disabled", true);
        }
    }

    if (showIssue) {
        if (requiredFieldMissingMsg) {
            $errorMessageLbl.text("Some required fields are empty. Please fill them in.");
            addCSSClass($errorMessageLbl, "text-warning");
        } else if (smallerEndFrameMsg) {
            $errorMessageLbl.text("The requested end frame in the Blender render is less than the start frame! Please make the end frame more than the start frame.");
            addCSSClass($errorMessageLbl, "text-warning");
        } else {
            $errorMessageLbl.text("Please note: the Server will reject invalid job requests.");
            removeCSSClass($errorMessageLbl, "text-warning");
        }
    }
}