lon = 30;
lat = -46;

% dp DIRPW Primary wave direction
filelists.DIRPW = 'F:\dp.txt';

% hs HTSGW Sig height of wind waves and swell
filelists.HTSGW = 'F:\hs.txt';

% tp PERPW Primary wave mean period
filelists.PERPW = 'F:\tp.txt';

% wind UGRD, VGRD u wind, v wind
filelists.UGRD = 'F:\wind.txt';
filelists.VGRD = 'F:\wind.txt';

convert_nww3_data('nww3.csv', lon, lat, filelists);

% z = read_grib_param('F:\waves\nww3.hs.200801.grb', 'HTSGW');
% fig = figure;
% set(fig,'DoubleBuffer','on');
% set(gca,'xlim',[min(lon(:)) max(lon(:))]);
% set(gca,'ylim',[min(lat(:)) max(lat(:))]);
% surface(lon, lat, z(:,:,1), 'EdgeColor', 'none');
% xlabel(gca,'Longitude [degrees]');
% ylabel(gca,'Latitude [degrees]');
% colorbar;
