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

// SECTION: Form items
let $typeSelect = $("#type-select");
let $blenderRenderSettings = $("#blender-render-settings");

$typeSelect.change(function () {
    let isHidingSettings = $blenderRenderSettings.hasClass("d-none");
    if ($typeSelect.val() > 2 && isHidingSettings) {
        $blenderRenderSettings.removeClass("d-none");
    } else if ($typeSelect.val() < 3 && !isHidingSettings) {
        $blenderRenderSettings.addClass("d-none");
    }
});