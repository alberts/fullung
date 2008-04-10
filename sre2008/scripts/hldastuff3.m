function hldastuff3
hldafile = 'C:\home\albert\SRE2008\data\hlda.h5';
counts = hdf5read(hldafile,'/counts');
GC = hdf5read(hldafile,'/globalcov');
CC = cell({512,1});
for i=1:1:512
    name = sprintf('/classcov/%d', i-1);
    CC{i} = hdf5read(hldafile, name);
end

datadim = 79;
hldadim = 39;
iters = 300;

A = eye(datadim);
A = hlda_optimizer(A, hldadim, GC, CC, counts, iters);
Trans = A(:,1:hlda_dim)';

size(Trans);
