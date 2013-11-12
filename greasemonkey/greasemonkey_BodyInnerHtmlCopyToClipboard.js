// ==UserScript==
// @name        BodyInnerHtmlCopyToClipboard
// @namespace   http://griddoor.appspot.com/
// @description Copy the source of the page
// @version     1
// @grant       GM_setClipboard
// ==/UserScript==

GM_setClipboard(document.body.innerHTML);
