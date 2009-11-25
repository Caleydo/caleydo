var bboxes = new Array();

function myjsStart() {
    alert("myjsStart");
    var	appcontent = window.document.getElementById("appcontent");
    if (appcontent) {
	    appcontent.addEventListener("DOMContentLoaded", startVisLinks, false);
    }
} 

function selectVisLink() {
	id = content.document.defaultView.getSelection();

	var xhttp = new XMLHttpRequest();
	xhttp.open("GET", "http://localhost:8080/visdaemon/selection?name=firefox&id=" + id, false);
	xhttp.send("");
	xmlDoc = xhttp.responseXML;
}

function startVisLinks() {
    // alert("startVisLinks");
    bboxes = new Array();
	register();
	setTimeout("triggerSearch()", 1000);
}

function register() {
	var x = window.screenX;
	var y = window.screenY;
	var w = window.outerWidth;
	var h = window.outerHeight;

	var xml = "";
    xml += "<boundingBox";
    xml += " x=\""+x+"\"";
    xml += " y=\""+y+"\"";
    xml += " width=\""+w+"\"";
    xml += " height=\""+h+"\"";
    xml += " />\n";

    xml = escape(xml);

	var xhttp = new XMLHttpRequest();
	xhttp.open("GET", "http://localhost:8080/visdaemon/register?name=firefox&xml=" + xml, false);
	xhttp.send("");
	xmlDoc = xhttp.responseXML;
}

function triggerSearch(event) {
	var id = getId();
	if (id != null) {
        var doc = content.document;
		removeBoundingBoxes(doc);
		var bbs = searchDocument(doc, id);
		sendBoundingBoxes(bbs);
	} else {
//		alert("request timed out, could not get filter from vislink daemon");
	}
	setTimeout("triggerSearch()", 3000);
}

function getId() {
//alert("getId() started");
	var xhttp = new XMLHttpRequest();
	xhttp.open("GET","http://localhost:8080/visdaemon/propagation?name=firefox", false);
	xhttp.send("");
	xmlDoc = xhttp.responseXML;
	var ids = xmlDoc.getElementsByTagName("id");
	if (ids.length > 0) {
// alert("select id = " + ids[0].childNodes[0].nodeValue);
		return ids[0].childNodes[0].nodeValue;
	} else {
// alert("no ids to select");
		return null;
	}
}

function searchDocument(doc, id) {
	var textnodes = doc.evaluate("//body//*/text()", doc, null, XPathResult.ANY_TYPE, null);
	var result = new Array();
	while(node = textnodes.iterateNext()) {
		var s = node.nodeValue;
        var i = s.indexOf(id);
        if (i != -1) {
            result[result.length] = node.parentNode;
        }
	}
//    alert("hits: " + result.length);

    var bbs = new Array();
	for(var i=0; i<result.length; i++) {
		var r = result[i];
		for (var j=0; j<r.childNodes.length; j++) {
			currentNode = r.childNodes[j];
			sourceString = currentNode.nodeValue;
			if (sourceString != null) {
				var idx = sourceString.indexOf(id);
				while (idx >= 0) {
					var s1 = sourceString.substring(0, idx);
					var s2 = sourceString.substring(idx, idx + id.length);
					var s3 = sourceString.substr(idx + id.length);
					
					var d2 = doc.createElement("SPAN");
					d2.style.outline = "2px solid red";
					var t2 = doc.createTextNode(s2);
					d2.appendChild(t2);
					var t3 = doc.createTextNode(s3);
					
					currentNode.nodeValue = s1;
					var nextSib = currentNode.nextSibling
					if (nextSib == null) {
						r.appendChild(d2);
						r.appendChild(t3);
					} else {
						r.insertBefore(d2, nextSib);
						r.insertBefore(t3, nextSib);
					}
					
					var bb = findBoundingBox(doc, d2);
					if (bb != null) {
    					bbs[bbs.length] = bb;
    				}
					
					r.removeChild(t3);
					r.removeChild(d2);
					currentNode.nodeValue = sourceString;
					
					var idx = sourceString.indexOf(id, id.length + idx);
				};
			}
		}
	}
//    alert(bbs);
    addBoundingBoxes();
    return bbs;
}

function findBoundingBox(doc, obj) {
	var w = obj.offsetWidth;
	var h = obj.offsetHeight;
	var curleft = curtop = 0;

    if (obj.offsetParent) {
        do {
            curleft += obj.offsetLeft;
            curtop += obj.offsetTop;
        } while (obj = obj.offsetParent);
    }

    var body = doc.getElementsByTagName("body")[0];
    var win = body.ownerDocument.defaultView;
    var yoffset = win.outerHeight - win.innerHeight - 2;

    var ret = null;
    // check if visible
    if (((curtop - win.pageYOffset) > 0) && ((curtop - win.pageYOffset) < win.innerHeight) && 
        ((curleft - win.pageXOffset) > 0) && ((curleft - win.pageXOffset) < win.innerWidth)) {
            
        finaltop = curtop + win.screenY + yoffset - win.pageYOffset;
        finalleft = curleft + win.screenX + 1;
        
        // debug("[(x:" + finalleft + ", y:" + finaltop + "), (w:" + w + ", h:" + h + ")]");
        ret = new Object();
        ret.x = finalleft;
        ret.y = finaltop;
        ret.width = w + 2;
        ret.height = h + 2;
    }

/*
    var bb = doc.createElement("DIV");
    bb.style.position = "absolute";
    bb.style.top = curtop + "px";
    bb.style.left = curleft + "px";
    bb.style.width = w + "px";
    bb.style.height = h + "px";
    bb.style.outline = "2px solid red";
    bboxes[bboxes.length] = bb;
*/
    return ret;
}

function addBoundingBoxes() {
    var body = content.document.getElementsByTagName("body")[0];
    for (var i = 0; i < bboxes.length; i++) {
        body.appendChild(bboxes[i]);
    }
}

function sendBoundingBoxes(bbs) {
    var xml = "<boundingBoxList>";
    for (var i = 0; i<bbs.length; i++) {
        xml += "<boundingBox";
        xml += " x=\""+bbs[i].x+"\"";
        xml += " y=\""+bbs[i].y+"\"";
        xml += " width=\""+bbs[i].width+"\"";
        xml += " height=\""+bbs[i].height+"\"";
        xml += " />\n";
    }
    xml += "</boundingBoxList>\n";

    xml = escape(xml);
    var requrl = "http://localhost:8080/visdaemon/reportVisualLinks?name=firefox&xml=" + xml;
    
	var xhttp = new XMLHttpRequest();
	xhttp.open("GET", requrl, false);
	xhttp.send("");
	xmlDoc = xhttp.responseXML;
}

function removeBoundingBoxes(doc) {
    var body = doc.getElementsByTagName("body")[0];
    for (var i = 0; i < bboxes.length; i++) {
    	body.removeChild(bboxes[i]);
    }
    bboxes = new Array();
}

// window.addEventListener('load', myjsStart, false);
