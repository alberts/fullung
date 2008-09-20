function [lon, lat] = nww3_lon_lat
%
% NWW3_LON_LAT returns the NWW3 longitude and latitude grid.
%

% number of grid points NX, NY
nx = 288;
ny = 157;

% grid increments SX, SY
sx = 1.25;
sy = 1.00;

lon = 0:sx:360-sx;
lat = 78:-sy:-78;

if length(lon) ~= nx
    error('Invalid number of longitude coordinates');
end
if length(lat) ~= ny
    error('Invalid number of latitude coordinates');
end

[lon, lat] = meshgrid(lon, lat);
