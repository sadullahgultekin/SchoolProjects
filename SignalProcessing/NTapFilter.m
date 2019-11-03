[data, Fs] = audioread('mike.wav');

K = 0.1;
data = resize(data, Fs, K);
initialData = data;
data = data + delayseq(data,K,Fs);

% Case 1 %
N = 2;
K = 0.1;
snr1 = [];
for i = 0:0.01:1
    filter = ntapfilter(N, K, i, initialData, Fs);
    snr1 = [snr1 ; snr(initialData, filter)];
end
figure(1)
plot(snr1)
title('Constant N=2, K=0.1, alpha from 0 to 1');


% Case 2 %
a = 0.2;
K = 0.1;
snr2 = [];
for i = 1:50
    filter = ntapfilter(i, K, a, initialData, Fs);
    snr2 = [snr2 ; snr(initialData, filter)];
end
figure(2)
plot(snr2)
title('Constant K=0.1, alpha=0.2, N from 1 to 50');


% Case 3 %
a = 0.2;
N = 2;
snr3 = [];
for i = 0.1:0.1:0.4
    filter = ntapfilter(N, i, a, initialData, Fs);
    snr3 = [snr3 ; snr(initialData, filter)];
end
figure(3)
plot(snr3)
title('Constant alpha=0.2, N=2, K from 0.1 to 0.4');

function result = snr(original, filtered)
    Nom = sum(filtered.^2);
    Denom = sum((original-filtered).^2);
    result = 10*log(Nom/Denom);
end

function result = resize(data,Fs,K)
    result = [data ; zeros((K*Fs),1)];
end

function returnVal = ntapfilter(N, K, alpha, signal, Fs)
    returnVal = signal;
    for i=1:N
        signal = delayseq(signal, Fs, K);
        returnVal = returnVal + signal * (-alpha)^i;
    end
end