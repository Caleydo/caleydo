var stopped = true;

function selectVisLink() {
	var	selid =	content.getSelection().toString();
	var	selectionId	= "" + selid +	"";

	if (selectionId	== null	|| selectionId == "") return;
	window.localSelectionId = selectionId;
	
	reportVisLinks(selectionId);
}

function reportVisLinks(selectionId) {
	var	doc	= content.document;
	var	bbs	= searchDocument(doc, selectionId);
	var	xml	= generateBoundingBoxesXML(bbs,	true);

	var	requrl = "http://localhost:8080/visdaemon/selection";
	requrl += "?name=" + window.visLinkAppName;
	requrl += "&id=" + selectionId;
	requrl += "&xml=" +	xml;

	var	xhttp =	new	XMLHttpRequest();
	xhttp.open("GET", requrl, false);
	xhttp.send("");
	xmlDoc = xhttp.responseXML;
}

function startVisLinks() {
	// alert("startVisLinks");
	stopped = false;
	if (register()) {
		window.addEventListener('unload', stopVisLinks, false);
		window.addEventListener('scroll', windowChanged, false);
		window.addEventListener('resize', resize, false);
		setTimeout("triggerSearch()", 500);
	}
}

function windowChanged() {
	if (window.localSelectionId != null) {
		reportVisLinks(window.localSelectionId);
	} else {
//		clearVisualLinks();
		reportVisLinks(window.localSelectionId);
	}
}

function stopVisLinks() {
	stopped = true;
	window.removeEventListener('unload', stopVisLinks, false);
	window.removeEventListener('scroll', clearVisualLinks, false);
	unregister();
}

function register()	{
	if (window.visLinkAppName == null) {
		window.visLinkAppName =	"firefox-" + (new Date()).getTime();
	}

	var win = content.document.defaultView;
	var x = win.screenX + (win.outerWidth - win.innerWidth) / 2;
	var y = win.screenY + (win.outerHeight - win.innerHeight);
	var w = win.innerWidth;
	var h = win.innerHeight;

	var	xml	= "";
	xml	+= "<boundingBox";
	xml	+= " x=\""+x+"\"";
	xml	+= " y=\""+y+"\"";
	xml	+= " width=\""+w+"\"";
	xml	+= " height=\""+h+"\"";
	xml	+= " />\n";

	xml	= escape(xml);

	var	requrl = "http://localhost:8080/visdaemon/register";
	requrl += "?name=" + window.visLinkAppName;
	requrl += "&xml=" +	xml;
	
	try {
		var	xhttp =	new	XMLHttpRequest();
		xhttp.open("GET", requrl, false);
		xhttp.send("");
		xmlDoc = xhttp.responseXML;
	} catch (err) {
		alert("Could not establish connection to visdaemon.");
		stopped = true;
		return false;
	}
	return true;
}

function unregister() {
	var	requrl = "http://localhost:8080/visdaemon/unregister";
	requrl += "?name=" + window.visLinkAppName;
	try {
		var	xhttp =	new	XMLHttpRequest();
		xhttp.open("GET", requrl, false);
		xhttp.send("");
		xmlDoc = xhttp.responseXML;
	} catch (err) {
		// vis link server no reachable, nothing to do
	}
}

function clearVisualLinks()	{
	var	requrl = "http://localhost:8080/visdaemon/clearVisualLinks";
	requrl += "?name=" + window.visLinkAppName;

	try	{
		var	xhttp =	new	XMLHttpRequest();
		xhttp.open("GET", requrl, false);
		xhttp.send("");
		xmlDoc = xhttp.responseXML;
	} catch (err) {
		alert("Connection to visdaemon lost, stopping");
		stopVisLinks();
	}
}

function validateWindowPosition() {
	var oldPos = window.oldPos;
	if (oldPos != null) {
		if (window.screenX != oldPos.x || window.screenY != oldPos.y) {
			register();
			oldPos.x = window.screenX;
			oldPos.y = window.screenY;
			windowChanged();
		}
	} else {
		window.oldPos = new Object();
		window.oldPos.x = window.screenX;
		window.oldPos.y = window.screenY;
	}
}

function resize() {
	register();
	windowChanged();
}

function triggerSearch() {
	validateWindowPosition();
	if (stopped) return;
	var	id = getId();
	if (stopped) return; // second check, because getId() might have lost connection to daemon
	if (id != null)	{
		window.localSelectionId = id;
		var	doc	= content.document;
		var	bbs	= searchDocument(doc, id);
		var	xml	= generateBoundingBoxesXML(bbs,	false);
		sendBoundingBoxes(xml);
	}
	setTimeout("triggerSearch()", 200);
}

function getId() {
	var	requrl = "http://localhost:8080/visdaemon/propagation";
	requrl += "?name=" + window.visLinkAppName;

	try {
		var	xhttp =	new	XMLHttpRequest();
		xhttp.open("GET", requrl, false);
		xhttp.send("");
		xmlDoc = xhttp.responseXML;
	} catch (err) {
		alert("Connection to visdaemon lost, stopping");
		stopVisLinks();
		return null;
	}

	var	ids	= xmlDoc.getElementsByTagName("id");
	if (ids.length > 0)	{
		if (ids[0].childNodes[0] !=	null) {
			return ids[0].childNodes[0].nodeValue;
		}
	}
	return null;
}

function searchDocument(doc, id) {
	var	textnodes =	doc.evaluate("//body//*/text()", doc, null,	XPathResult.ANY_TYPE, null);
	var	result = new Array();
	while(node = textnodes.iterateNext()) {
		var	s =	node.nodeValue;
		var	i =	s.indexOf(id);
		if (i != -1) {
			result[result.length] =	node.parentNode;
		}
	}
//	  alert("hits: " + result.length);

	var	bbs	= new Array();
	for(var	i=0; i<result.length; i++) {
		var	r =	result[i];
		for	(var j=0; j<r.childNodes.length; j++) {
			currentNode	= r.childNodes[j];
			sourceString = currentNode.nodeValue;
			if (sourceString !=	null) {
				var	idx	= sourceString.indexOf(id);
				while (idx >= 0) {
					var	s1 = sourceString.substring(0, idx);
					var	s2 = sourceString.substring(idx, idx + id.length);
					var	s3 = sourceString.substr(idx + id.length);
					
					var	d2 = doc.createElement("SPAN");
					d2.style.outline = "2px	solid red";
					var	t2 = doc.createTextNode(s2);
					d2.appendChild(t2);
					var	t3 = doc.createTextNode(s3);
					
					currentNode.nodeValue =	s1;
					var	nextSib	= currentNode.nextSibling
					if (nextSib	== null) {
						r.appendChild(d2);
						r.appendChild(t3);
					} else {
						r.insertBefore(d2, nextSib);
						r.insertBefore(t3, nextSib);
					}
					
					var	bb = findBoundingBox(doc, d2);
					if (bb != null)	{
						bbs[bbs.length]	= bb;
					}
					
					r.removeChild(t3);
					r.removeChild(d2);
					currentNode.nodeValue =	sourceString;
					
					var	idx	= sourceString.indexOf(id, id.length + idx);
				};
			}
		}
	}
	return bbs;
}

function findBoundingBox(doc, obj) {
	var	w =	obj.offsetWidth;
	var	h =	obj.offsetHeight;
	var	curleft	= curtop = 0;

	if (obj.offsetParent) {
		do {
			curleft	+= obj.offsetLeft;
			curtop += obj.offsetTop;
		} while	(obj = obj.offsetParent);
	}

	var	body = doc.getElementsByTagName("body")[0];
	var	win	= body.ownerDocument.defaultView;
	var	yoffset	= win.outerHeight -	win.innerHeight	- 2;

	var	ret	= null;
	// check if	visible
	//if (((curtop - win.pageYOffset)	> 0) &&	((curtop - win.pageYOffset)	< win.innerHeight) && 
	//	((curleft -	win.pageXOffset) > 0) && ((curleft - win.pageXOffset) <	win.innerWidth)) {
			
		finaltop = curtop + win.screenY + yoffset - win.pageYOffset;
		finalleft = curleft + win.screenX + 1 - win.pageXOffset;
		
		ret	= new Object();
		ret.x =	finalleft;
		ret.y =	finaltop;
		ret.width =	w +	2;
		ret.height = h + 2;
	//}
	return ret;
}

function generateBoundingBoxesXML(bbs, source) {
	var	xml	= "<boundingBoxList>";
	for	(var i = 0;	i<bbs.length; i++) {
		xml	+= "<boundingBox";
		xml	+= " x=\""+bbs[i].x+"\"";
		xml	+= " y=\""+bbs[i].y+"\"";
		xml	+= " width=\""+bbs[i].width+"\"";
		xml	+= " height=\""+bbs[i].height+"\"";
		xml	+= " source=\""+source+"\"";
		xml	+= " />\n";
		source = false;
	}
	xml	+= "</boundingBoxList>\n";

	xml	= escape(xml);
	
	return xml;
}

function sendBoundingBoxes(xml)	{
	var	requrl = "http://localhost:8080/visdaemon/reportVisualLinks"
	requrl += "?name=" + window.visLinkAppName;
	requrl += "&xml=" +	xml;
	
	var	xhttp =	new	XMLHttpRequest();
	xhttp.open("GET", requrl, false);
	xhttp.send("");
	xmlDoc = xhttp.responseXML;
}

// window.addEventListener('load', myjsStart, false);
/*
function myjsStart() {
	alert("myjsStart");
	var	appcontent = window.document.getElementById("appcontent");
	if (appcontent)	{
		appcontent.addEventListener("DOMContentLoaded",	startVisLinks, false);
	}
} 
*/
