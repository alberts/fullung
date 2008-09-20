function [values, stime] = read_grib_param(filename, param)
%
% READ_GRIB_PARAM read values and timestamps for one parameter from a GRIB
% file.
%

[values, gribrec] = getgrib(filename, param);
stime = {gribrec.stime};
stime = stime(strcmp({gribrec.parameter},param));
values = values.data;
values(isnan(values)) = 0;
if size(values, 3) ~= length(stime)
    error('Number of values not equal to number of timestamps');
end
