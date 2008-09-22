function [values, stime] = read_grib_param(filename, param)
%
% READ_GRIB_PARAM read values and timestamps for one parameter from a GRIB
% file.
%
if ~exist(filename,'file')
    error('GRIB file %s does not exist', filename);
end
[values, gribrec] = getgrib(filename, param);
stime = {gribrec.stime};
stime = stime(strcmp({gribrec.parameter},param));
values = values.data;
% set NaN values to 0 for interpolation to work
values(isnan(values)) = 0;
if size(values, 3) ~= length(stime)
    error('Number of values not equal to number of timestamps');
end
