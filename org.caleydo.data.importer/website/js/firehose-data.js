// Nils Gehlenborg, July 2012
// start scope
(function() {
// ---------------------------------

function initializeData( callback ) {
	$.getJSON( "http://compbio.med.harvard.edu/tcga/stratomex/data/2012_06_23.json", callback );
}


function renderContent( data )
{
	renderTumorSelection( data, "#tumor-selection-container" );
	renderTumorType( data, 0 );	
}

function renderTumorType( data, tumorIndex ) {
	renderHeader( data, tumorIndex, "#header-container" );	
	renderControls( data, tumorIndex, "#controls-container" );	
	renderTable( data, tumorIndex, "#table-container" );		
}



function renderTumorSelectionBootstrap( data, element ) {
	
	var tumorTypes = [];
	
	for ( var i = 0; i < data.details.length; ++i ) {
		tumorTypes.push( "<li><a href=\"#\" id=\"" + "tumor-" + i + "\" value=\"" + i + "\">" + data.details[i].tumorAbbreviation + " - " + data.details[i].tumorName + "</a></li>" );
	}
	
	$( "<div/>", {
		class: "btn-group",
		id: "tumor-selection-dropdown",
		html: "<a class=\"btn dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\"><span id=\"tumor-selection-dropdown-title\">" + data.details[0].tumorAbbreviation + " - " + data.details[0].tumorName + "</span><span class=\"caret\"></span></a>",
	}).appendTo( element );
	
	$( "<ul/>", {
		id: "tumor-selection",
		class: "dropdown-menu",
		"html": tumorTypes.join("\n")
	}).appendTo( "#tumor-selection-dropdown" );
	
	
	$( "#tumor-selection" ).on( "change", function() {
		renderTumorType( data, this.value );
	});	
	
	$('#tumor-3' ).on('click', function(){
    	var text = $(this).text(); //.replace(/\ /g,'&nbsp;');
    	$("#tumor-selection-dropdown-title").html(text);
	});	
}


function renderTumorSelection( data, element ) {
	
	var tumorTypes = [];
	
	for ( var i = 0; i < data.details.length; ++i ) {
		tumorTypes.push( "<option id=\"" + "tumor_" + i + "\" value=\"" + i + "\">" + data.details[i].tumorAbbreviation + " - " + data.details[i].tumorName + "</option>" );
	}
	
	$( "<select/>", {
		id: "tumor-selection",
		class: "",
		"html": tumorTypes.join("\n")
	}).appendTo( element );
	
	
	$( "#tumor-selection" ).on( "change", function() {
		renderTumorType( data, this.value );
	});	
}


function renderHeader( data, tumorIndex, element ) {	
	// set default element
	var element = element || "#header-container";
		
	// clear the element
	$( element ).html( "" );
	
	// render caption
	$( "<div>", {
		class: "",
		"html": "<h3>" + data.details[tumorIndex].tumorAbbreviation + " - " + data.details[tumorIndex].tumorName + " (" + data.analysisRun  + ")</h3>"
	}).appendTo( element );	
}


function renderControls( data, tumorIndex, element ) {	
	// set default element
	var element = element || "#controls-container";

	// clear the element
	$( "#webstart-button-container" ).html( "" );
	$( "#download-button-container" ).html( "" );
	$( "#report-button-container" ).html( "" );
		
	// links
	$( "<a/>", {
		"href": data.details[tumorIndex]["Caleydo JNLP"], 
		class: "btn btn-primary",
		"html": "<i class=\"icon-play icon-white\"></i>&nbsp;Start with " + data.details[tumorIndex].tumorAbbreviation
	}).appendTo( "#webstart-button-container" );

	$( "<a/>", {
		"href": data.details[tumorIndex]["Caleydo Project"], 
		class: "",
		"html": "<i class=\"icon-download\"></i>&nbsp;Download Data Set"
	}).appendTo( "#download-button-container" );

	$( "<a/>", {
		"href": data.details[tumorIndex]["Firehose Report"], 
		class: "",
		target: "_new",
		"html": "<i class=\"icon-file\"></i>&nbsp;View Nozzle Report"
	}).appendTo( "#report-button-container" );
	
}


function renderTable( data, tumorIndex, element ) {
	
	// set default element
	var element = element || "#table-container";
		
	// clear the element
	$( element ).html( "" );
	

	// render table
	$( "<table/>", {
		"width": "100%",
		class: "table table-striped",
		"html": _headerRender() + _.map( data.details[tumorIndex].dataSets, _rowRender ).join("\n"),
	}).appendTo( element );
}


function _headerRender() {
		return "<thead><tr><th width=10%>" + "Data Type" + "</th><th>" + "#Patients" + "</th><th width=40%>" + "Patient Stratifications" + 
										  "</td><th>" + "#Genes" + "</th><th width=40%>" + "Gene Stratifications" + "</th></tr></thead>";			
	}

function _rowRender( dataset, datasetName ) {
	if ( dataset ) {
		return "<tr><th>" + datasetName + "</th><td>" + dataset.sample.count + "</td><td>" + dataset.sample.groupings + 
										  "</td><td>" + dataset.gene.count + "</td><td>" + dataset.gene.groupings + "</td></tr>";			
	}
	else {
		return "<tr><th>" + datasetName + "</th><td>" + "0" + "</td><td>" + "" + "</td><td>" + "0" + "</td><td>" + "" + "</td></tr>";					
	}
}


initializeData( renderContent )

// ---------------------------------
})();
// end scope  
