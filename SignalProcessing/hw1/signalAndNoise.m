%%%%%%%%%%%%%
% PROBLEM 1 %
%%%%%%%%%%%%%

figure(1)

clf('reset')

x = -100:0.3:100;

subplot(4,2,1)
y1 = sin(x);
plot(x,y1)
title('plot of sin(x)')

subplot(4,2,2)
y2 = sin(50 * x);
plot(x,y2)
title('plot of sin(50x)')

subplot(4,2,3)
y3 = 50 * sin(x);
plot(x,y3)
title('plot of 50sin(x)')

subplot(4,2,4)
y4 = sin(x) + 50;
plot(x,y4)
title('plot sin(x)+50')

subplot(4,2,5)
y5 = sin(x + 50);
plot(x,y5)
title('plot of sin(x+50)')

subplot(4,2,6)
y6 = 50 * sin(50*x);
plot(x,y6)
title('plot of 50sin(50x)')

subplot(4,2,7)
y7 = x .* sin(x);
plot(x,y7)
title('plot of xsin(x)')

subplot(4,2,8)
y8 = sin(x) ./ x;
plot(x,y8)
title('plot sin(x)/x')

%%%%%%%%%%%%%
% PROBLEM 2 %
%%%%%%%%%%%%%

figure(2)

clf('reset')

x = -20:0.05:20;

subplot(5,2,1)
y1 = sin(x);
plot(x,y1)
title('plot of sin(x)')

subplot(5,2,2)
y2 = sin(50*x);
plot(x,y2)
title('plot of sin(50x)')

subplot(5,2,3)
y3 = 50 * sin(x);
plot(x,y3)
title('plot of 50sin(x)')

subplot(5,2,4)
y4 = sin(x) + 50;
plot(x,y4)
title('plot of sin(x)+50')

subplot(5,2,5)
y5 = sin(x + 50);
plot(x,y5)
title('plot of sin(x+50)')

subplot(5,2,6)
y6 = 50 * sin(50 * x);
plot(x,y6)
title('plot of 50sin(50x)')

subplot(5,2,7)
y7 = x .* sin(x);
plot(x,y7)
title('plot of xsin(x)')

subplot(5,2,8)
y8 = sin(x) ./ x;
plot(x,y8)
title('plot of sin(x)/x')

subplot(5,2,9)
y9 = y1 + y2 + y3 + y4 + y5 + y6 + y7 + y8;
plot(x,y9)
title('plot of y1+y2+y3+y4+y5+y6+y7+y8')

%%%%%%%%%%%%%
% PROBLEM 3 %
%%%%%%%%%%%%%

figure(3)

clf('reset')

x = linspace(-20,20,41);
z = randn(1,41);

subplot(5,2,1)
y10 = z;
plot(x,y10)
title('plot of z')

subplot(5,2,2)
y11 = z + x;
plot(x,y11)
title('plot of x+z')

subplot(5,2,3)
y12 = z + sin(x);
plot(x,y12)
title('plot of z+sin(x)')

subplot(5,2,4)
y13 = z .* sin(x);
plot(x,y13)
title('plot of zsin(x)')

subplot(5,2,5)
y14 = x .* sin(x);
plot(x,y14)
title('plot of xsin(x)')

subplot(5,2,6)
y15 = sin(x + z);
plot(x,y15)
title('plot of sin(x+z)')

subplot(5,2,7)
y16 = z .* sin(50 * x);
plot(x,y16)
title('plot of zsin(50x)')

subplot(5,2,8)
y17 = sin(x + 50 * z);
plot(x,y17)
title('plot of sin(x+50z)')

subplot(5,2,9)
y18 = sin(x) ./ z;
plot(x,y18)
title('plot of sin(x)/z')

subplot(5,2,10)
y19= y11 + y12 + y13 + y14 + y15 + y16 + y17 + y18;
plot(x,y19)
title('plot of y11+y12+y13+y14+y15+y16+y17+y18')

%%%%%%%%%%%%%
% PROBLEM 4 %
%%%%%%%%%%%%%

figure(4)

clf('reset')

z = rand(1,41);

subplot(5,2,1)
y20 = z;
plot(x,y20)
title('plot of z')

subplot(5,2,2)
y21 = z + x;
plot(x,y21)
title('plot of z+x')

subplot(5,2,3)
y22 = z + sin(x);
plot(x,y22)
title('plot of z+sin(x)')

subplot(5,2,4)
y23 = z .* sin(x);
plot(x,y23)
title('plot of zsin(x)')

subplot(5,2,5)
y24 = x .* sin(x);
plot(x,y24)
title('plot of xsin(x)')

subplot(5,2,6)
y25 = sin(x + z);
plot(x,y25)
title('plot of sin(x+z)')

subplot(5,2,7)
y26 = z .* sin(50 * x);
plot(x,y26)
title('plot of zsin(50x)')

subplot(5,2,8)
y27 = sin(x + 50 * z);
plot(x,y27)
title('plot of sin(x+50z)')

subplot(5,2,9)
y28 = sin(x) ./ z;
plot(x,y28)
title('plot of sin(x)/z')

subplot(5,2,10)
y29= y21 + y22 + y23 + y24 + y25 + y26 + y27 + y28;
plot(x,y29)
title('plot of y21+y22+y23+y24+y25+y26+y27+y28')

%%%%%%%%%%%%%
% PROBLEM 5 %
%%%%%%%%%%%%%

figure(5)

clf('reset')
q = randn(10000,1);
r1 = 1 .* q;
r2 = 2 .* q;
r3 = 4 .* q;
r4 = 16 .* q;

subplot(2,2,1)
hist(r1)
title("hist of r1")
subplot(2,2,2)
hist(r2)
title("hist of r2")
subplot(2,2,3)
hist(r3)
title("hist of r3")
subplot(2,2,4)
hist(r4)
title("hist of r4")

%%%%%%%%%%%%%
% PROBLEM 6 %
%%%%%%%%%%%%%

figure(6)

clf('reset')

r6 = 1 .* randn(10000,1) + 10;
r7 = 2 .* randn(10000,1) + 20;
r8 = 1 .* randn(10000,1) - 10;
r9 = 2 .* randn(10000,1) - 20;

subplot(2,2,1)
hist(r6)
title("hist of r6")
subplot(2,2,2)
hist(r7)
title("hist of r7")
subplot(2,2,3)
hist(r8)
title("hist of r8")
subplot(2,2,4)
hist(r9)
title("hist of r9")

%%%%%%%%%%%%%
% PROBLEM 7 %
%%%%%%%%%%%%%

figure(7)

clf('reset')
y = rand(10000,1);
r11 = 1 .* y - 0.5;
r21 = 2 .* y - 1;
r31 = 4 .* y - 2;
r41 = 16 .* y - 8;

subplot(2,2,1)
hist(r11)
title("hist of r11")
subplot(2,2,2)
hist(r21)
title("hist of r21")
subplot(2,2,3)
hist(r31)
title("hist of r31")
subplot(2,2,4)
hist(r41)
title("hist of r41")


%%%%%%%%%%%%%
% PROBLEM 8 %
%%%%%%%%%%%%%

figure(8)

clf('reset')

r61 = 1 .* rand(10000,1) + 10 - 0.5;
r71 = 2 .* rand(10000,1) + 20 - 1;
r81 = 1 .* rand(10000,1) - 10 - 0.5;
r91 = 2 .* rand(10000,1) - 20 - 1;

subplot(2,2,1)
hist(r61)
title("hist of r61")
subplot(2,2,2)
hist(r71)
title("hist of r71")
subplot(2,2,3)
hist(r81)
title("hist of r81")
subplot(2,2,4)
hist(r91)
title("hist of r91")

