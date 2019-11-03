clear;
clc;

% read the data
[y, Fs] = audioread('PinkPanther30.wav');

% create empty array to fill
numberOfPeaks = zeros(5,1);

% make low pass filter for different cut off values
for N=1000:1000:4000
    numberOfPeaks(N/1000) = length(findpeaks(lowpass(y,N,Fs)));
end

% add the signal without filter
numberOfPeaks(5) = length(findpeaks(y));

% plot the data
x = [1000:1000:4000];
x = [x Fs];

plot(x,numberOfPeaks)