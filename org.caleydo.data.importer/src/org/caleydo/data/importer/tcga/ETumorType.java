/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.data.importer.tcga;

/**
 * TCGA tumor types as listed here:
 * https://tcga-data.nci.nih.gov/tcga/tcgaHome2.jsp
 * 
 * @author Marc Streit
 * 
 */
public enum ETumorType {

	BLCA("Bladder Urothelial Carcinoma"),
	BRCA("Breast invasive carcinoma"),
	CESC("Cervical squamous cell carcinoma and endocervical adenocarcinoma"),
	COADREAD("Colon adenocarcinoma / Rectum adenocarcinoma"),
	DLBC("Lymphoid Neoplasm Diffuse Large B-cell Lymphoma"),
	GBM("Glioblastoma multiforme"),
	HNSC("Head and Neck squamous cell carcinoma"),
	KICH("Kidney Chromophobe"),
	KIRC("Kidney renal clear cell carcinoma"),
	KIRP("Kidney renal papillary cell carcinoma"),
	LAML("Acute Myeloid Leukemia"),
	LGG("Brain Lower Grade Glioma"),
	LIHC("Liver hepatocellular carcinoma"),
	LUAD("Lung adenocarcinoma"),
	LUSC("Lung squamous cell carcinoma"),
	OV("Ovarian serous cystadenocarcinoma"),
	PAAD("Pancreatic adenocarcinoma"),
	PANCAN8("All samples from: BRCA, COAD, GBM, KIRC, LUSC, OV, READ, UCEC"),
	PRAD("Prostate adenocarcinoma"),
	SKCM("Skin Cutaneous Melanoma"),
	STAD("Stomach adenocarcinoma"),
	SARC("Sarcoma"),
	THCA("Thyroid carcinoma"),
	UCEC("Uterine Corpus Endometrioid Carcinoma");

	/** The full name of the tumor type **/
	private String tumorName;

	ETumorType(String tumorName) {
		this.tumorName = tumorName;
	}

	/**
	 * @return the tumorName, see {@link #tumorName}
	 */
	public String getTumorName() {
		return tumorName;
	}
}
