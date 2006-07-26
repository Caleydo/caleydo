PDFLATEX_CMD = pdflatex
LATEX_CMD = latex
DVIPS_CMD = dvips
BIBTEX_CMD = bibtex
FILES = main.tex
CURRENT_DATE = `date +%Y%m%d`

all: clean bin_pdf/main.pdf

slides: clean slides.pdf 

handout: clean handout.pdf

view: all view_text

vslides: slides view_slides_command

vhandout: handout view_handout_command

bin_pdf/main.pdf: ${FILES}
	${PDFLATEX_CMD} main.tex
	${BIBTEX_CMD} main
	${PDFLATEX_CMD} main.tex
	${PDFLATEX_CMD} main.tex

main.dvi: ${FILES}
	${LATEX_CMD} main.tex
	${BIBTEX_CMD} main
	${LATEX_CMD} main.tex
	${LATEX_CMD} main.tex

main.ps: main.dvi
	${DVIPS_CMD} main.dvi -o main.ps

slides.pdf: slides_main.tex
	${PDFLATEX_CMD} slides_main.tex
	${PDFLATEX_CMD} slides_main.tex

handout.pdf: handout.tex
	${PDFLATEX_CMD} handout.tex
	${PDFLATEX_CMD} handout.tex


# open the acrobat reader with the pdfs
view_text:
	-acroread bin_pdf/main.pdf -openInNewWindow

view_slides_command:
	-acroread slides_main.pdf -openInNewWindow

view_handout_command:
	-acroread handout.pdf -openInNewWindow

# clean up all temporary latex files
clean:
	-rm *.aux *.bbl main.dvi main.ps bin_pdf/main.pdf slides_main.pdf handout.pdf *.blg *.idx *.ilg *.ind *.toc *.log *.log *.brf *.nav *.snm *.out *.lof *.lot *.gxg *.glx *.gxs *.glo *.gls semantic.cache auto biblio/auto -f -R; mv *~* /tmp/; mv biblio/*~* /tmp/ 

archive: clean
	(tar -czf latex_report_vorlage_${CURRENT_DATE}.tgz * )

force:
	touch *tex

