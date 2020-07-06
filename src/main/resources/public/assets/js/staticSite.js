// SECTION: Help Buttons
$(".login-help-btn").click(function () {
    $("#login-help-modal").modal("show");
});
$(".logout-help-btn").click(function () {
    $("#logout-help-modal").modal("show");
});
$(".project-type-help-btn").click(function () {
    $("#project-type-help-modal").modal("show");
});
$(".project-location-help-btn").click(function () {
    $("#project-location-help-modal").modal("show");
});
$(".server-info-btn").click(function () {
    $("#server-info-modal").modal("show");
});
$(".job-que-info-btn").click(function () {
    $("#job-que-info-modal").modal("show");
});


// SECTION: Contact Buttons
let contacts = [
    "kyang@eastsideprep.org"
];
$("#support-btn").click(function () {
    let subject = "Requesting Help with EPRender";

    $.each(contacts, function (index, value) {
        document.location.href = "mailto:" + value + "?subject=" + subject;
    });
});
$("#feedback-btn").click(function () {
    let subject = "Feedback for EPRender";

    $.each(contacts, function (index, value) {
        document.location.href = "mailto:" + value + "?subject=" + subject;
    });
});


// SECTION: Debug
$("#post-login-form").removeClass("d-none");
$("#job-submit-btn").removeClass("d-none");


// SECTION: Form items
let $projectTypeSelect = $("#type-select");
let $blenderRenderSettings = $("#blender-render-settings");
let $blenderUseAllFramesCheck = $("#use-all-frames-check-box");
let $endFrameInput = $("#end-frame-input");
let $startFrameInput = $("#start-frame-input");

// show blender render settings
$projectTypeSelect.change(function () {
    let isHidingSettings = $blenderRenderSettings.hasClass("d-none");
    if ($projectTypeSelect.val() > 2) {
        showElement($blenderRenderSettings);
    } else {
        showElement($blenderRenderSettings, false);
    }
});

// disable blender frame selection if use all is checked
$blenderUseAllFramesCheck.change(function () {
    if ($blenderUseAllFramesCheck.prop("checked")) {
        if (!$startFrameInput.prop("disabled")) {
            $startFrameInput.prop("disabled", true);
            $endFrameInput.prop("disabled", true);
            removeCSSClass($startFrameInput, "bg-warning");
            removeCSSClass($endFrameInput, "bg-warning");
        }
    } else {
        if ($startFrameInput.prop("disabled")) {
            $startFrameInput.prop("disabled", false);
            $endFrameInput.prop("disabled", false);
        }
    }
});