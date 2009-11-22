var bboxes = new Array();

function start() {
	setTimeout("triggerSearch()", 2000);
}

function triggerSearch() {
	var id = getId();
	if (id != null) {
		removeBoundingBoxes();
		searchDocument(id);
	} else {
		//alert("request timed out, could not get filter from vislink daemon");
	}
	setTimeout("triggerSearch()", 500);
}

function getId() {
	var xhttp = new XMLHttpRequest();
	xhttp.open("GET","propagation",false);
	xhttp.send("");
	xmlDoc = xhttp.responseXML;
	var ids = xmlDoc.getElementsByTagName("id");
	if (ids.length > 0) {
		return ids[0].childNodes[0].nodeValue;
	} else {
		return null;
	}
}

function searchDocument(id) {
	var textnodes = document.evaluate("//body//*/text()", document, null, XPathResult.ANY_TYPE, null);
	var result = new Array();
	while(node = textnodes.iterateNext()) {
		var s = node.nodeValue;
        var i = s.indexOf(id);
        if (i != -1) {
            result[result.length] = node.parentNode;
        }
	}
	debug("hits: " + result.length);

	for(var i=0; i<result.length; i++) {
		var r = result[i];
		for (var j=0; j<r.childNodes.length; j++) {
			currentNode = r.childNodes[j];
			sourceString = currentNode.nodeValue;
			if (sourceString != null) {
				var idx = sourceString.indexOf(id);
				while (idx > 0) {
					var s1 = sourceString.substring(0, idx);
					var s2 = sourceString.substring(idx, idx + id.length);
					var s3 = sourceString.substr(idx + id.length);
					
					var d2 = document.createElement("SPAN");
					d2.style.outline = "2px solid red";
					var t2 = document.createTextNode(s2);
					d2.appendChild(t2);
					var t3 = document.createTextNode(s3);
					
					currentNode.nodeValue = s1;
					var nextSib = currentNode.nextSibling
					if (nextSib == null) {
						r.appendChild(d2);
						r.appendChild(t3);
					} else {
						r.insertBefore(d2, nextSib);
						r.insertBefore(t3, nextSib);
					}
					
					findBoundingBox(d2);
					
					r.removeChild(t3);
					r.removeChild(d2);
					currentNode.nodeValue = sourceString;
					
					var idx = sourceString.indexOf(id, id.length + idx);
				};
			}
		}
	}
}

function findBoundingBox(obj) {
	var w = obj.offsetWidth;
	var h = obj.offsetHeight;
	var curleft = curtop = 0;

    if (obj.offsetParent) {
        do {
            curleft += obj.offsetLeft;
            curtop += obj.offsetTop;
        } while (obj = obj.offsetParent);
    }
    var yoffset = window.outerHeight - 24 - window.innerHeight;
    finaltop = curtop + window.screenY + yoffset;
    finalleft = curleft + window.screenX;
    
    debug("[(x:" + finalleft + ", y:" + finaltop + "), (w:" + w + ", h:" + h + ")]");
    var body = document.getElementsByTagName("body")[0];
    var bb = document.createElement("DIV");
    body.appendChild(bb);
    bb.style.position = "absolute";
    bb.style.top = curtop + "px";
    bb.style.left = curleft + "px";
    bb.style.width = w + "px";
    bb.style.height = h + "px";
    bb.style.outline = "2px solid red";
    bboxes[bboxes.length] = bb;
}

function removeBoundingBoxes() {
    var body = document.getElementsByTagName("body")[0];
    for (var i = 0; i < bboxes.length; i++) {
    	body.removeChild(bboxes[i]);
    }
    bboxes = new Array();
}

function debug(s) {
	var d = document.getElementById("debug");
	d.innerHTML += s + "<br />";
}