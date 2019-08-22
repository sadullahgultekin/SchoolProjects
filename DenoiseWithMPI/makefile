run: a.out
	mpiexec -n 51 a.out lena200_noisy.txt clearImage.txt 0.6 0.1
	python3 text_to_image.py clearImage.txt img.jpg
a.out: main.cpp
	mpic++ main.cpp