function varnormhlda

% inputh5 = 'Z:\data\sre05_1conv4w_1conv4w_mfcc2.h5';
% outputh5 = 'Z:\data\sre05_1conv4w_1conv4w_mfcc2_hlda.h5';

% XXX instead of transforming orig here, can use applyhlda
% inputh5 = 'z:\data\sre04_background_mfcc2_orig.h5';
% outputh5 = 'z:\data\sre04_background_mfcc2_hlda.h5';

load stddev.mat stddev;   
load hldaoutput.mat Trans;

info = hdf5info(inputh5);
groups = info.GroupHierarchy.Groups;
for i=1:1:length(groups)
   group = groups(i);
   datasets = group.Datasets;
   for j=1:1:length(datasets)
      dataset = datasets(j);
      name = dataset.Name;
      disp(name);
      mfcc = double(hdf5read(inputh5, name));
      
      % XXX check for data having the wrong order here somewhere
      for k = 1:1:size(mfcc, 2)
          mfcc(:,k) = mfcc(:,k) ./ stddev;
      end
      mfcc = Trans * mfcc;

      if ~exist(outputh5,'file') || (i==1&&j==1)
          writemode = 'overwrite';
      else
          writemode = 'append';
      end
      hdf5write(outputh5, name, single(mfcc), 'WriteMode', writemode);
   end
end
