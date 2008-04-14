function checkhlda
hldafile = 'z:\data\hlda.h5';
ubmfile = 'z:\data\ubm8_final_79_512.h5';
deltas = zeros(512,1);
for i=1:1:512
    C = hdf5read(hldafile,sprintf('/classcov/%d',i-1)); 
    S = hdf5read(ubmfile,sprintf('/variances/%d',i-1));
    deltas(i) = sum((diag(C)'-S)./S);
end
deltas = abs(deltas);
min(deltas)
max(deltas)
mean(deltas)
plot(sort(deltas));
