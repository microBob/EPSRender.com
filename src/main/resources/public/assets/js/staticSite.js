// Help Btns
$(".login-help-btn").click(function() {
    $("#login-help-modal").modal("show");
});
$(".logout-help-btn").click(function() {
    $("#logout-help-modal").modal("show");
});
$(".project-type-help-btn").click(function() {
    $("#project-type-help-modal").modal("show");
});
$(".project-location-help-btn").click(function() {
    $("#project-location-help-modal").modal("show");
});
$(".server-info-btn").click(function() {
    $("#server-info-modal").modal("show");
});
$(".job-que-info-btn").click(function() {
    $("#job-que-info-modal").modal("show");
});

// Contact Btns
let contacts = [
        "kyang@eastsideprep.org"
];
$("#support-btn").click(function() {
    let subject = "Requesting Help with EPRender";
    
    $.each(contacts, function(index, value) {
        document.location.href = "mailto:"+value+"?subject="+subject;
    });
});
$("#feedback-btn").click(function() {
    let subject = "Feedback for EPRender";
    
    $.each(contacts, function(index, value) {
        document.location.href = "mailto:"+value+"?subject="+subject;
    });
});