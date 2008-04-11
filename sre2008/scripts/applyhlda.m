function applyhlda
load hldaoutput.mat Trans;

% inputh5 = 'C:\home\albert\SRE2008\data\sre05_1s1s_gmm.h5';
% outputh5 = 'C:\home\albert\SRE2008\data\sre05_1s1s_gmmnap.h5';

inputh5 = 'z:\data\sre04_background_mfcc2.h5';
outputh5 = 'z:\data\sre04_background_mfcc2_hlda.h5';

% inputh5 = 'C:\home\albert\SRE2008\data\sre04_background_gmm.h5';
% outputh5 = 'C:\home\albert\SRE2008\data\sre04_background_gmmnap.h5';

%if exist(outputh5,'file'), delete(outputh5); end;

info = hdf5info(inputh5);

groups = info.GroupHierarchy.Groups;
for i=1:1:length(groups)
   group = groups(i);
   datasets = group.Datasets;
   for j=1:1:length(datasets)
      dataset = datasets(j);
      name = dataset.Name;
      disp(name);
      x = double(hdf5read(inputh5, name));
	  y = Trans*x;
      if ~exist(outputh5,'file') || (i==1&&j==1)
          writemode = 'overwrite';
      else
          writemode = 'append';
      end
      hdf5write(outputh5, name, single(y), 'WriteMode', writemode);
   end
end
