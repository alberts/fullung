function [weights,means,variances]=readubm(filename)
info = hdf5info(filename);
k = length(info.GroupHierarchy.Groups(1).Datasets);
f = max(info.GroupHierarchy.Groups(1).Datasets(1).Dims);
weights = hdf5read(filename,'/weights');
means = zeros(f, k);
variances = zeros(f, k);
for i=1:1:k
    means(:,i) = hdf5read(filename,sprintf('/means/%d',i-1));
    variances(:,i) = hdf5read(filename,sprintf('/variances/%d',i-1));
end
