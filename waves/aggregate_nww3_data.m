function aggregate_nww3_data(filenames, lon, lat, grbfiles, method)
%
% AGGREGATE_NWW3_DATA
%

if ~iscell(filenames)
    filenames = {filenames};
end
if length(lon(:)) ~= length(filenames)
    error('Must specify equal number of filenames and longitudes');
end
if length(lat(:)) ~= length(filenames)
    error('Must specify equal number of filenames and latitudes');
end

params = fieldnames(grbfiles);
filecount = unique(cellfun(@length,struct2cell(grbfiles)));
if length(filecount(:)) ~= 1
    error('GRIB files are inconsistent');
end
if filecount==0
    error('No GRIB files specified');
end

if ~exist('method','var') || isempty(method)
    method = 'linear';
end

[nww3_lon, nww3_lat] = nww3_lon_lat;

fids = cell(1,length(filenames));
for k=1:length(fids)
    fid = fopen(filenames{k},'w');
    fprintf(fid,'Timestamp');
    for j=1:length(params)
        fprintf(fid,',%s',params{j});
    end
    fprintf(fid,'\n');
    fids{k} = fid;
end

for i=1:filecount
    data = cell(1,length(params));
    stimes = cell(1,length(params));
    for j=1:length(params)
        param = params{j};
        param_files = grbfiles.(param);
        fprintf('reading %s... ', param_files{i});
        tic;
        [data{j}, stimes{j}] = read_grib_param(param_files{i}, param);
        fprintf('%g seconds\n', toc);
    end
    if ~all(cellfun(@(varargin)length(unique(varargin)),stimes{:})==1)
        error('Timestamps are not consistent over parameters');
    end
    stime = stimes{1};
    for forecast = 1:length(stime)
        for k = 1:length(fids)
            fprintf(fids{k}, stime{forecast});
        end
        for j=1:length(data)
            z = data{j}(:,:,forecast);
            zi = interp2(nww3_lon, nww3_lat, z, lon, lat, method);
            for k = 1:length(fids)
                fprintf(fids{k},',%.15E',zi(k));
            end
        end
        for k = 1:length(fids)
            fprintf(fids{k}, '\n');
        end
    end
end

for k=1:length(fids)
    fclose(fids{k});
end
