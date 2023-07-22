"use strict";

let sizeDiv = document.querySelector("form .sizes");
let currentSize = null;

sizeDiv.addEventListener("mousedown", function(event) {
    let target = event.target.closest(".size");
    if (!target || !this.contains(target)) return;
    console.log(target);

    if (currentSize) {
        currentSize.classList.remove("size-selected");
    }
    target.classList.add("size-selected");
    currentSize = target;
});
