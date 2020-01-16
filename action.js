if (window.history.replaceState) {
    window.history.replaceState(null, null, window.location.href);
}

function toggleWhatIsThisText() {
    // var btnSelf = document.getElementById("whatIsThisBtn")
    // btnSelf.value = btnSelf.value === "Got It!" ? "What is This?" : "Got It!";

    const modalText = document.getElementById("whatIsThisText");
    modalText.style.display = 'block';
    // textSpan.style.display = textSpan.style.display === 'none' ? '' : 'none';
}

function serverDataTabs(showServerStatus) {
    const serverStatElement = document.getElementById("serverStatusData");
    const serverStatDataBtn = document.getElementById("serverStatusDataBtn");
    const jobQueElement = document.getElementById("jobQueData");
    const jobQueDataBtn = document.getElementById("jobQueDataBtn");

    if (showServerStatus) {
        if (!serverStatDataBtn.classList.contains("w3-black")) {
            serverStatElement.style.display = "block";
            jobQueElement.style.display = "none";
            serverStatDataBtn.classList.add("w3-black");
            jobQueDataBtn.classList.remove("w3-black");
        }
    } else {
        if (!jobQueDataBtn.classList.contains("w3-black")) {
            serverStatElement.style.display = "none";
            jobQueElement.style.display = "block";
            jobQueDataBtn.classList.add("w3-black");
            serverStatDataBtn.classList.remove("w3-black");
        }
    }
}

function showRenderingFrames() {
    const renderingFramesDiv = document.getElementById("blenderRenderingFrames");
    const typeSelector = document.getElementById("projectTypeInput");

    if (typeSelector.value === '2') {
        renderingFramesDiv.style.display = "block";
    } else {
        renderingFramesDiv.style.display = "none";
    }
}

function clearForm() {
    document.getElementById("emailInput").value = "";
    document.getElementById("projectTypeInput").value = 0;
    document.getElementById("projectPathInput").value = "";
    document.getElementById("blenderStartFrameInput").value = "";
    document.getElementById("blenderEndFrameInput").value = "";
    document.getElementById("blenderRenderingFrames").style.display = "none";
}