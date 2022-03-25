all:
	bibtex article
	pdflatex -shell-escape article.tex
	firefox article.pdf
	rm article.log
	clear

bib:
	bibtex article
