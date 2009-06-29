var isIE = false;

var pageNum = 0;
var iconsOnPage = 20;

function init() {
	//alert(BrowserDetect.browser + "\r\n" + BrowserDetect.version);
	if (BrowserDetect.browser == "Explorer" && BrowserDetect.version == 6) {
		isIE = true;
	}

	load();
}

function load() {
	var iconsOnThisPage = iconsOnPage;
	var tdStart = 0;

	// we are on the second page, so we add the back arrow
	if (pageNum > 0) {
		var tdNode = document.getElementById("td1");
		var aNode = document.createElement("a");
		var imgNode = document.createElement("img");

		imgNode.src = "../images/iconbrowser/assets/arrowLeft.png";

		aNode.href = "javascript:iconPageBack();";
		aNode.appendChild(imgNode);

		while (tdNode.childNodes.length > 0)
			tdNode.removeChild(tdNode.lastChild);

		tdNode.appendChild(aNode);

		if (!isIE) {
			tdNode.setAttribute("class", "iconbox");
		} else {
			tdNode.setAttribute("className", "iconbox");
		}

		// decrement num of Icons on this page
		iconsOnThisPage--;
		tdStart = 1;
	}

	var currentIconNum = 0;
	if (pageNum > 0) {
		currentIconNum = pageNum * (iconsOnPage - 2) + 1;
	}

	//we have more icons, so we add the next arrow
	if (pageNum * currentIconNum + iconsOnPage < icons.length) {
		var tdNode = document.getElementById("td" + iconsOnPage);
		var aNode = document.createElement("a");
		var imgNode = document.createElement("img");
		imgNode.src = "../images/iconbrowser/assets/arrowRight.png";

		aNode.href = "javascript:iconPageNext();";
		aNode.appendChild(imgNode);

		while (tdNode.childNodes.length > 0)
			tdNode.removeChild(tdNode.lastChild);

		tdNode.appendChild(aNode);
		if (!isIE) {
			tdNode.setAttribute("class", "iconbox");
		} else {
			tdNode.setAttribute("className", "iconbox");
		}

		// decrement num of Icons on this page
		iconsOnThisPage--;

	} else {
		var tdNode = document.getElementById("td" + iconsOnPage);
		while (tdNode.childNodes.length > 0)
			tdNode.removeChild(tdNode.lastChild);

		tdNode.removeAttribute("onclick");
	}

	//fill the icons
	for (i = tdStart; i <= iconsOnThisPage; i++) {
		if (i + 1 > iconsOnThisPage)
			continue;

		var tdNode = document.getElementById("td" + (i + 1));

		if (tdNode == null)
			alert(i + 1);

		while (tdNode.childNodes.length > 0)
			tdNode.removeChild(tdNode.lastChild);

		if (icons[currentIconNum] != null) {
			var imgNode = document.createElement("img");
			var aNode = document.createElement("a");

			imgNode.src = pathPicSmall + icons[currentIconNum];
			aNode.href = "javascript:loadBigPic('" + icons[currentIconNum] + "');";
			aNode.appendChild(imgNode);

			if (i == tdStart)
				loadBigPic(icons[currentIconNum]);

			if (!isIE) {
				tdNode.setAttribute("class", "iconboxWithBackground");
				imgNode.setAttribute("style", "border:0px; width:70px; height:70px;");
			} else {
				tdNode.setAttribute("className", "iconboxWithBackground");
				imgNode.style.cssText = "border:0px; width:70px; height:70px;";
			}

			tdNode.appendChild(aNode);
		} else {
			if (!isIE) {
				tdNode.setAttribute("class", "iconbox");
			} else {
				tdNode.setAttribute("className", "iconbox");
			}
		}
		currentIconNum++;
	}
}

function iconPageBack() {
	if (pageNum <= 0)
		return;

	pageNum--;
	load();
}

function iconPageNext() {
	pageNum++;
	load();
}

function loadBigPic(pic) {
	var tdNode = document.getElementById("tdBigPic");

	while (tdNode.childNodes.length > 0)
		tdNode.removeChild(tdNode.lastChild);

	var imgNode = document.createElement("img");
	var spanNode = document.createElement("span");
	spanNode.innerHTML = pic.substring(0, pic.length - 4).toUpperCase();

	if (!isIE) {
		spanNode.setAttribute("class", "bigpicTextOverlay");
		imgNode.setAttribute("class", "imageBig");
	} else {
		spanNode.setAttribute("className", "bigpicTextOverlay");
		imgNode.setAttribute("className", "imageBig");
	}
	imgNode.src = pathPicBig + pic;

	if (doBigpicTextOverlay)
		tdNode.appendChild(spanNode);

	tdNode.appendChild(imgNode);

	loadBigPicDescription(pic);

	loadAbstractions(pic);
}

function loadBigPicDescription(pic) {
	var tdNode = document.getElementById("tdBigPicDescription");

	while (tdNode.childNodes.length > 0)
		tdNode.removeChild(tdNode.lastChild);

	var textNode = document.createTextNode(Description.getDescription(pic));

	tdNode.appendChild(textNode);

}

function loadAbstractions(pic) {
	if (!pic.match(/[a-zA-Z]1[.]png/))
		return;

	var pic2 = pic.replace(/1[.]png/g, "2.png");
	var pic3 = pic.replace(/1[.]png/g, "3.png");
	var pic4 = pic.replace(/1[.]png/g, "4.png");

	var tdNode2 = document.getElementById("abstd1");
	var tdNode3 = document.getElementById("abstd2");
	var tdNode4 = document.getElementById("abstd3");

	replaceAbstractionsPic(tdNode2, pic2);
	replaceAbstractionsPic(tdNode3, pic3);
	replaceAbstractionsPic(tdNode4, pic4);
}

function replaceAbstractionsPic(tdNode, imagePath) {
	if (tdNode == null)
		return;

	var imgNode = document.createElement("img");
	var aNode = document.createElement("a");

	imgNode.src = pathPicSmall + imagePath;
	aNode.href = "javascript:loadBigPic('" + imagePath + "');";
	aNode.appendChild(imgNode);

	if (!isIE) {
		imgNode.setAttribute("style", "border:0px; width:70px; height:70px;");
	} else {
		imgNode.style.cssText = "border:0px; width:70px; height:70px;";
	}

	while (tdNode.childNodes.length > 0)
		tdNode.removeChild(tdNode.lastChild);

	tdNode.appendChild(aNode);
}
