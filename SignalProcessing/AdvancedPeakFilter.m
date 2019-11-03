clear
data = csvread('exampleSignal.csv');
A = zeros(30,1);

for i = 1:30
    peaks = findpeaks(filter(ones(i,1)./(1/i),1,data));
    A(i) = numel(peaks);
end

plot(A)