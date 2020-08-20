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
let $projectFolderNameInput = $("#project-folder-name-input");
let $errorMessageLbl = $("#warning-error-p");
let $jobSubmitBtn = $("#job-submit-btn");

// pre-check
let checkForBlender = false;

// check if all required fields are filled
function completionValidation(showIssue = false) {
    /*
     * 1) check if project type is > 2 (whether need to check on blender render)
     * 2) check if project folder name is empty
     * 2.1) display #2 if showIssue
     * 3) check blender render settings if #1 is true
     * 3.1) check if start frame is empty
     * 3.1.1) display #3.1 if showIssue
     * 3.2) check if end frame is empty
     * 3.2.1) display #3.2 if showIssue
     * 3.3) if no other errors so far, check if end frame < start frame
     * 3.3.1) display #3.3 if showIssue
     * 4) enable the submit btn if all above succeeded, disable if not
     * 5) set the update the warning text field
     * 5.1) show missing values in required fields
     * 5.2) show end frame is smaller than start
     */

    let requiredFieldMissingMsg = false;
    let smallerEndFrameMsg = false;

    // 1
    if ($projectTypeSelect.val() > 2) {
        checkForBlender = !$blenderUseAllFramesCheck.prop("checked");
    } else {
        checkForBlender = false;
    }

    // 2
    if ($projectFolderNameInput.val() === "") {
        requiredFieldMissingMsg = true;
        //2.1
        if (showIssue) {
            addCSSClass($projectFolderNameInput, "bg-warning")
        }
    } else {
        removeCSSClass($projectFolderNameInput, "bg-warning");
    }

    // 3
    if (checkForBlender) {
        // 3.1
        if ($startFrameInput.val().toString() === "") {
            requiredFieldMissingMsg = true;
            // 3.1.1
            if (showIssue) {
                addCSSClass($startFrameInput, "bg-warning");
            }
        } else {
            removeCSSClass($startFrameInput, "bg-warning");
        }

        // 3.2
        if ($endFrameInput.val().toString() === "") {
            requiredFieldMissingMsg = true;
            // 3.2.1
            if (showIssue) {
                addCSSClass($endFrameInput, "bg-warning");
            }
        }
        // 3.3
        if (!requiredFieldMissingMsg && $endFrameInput.val() < $startFrameInput.val()) {
            smallerEndFrameMsg = true;
            // 3.3.1
            if (showIssue) {
                addCSSClass($endFrameInput, "bg-warning");
            }
        } else {
            removeCSSClass($endFrameInput, "bg-warning");
        }
    }

    // 4
    if (!requiredFieldMissingMsg && !smallerEndFrameMsg) {
        if ($jobSubmitBtn.prop("disabled")) {
            $jobSubmitBtn.prop("disabled", false);
        }
    } else {
        if (!$jobSubmitBtn.prop("disabled")) {
            $jobSubmitBtn.prop("disabled", true);
        }
    }

    // 5
    if (showIssue) {
        // 5.1
        if (requiredFieldMissingMsg) {
            $errorMessageLbl.text("Some required fields are empty. Please fill them in.");
            addCSSClass($errorMessageLbl, "text-warning");
        }
        // 5.2
        else if (smallerEndFrameMsg) {
            $errorMessageLbl.text("The requested end frame in the Blender render is less than the start frame! Please make the end frame more than the start frame.");
            addCSSClass($errorMessageLbl, "text-warning");
        } else {
            $errorMessageLbl.text("Please note: the Server will reject invalid job requests.");
            removeCSSClass($errorMessageLbl, "text-warning");
        }
    }
}

$("#new-job-request-form").submit(function () {
    console.log("Sending new job request!");

    let projectType = $($projectTypeSelect).val();
    let blenderUseAll, blenderStartFrame, blenderEndFrame;
    let projectFolderName = $($projectFolderNameInput).val();
    if (projectType > 1) {
        blenderUseAll = $($blenderUseAllFramesCheck).prop("checked");
        if (!blenderUseAll) {
            blenderStartFrame = $($startFrameInput).val();
            blenderEndFrame = $($endFrameInput).val();

            request({
                url: "/add_new_job?projectType=" + projectType + "&blenderUseAll=false&blenderStartFrame=" + blenderStartFrame + "&blenderEndFrame=" + blenderEndFrame + "&projectFolderName=" + projectFolderName,
                verb: "PUT"
            }).then(() => {
                console.log("Add job successful!");
            }).catch(onerror => {
                console.log("Add job Error: " + onerror);
            });
        } else {
            request({
                url: "/add_new_job?projectType=" + projectType + "&blenderUseAll=true&projectFolderName=" + projectFolderName,
                verb: "PUT"
            }).then(() => {
                console.log("Add job successful!");
            }).catch(onerror => {
                console.log("Add job Error: " + onerror);
            });
        }
    } else {
        request({
            url: "/add_new_job?projectType=" + projectType + "&projectFolderName=" + projectFolderName,
            verb: "PUT"
        }).then(() => {
            console.log("Add job successful!");
        }).catch(onerror => {
            console.log("Add job Error: " + onerror);
        });
    }
});