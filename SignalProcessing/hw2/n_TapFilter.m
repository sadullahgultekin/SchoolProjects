clear
hfile = 'mike.wav';

[signal, Fs] = audioread(hfile);
K = 0.1;
signal = [signal ; zeros((Fs*K),1)];
originalSignal = signal;
signal = signal + delayseq(signal,K,Fs);

%%{
% 1 %
allSNRs = [];
N = 2;
K = 0.1;
for alpha = 0:0.1:1
    allSNRs = [allSNRs snr(originalSignal,nTapFilter(signal,alpha,K,N,Fs))];
end
subplot(3,1,1)
plot(0:0.1:1,allSNRs)
%}

%%{
% 2 %
allSNRs = [];
alpha = 0.5;
K = 0.1;
for N = 1:50
    allSNRs = [allSNRs snr(originalSignal,nTapFilter(signal,alpha,K,N,Fs))];
end
subplot(3,1,2)
plot(1:50,allSNRs)
%}

%%{
% 3 %
allSNRs = [];
alpha = 0.5;
N = 2;
for K = 0.1:0.1:0.4
    allSNRs = [allSNRs snr(originalSignal,nTapFilter(signal,alpha,K,N,Fs))];
end
subplot(3,1,3)
plot(1:4,allSNRs)
%}

function result = nTapFilter(signal,alpha,K,N,fs)
    result = signal;
    for i=1:N
        signal = delayseq(signal,fs,K);
        result = result + ((-1)*alpha)^i * signal;
    end
end

function snrValue = snr(I, E)
    snrValue = 10*log(sum(I.^2)/sum((E-I).^2));
end

