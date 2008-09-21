filenames = {'nww3_v1.csv','nww3_v2.csv'};
coords = [30 -46; 30.1 -46.1];
lon = coords(:,1);
lat = coords(:,2);

datadir = 'F:\waves';
params = {'DIRPW', 'HTSGW', 'PERPW', 'UGRD', 'VGRD'};
codes = {'dp', 'hs', 'tp', 'wind', 'wind'};
grbfiles = struct();
for i=1:length(params)
    d = dir([datadir,filesep,'nww3.',codes{i},'.*.grb']);
    f = @(x)[datadir,filesep,x];
    basenames = unique({d.name});
    grbfiles.(params{i}) = cellfun(f,basenames,'UniformOutput',0);
end

convert_nww3_data(filenames, lon, lat, grbfiles);

% z = read_grib_param('F:\waves\nww3.hs.200801.grb', 'HTSGW');
% fig = figure;
% set(fig,'DoubleBuffer','on');
% set(gca,'xlim',[min(lon(:)) max(lon(:))]);
% set(gca,'ylim',[min(lat(:)) max(lat(:))]);
% surface(lon, lat, z(:,:,1), 'EdgeColor', 'none');
% xlabel(gca,'Longitude [degrees]');
% ylabel(gca,'Latitude [degrees]');
% colorbar;
