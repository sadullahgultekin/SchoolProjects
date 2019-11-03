% read the data
RGB = imread('Hubble-Massive-Panorama.png');
% turn it to gray mode
temp = rgb2gray(RGB);
% make it binerized
img = imbinarize(temp);
% we need to use a frequency more than 1800
Fs = 2000;
% final array that will contain all signals
newimg = zeros(1024,Fs);

for i = 1:1024
    for j = 1:900
        % if pixel is black
        if img(j,i)
            % amplitude ranks from 10 to 1
            Amplitude = 11 - ceil(j/90);
            % Fs number rank from 0 to 1
            t = linspace(0, 1, Fs);
            % signal of pixel
            wave = Amplitude*sin(2*pi*j*t);
            % add signals one by one
            newimg(i,:) = newimg(i,:) + wave;
        end
    end
end

% make the sound
temp = transpose(newimg);
sound(temp(:),Fs)