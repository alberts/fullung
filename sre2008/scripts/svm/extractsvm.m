% sre04hdfin = 'telmicvar_sre04.h5';
% sre05hdfin = 'telmicvar_sre05.h5';
sre04hdfin = 'mmmv_sre04.h5';
% hdfout = 'telmicvar_svm.h5';
hdfout = 'mmmv_svm.h5';

% sre05hdfin = 'micmic200_sre05.h5';
% hdfout = 'micmic200_svm.h5';

count = 0;
hdf5write(hdfout, '/', []);

% sre04
fid = fopen('sre04_svm.txt');
segments04 = textscan(fid,'%s');
segments04 = segments04{1};
fclose(fid);
for i=1:1:length(segments04)
    count = count + 1;
    name = segments04{i};
    inputname = sprintf('/%s/%d',name(1:end-2),name(end)-'a');
    outputname = sprintf('/svm%d', count);
    fprintf('%s:%s -> %s:%s\n', sre04hdfin, inputname, hdfout, outputname);
    x = hdf5read(sre04hdfin, inputname);
    hdf5write(hdfout, outputname, single(x), 'WriteMode', 'append');
end

% % sre05
% fid = fopen('sre05_svm.txt');
% segments05 = textscan(fid,'%s');
% segments05 = segments05{1};
% fclose(fid);
% for i=1:1:length(segments05)
%     count = count + 1;
%     name = segments05{i};
%     inputname = sprintf('/%s/%d',name(1:end-2),name(end)-'a');
%     outputname = sprintf('/svm%d', count);
%     fprintf('%s:%s -> %s:%s\n', sre05hdfin, inputname, hdfout, outputname);
%     x = hdf5read(sre05hdfin, inputname);
%     hdf5write(hdfout, outputname, single(x), 'WriteMode', 'append');
% end

% fid = fopen('sre05_mic_background.txt');
% segments05 = textscan(fid,'%s');
% segments05 = segments05{1};
% fclose(fid);
% for i=1:1:length(segments05)
%     count = count + 1;
%     name = segments05{i};
%     inputname = sprintf('/%s/%d',name(1:end-2),name(end)-'a');
%     outputname = sprintf('/svm%d', count);
%     fprintf('%s:%s -> %s:%s\n', sre05hdfin, inputname, hdfout, outputname);
%     x = hdf5read(sre05hdfin, inputname);
%     hdf5write(hdfout, outputname, single(x), 'WriteMode', 'append');
% end
