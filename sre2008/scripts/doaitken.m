function doaitken
featuredim = 79;
gmmdim = 512;

trialfile = 'aitken05.txt';
statsfile = 'aitken05.h5';
ubmfile = 'aitken_ubm8_final_79_512.h5';

ubmmeans = zeros(featuredim, gmmdim);
ubmvars = zeros(featuredim, gmmdim);
for i = 1:1:gmmdim
   ubmmeans(:,i) = hdf5read(ubmfile, sprintf('/means/%d', i-1));
   ubmvars(:,i) = hdf5read(ubmfile, sprintf('/variances/%d', i-1));
end

fid = fopen(trialfile);
while 1
    tline = fgetl(fid);
    if ~ischar(tline), break, end;
    disp(tline);
    parts = strsplit(' ', tline);

    modelprefix = sprintf('/%s', parts{1});
    modeln = hdf5read(statsfile, sprintf('%s/n', modelprefix));
    modeln = double(modeln);
    modelex = hdf5read(statsfile, sprintf('%s/ex', modelprefix));
    modelex = double(modelex);

    trialprefix = sprintf('/%s/%s', parts{2}, parts{3});
    trialn = hdf5read(statsfile, sprintf('%s/n', trialprefix));
    trialn = double(trialn);
    trialex = hdf5read(statsfile, sprintf('%s/ex', trialprefix));
    trialex = double(trialex);

    % label is 0 for nontarget and 1 for target
    label = strcmp(parts{4}, 'target');
end
fclose(fid);
