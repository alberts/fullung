function applysv
dim = 38;
mixtures = 512;
ubmfile = 'C:\home\albert\SRE2008\data\ubm_floored_512_3.h5';
stddev = zeros(dim, mixtures);
for i=1:1:mixtures
    name = sprintf('/variances/%d', i-1);
    stddev(:,i) = sqrt(hdf5read(ubmfile, name));
end
weights = hdf5read(ubmfile,'/weights');

inputh5 = 'C:\home\albert\SRE2008\data\sre05_1s1s_gmmfc.h5';
outputh5 = 'C:\home\albert\SRE2008\data\sre05_1s1s_gmmfc_fixed.h5';
% inputh5 = 'C:\home\albert\SRE2008\data\sre04_background_gmmfc.h5';
% outputh5 = 'C:\home\albert\SRE2008\data\sre04_background_gmmfc_fixed.h5';

info = hdf5info(inputh5);

groups = info.GroupHierarchy.Groups;
for i=1:1:length(groups)
   group = groups(i);
   datasets = group.Datasets;
   for j=1:1:length(datasets)
      dataset = datasets(j);
      name = dataset.Name;
      disp(name);
      x = single(hdf5read(inputh5, name));

      % multiply by sqrt of weight
      for k=1:1:mixtures
          w = weights(k);
          indices = (k-1)*dim+1:k*dim;
          % XXX this seemed to do damage?
%          x(indices) = sqrt(w) * x(indices);
          x(indices) = 1.0 * x(indices);
      end

      if ~exist(outputh5,'file') || (i==1&&j==1)
          writemode = 'overwrite';
      else
          writemode = 'append';
      end
      hdf5write(outputh5, name, single(x), 'WriteMode', writemode);
   end
end
