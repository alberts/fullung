filenames = 'waves.csv';
lon = 18;
lat = -34.5;

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

aggregate_nww3_data(filenames, lon, lat, grbfiles);
