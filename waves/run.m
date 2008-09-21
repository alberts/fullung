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
    basenames = basenames(1:2);
    grbfiles.(params{i}) = cellfun(f,basenames,'UniformOutput',0);
end

convert_nww3_data(filenames, lon, lat, grbfiles);
