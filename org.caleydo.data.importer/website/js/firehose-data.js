// Nils Gehlenborg, July 2012
// start scope
(function() {
// ---------------------------------

var tumor;
var analysis;
var runs;

// Read a page's GET URL variables and return them as an associative array.
function getUrlParameters( decode )
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');

    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        
        if ( hash.length == 2 ) {
	        vars.push(hash[0]);
	        
	        if ( decode == true ) {
		        vars[hash[0]] = decodeURIComponent( hash[1].split( "#" )[0] );
		    }
		    else {
		        vars[hash[0]] = hash[1].split( "#" )[0];	    
			}	            	
        }	    
    }

    return vars;
}   


function getBaseUrl()
{
    //return window.location.href.slice(0, window.location.href.indexOf('?'));
    return "http://compbio.med.harvard.edu/tcga/stratomex";
}   

function getCurrentUrl()
{
    var base = getBaseUrl();

    return base + "/index.html"
    			+ "?" + "analysis=" + analysis
    			+ "&" + "tumor=" + tumor;
}   


function initialize() {
	tumor = getUrlParameters()["tumor"];
	analysis = getUrlParameters()["analysis"];
	
	loadRuns( function( data ) {
		runs = data;
		analysisIndex = renderAnalysisSelection( data, "#analysis-selection-container" );
		
		loadRun( getBaseUrl() + "/data/" + runs[analysisIndex].json, renderContent );
	});	
}


function loadRuns( callback ) {
	$.getJSON( getBaseUrl() + "/data/" + "tcga_analysis_runs.json", callback );
}


function loadRun( url, callback ) {
	$.getJSON( url, callback );
}


function renderContent( data )
{
	if ( !data ) {
		$( "<div/>", {
			class: "alert alert-info",
			"html": "<a class=\"close\" data-dismiss=\"alert\" href=\"#\">x</a>" + getCurrentUrl()
		}).appendTo(  "#header-container"  );		
		
	}

	tumorIndex = renderTumorSelection( data, "#tumor-selection-container", tumor );
	renderTumorType( data, tumorIndex );	
}

function renderTumorType( data, tumorIndex ) {
	renderHeader( data, tumorIndex, "#header-container" );	
	renderTable( data, tumorIndex, "#table-container" );	
	renderControls( data, tumorIndex, "#controls-container" );	
}

function clearTumorType() {
	$( "#header-container" ).html("");	
	$( "#table-container" ).html("");	
	$( "#controls-container" ).html("");
	
	$( "#tumor-selection-container" ).html(""); 	
	$( "#webstart-instructions-container" ).html( "" );
	$( "#webstart-button-container" ).html( "" );
	$( "#download-button-container" ).html( "" );
	$( "#report-button-container" ).html( "" );
	$( "#direct-link-container" ).html( "" );
	$( "#direct-link-url-container" ).html( "" );		
}


/*
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
*/


function renderAnalysisSelection( runs, element ) {
	
	var analysisDates = [];
	var analysisIndex = 0;
	console.log(runs);
	
	for ( var i = 0; i < runs.length; ++i ) {
		if ( runs[i].id === analysis ) {
			analysisIndex = i;
			analysisDates.push( "<option selected id=\"" + "analysis_" + i + "\" value=\"" + i + "\">" + runs[i].label + "</option>" );
		}
		else {
			analysisDates.push( "<option id=\"" + "analysis_" + i + "\" value=\"" + i + "\">" + runs[i].label + "</option>" );
		}		
	}
	
	$( "<select/>", {
		id: "analysis-selection",
		class: "",
		"html": analysisDates.join("\n")
	}).appendTo( element );

	analysis = runs[analysisIndex].id;
	
	
	$( "#analysis-selection" ).on( "change", function() {
		clearTumorType();
		loadRun( getBaseUrl() + "/" + runs[this.value].json, renderContent );		
	});	
	
	return analysisIndex;
}


function renderTumorSelection( data, element ) {	
	var tumorTypes = [];
	var tumorIndex = 0;
	
	$( element ).html("");
	
	for ( var i = 0; i < data.details.length; ++i ) {
		if ( data.details[i].tumorAbbreviation === tumor ) {
			tumorIndex = i;
			tumorTypes.push( "<option selected id=\"" + "tumor_" + i + "\" value=\"" + i + "\">" + data.details[i].tumorAbbreviation + " - " + data.details[i].tumorName + "</option>" );
		}
		else {
			tumorTypes.push( "<option id=\"" + "tumor_" + i + "\" value=\"" + i + "\">" + data.details[i].tumorAbbreviation + " - " + data.details[i].tumorName + "</option>" );			
		}		
	}

	tumor = data.details[tumorIndex].tumorAbbreviation;
	
	$( "<select/>", {
		id: "tumor-selection",
		class: "",
		"html": tumorTypes.join("\n")
	}).appendTo( element );
	
	
	$( "#tumor-selection" ).on( "change", function() {
		renderTumorType( data, this.value );
		tumor = data.details[this.value].tumorAbbreviation;
	});	
	
	return tumorIndex;
}


function renderHeader( data, tumorIndex, element ) {	
	// set default element
	var element = element || "#header-container";
		
	// clear the element
	$( element ).html( "" );
	
	// render caption
	$( "<div>", {
		class: "",
		"html": "<h2>" + data.details[tumorIndex].tumorAbbreviation + " - " + data.details[tumorIndex].tumorName + " (" + data.analysisRun  + ")</h2>"
	}).appendTo( element );
}


function renderControls( data, tumorIndex, element ) {	
	// set default element
	var element = element || "#controls-container";

	// clear the element
	$( "#webstart-instructions-container" ).html( "" );
	$( "#webstart-button-container" ).html( "" );
	$( "#download-button-container" ).html( "" );
	$( "#report-button-container" ).html( "" );
	$( "#direct-link-container" ).html( "" );
	$( "#direct-link-url-container" ).html( "" );		
		
	// links
	$( "<span/>", {
		class: "",
		"html": "Start <b>Caleydo Stratomex " + data.caleydoVersion + "</b> with Java Web Start and automatically load all data for the selected tumor type."
	}).appendTo( "#webstart-instructions-container" );	
	
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

	$( "<div/>", {
		class: "",
		"html": "<i class=\"icon-retweet\"></i>&nbsp;<a id=\"direct-link\" href=\"#\">Get direct link</a>"
	}).appendTo(  "#direct-link-container"  );
	
	$( "#direct-link" ).on( "click", function() {
		$( "#direct-link-url-container" ).html( "" );		
		$( "<div/>", {
			class: "alert alert-info",
			"html": "<a class=\"close\" data-dismiss=\"alert\" href=\"#\">x</a>" + getCurrentUrl()
		}).appendTo(  "#direct-link-url-container"  );
	});		
}


function renderTable( data, tumorIndex, element ) {
	
	// set default element
	var element = element || "#table-container";
		
	// clear the element
	$( element ).html( "" );
	

	// render table
	$( "<h3/>", {
		"html": "Molecular Data Types",
	}).appendTo( element );
	
	$( "<table/>", {
		"width": "100%",
		class: "table table-striped table-condensed",
		"html": _genomicHeaderRender() + _.map( data.details[tumorIndex].genomic, _genomicRowRender ).join("\n"),
	}).appendTo( element );

	$( "<h3/>", {
		"html": "Other Data Types",
	}).appendTo( element );
	
	$( "<table/>", {
		"width": "100%",
		class: "table table-striped",
		"html": _nonGenomicHeaderRender() + _.map( data.details[tumorIndex].nonGenomic, _nonGenomicRowRender ).join("\n"),
	}).appendTo( element );

}


function _genomicHeaderRender() {
		return "<thead><tr><th width=10%>" + "Data Type" + "</th><th>" + "#Patients" + "</th><th width=40%>" + "Patient Stratifications" + 
										  "</td><th>" + "#Genes" + "</th><th width=40%>" + "Gene Stratifications" + "</th></tr></thead>";			
	}

function _genomicRowRender( dataset, datasetName ) {
	if ( dataset ) {
		return "<tr><th>" + datasetName + "</th><td>" + dataset.sample.count + "</td><td>" + dataset.sample.groupings.join( "; ") + 
										  "</td><td>" + dataset.gene.count + "</td><td>" + dataset.gene.groupings.join( "; ") + "</td></tr>";			
	}
	/*
	else {
		return "<tr><th>" + datasetName + "</th><td>" + "0" + "</td><td>" + "" + "</td><td>" + "0" + "</td><td>" + "" + "</td></tr>";					
	}
	*/
}


function _nonGenomicHeaderRender() {
		return "<thead><tr><th width=10%>" + "Data Type" + "</th><th>" + "#Patients" + "</th><th>" + "Parameters" + 
										  "</th></tr></thead>";			
	}

function _nonGenomicRowRender( dataset, datasetName ) {
	if ( dataset ) {
		return "<tr><th>" + datasetName + "</th><td>" + dataset.count + "</td><td>" + dataset.parameters.join( "; ") + 
										  "</td></tr>";			
	}
	else {
		return "<tr><th>" + datasetName + "</th><td>" + "0" + "</td><td>" + "" + "</td></tr>";					
	}
}

initialize();



// ---------------------------------
})();
// end scope  
