function convert_nww3_data(filename, lon, lat, filelists, method)
if ~exist('method','var') || isempty(method)
    method = 'linear';
end

[nww3_lon, nww3_lat] = nww3_lon_lat;

params = fieldnames(filelists);
for i=1:length(params)
    param = params{i};
    filelist = filelists.(param);
    filenames.(param) = textread(filelist,'%s');
end
filecount = unique(cellfun(@length,struct2cell(filenames)));
if length(filecount(:)) ~= 1
    error('Filelists are inconsistent');
end

fid = fopen(filename,'w');
fprintf(fid,'Timestamp');
for j=1:length(params)
    fprintf(fid,',%s',params{j});
end
fprintf(fid,'\n');

for i=1:filecount
    data = cell(1,length(params));
    stimes = cell(1,length(params));
    for j=1:length(params)
        param = params{j};
        param_files = filenames.(param);
        fprintf('reading %s...', param_files{i});
        tic;
        [data{j}, stimes{j}] = read_grib_param(param_files{i}, param);
        fprintf('%g seconds\n', toc);
    end
    if ~all(cellfun(@(varargin)length(unique(varargin)),stimes{:})==1)
        error('Timestamps are not consistent over parameters');
    end
    stime = stimes{1};
    for forecast = 1:length(stime)
        fprintf(fid, stime{forecast});
        for j=1:length(data)
            z = data{j}(:,:,forecast);
            zi = interp2(nww3_lon, nww3_lat, z, lon, lat, method);
            fprintf(fid,',%.15E',zi);
        end
        fprintf(fid,'\n');
    end
end

fclose(fid);
