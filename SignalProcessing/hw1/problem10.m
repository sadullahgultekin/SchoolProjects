%%%%%%%%%%%%%%
% PROBLEM 10 %
%%%%%%%%%%%%%%

B = imread('lena.png');
B = rgb2gray(B);

meanOfColumns = mean(B);
meanOfMatrix = mean(meanOfColumns);

standartDeviation = std2(B);

[~,I] = min(B(:));
[min_row, min_col] = ind2sub(size(B),I);
minElem = min(min(B));

[~,I] = max(B(:));
[max_row, max_col] = ind2sub(size(B),I);
maxElem = max(max(B));

disp("Mean is " + meanOfMatrix)
disp("Standard Deviation is " + standartDeviation)
disp("Mininum elment is " + minElem + " and its position is " + min_row + ";" + min_col)
disp("Maximum elment is " + maxElem + " and its position is " + max_row + ";" + max_col)

