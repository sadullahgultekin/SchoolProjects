%%%%%%%%%%%%% 
% PROBLEM 9 %
%%%%%%%%%%%%%
figure(1)

clf('reset')

filename = 'exampleSignal.csv';
A = csvread(filename);
A = A(3:end);
findpeaks(A)

figure(2)

clf('reset')
findpeaks(A,'MinPeakDistance', 500)
