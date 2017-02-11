// ==UserScript==
// @name        MultiStrsFind
// @namespace   http://griddoor.appspot.com/
// @description Just like CTRL+F, but iterate a list of strings
// @version     1
// @grant       none
// @run-at      document-start
// ==/UserScript==

var STRS_TO_FIND = ['the','an','are'];
var KEY_CODE = 78;//n, same as VIM
var KEY_CODE2 = 112;//F1, another key for some sites already using n, like twitter

var i = 0;
if(STRS_TO_FIND.length>1){//must be 2 or greater, or this script is meaningless.
	window.onkeyup = function (e) {
		if (e.which == KEY_CODE || e.which == KEY_CODE2){
			//console.log('specific key up, i equals '+i);
			while(!find(STRS_TO_FIND[i])){
				++i;
				//console.log('member not found, i incremented, equals '+i);
				document.getSelection().removeAllRanges();
				if(i==STRS_TO_FIND.length){
					//console.log('i the index exeeding, reset to 0');
					//alert('i the index exeeding, reset to 0');
					i=0;
					break;
				}
			}
		}
	}
}
