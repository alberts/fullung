function hldagmm

hldafile = 'C:\home\albert\SRE2008\data\hlda.h5';
inputfile = 'C:\home\albert\SRE2008\data\ubm7_final_512.h5';
outputfile = 'C:\home\albert\SRE2008\data\ubm7_hldaorig_512.h5';

weights = single(hdf5read(inputfile,'/weights'));
mixcount = length(weights);
hdf5write(outputfile, '/weights', weights, 'WriteMode', 'overwrite');

load hldaoutput.mat Trans;

for i=1:1:mixcount
    name = sprintf('/means/%d', i-1);
    mean = double(hdf5read(inputfile, name))';
    mean = Trans * mean;
    mean = single(mean);
    hdf5write(outputfile, name, mean, 'WriteMode', 'append');
end

for i=1:1:mixcount
    name = sprintf('/classcov/%d', i-1);
    classcov = double(hdf5read(hldafile, name));
    variance = diag(Trans*classcov*Trans');
    variance = single(variance);
    name = sprintf('/variances/%d', i-1);    
    hdf5write(outputfile, name, variance, 'WriteMode', 'append');
end
