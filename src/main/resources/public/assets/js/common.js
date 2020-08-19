// HTTP request
function request(obj) {
    return new Promise((resolve, reject) => {
        let xhr = new XMLHttpRequest();

        xhr.open(obj.verb || "GET", obj.url);
        xhr.onload = () => {
            if (xhr.status >= 200 && xhr.status < 300) {
                resolve(xhr.response);
            } else {
                reject(xhr.statusText);
            }
        };
        xhr.onerror = () => reject(xhr.statusText);
        xhr.send(obj.body);
    });
}


// SECTION: frequently used CSS
// Safe add CSS class
function addCSSClass(selector, className) {
    if (!selector.hasClass(className)) {
        selector.addClass(className);
    }
}

// Safe remove CSS class
function removeCSSClass(selector, className) {
    if (selector.hasClass(className)) {
        selector.removeClass(className);
    }
}

// Show/Hide element
function showElement(selector, show = true) {
    if (show) {
        removeCSSClass(selector, "d-none");
    } else {
        addCSSClass(selector, "d-none");
    }
}