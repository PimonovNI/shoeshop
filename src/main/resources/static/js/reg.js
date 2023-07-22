"use strict";

const regexEmail = /(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/;
const regexPassword = /([a-z].*[A-Z].*\d)|([a-z].*\d.*[A-Z])|([A-Z].*[a-z].*\d)|([A-Z].*\d.*[a-z])|(\d.*[a-z].*[A-Z])|(\d.*[A-Z].*[a-z])/;

let errorDivs = [];

function createErrorDiv(message, elem) {
    let div = document.createElement("div");
    div.className = "error";
    div.innerHTML = "<div>" + message + "</div>"
    elem.after(div);
    errorDivs.push(div);

    function hideMessage() {
        div.remove();
        delete errorDivs[errorDivs.indexOf(div)];
        elem.removeEventListener("input", hideMessage);
    }

    elem.addEventListener("input", hideMessage);

    return div;
}

document.forms[0].addEventListener("submit", function(event) {
    let username = this.elements.username.value;
    let email = this.elements.email.value;
    let password = this.elements.password.value;
    let isFindError = false;

    errorDivs.forEach(div => div.remove());

    if (!password || password.length < 2 || password.length > 128 || !password.match(regexPassword)) {
        let message = "";
        if (password) {
            if (password.length < 2 || password.length > 128) message = "Має бути біль 2 символів і меньше 128";
            if (!password.match(regexPassword)) message += (message ? "" : "\n") + "Використайте літери у різних реєстрах та число"; 
        }
        else {
            message = "Введіть пароль";
        }
        createErrorDiv(message, this.elements.password);
        this.elements.password.focus();
        isFindError = true;  
    }

    if (!email || !email.match(regexEmail)) {
        let message = email ? "Неправильний формат пошти" : "Введіть пошту";
        createErrorDiv(message, this.elements.email);
        this.elements.email.focus();
        isFindError = true;
    }

    if (!username || username.length < 2 || username.length > 128) {
        let message = username ? "Має бути біль 2 символів і меньше 128" : "Введіть логін";
        createErrorDiv(message, this.elements.username);
        this.elements.username.focus();
        isFindError = true;
    }

    if (isFindError) {
        event.preventDefault();
    }
});