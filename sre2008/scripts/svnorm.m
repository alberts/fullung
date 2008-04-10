function svnorm
mu = 0;
std = 1;
x = mu + std*randn(1,10000);
figure;hist(x,100);
y = 0.5*(1+erf((x-mu)/(std*sqrt(2))));
figure;hist(y,100);
