function [values, stime] = read_grib_param(filename, param)
% Read values for one parameter from a GRIB file.

grib_struct = read_grib(filename,-1,'HeaderFlag',1,'DataFlag',0,'ScreenDiag',0);
stime = {grib_struct.stime};
stime = stime(strcmp({grib_struct.parameter},param));
values = getgrib(filename, param);
values = values.data;
values(isnan(values)) = 0;
if size(values, 3) ~= length(stime)
    error('Number of values not equal to number of timestamps');
end
