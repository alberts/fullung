function varnorm

inputh5 = 'Z:\data\sre04_background_mfcc2_orig.h5';
outputh5 = 'Z:\data\sre04_background_mfcc2.h5';

if 0
    bgh5 = 'Z:\data\sre04_background_mfcc2_orig.h5';
    stddev = trainvarnorm(bgh5);
    save stddev.mat stddev;
    return
else
    load stddev.mat stddev;    
end

info = hdf5info(inputh5);
groups = info.GroupHierarchy.Groups;
for i=1:1:length(groups)
   group = groups(i);
   datasets = group.Datasets;
   for j=1:1:length(datasets)
      dataset = datasets(j);
      name = dataset.Name;
      disp(name);
      mfcc = single(hdf5read(inputh5, name));
      for k = 1:1:size(mfcc, 2)
          mfcc(:,k) = mfcc(:,k) ./ stddev;
      end
      if ~exist(outputh5,'file') || (i==1&&j==1)
          writemode = 'overwrite';
      else
          writemode = 'append';
      end
      hdf5write(outputh5, name, single(mfcc), 'WriteMode', writemode);
   end
end

function [stddev]=trainvarnorm(bgh5)
info = hdf5info(bgh5);
groups = info.GroupHierarchy.Groups;
n = 0;
dim = 79;
mean = zeros(dim,1);
S = zeros(dim,1);
for i=1:1:length(groups)
   group = groups(i);
   datasets = group.Datasets;
   for j=1:1:length(datasets)
      dataset = datasets(j);
      name = dataset.Name;
      disp(name);
      mfcc = single(hdf5read(bgh5, name));
      for k = 1:1:size(mfcc, 2)
          x = mfcc(:,k);
          n = n + 1;
          delta = x - mean;
          mean = mean + delta/n;
          S = S + delta.*(x - mean);
      end
   end
end
variance = S/(n-1);
stddev = sqrt(variance);

% mfcc = hdf5read(bgh5,'/taaf/0');
% for k = 1:1:size(mfcc, 2)
%     mfcc(:,k) = mfcc(:,k) ./ stddev;
% end
% figure;
% plot(mfcc(1,:));
% figure;
% plot(mfcc(30,:));
% figure;
% plot(mfcc(79,:));
