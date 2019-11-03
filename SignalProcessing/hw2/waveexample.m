hfile = 'laughter.wav';
clear y Fs

[y, Fs] = audioread(hfile);
duration = numel(y) / Fs;

% EXERCISE I

sound(y(1:4:end), Fs);
pause(duration)

% EXERCISE II

x = zeros(1,length(y)*2);
for i = 1:length(x)
    if mod(i,2) == 1
        x(1,i) = y(floor(i/2)+1);
    else
        x(1,i) = y(floor(i/2));
    end
end

sound(x, Fs);
pause(duration + 7)

% EXERCISE III

newFs = Fs * 2;
sound(y, newFs);
pause(duration)

% EXERCISE IV

newFs = Fs / 2;
sound(y, newFs);




