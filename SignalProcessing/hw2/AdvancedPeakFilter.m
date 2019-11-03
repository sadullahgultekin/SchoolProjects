data = csvread('exampleSignal.csv');
A = [];

for i = 1:30
    peaks = findpeaks(MovingAverageFilter(data,i));
    A = [A numel(peaks)];
end

figure(1)
plot(A)

function [filteredNumbers] = MovingAverageFilter(originalNumbers, N)
    filteredNumbers = originalNumbers;
    for i = 1:length(originalNumbers)
        a = 0;
        for j = i-N+1:i
            if j > 0
                a = a + originalNumbers(j);
            end
        end
        filteredNumbers(i) = a/N;
    end
end